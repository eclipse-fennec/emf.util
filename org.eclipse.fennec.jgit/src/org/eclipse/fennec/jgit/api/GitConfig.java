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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface GitConfig {
	String repo();

	String branch() default "main";

	/**
	 * URL of the remote an <em>on-disk</em> {@link #repo()} is mirrored to:
	 * {@code git://}, {@code git@host:path}, {@code ssh://}, {@code http://} or
	 * {@code https://}. Commits are then written into the local repository — durable
	 * the moment they are made — and additionally pushed to, and fetched from, this
	 * remote.
	 * <p>
	 * Empty means the on-disk repository stands alone, with no remote at all. When
	 * {@link #repo()} is itself a URL that URL <em>is</em> the remote and the service
	 * holds an in-memory mirror of it; naming a different remote as well is a
	 * configuration error rather than a third combination.
	 */
	@AttributeDefinition(required = false)
	String remote() default "";

	/**
	 * Path to the SSH private key authenticating against an SSH remote. Empty means
	 * no key: the SSH stack is then not built at all, which is what an anonymous
	 * {@code git://}, an {@code http(s)://} or an on-disk repository wants.
	 */
	@AttributeDefinition(required = false)
	String privateKey() default "";

	/**
	 * Passphrase of {@link #privateKey()}, if it is encrypted. Empty means the key
	 * is not encrypted.
	 */
	@AttributeDefinition(required = false)
	String privateKeyPassphrase() default "";

	/**
	 * Path to an OpenSSH {@code known_hosts} file used to verify the SSH server's
	 * host key. When empty, the Apache MINA sshd backend falls back to its default
	 * location ({@code ~/.ssh/known_hosts}). Only relevant for SSH remotes.
	 */
	@AttributeDefinition(required = false)
	String knownHosts() default "";

	/**
	 * Author and committer name recorded on commits written through this service.
	 * A {@link CommitRequest} may override it per commit. Configuring it is
	 * required for an in-memory repository, which has no {@code user.name} of its
	 * own.
	 */
	String authorName() default "Fennec Git Service";

	/**
	 * Author and committer e-mail recorded on commits written through this service.
	 */
	String authorEmail() default "fennec@eclipse.org";

	/**
	 * Whether every commit is pushed to the remote right away. Off by default:
	 * committing and pushing are two explicit calls, so a caller can build up
	 * several commits and push them in one go. A {@link CommitRequest} may override
	 * it per commit.
	 */
	boolean pushOnCommit() default false;

	/**
	 * User name for a remote over https. Not used for SSH remotes, which
	 * authenticate with {@link #privateKey()}.
	 */
	@AttributeDefinition(required = false)
	String username() default "";

	/**
	 * Password or access token belonging to {@link #username()}.
	 */
	@AttributeDefinition(type = AttributeType.PASSWORD, required = false)
	String password() default "";
}
