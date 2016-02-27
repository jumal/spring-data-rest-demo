# Spring Data REST Demo
[![Build Status](https://travis-ci.org/jumal/spring-data-rest-demo.svg?branch=master)](https://travis-ci.org/jumal/spring-data-rest-demo)
[![License](https://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Description
Includes
* Hypermedia REST service testing
* HAL browser usage

## Docker support
#### Building an image
`mvn package docker:build`
#### Running the image
`docker run -e "SPRING_PROFILES_ACTIVE=prod" -p 80:8080 -t jumal/spring-data-rest-demo`
