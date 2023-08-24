#!/bin/bash
cd main-service
rm -fr target
mvn clean package

cd ..

cd  stat-service
rm -fr target
mvn clean package

cd ..

mvn clean install

docker-compose up