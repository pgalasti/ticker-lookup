#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
java -jar "$DIR/target/ticker-lookup-1.0-SNAPSHOT.jar" "$@"