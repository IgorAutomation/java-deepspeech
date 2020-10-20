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

BUILD_TYPE=$(get_build_type "$BUILD_TYPE")
GENERATION_DIR="cmake-build-${BUILD_ARCH}-${BUILD_TYPE}"

echo "BUILD_TYPE: $BUILD_TYPE"
echo "BUILD_ARCH: $BUILD_ARCH"
echo "GENERATION_DIR: $GENERATION_DIR"

if [ ! -e "$GENERATION_DIR/created-at" ];
then
  rm -rf "$GENERATION_DIR" \
    && mkdir "$GENERATION_DIR" \
    && cd "$GENERATION_DIR" \
    && cmake -DCMAKE_BUILD_TYPE="$1" --config "$BUILD_TYPE" .. \
    && date > "created-at" \
    && cd ..
fi

cd "$GENERATION_DIR/.." \
  && pwd \
  && cmake --build "$GENERATION_DIR" --target all --config "$BUILD_TYPE" -- -j 8
