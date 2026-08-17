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

import java.lang.annotation.Annotation;

import org.eclipse.fennec.jgit.api.GitConfig;

/**
 * A hand-built {@link GitConfig} so the component can be activated from a plain
 * JUnit test, the way Declarative Services would activate it with a configuration.
 * <p>
 * The defaults mirror the ones declared on the annotation; only what a test cares
 * about is set.
 */
class TestGitConfig implements GitConfig {

	private final String repo;
	private String branch = "main";
	private String privateKey;
	private String privateKeyPassphrase;
	private String knownHosts = "";
	private String authorName = "Fennec Git Service";
	private String authorEmail = "fennec@eclipse.org";
	private boolean pushOnCommit = false;
	private String username = "";
	private String password = "";

	TestGitConfig(String repo) {
		this.repo = repo;
	}

	TestGitConfig branch(String branch) {
		this.branch = branch;
		return this;
	}

	TestGitConfig author(String name, String email) {
		this.authorName = name;
		this.authorEmail = email;
		return this;
	}

	TestGitConfig pushOnCommit(boolean pushOnCommit) {
		this.pushOnCommit = pushOnCommit;
		return this;
	}

	TestGitConfig credentials(String username, String password) {
		this.username = username;
		this.password = password;
		return this;
	}

	@Override
	public String repo() {
		return repo;
	}

	@Override
	public String branch() {
		return branch;
	}

	@Override
	public String privateKey() {
		return privateKey;
	}

	@Override
	public String privateKeyPassphrase() {
		return privateKeyPassphrase;
	}

	@Override
	public String knownHosts() {
		return knownHosts;
	}

	@Override
	public String authorName() {
		return authorName;
	}

	@Override
	public String authorEmail() {
		return authorEmail;
	}

	@Override
	public boolean pushOnCommit() {
		return pushOnCommit;
	}

	@Override
	public String username() {
		return username;
	}

	@Override
	public String password() {
		return password;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return GitConfig.class;
	}
}
