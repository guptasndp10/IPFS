#!/bin/sh
set -e

user=ipfs
repo="$IPFS_PATH"

if [ `id -u` -eq 0 ]; then
  echo "Changing user to $user"
  su-exec "$user" test -w "$repo" || chown  -R -- "$user" "$repo"
  exec su-exec "$user" "$0" "$@"
fi

ipfs version

if [ ! -z $SWARM_KEY ]; then echo "$SWARM_KEY" > $repo/swarm.key; fi
if [ ! -z $SWARM_KEY ]; then echo "HERE IS A KEY" && echo "$SWARM_KEY" > /root/swarm.key; fi

if [ -e "$repo/config" ]; then
  echo "Found IPFS fs-repo at $repo"
else
  echo 'Initializing IPFS....'
  ipfs init
  ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001
  ipfs config Addresses.Gateway /ip4/0.0.0.0/tcp/8080
  echo "Removing default bootstrap nodes...."
  ipfs bootstrap rm --all
fi

if [ "$1" = "daemon" ]; then
  shift
else
  echo "DEPRECATED: arguments have been set but the first argument isn't 'daemon'" >&2
  echo "DEPRECATED: run 'docker run ipfs/go-ipfs daemon $@' .instead" >&2
  echo "DEPRECATED: see the following PR for more information" >&2
  echo "DEPRECATED: * https://github.com/ipfs/go-ipfs/pull/3573" >&2
  echo "DEPRECATED: * https://github.com/ipfs/go-ipfs/pull/3685" >&2
fi

exec ipfs daemon "$@"