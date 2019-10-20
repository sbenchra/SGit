#!/bin/bash

#build jar file
sbt assembly
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"

#activate sgit command
alias sgit=$DIR'/sgit_processing.sh'
