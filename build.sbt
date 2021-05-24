enablePlugins(org.nlogo.build.NetLogoExtension, org.nlogo.build.ExtensionDocumentationPlugin)

javaSource in Compile := baseDirectory.value / "src"

name := "gis"

version := "1.3.0"

netLogoClassManager := "org.myworldgis.netlogo.GISExtension"

netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

javacOptions ++= Seq("-g", "-deprecation", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
  "-encoding", "us-ascii")

libraryDependencies ++= Seq(
  "com.vividsolutions"         % "jts"                % "1.13",
  "commons-httpclient"         % "commons-httpclient" % "3.1",
  "commons-logging"            % "commons-logging"    % "1.2",
  "commons-codec"              % "commons-codec"      % "1.10",
  "org.ngs"                    % "ngunits"            % "1.0.0" from "https://s3.amazonaws.com/ccl-artifacts/ngunits-1.0.jar",
  "javax.media"                % "jai_core"           % "1.1.3" from "https://s3.amazonaws.com/ccl-artifacts/jai_core-1.1.3.jar",
  "com.sun.media"              % "jai_codec"          % "1.1.3" from "https://s3.amazonaws.com/ccl-artifacts/jai_codec-1.1.3.jar",
  "org.tinfour"                % "TinfourCore"        % "2.1.5",
  "com.googlecode.json-simple" % "json-simple"        % "1.1.1")

resolvers      += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"
netLogoVersion := "6.2.0-d27b502"
isSnapshot := true
