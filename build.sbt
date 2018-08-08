enablePlugins(org.nlogo.build.NetLogoExtension, org.nlogo.build.ExtensionDocumentationPlugin)

javaSource in Compile := baseDirectory.value / "src"

name := "gis"

version := "1.1.1"

netLogoClassManager := "org.myworldgis.netlogo.GISExtension"

netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

javacOptions ++= Seq("-g", "-deprecation", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
  "-encoding", "us-ascii")

libraryDependencies ++= Seq(
  "com.vividsolutions" % "jts"                % "1.13",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-logging"    % "commons-logging"    % "1.2",
  "commons-codec"      % "commons-codec"      % "1.10",
  "org.ngs"            % "ngunits"            % "1.0.0" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/ngunits-1.0.jar",
  "javax.media"        % "jai_core"           % "1.1.3" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/jai_core-1.1.3.jar",
  "com.sun.media"      % "jai_codec"          % "1.1.3" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/jai_codec-1.1.3.jar" )

netLogoVersion := "6.0.2-M1"
