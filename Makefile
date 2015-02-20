ifeq ($(origin JAVA_HOME), undefined)
  JAVA_HOME=/usr
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
  JAVA_HOME := `cygpath -up "$(JAVA_HOME)"`
else
  COLON=:
endif

JAVAC=$(JAVA_HOME)/bin/javac
SRCS=$(wildcard src/org/myworldgis/io/asciigrid/*.java src/org/myworldgis/io/shapefile/*.java src/org/myworldgis/netlogo/*.java src/org/myworldgis/netlogo/gui/*.java src/org/myworldgis/projection/*.java src/org/myworldgis/util/*.java src/org/myworldgis/wkt/*.java)

JARS=jai_codec-1.1.3.jar jai_core-1.1.3.jar ngunits-1.0.jar jts-1.9.jar commons-codec-1.3.jar commons-logging-1.1.jar commons-httpclient-3.0.1.jar
JARSPATH=jai_codec-1.1.3.jar$(COLON)jai_core-1.1.3.jar$(COLON)ngunits-1.0.jar$(COLON)jts-1.9.jar$(COLON)commons-codec-1.3.jar:commons-logging-1.1.jar$(COLON)commons-httpclient-3.0.1.jar

gis.jar: $(SRCS) manifest.txt Makefile NetLogoHeadless.jar $(JARS)
	mkdir -p classes
	$(JAVAC) -g -deprecation -Xlint:all -Xlint:-serial -Xlint:-path -encoding us-ascii -source 1.5 -target 1.5 -classpath NetLogoHeadless.jar$(COLON)$(JARSPATH) -d classes $(SRCS)
	jar cmf manifest.txt gis.jar -C classes .

NetLogoHeadless.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/6.0-M1/NetLogoHeadless.jar' -o NetLogoHeadless.jar

jai_codec-1.1.3.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jai_codec-1.1.3.jar' -o jai_codec-1.1.3.jar
jai_core-1.1.3.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jai_core-1.1.3.jar' -o jai_core-1.1.3.jar
ngunits-1.0.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/ngunits-1.0.jar' -o ngunits-1.0.jar
jts-1.9.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jts-1.9.jar' -o jts-1.9.jar
commons-codec-1.3.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-codec-1.3.jar' -o commons-codec-1.3.jar
commons-logging-1.1.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-logging-1.1.jar' -o commons-logging-1.1.jar
commons-httpclient-3.0.1.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-httpclient-3.0.1.jar' -o commons-httpclient-3.0.1.jar

gis.zip: gis.jar
	rm -rf gis
	mkdir gis
	cp -rp *.jar README.md Makefile src manifest.txt build.xml gis
	zip -rv gis.zip gis
	rm -rf gis
