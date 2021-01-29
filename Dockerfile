FROM adoptopenjdk/openjdk11:alpine

COPY target/discord-*-jar-with-dependencies.jar /opt/bot.jar

ENTRYPOINT ["sh", "-c", "java -jar /opt/bot.jar ${TOKEN} ${BM_ID} ${SERVER_IP} ${STATUS_CMD}"]