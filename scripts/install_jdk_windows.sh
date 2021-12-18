#!/usr/bin/env bash

BASEDIR=$(dirname $0)
WORKDIR=$BASEDIR/../work

function jdk() {
  pushd $WORKDIR
  VERSION="17.0.1+12"
  URL="https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_windows_hotspot_17.0.1_12.zip"
  curl -L -o temurin_jdk.zip $URL
  if [ $? -ne 0 ]; then
    echo JDK downloading failed from $URL
    popd
    exit 1
  fi

  unzip -q temurin_jdk.zip
  if [ $? -ne 0 ]; then
    echo JDK extracting failed.
    popd
    exit 1
  fi
  rm temurin_jdk.zip
  mv jdk-${VERSION} jdk-win
  popd
}

jdk
