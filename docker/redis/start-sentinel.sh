#!/bin/sh
set -eu

SENTINEL_CONF="${SENTINEL_CONF:?SENTINEL_CONF is required}"
RUNTIME_SENTINEL_CONF="/tmp/sentinel.conf"

cp "$SENTINEL_CONF" "$RUNTIME_SENTINEL_CONF"
chmod 666 "$RUNTIME_SENTINEL_CONF"
exec redis-server "$RUNTIME_SENTINEL_CONF" --sentinel
