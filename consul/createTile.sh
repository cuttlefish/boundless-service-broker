#!/bin/sh

TILE_VERSION=1.0
TILE_NAME=Consul

zip -r ${TILE_NAME}-${TILE_VERSION}.pivotal metadata releases content_migrations
