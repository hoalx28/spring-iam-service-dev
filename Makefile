#!make
include .env

docker-build:
	docker build -t ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .
docker-run:
	docker run -i -t ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG}
docker-run-build:

docker-push:
	docker image push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG}