FROM maven:3.9.16-eclipse-temurin-21

WORKDIR /app

COPY . .

CMD ["mvn", "--batch-mode", "--no-transfer-progress", "test"]
