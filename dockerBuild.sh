#!/bin/bash
mvn clean install -DskipTests
docker build -t squadtopida/discord-squad-statusbot:latest .
