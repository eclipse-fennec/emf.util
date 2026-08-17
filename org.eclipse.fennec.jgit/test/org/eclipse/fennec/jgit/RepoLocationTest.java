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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The configured {@code repo} is either a URL of a remote or a path on disk, and the
 * whole activation takes a different route depending on which. Getting the
 * classification wrong is silent and confusing — a mirror is built for something that
 * is a directory, or a directory is looked for where a URL was given — so it is
 * pinned down here for every form that reasonably occurs.
 */
public class RepoLocationTest {

	@ParameterizedTest
	@ValueSource(strings = { //
			"git://127.0.0.1:9418/served", //
			"git@github.com:eclipse-fennec/emf.util.git", //
			"ssh://git@github.com/eclipse-fennec/emf.util.git", //
			"https://github.com/eclipse-fennec/emf.util.git", //
			"http://internal.example.com/models.git" })
	public void testUrlsAreRemote(String repo) {
		assertThat(GitServiceImpl.isRemote(repo)).as(repo).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { //
			"/var/lib/fennec/models", //
			"./models", //
			"../models", //
			"testRepo", //
			"C:\\repos\\models" })
	public void testPathsAreLocal(String repo) {
		assertThat(GitServiceImpl.isRemote(repo)).as(repo).isFalse();
	}

	/**
	 * Directory names are not schemes: a local directory that happens to begin with
	 * the letters of a scheme is still a directory.
	 */
	@ParameterizedTest
	@ValueSource(strings = { "gitRepo", "git-models", "gitlab-export", "sshkeys", "httpsdump" })
	public void testDirectoriesNamedLikeASchemeAreLocal(String repo) {
		assertThat(GitServiceImpl.isRemote(repo)).as(repo).isFalse();
	}

	@Test
	public void testAbsolutePathContainingGitIsLocal() {
		assertThat(GitServiceImpl.isRemote("/home/user/git/models")).isFalse();
	}
}
