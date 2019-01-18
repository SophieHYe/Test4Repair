#!/bin/bash
mvn package assembly:single
java -jar ./target/test4repair-0.0.1-SNAPSHOT-jar-with-dependencies.jar chart 1 M_ISO source tests
