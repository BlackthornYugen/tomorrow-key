# Introduction

Was inspired by the cool stuff random.org offers
to support transparent giveaways. Thought it would
be cool to make an API that let you encrypt
something that can't be decrypted until a given
date.

Just a toy for now. Don't trust me to keep it
secure or running at all.

# Quickstart

```shell
docker run \
  --detach \
  --volume data:/data \
  --publish 7070:8080 \
  --env SPRING_DATASOURCE_URL=jdbc:h2:file:/data/h2db \
  --rm \
  --name tk ghcr.io/blackthornyugen/tomorrow-key:latest
```