#!/bin/sh -e
if [ -e scripts/env.sh ]; then . scripts/env.sh; fi
if [ -e env.sh ]; then . env.sh; fi

if [ -z "$DOCK_CROSS_IMAGE" ]
then
    DOCK_CROSS_IMAGE="linux-x64"
fi

DOCK_CROSS="$HOME/.run-in-$DOCK_CROSS_IMAGE"

if [ ! -e "$DOCK_CROSS" ]
then
  if [ -e "$PROJECT_DIR/docker/Dockerfile-$DOCK_CROSS_IMAGE" ]
  then
    NEW_DOCK_CROSS_IMAGE="custom-dockcross-$DOCK_CROSS_IMAGE"
    docker build --file "$PROJECT_DIR/docker/Dockerfile-$DOCK_CROSS_IMAGE" -t $NEW_DOCK_CROSS_IMAGE "$PROJECT_DIR/docker"
    DOCK_CROSS_IMAGE=$NEW_DOCK_CROSS_IMAGE
  else
    DOCK_CROSS_IMAGE=dockcross/$DOCK_CROSS_IMAGE
  fi
  docker run $DOCK_CROSS_IMAGE > "$DOCK_CROSS.tmp"
  mv "$DOCK_CROSS.tmp" "$DOCK_CROSS"
  chmod +x "$DOCK_CROSS"
fi

$DOCK_CROSS "$@"
