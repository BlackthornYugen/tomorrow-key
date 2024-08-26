# Quickstart

```shell
docker run \
  --detatch \
  --volume data:/data \
  --publish 7070:8080 \
  --env SPRING_DATASOURCE_URL=jdbc:h2:file:/data/h2db \
  --rm \
  --name tk ghcr.io/blackthornyugen/tomorrow-key:latest
```