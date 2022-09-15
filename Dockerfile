FROM openjdk:17-ea-jdk-oracle
COPY build/libs/courses-0.0.1-SNAPSHOT.jar courses-0.0.1-SNAPSHOT.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/courses-0.0.1-SNAPSHOT.jar"]
