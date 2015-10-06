enablePlugins(org.nlogo.build.NetLogoExtension)

javaSource in Compile <<= baseDirectory(_ / "src")

name := "gis"

netLogoClassManager := "org.myworldgis.netlogo.GISExtension"

netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

javacOptions ++= Seq("-g", "-deprecation", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
  "-encoding", "us-ascii")

val netLogoJarURL =
    Option(System.getProperty("netlogo.jar.url")).getOrElse("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar")

val netLogoJarOrDependency = {
  import java.io.File
  import java.net.URI
  if (netLogoJarURL.startsWith("file:"))
    Seq(unmanagedJars in Compile += new File(new URI(netLogoJarURL)))
  else
    Seq(libraryDependencies += "org.nlogo" % "NetLogo" % "5.3.0" from netLogoJarURL)
}

netLogoJarOrDependency

libraryDependencies ++= Seq(
  "com.vividsolutions" % "jts"                % "1.13",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-logging"    % "commons-logging"    % "1.2",
  "commons-codec"      % "commons-codec"      % "1.10",
  "org.ngs"            % "ngunits"            % "1.0.0" from "http://ccl.northwestern.edu/devel/ngunits-1.0.jar",
  "javax.media"        % "jai_core"           % "1.1.3" from "http://ccl.northwestern.edu/devel/jai_core-1.1.3.jar",
  "com.sun.media"      % "jai_codec"          % "1.1.3" from "http://ccl.northwestern.edu/devel/jai_codec-1.1.3.jar" )
