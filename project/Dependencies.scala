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

object Dependencies {

  val redhatRepo = "RedHat Repository" at "http://repository.jboss.org/nexus/content/groups/public-jboss"
  val typesafeRepo = "TypeSafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

  val resolutionRepos = Seq(typesafeRepo, redhatRepo)

  /**
   *  Version corresponding to source compatibility.
   *  See: https://github.com/hyperic/sigar
   */
  val sigarVersion = "1.6.5"
  /**
   *  Version corresponding to vendor binary releases.
   *  See: http://repository.jboss.org/nexus/content/groups/public-jboss/org/hyperic/sigar/
   */
  val sigarBuildVersion = sigarVersion + "." + "132"
  /**
   * Licence of Sigar java code and binary libraries.
   * See: https://github.com/akka/akka/issues/16121
   */
  val sigarLicence = "http://www.apache.org/licenses/LICENSE-2.0.html"

  //

  val slf4Api = "org.slf4j" % "slf4j-api" % "1.7.7" // MIT
  val slf4Jul = "org.slf4j" % "jul-to-slf4j" % "1.7.7" // MIT
  val slf4Log4j = "org.slf4j" % "log4j-over-slf4j" % "1.7.7" // MIT
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.2" // EPL 1.0 / LGPL 2.1

  val sigarJar = "org.hyperic" % "sigar" % sigarBuildVersion withSources () withJavadoc () // ApacheV2
  val sigarZip = "org.hyperic" % "sigar-dist" % sigarBuildVersion // ApacheV2

  val junit = "junit" % "junit" % "4.11" // Common Public License 1.0
  val junitInterface = "com.novocode" % "junit-interface" % "0.11" // MIT

  val osgiCore = "org.osgi" % "org.osgi.core" % "4.3.1" // ApacheV2
  val osgiCompendium = "org.osgi" % "org.osgi.compendium" % "4.3.1" // ApacheV2

  //

  /** Scope for artifacts not present in Maven Central. */
  val external = config("external").hide

  def external(deps: ModuleID*): Seq[ModuleID] = deps map (_ % external.name)
  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def optional(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile,optional")
  def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided,optional")
  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

}
