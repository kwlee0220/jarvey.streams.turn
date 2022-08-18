FROM sgrio/java:jre_8

RUN mkdir -p /jarvey
WORKDIR /jarvey
COPY jarvey.streams.turn-0.1.0-all.jar ./jarvey.streams.turn.jar

CMD ["java", "-cp", "/jarvey/jarvey.streams.turn.jar", "jarvey.streams.turn.TurnDetectorDockerMain"]