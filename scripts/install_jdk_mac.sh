#!/usr/bin/env bash

function jdk14() {
  URL="https://download.java.net/java/early_access/jdk14/33/GPL/openjdk-14-ea+33_osx-x64_bin.tar.gz"
  curl -L -o openjdk14.tgz $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    exit 1
  fi

  tar zxvf openjdk14.tgz
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    exit 1
  fi
  rm openjdk14.tgz
  mv jdk-14.jdk $HOME/jdk-14
}

function jdk13() {
  URL="https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_osx-x64_bin.tar.gz"
  curl -L -o openjdk13.tgz $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    exit 1
  fi

  tar zxvf openjdk13.tgz
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    exit 1
  fi
  rm openjdk13.tgz
  mv jdk-13.0.2.jdk $HOME/jdk-13
}

jdk13
jdk14
