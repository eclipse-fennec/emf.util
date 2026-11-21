/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Opt-in checks against real schemas that are not checked in (licensing / size):
 * <ul>
 * <li>{@code MOF_CIM_SCHEMA} — path to the root file of a DMTF CIM Schema distribution, e.g.
 * {@code cim_schema_2.56.0.mof} from {@code cim_schema_2.56.0Final-MOFs.zip}
 * (https://www.dmtf.org/standards/cim). Imported in strict mode: every superclass and
 * {@code REF} target must resolve, and the result must validate.</li>
 * <li>{@code MOF_CORPUS_DIR} — a directory of unrelated MOF files, e.g. a Windows
 * {@code System32\wbem}. Each file is imported on its own in lenient mode (they reference
 * classes of other files and of the WMI repository); this checks the parser.</li>
 * </ul>
 */
@DisplayName("MofImporter: opt-in real-world schemas")
class MofCorpusTest {

	@Test
	@EnabledIfEnvironmentVariable(named = "MOF_CIM_SCHEMA", matches = ".+")
	@DisplayName("the complete DMTF CIM Schema imports strictly and validates")
	void cimSchema() {
		Path root = Path.of(System.getenv("MOF_CIM_SCHEMA"));
		MofImport result = MofImporter.importFrom(root);
		List<EClass> classes = result.getEClasses();
		System.out.println("CIM schema: " + classes.size() + " classes, " + result.warnings().size() + " warnings");
		result.warnings().stream().limit(20).forEach(w -> System.out.println("  " + w));
		assertThat(classes).hasSizeGreaterThan(1000);
		assertThat(result.packages()).extracting(EPackage::getName).containsExactly("CIM");
		Resource r = new ResourceImpl(org.eclipse.emf.common.util.URI.createURI("mem:/cim.ecore"));
		new ResourceSetImpl().getResources().add(r);
		r.getContents().addAll(result.packages());
		Diagnostic d = Diagnostician.INSTANCE.validate(result.packages().get(0));
		assertThat(d.getSeverity()).as(d.toString()).isLessThan(Diagnostic.ERROR);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "MOF_CORPUS_DIR", matches = ".+")
	@DisplayName("every MOF file of a corpus directory parses")
	void corpus() throws IOException {
		List<Path> files;
		try (Stream<Path> s = Files.list(Path.of(System.getenv("MOF_CORPUS_DIR")))) {
			files = s.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".mof")).sorted().toList();
		}
		List<String> failures = new ArrayList<>();
		int classes = 0;
		for (Path file : files) {
			try {
				MofImport result = MofImporter.importFrom(file, ImportOptions.defaults().withStrict(false));
				classes += result.getEClasses().size();
				Resource r = new ResourceImpl(org.eclipse.emf.common.util.URI.createURI("mem:/corpus.ecore"));
				new ResourceSetImpl().getResources().add(r);
				r.getContents().addAll(result.packages());
				for (EPackage p : result.packages()) {
					Diagnostic d = Diagnostician.INSTANCE.validate(p);
					if (d.getSeverity() >= Diagnostic.ERROR) {
						failures.add(file + ": invalid Ecore: " + d.getChildren().get(0).getMessage());
					}
				}
			} catch (MofException e) {
				failures.add(e.getMessage());
			}
		}
		System.out.println("Corpus: " + files.size() + " files, " + classes + " classes, " + failures.size()
				+ " failures");
		failures.forEach(f -> System.out.println("  " + f));
		assertThat(failures).isEmpty();
	}
}
