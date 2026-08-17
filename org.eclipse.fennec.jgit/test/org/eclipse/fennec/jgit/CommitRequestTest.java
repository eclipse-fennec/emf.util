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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.CommitRequest.Change;
import org.eclipse.fennec.jgit.api.CommitRequest.Change.Type;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.junit.jupiter.api.Test;

/**
 * Covers the request value object on its own: what it accepts, what it rejects and
 * what it hands on to the writer.
 */
public class CommitRequestTest {

	private static byte[] bytes(String content) {
		return content.getBytes(StandardCharsets.UTF_8);
	}

	@Test
	public void testChangesKeepTheirOrder() {
		CommitRequest request = CommitRequest.builder("msg") //
				.put("a", bytes("a")) //
				.delete("b") //
				.deleteTree("c") //
				.put("d", bytes("d")) //
				.build();

		assertThat(request.getChanges()).extracting(Change::getType, Change::getPath) //
				.containsExactly( //
						org.assertj.core.groups.Tuple.tuple(Type.PUT, "a"), //
						org.assertj.core.groups.Tuple.tuple(Type.DELETE, "b"), //
						org.assertj.core.groups.Tuple.tuple(Type.DELETE_TREE, "c"), //
						org.assertj.core.groups.Tuple.tuple(Type.PUT, "d"));
	}

	@Test
	public void testDefaults() {
		CommitRequest request = CommitRequest.builder("msg").put("a", bytes("a")).build();

		assertThat(request.getMessage()).isEqualTo("msg");
		assertThat(request.getAuthorName()).isNull();
		assertThat(request.getAuthorEmail()).isNull();
		// null, not false: the service must be able to tell "unspecified" from "do not push",
		// because unspecified follows the configured pushOnCommit.
		assertThat(request.getPush()).isNull();
	}

	@Test
	public void testPushIsATriState() {
		assertThat(CommitRequest.builder("m").put("a", bytes("a")).push(true).build().getPush()).isTrue();
		assertThat(CommitRequest.builder("m").put("a", bytes("a")).push(false).build().getPush()).isFalse();
	}

	@Test
	public void testAuthorOverride() {
		CommitRequest request = CommitRequest.builder("msg").put("a", bytes("a")) //
				.author("Hans Wurst", "hw@example.com").build();

		assertThat(request.getAuthorName()).isEqualTo("Hans Wurst");
		assertThat(request.getAuthorEmail()).isEqualTo("hw@example.com");
	}

	@Test
	public void testContentIsCopiedOnTheWayIn() {
		byte[] content = bytes("original");
		CommitRequest request = CommitRequest.builder("msg").put("a", content).build();

		content[0] = 'X'; // the caller reusing its buffer must not change the pending commit

		assertThat(request.getChanges().get(0).getContent()).isEqualTo(bytes("original"));
	}

	@Test
	public void testChangesAreNotModifiable() {
		CommitRequest request = CommitRequest.builder("msg").put("a", bytes("a")).build();

		assertThatThrownBy(() -> request.getChanges().clear()).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void testDeleteCarriesNoContent() {
		CommitRequest request = CommitRequest.builder("msg").delete("a").build();

		assertThat(request.getChanges().get(0).getContent()).isNull();
	}

	@Test
	public void testStreamIsReadAndClosed() {
		class TrackingStream extends ByteArrayInputStream {
			private boolean closed;

			TrackingStream(byte[] buf) {
				super(buf);
			}

			@Override
			public void close() throws IOException {
				closed = true;
				super.close();
			}
		}
		TrackingStream stream = new TrackingStream(bytes("streamed"));

		CommitRequest request = CommitRequest.builder("msg").put("a", stream).build();

		assertThat(request.getChanges().get(0).getContent()).isEqualTo(bytes("streamed"));
		assertThat(stream.closed).as("stream closed by the builder").isTrue();
	}

	@Test
	public void testUnreadableStreamFailsAsAWriteException() {
		InputStream broken = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("boom");
			}
		};

		assertThatThrownBy(() -> CommitRequest.builder("msg").put("a", broken)) //
				.isInstanceOf(GitWriteException.class) //
				.hasMessageContaining("a") //
				.hasRootCauseMessage("boom");
	}

	@Test
	public void testBlankMessageIsRejected() {
		assertThatThrownBy(() -> CommitRequest.builder(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> CommitRequest.builder("")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> CommitRequest.builder("   ")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testEmptyRequestIsRejected() {
		assertThatThrownBy(() -> CommitRequest.builder("msg").build()).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("at least one change");
	}

	@Test
	public void testNullContentIsRejected() {
		assertThatThrownBy(() -> CommitRequest.builder("msg").put("a", (byte[]) null))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> CommitRequest.builder("msg").put("a", (InputStream) null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testBlankPathIsRejected() {
		assertThatThrownBy(() -> CommitRequest.builder("msg").put(null, bytes("a")))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> CommitRequest.builder("msg").put("  ", bytes("a")))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> CommitRequest.builder("msg").delete("/")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testPathIsNormalized() {
		CommitRequest request = CommitRequest.builder("msg") //
				.put("/leading", bytes("a")) //
				.delete("  /spaced/deep  ") //
				.build();

		assertThat(request.getChanges()).extracting(Change::getPath).containsExactly("leading", "spaced/deep");
	}
}
