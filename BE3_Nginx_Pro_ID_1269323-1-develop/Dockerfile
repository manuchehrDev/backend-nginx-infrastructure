FROM openjdk:18-jdk-alpine

WORKDIR /app

# Копируем все файлы
COPY . .

# Даем права и собираем
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# Копируем JAR
RUN cp build/libs/*.jar app.jar

ENV SERVER_PORT=8080
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=shopdb
ENV DB_USER=postgres
ENV DB_PASSWORD=1234

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]