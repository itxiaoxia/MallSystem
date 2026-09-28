#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MAVEN_VERSION=3.9.11
MAVEN_DIR="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"

if [ ! -x "$MAVEN_DIR/bin/mvn" ]; then
  echo "Use mvnw.cmd on Windows, or install Maven 3.9.11 on Unix." >&2
  exit 1
fi

exec "$MAVEN_DIR/bin/mvn" "$@"
