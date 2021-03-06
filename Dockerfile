FROM openjdk:8-jre-alpine

ENV JAVA_APP_JAR companion-webapp-gateway-1.0-SNAPSHOT-fat.jar

EXPOSE 5701 54327

COPY target/$JAVA_APP_JAR /app/
RUN chmod 777 /app/

WORKDIR /app/
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $JAVA_APP_JAR -cluster"]