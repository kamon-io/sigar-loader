/* =========================================================================================
 * Copyright © 2013-2014 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

import sbt._
import Keys._

/** Sigar distribution repackaging. */
object SigarRepack {
  import UnzipTask._
  import sbt.Package._
  import Dependencies._
  import com.typesafe.sbt.osgi.OsgiKeys

  /** Helper settings for extracted sigar sources. */
  lazy val sigarSources = SettingKey[File]("sigar-sources", "Location of extracted sigar sources.")

  /** Helper settings for extracted sigar javadoc. */
  lazy val sigarJavadoc = SettingKey[File]("sigar-javadoc", "Location of extracted sigar javadoc.")

  /** Native o/s libraries folder inside kamon-sigar.jar. Hardcoded in [kamon.sigar.SigarProvisioner.java]. */
  lazy val nativeFolder = "native"

  /** Full class name of the sigar activator. Provides http://wiki.osgi.org/wiki/Bundle-Activator. */
  lazy val activatorClass = "kamon.sigar.SigarActivator"

  /** Full class name of the sigar load time agent. Provides Agent-Class and Premain-Class contracts. */
  lazy val agentClass = "kamon.sigar.SigarAgent"

  /** Full class name of the sigar main class. Provides sigar command line interface. */
  lazy val mainClass = "org.hyperic.sigar.cmd.Runner"

  /** A name filter which matches java source files. */
  lazy val sourceFilter: NameFilter = new PatternFilter(
    java.util.regex.Pattern.compile("""(.+\.java)""")
  )

  /** A name filter which matches document files. */
  lazy val javadocFilter: NameFilter = new PatternFilter(
    java.util.regex.Pattern.compile("""(.+\.html)""")
  )

  /** A name filter which matches java class files. */
  lazy val classFilter: NameFilter = new PatternFilter(
    java.util.regex.Pattern.compile("""(.+\.class)""")
  )

  /** A name filter which matches native o/s libraries. */
  lazy val nativeFilter: NameFilter = new PatternFilter(
    java.util.regex.Pattern.compile("""(.+\.dll)|(.+\.dylib)|(.+\.lib)|(.+\.sl)|(.+\.so)""")
  )

  /** Required final jar manifest headers. Present in both default and OSGI packaging. */
  lazy val manifestHeaders = Seq(
    ("Main-Class", mainClass),
    ("Agent-Class", agentClass),
    ("Premain-Class", agentClass),
    ("Embedded-Sigar-Origin", redhatRepo.root),
    ("Embedded-Sigar-Licence", sigarLicence),
    ("Embedded-Sigar-Version", sigarVersion),
    ("Embedded-Sigar-BuildVersion", sigarBuildVersion)
  )

  /** Repackage origial Sigar classes, sources and native libraries. */
  lazy val settings = Seq(

    /** Hide external artifacts from pom.xml. */
    ivyConfigurations += external,

    /** Location of sigar source extraction. */
    sigarSources := target.value / "sigar-sources",

    /** Location of sigar source extraction. */
    sigarJavadoc := target.value / "sigar-javadoc",

    /** Origianl sigar resources extraction and relocation. */
    unzipTask := {
      val log = streams.value.log
      val report = update.value

      log.info(s"Unpack SRC: ${sigarJar}")
      val srcTarget = sigarSources.value
      val srcArtifact = locateArtifact(report, sigarJar, "sources")
      val srcFileList = extractArtifact(srcArtifact, srcTarget, sourceFilter, false)

      log.info(s"Unpack DOC: ${sigarJar}")
      val docTarget = sigarJavadoc.value
      val docArtifact = locateArtifact(report, sigarJar, "javadoc")
      val docFileList = extractArtifact(srcArtifact, srcTarget, javadocFilter, false)

      log.info(s"Unpack JAR: ${sigarJar}")
      val jarTarget = (classDirectory in Compile).value
      val jarArtifact = locateArtifact(report, sigarJar)
      val jarFileList = extractArtifact(jarArtifact, jarTarget, classFilter, false)

      log.info(s"Unpack ZIP: ${sigarZip}")
      val zipTarget = jarTarget / nativeFolder
      val zipArtifact = locateArtifact(report, sigarZip)
      val zipFileList = extractArtifact(zipArtifact, zipTarget, nativeFilter, true)
    },

    /** Unpack sigar resources before compile. */
    (Keys.compile in Compile) <<= (Keys.compile in Compile) dependsOn unzipTask,

    /** Include original sigar sources as our own. */
    (packageSrc in Compile) <<= (packageSrc in Compile) dependsOn unzipTask,
    (mappings in (Compile, packageSrc)) ++= {
      val base = sigarSources.value
      val finder = base ** sourceFilter
      val pairList = finder x relativeTo(base)
      pairList
    },

    /** Ensure JVM agent packaging with default manifest. */
    packageOptions in (Compile, packageBin) += ManifestAttributes(manifestHeaders: _*),

    /** Invoke verbose tesing in separate JVM. */
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-a"),
    fork in Test := true,
    exportJars := true

  )

}
