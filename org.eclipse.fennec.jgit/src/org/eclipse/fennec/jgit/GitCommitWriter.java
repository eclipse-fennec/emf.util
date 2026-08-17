/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.jgit;

import java.io.IOException;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.CommitRequest.Change;
import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEditor.DeletePath;
import org.eclipse.jgit.dircache.DirCacheEditor.DeleteTree;
import org.eclipse.jgit.dircache.DirCacheEditor.PathEdit;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Writes commits straight into the object database of a repository, without going
 * through a working tree or an index file.
 * <p>
 * This is what makes writing work at all for the in-memory mirror of a remote
 * repository, which has no working tree, and it keeps a single code path for
 * in-memory, bare and on-disk repositories. The index is assembled in core from the
 * tree of the current branch head, the requested changes are applied to it, and the
 * resulting tree becomes a commit whose parent is that same head. The branch ref is
 * then moved with the head it was built on as the expected old value, so a
 * concurrent update is detected instead of being overwritten.
 */
class GitCommitWriter {

	private final Repository repo;

	GitCommitWriter(Repository repo) {
		this.repo = repo;
	}

	/**
	 * Applies the changes of the request as one commit on the given ref.
	 *
	 * @param ref     the fully qualified ref to move, e.g. {@code refs/heads/main}
	 * @param request the changes to apply
	 * @param author  the author and committer to record
	 * @return the id of the new commit
	 */
	ObjectId commit(String ref, CommitRequest request, PersonIdent author) {
		try {
			ObjectId oldHead = resolveHead(ref);
			ObjectId newCommit;
			try (ObjectInserter inserter = repo.newObjectInserter(); RevWalk revWalk = new RevWalk(repo)) {
				RevCommit parent = oldHead == null ? null : revWalk.parseCommit(oldHead);
				DirCache index = readTree(parent);
				applyChanges(index, request, inserter);
				ObjectId treeId = index.writeTree(inserter);

				CommitBuilder commitBuilder = new CommitBuilder();
				commitBuilder.setTreeId(treeId);
				if (parent != null) {
					commitBuilder.setParentId(parent);
				}
				commitBuilder.setAuthor(author);
				commitBuilder.setCommitter(author);
				commitBuilder.setMessage(request.getMessage());
				newCommit = inserter.insert(commitBuilder);
				// The commit and everything below it must be readable before the ref points at it.
				inserter.flush();
			}
			updateRef(ref, oldHead, newCommit, request.getMessage());
			return newCommit;
		} catch (GitWriteException e) {
			throw e;
		} catch (Exception e) {
			throw new GitWriteException("Unable to commit to " + ref, e);
		}
	}

	/**
	 * The head the commit is built on. Overridable so a test can simulate the head
	 * moving away underneath a commit, which is otherwise only reachable by racing
	 * two writers.
	 */
	ObjectId resolveHead(String ref) throws IOException {
		return repo.resolve(ref);
	}

	/**
	 * Seeds an in-core index with the content of the parent commit, or leaves it
	 * empty for the very first commit of a repository.
	 */
	private DirCache readTree(RevCommit parent) throws IOException {
		DirCache index = DirCache.newInCore();
		DirCacheBuilder builder = index.builder();
		if (parent != null) {
			try (ObjectReader reader = repo.newObjectReader()) {
				builder.addTree(new byte[0], DirCacheEntry.STAGE_0, reader, parent.getTree());
			}
		}
		builder.finish();
		return index;
	}

	private void applyChanges(DirCache index, CommitRequest request, ObjectInserter inserter) throws IOException {
		// One editor per change, because the changes are ordered - a later change to the same
		// path must win, and a single editor rejects duplicate edits for one path.
		for (Change change : request.getChanges()) {
			DirCacheEditor editor = index.editor();
			switch (change.getType()) {
			case PUT:
				ObjectId blobId = inserter.insert(Constants.OBJ_BLOB, change.getContent());
				editor.add(new PathEdit(change.getPath()) {
					@Override
					public void apply(DirCacheEntry entry) {
						entry.setFileMode(FileMode.REGULAR_FILE);
						entry.setObjectId(blobId);
					}
				});
				break;
			case DELETE:
				editor.add(new DeletePath(change.getPath()));
				break;
			case DELETE_TREE:
				editor.add(new DeleteTree(change.getPath()));
				break;
			}
			editor.finish();
		}
	}

	/**
	 * Moves the ref, insisting that it still points at the head the commit was built
	 * on. Anything but a clean update is reported rather than forced.
	 */
	private void updateRef(String ref, ObjectId oldHead, ObjectId newCommit, String message) throws IOException {
		RefUpdate refUpdate = repo.updateRef(ref);
		refUpdate.setNewObjectId(newCommit);
		refUpdate.setExpectedOldObjectId(oldHead == null ? ObjectId.zeroId() : oldHead);
		refUpdate.setRefLogMessage("commit: " + message, false);
		Result result = refUpdate.update();
		switch (result) {
		case NEW:
		case FAST_FORWARD:
		case FORCED:
		case NO_CHANGE:
			return;
		case LOCK_FAILURE:
		case REJECTED:
		case REJECTED_CURRENT_BRANCH:
			throw new GitConflictException(
					ref + " moved while the commit was being built (" + result + "), fetch and retry");
		default:
			throw new GitWriteException("Unable to update " + ref + ": " + result);
		}
	}
}
