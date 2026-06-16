#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
APP_BASE_NAME="$(basename "$0")"

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

exec "$JAVACMD" \
    -Xmx64m \
    -Xms64m \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
