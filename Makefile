.PHONY: build clean install

build:
	mvn package

clean:
	mvn clean

install: build
	./install.sh
