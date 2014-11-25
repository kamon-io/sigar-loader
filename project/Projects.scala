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

object Projects extends Build {
  import Settings._
  import Dependencies._

  lazy val root = Project("root", file("."))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(noPublishing: _*)
    .aggregate(sigarLoader, sigarLoaderJavaIT, sigarLoaderOsgiIT)

  lazy val sigarLoader = Project("sigar-loader", file("sigar-loader"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(SigarPack.settings: _*)
    .settings(
      libraryDependencies ++=
        external(sigarJar, sigarZip) ++
        provided(osgiCore, osgiCompendium) ++
        test(junit, junitInterface, slf4Api, slf4Jul, slf4Log4j, logback)
    )

  lazy val sigarLoaderJavaIT = Project("sigar-loader-it-java", file("sigar-loader-it-java"))
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)

  lazy val sigarLoaderOsgiIT = Project("sigar-loader-it-osgi", file("sigar-loader-it-osgi"))
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)

  val noPublishing = Seq(publish := (), publishLocal := (), publishArtifact := false)

}
