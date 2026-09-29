#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MAVEN_VERSION=3.9.11
MAVEN_DIR="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"

if [ -x "$MAVEN_DIR/bin/mvn" ]; then
  exec "$MAVEN_DIR/bin/mvn" "$@"
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

echo "Maven 3.9.11 is not bundled; install Maven 3.9.11 or add it to PATH." >&2
exit 1
