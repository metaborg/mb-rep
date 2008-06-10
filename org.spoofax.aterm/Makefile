DESTDIR = /
PREFIX  = share/java

all:
	ant -f build.ant.xml jar

install: all
	echo "installing"
#	ant -f build.ant.xml -Dprefix=$(DESTDIR)/$(PREFIX) install

dist:
	ant -f build.ant.xml dist
	
check:
