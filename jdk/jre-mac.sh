#!/usr/bin/env sh
JDK_VERSION=13.0.1
JFX_VERSION=11.0.2
mac/jdk-${JDK_VERSION}.jdk/Contents/Home/bin/jlink \
  --compress=2 --strip-debug \
  --module-path "mac/jdk-${JDK_VERSION}.jdk/Contents/Home/jmods:mac/javafx-jmods-${JFX_VERSION}" \
  --add-modules java.base,java.logging,java.naming,javafx.base,javafx.controls,javafx.fxml \
  --output jre-mac
