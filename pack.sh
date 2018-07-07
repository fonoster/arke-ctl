#!/usr/bin/env bash

VERSION=1.0.1-alpha
FOLDER_NAME=arke-ctl.$VERSION

# Cleanup
rm -rf $FOLDER_NAME

mkdir -p $FOLDER_NAME/libs

cp -a libs/* $FOLDER_NAME/libs
cp sipioctl $FOLDER_NAME/
cp README.md $FOLDER_NAME/

tar -czvf $FOLDER_NAME.tar.gz $FOLDER_NAME
zip -r $FOLDER_NAME.zip $FOLDER_NAME

# Cleanup again
rm -rf $FOLDER_NAME
