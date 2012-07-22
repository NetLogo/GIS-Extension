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

gis.jar gis.jar.pack.gz: $(SRCS) manifest.txt Makefile NetLogoLite.jar $(JARS) $(addsuffix .pack.gz, $(JARS))
	mkdir -p classes
	$(JAVAC) -g -deprecation -Xlint:all -Xlint:-serial -Xlint:-path -encoding us-ascii -source 1.5 -target 1.5 -classpath NetLogoLite.jar$(COLON)$(JARSPATH) -d classes $(SRCS)
	jar cmf manifest.txt gis.jar -C classes .
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip gis.jar.pack.gz gis.jar

gis.zip: gis.jar
	rm -rf gis
	mkdir gis
	cp -rp *.jar gis.jar.pack.gz README.md Makefile src manifest.txt build.xml gis
	zip -rv gis.zip gis
	rm -rf gis

NetLogoLite.jar:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/NetLogoLite-971ed928.jar' -o NetLogoLite.jar

jai_codec-1.1.3.jar jai_codec-1.1.3.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jai_codec-1.1.3.jar' -o jai_codec-1.1.3.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip jai_codec-1.1.3.jar.pack.gz jai_codec-1.1.3.jar
jai_core-1.1.3.jar jai_core-1.1.3.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jai_core-1.1.3.jar' -o jai_core-1.1.3.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip jai_core-1.1.3.jar.pack.gz jai_core-1.1.3.jar
ngunits-1.0.jar ngunits-1.0.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/ngunits-1.0.jar' -o ngunits-1.0.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip ngunits-1.0.jar.pack.gz ngunits-1.0.jar
jts-1.9.jar jts-1.9.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/jts-1.9.jar' -o jts-1.9.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip jts-1.9.jar.pack.gz jts-1.9.jar
commons-codec-1.3.jar commons-codec-1.3.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-codec-1.3.jar' -o commons-codec-1.3.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip commons-codec-1.3.jar.pack.gz commons-codec-1.3.jar
commons-logging-1.1.jar commons-logging-1.1.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-logging-1.1.jar' -o commons-logging-1.1.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip commons-logging-1.1.jar.pack.gz commons-logging-1.1.jar
commons-httpclient-3.0.1.jar commons-httpclient-3.0.1.jar.pack.gz:
	curl -f -s -S 'http://ccl.northwestern.edu/devel/commons-httpclient-3.0.1.jar' -o commons-httpclient-3.0.1.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip commons-httpclient-3.0.1.jar.pack.gz commons-httpclient-3.0.1.jar
