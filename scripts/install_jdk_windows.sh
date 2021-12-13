#!/usr/bin/env bash

function jdk14() {
  VERSION=14.0.2
  URL="https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_windows-x64_bin.zip"
  curl -L -o openjdk14.zip $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    exit 1
  fi

  unzip -q openjdk14.zip
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    exit 1
  fi
  rm openjdk14.zip
  mv jdk-${VERSION} $HOME/jdk-14
}

function jdk13() {
  VERSION=13.0.2
  URL="https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_windows-x64_bin.zip"
  curl -L -o openjdk13.zip $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    exit 1
  fi

  unzip -q openjdk13.zip
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    exit 1
  fi
  rm openjdk13.zip
  mv jdk-${VERSION} $HOME/jdk-13
}

jdk13
jdk14
