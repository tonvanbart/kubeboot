FROM artifacts.kpn.org/datalab/rae/base-alpine:1.0.1
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]