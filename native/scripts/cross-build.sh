#!/bin/sh -e
# shellcheck source=./env.sh
. "$(dirname "$(realpath "$0")")/env.sh"

BUILD_TYPE="Release"
BUILD_ARCH="default"

while getopts ":b:a:" arg; do
  case ${arg} in
    b)
      BUILD_TYPE="${OPTARG}"
      ;;
    a)
      BUILD_ARCH="${OPTARG}"
      ;;
    ?)
      echo "Invalid option: -${arg} ${OPTARG}"
      echo
      exit 187
      ;;
  esac
done

cd "$PROJECT_DIR"
DOCK_CROSS_IMAGE=$BUILD_ARCH "./scripts/dockcross.sh" "./scripts/build.sh" -b "$BUILD_TYPE" -a "$BUILD_ARCH"

