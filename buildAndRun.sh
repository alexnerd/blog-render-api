#!/bin/sh
mvn clean package && docker build -t com.alexnerd/render-api .
docker rm -f render-api || true && docker run -d -p 8080:8080 -p 4848:4848 --name render-api com.alexnerd/render-api
