#!/bin/bash

#!/bin/bash

IMAGE_NAME="vidapi"

# Construir el JAR con Gradle
echo "Building vidapi project..."
gradle build || { exit 1; }
echo

# Construir la imagen Docker
echo "Building Docker image..."
sudo docker build -t $IMAGE_NAME . || { exit 1; }
echo

sudo docker-compose up -d
echo
echo -e "\e[32mEl contenedor está corriendo en http://localhost:8080\e[0m"
