#!/usr/bin/env bash

NO_COLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'
USAGE="Usage: ./run_examples.sh <folder-instrumented-jre-and-phosphor-jar>"

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

  if [ -d $jre_inst ]
    then
      announce "Running examples from $class"
      $jre_inst/bin/java -Xbootclasspath/a:$phosphor_jar -javaagent:$phosphor_jar -cp $EXAMPLES_JAR \
      -ea $class
    else
      warn "Skipping ${class} due to lack of instrumented JRE"
  fi
 }

function help {
  echo $USAGE
  echo -e "The instrumented jres are assumed to follow the following naming convention
  jre-inst-int: Integer tags
  jre-inst-obj: Object tags
  The Phosphor jar is assumed to be of the form find Phosphor*SNAPSHOT.jar, where * is the version
  Note that these are the names created by mvn verify in the Phosphor project"
  }

if [ $# == 1  ] && [ $1 == "-h" ]
  then
    help
    exit 0
fi

if [ $# != 1 ]
  then
    help
    exit 1
fi

# Run tests
if [ ! -d $1 ]
  then
  warn "Directory $1 not found"
  exit 1
fi

JRE_INT=${1}/jre-inst-int
JRE_OBJ=${1}/jre-inst-obj
PHOSPHOR_JAR=$(find $1 -iname "Phosphor-[0-9]*SNAPSHOT.jar")
EXAMPLES_JAR=$(find ./target -iname "phosphor-examples-*SNAPSHOT.jar")

if [ -z $PHOSPHOR_JAR ]
  then
    warn "Missing Phosphor JAR, run mvn package (or mvn verify) in the appropriate project"
    exit 1
fi

if [ -z $EXAMPLES_JAR ]
 then
  warn "Missing phosphor-examples JAR, running mvn package"
  mvn package
  EXAMPLES_JAR=$(find ./target -iname "phosphor-examples-*SNAPSHOT.jar")
fi

run_example $JRE_INT $PHOSPHOR_JAR com.josecambronero.IntegerTagExamples
run_example $JRE_OBJ $PHOSPHOR_JAR com.josecambronero.ObjectTagExamples
# TODO automatic
# TODO implicit flows