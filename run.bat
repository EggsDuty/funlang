@echo off
setlocal enabledelayedexpansion

set "SAMPLE_FILE=samples\variable.funlang"
set "OUTPUT_DIR=out"
set "JAR_FILE=target\funlang-2025-1.0.jar"

call mvn clean package

java -jar "%JAR_FILE%" "%SAMPLE_FILE%" "%OUTPUT_DIR%"

cd "%OUTPUT_DIR%"
javac FunProgram.java

java FunProgram
set EXIT_CODE=%errorlevel%

cd ..
