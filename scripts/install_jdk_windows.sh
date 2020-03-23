#!/usr/bin/env bash

function jdk14() {
  URL="https://download.java.net/java/GA/jdk14/076bab302c7b4508975440c56f6cc26a/36/GPL/openjdk-14_windows-x64_bin.zip"
  curl -L -o openjdk14.zip $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    exit 1
  fi

  unzip openjdk14.zip
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    exit 1
  fi
  rm openjdk14.zip
  mv jdk-14 $HOME/jdk-14
}

jdk14
