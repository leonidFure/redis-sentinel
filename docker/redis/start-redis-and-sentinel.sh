#!/bin/sh
set -eu

REDIS_CONF="${REDIS_CONF:?REDIS_CONF is required}"
SENTINEL_CONF="${SENTINEL_CONF:?SENTINEL_CONF is required}"
RUNTIME_SENTINEL_CONF="/tmp/sentinel.conf"

redis-server "$REDIS_CONF" &
REDIS_PID=$!

cp "$SENTINEL_CONF" "$RUNTIME_SENTINEL_CONF"
chmod 666 "$RUNTIME_SENTINEL_CONF"
redis-server "$RUNTIME_SENTINEL_CONF" --sentinel &
SENTINEL_PID=$!

shutdown() {
  kill "$REDIS_PID" "$SENTINEL_PID" 2>/dev/null || true
  wait "$REDIS_PID" "$SENTINEL_PID" 2>/dev/null || true
}

trap 'shutdown; exit 0' TERM INT

while kill -0 "$REDIS_PID" 2>/dev/null && kill -0 "$SENTINEL_PID" 2>/dev/null; do
  sleep 1
done

shutdown
exit 1
