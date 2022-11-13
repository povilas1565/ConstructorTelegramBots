FROM openjdk:17.0.2-slim
VOLUME /tmp
ARG JAR=target/*.war

COPY ${JAR} app.war
ENTRYPOINT java -Dfile.encoding=UTF-8 $JAVA_OPTS -jar /app.war $RUN_ARGS