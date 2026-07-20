FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean test installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/install/multi-racingcar/ ./
ENV PORT=8080
ENV JAVA_OPTS="-Xms64m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"
EXPOSE 8080
CMD ["bin/multi-racingcar"]
