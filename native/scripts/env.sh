#!/bin/sh

REAL_PATH=$(realpath "$0")
SCRIPTS_DIR="$(dirname "$REAL_PATH")"
PROJECT_DIR=$(realpath "$SCRIPTS_DIR/..")
PROJECT_ROOT=$(realpath "$PROJECT_DIR/..")

export SCRIPTS_DIR
export PROJECT_DIR
export PROJECT_DIR

get_build_type() {
  _BUILD_TYPE=$1
  if [ -z "$_BUILD_TYPE" ];
  then
      _BUILD_TYPE="Debug"
  elif [ "x${_BUILD_TYPE}" != "xRelease" -a "x${_BUILD_TYPE}" != "xDebug" ];
  then
    echo "The only build types supported are Release and Debug" 1>&2
    exit 187
  fi
  echo "$_BUILD_TYPE"
}
