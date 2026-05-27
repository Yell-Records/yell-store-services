FROM amazoncorretto:22
WORKDIR /app
COPY build/libs/yell-store-services.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
