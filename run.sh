#!/bin/bash

SCALA_version="scala-2.12"

# JAR_name : The name you gave to your fat JAR in the build.sbt file.
JAR_name="sgit.jar"

SOURCE_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"
JAR_PATH=$SOURCE_PATH/target/$SCALA_version/$JAR_name

# $@: args commandline
scala $JAR_PATH $@


