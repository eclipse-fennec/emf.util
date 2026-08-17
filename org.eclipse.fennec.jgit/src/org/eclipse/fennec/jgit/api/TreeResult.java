/*
 * ******************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 * ******************************************************************
 */
package org.eclipse.fennec.jgit.api;

import java.util.List;

public class TreeResult {

	private String commitId;
	private List<String> files;
	
	public TreeResult(String commitId, List<String> files) {
		this.commitId = commitId;
		this.files = files;
	}

	public String getCommitId() {
		return commitId;
	}

	public List<String> getFiles() {
		return files;
	}
	
}
