FROM openjdk:8-jdk-alpine
WORKDIR /AkkaApi

COPY ./target/pack/bin ./bin
COPY ./target/pack/lib ./lib

EXPOSE 8080

CMD ["./bin/main"]