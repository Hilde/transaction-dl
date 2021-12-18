#!/usr/bin/env bash

OS=$1
VERSION="17.0.1"
ARCH="x64"

BASEDIR=$(dirname $0)

if [ "$OS" == "" ]; then
  echo Specify the os: windows, osx, linux
  exit 1
fi

case "$OS" in
  "windows")
    DEST=jdk-win/jmods
    ;;
  "osx")
    DEST=jdk-mac/Contents/Home/jmods
    ;;
esac

pushd $BASEDIR/../work
URL="https://download2.gluonhq.com/openjfx/${VERSION}/openjfx-${VERSION}_${OS}-${ARCH}_bin-jmods.zip"
curl -L -o openjfx-jmods.zip $URL
if [ $? -ne 0 ]; then
  echo OpenJFX downloading failed from $URL
  popd
  exit 1
fi

unzip -q openjfx-jmods.zip
if [ $? -ne 0 ]; then
  echo OpenJFX extracting failed.
  popd
  exit 1
fi
rm openjfx-jmods.zip
mv javafx-jmods-${VERSION}/*.jmod $JAVA_HOME/jmods/
popd
