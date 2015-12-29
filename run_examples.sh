#!/usr/bin/env bash

NO_COLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'

function warn {
  printf "$RED $1 $NO_COLOR\n"
 }

function announce {
 printf "$GREEN $1 $NO_COLOR\n"
 }

function run_example {
  jre_inst=$1
  phosphor_jar=$2
  class=$3

  announce "Running examples from $class"
  $jre_inst/bin/java -Xbootclasspath/a:$phosphor_jar -javaagent:$phosphor_jar -cp $EXAMPLES_JAR \
  -ea $class
 }


if [ $# != 2 ]
  then
    warn "Usage: ./run_examples.sh <path-instrumented-jre> <path-phosphor-jar>"
    exit 1
fi

# Run tests
JRE_INST=$1
PHOSPHOR_JAR=$2
EXAMPLES_JAR=./target/phosphor-examples-1.0-SNAPSHOT.jar

if [ ! -d $JRE_INST ] || [ ! -f $PHOSPHOR_JAR ]
  then
  warn "Arguments provided not found"
  exit 1
fi


if [ ! -f $EXAMPLES_JAR ]
 then
  warn "Missing $EXAMPLES_JAR, running mvn package"
  mvn package
fi


run_example $JRE_INST $PHOSPHOR_JAR com.josecambronero.IntegerTagExamples
#TODO: run_example $JRE_INST $PHOSPHOR_JAR ObjectTagExamples
