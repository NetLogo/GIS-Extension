enablePlugins(org.nlogo.build.NetLogoExtension)

javaSource in Compile <<= baseDirectory(_ / "src")

name := "gis"

netLogoClassManager := "org.myworldgis.netlogo.GISExtension"

javacOptions ++= Seq("-g", "-deprecation", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
  "-encoding", "us-ascii")

val netLogoJarURL =
    Option(System.getProperty("netlogo.jar.url")).getOrElse("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar")

val netLogoJarOrDependency =
  Option(System.getProperty("netlogo.jar.url"))
    .orElse(Some("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar"))
    .map { url =>
      import java.io.File
      import java.net.URI
      if (url.startsWith("file:"))
        (Seq(new File(new URI(url))), Seq())
      else
        (Seq(), Seq("org.nlogo" % "NetLogo" % "5.3.0" from url))
    }.get

unmanagedJars in Compile ++= netLogoJarOrDependency._1

libraryDependencies      ++= netLogoJarOrDependency._2

libraryDependencies ++= Seq(
  "com.vividsolutions" % "jts"                % "1.13",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-logging"    % "commons-logging"    % "1.2",
  "commons-codec"      % "commons-codec"      % "1.10",
  "org.ngs"            % "ngunits"            % "1.0.0" from "http://ccl.northwestern.edu/devel/ngunits-1.0.jar",
  "javax.media"        % "jai_core"           % "1.1.3" from "http://ccl.northwestern.edu/devel/jai_core-1.1.3.jar",
  "com.sun.media"      % "jai_codec"          % "1.1.3" from "http://ccl.northwestern.edu/devel/jai_codec-1.1.3.jar" )

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  val gisZip = baseDirectory.value / "gis.zip"
  if (gisZip.exists) {
    IO.unzip(gisZip, baseDirectory.value)
    for (file <- (baseDirectory.value / "gis" ** "*.jar").get)
      IO.copyFile(file, baseDirectory.value / file.getName)
    IO.delete(baseDirectory.value / "gis")
  } else {
    sys.error("No zip file - gis extension not built")
  }
  jar
}
