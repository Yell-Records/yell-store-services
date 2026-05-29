#!/bin/bash

if [ ! -f .env ]; then
  echo "ERROR: .env file not found. Create one before running this script."
  exit 1
fi

echo "Building JAR..."
./gradlew clean bootJar --no-daemon

echo "Building Docker image..."
docker build -t yell-records-backend .

echo "Running container..."
docker run -p 8080:8080 \
  --env-file .env \
  --add-host=host.docker.internal:host-gateway \
  yell-records-backend
