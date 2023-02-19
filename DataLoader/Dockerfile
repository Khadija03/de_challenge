FROM openjdk:8-jdk-alpine
WORKDIR /DataLoader

COPY ./target/pack/bin ./bin
COPY ./target/pack/lib ./lib

COPY ./data ./data
EXPOSE 9043
CMD ["./bin/main"]
