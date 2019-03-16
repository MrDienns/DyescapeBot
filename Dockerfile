FROM openjdk:alpine

RUN mkdir -p /opt/dyescape/dyescape-bot
WORKDIR /opt/dyescape/dyescape-bot

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /opt/dyescape/dyescape-bot/DyescapeBot.jar" ]

COPY ./target/DyescapeBot.jar /opt/dyescape/dyescape-bot/DyescapeBot.jar
