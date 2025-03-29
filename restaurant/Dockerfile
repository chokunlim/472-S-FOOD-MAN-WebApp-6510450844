# ใช้ OpenJDK 21 เป็น Base Image
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . /app

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

RUN chmod +x wait-for-it.sh

RUN ./mvnw dependency:resolve

RUN ./mvnw package -DskipTests

CMD ./wait-for-it.sh my-mysql:3306 -- java -jar target/restaurant-0.0.1-SNAPSHOT.jar