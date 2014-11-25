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
import com.typesafe.sbt.osgi.SbtOsgi._

object Osgi {
  import Dependencies._

  def settings: Seq[Setting[_]] = defaultOsgiSettings ++ Seq(
    packagedArtifact in (Compile, packageBin) <<= (artifact in (Compile, packageBin), OsgiKeys.bundle).identityMap
  )

  def defaultImports = Seq("*")
  def optionalResolution(packageName: String) = "%s;resolution:=optional".format(packageName)
  def versionedImport(packageName: String, lower: String, upper: String) = s"""${packageName};version="[${lower},${upper})""""

  def exports(packages: Seq[String] = Seq(), imports: Seq[String] = Nil) = settings ++ Seq(
    OsgiKeys.importPackage := imports ++ defaultImports,
    OsgiKeys.exportPackage := packages
  )

  def sigarImport(packageName: String = s"org.hyperic.*;version=${sigarVersion}") = optionalResolution(packageName)

  import SigarPack._
  val sigarLoader = settings ++ Seq(
    OsgiKeys.additionalHeaders := headers.toMap,
    OsgiKeys.privatePackage := Seq(nativeFolder),
    OsgiKeys.bundleActivator := Option(activatorClass),
    OsgiKeys.importPackage := Seq("org.osgi.*", "!*"),
    OsgiKeys.exportPackage := Seq("kamon.sigar.*", s"org.hyperic.*;-split-package:=first;version=${sigarVersion}")
  )

}
