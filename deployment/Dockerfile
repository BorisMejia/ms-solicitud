FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY applications/app-service/build/libs/*.jar ms-solicitud.jar
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar ms-solicitud.jar" ]
