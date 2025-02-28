#!/bin/bash

IMAGE_NAME="vidapi"

if [ -z "$IMAGE_NAME" ]; then
	  echo "Using: $0 "
	    exit 1
fi

# Obtener los IDs de los contenedores basados en la imagen
CONTAINER_IDS=$(sudo docker ps -a -q --filter ancestor="$IMAGE_NAME")

# Detener y eliminar los contenedores
echo "Eliminating containters for image: $IMAGE_NAME"
echo
if [ -n "$CONTAINER_IDS" ]; then
	sudo docker stop $CONTAINER_IDS
	sudo docker rm $CONTAINER_IDS
fi

echo
echo -e "\e[32mFinished eliminating containers.\e[0m"
