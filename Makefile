.PHONY: build clean install

build:
	mvn package

clean:
	mvn clean
	rm -rf build

install: build
	./install.sh
