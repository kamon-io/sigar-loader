// Use padding.
val kamonRelease = "rev002"

// Compatible with:
// http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/Version.html
// http://books.sonatype.com/mvnref-book/reference/pom-relationships-sect-pom-syntax.html
version in ThisBuild := Dependencies.sigarVersion + "-" + kamonRelease + "-SNAPSHOT"
