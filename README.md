# Spring Data REST Demo

## Description
Includes
* Hypermedia REST service testing
* HAL browser usage

## Docker support
#### Building an image
`mvn package docker:build`
#### Running the image
`docker run -e "SPRING_PROFILES_ACTIVE=prod" -p 80:8080 -t jumal/spring-data-rest-demo`
