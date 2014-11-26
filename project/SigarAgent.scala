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
import sbt.Keys._

/**
 * Sigar java agent build injection settings.
 */
object SigarAgent {
  import Projects.sigarLoader

  lazy val sigarArtifact = TaskKey[File]("sigar-artifact", "Location of Sigar java agent jar.")

  lazy val sigarOptions = TaskKey[String]("sigar-options", "JVM command line options for Sigar java agent.")

  lazy val sigarFolder = SettingKey[File]("sigar-folder", "Location of native library extracted by Sigar java agent.")

  val sigarFolderProperty = "kamon.sigar.folder"

  def provideSigarOptions = (sigarArtifact, sigarFolder) map { (artifact, folder) =>
    "-javaagent:" + artifact + "=" + sigarFolderProperty + "=" + folder
  }

  def locateSigarArtifact(classpath: TaskKey[Classpath]) = classpath map { artifactList =>
    require(artifactList.size == 1, "Expecting single artifact, while found: " + artifactList)
    artifactList(0).data
  }

  /** Inject Sigar java agent in tests without dependency on sigar-loader classpath. */
  def settings = Seq(
    sigarArtifact <<= locateSigarArtifact(exportedProducts in Compile in sigarLoader),
    sigarFolder := target.value / "native",
    sigarOptions <<= provideSigarOptions,
    javaOptions in Test += sigarOptions.value,
    fork in Test := true
  )

}
