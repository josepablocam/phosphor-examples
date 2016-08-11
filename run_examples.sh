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
  local jre_inst=$1
  local phosphor_jar=$2
  local examples_jar=$3
  local class=$4
  local rest="${@:5}"

  if [ -d $jre_inst ]
    then
      announce "Running examples from $class"
      $jre_inst/bin/java -Xbootclasspath/a:$phosphor_jar -javaagent:$phosphor_jar -cp $examples_jar\
      -ea $class $rest
    else
      warn "Skipping ${class} due to lack of instrumented JRE"
  fi
 }

 function inst_jar {
   local phosphor_jar=$1
   local taint_src=$2
   local taint_sink=$3
   local src=$4
   local dest_folder=$5
   java -jar $phosphor_jar -taintSources $taint_src -taintSinks $taint_sink $src $dest_folder
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
JRE_IMP=${1}/jre-inst-implicit
PHOSPHOR_JAR=$(find $1 -iname "Phosphor-[0-9]*SNAPSHOT.jar")
EXAMPLES_JAR=$(find ./target -depth 1 -iname "phosphor-examples-*SNAPSHOT.jar")

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

# Instrument our example with integer tags
inst_jar $PHOSPHOR_JAR src/main/resources/taint-sources src/main/resources/taint-sinks $EXAMPLES_JAR target/inst_examples/
INST_EXAMPLES_JAR=$(find target/inst_examples/ -iname "phosphor-examples-*SNAPSHOT.jar")

run_example $JRE_INT $PHOSPHOR_JAR $EXAMPLES_JAR com.josecambronero.IntegerTagExamples
run_example $JRE_OBJ $PHOSPHOR_JAR $EXAMPLES_JAR com.josecambronero.ObjectTagExamples
run_example $JRE_IMP $PHOSPHOR_JAR $EXAMPLES_JAR com.josecambronero.ImplicitFlowsExamples
# only runnning with integer tags, but the idea is the same for other cases
run_example $JRE_INT $PHOSPHOR_JAR $INST_EXAMPLES_JAR com.josecambronero.AutoExample
# this doesn't seem to work, requires pre-instrumented JAR
echo "==> Autoexample fails (i.e. no exception) with non-pre-instrumented jar"
run_example $JRE_INT $PHOSPHOR_JAR $EXAMPLES_JAR com.josecambronero.AutoExample\
  -taintSources src/main/resources/taint-sources -taintSinks src/main/resources/taint-sinks