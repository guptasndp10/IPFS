FROM openjdk:8-jre-alpine
EXPOSE 8090
WORKDIR /out/ipfs

COPY build/libs/IPFS-0.0.1-SNAPSHOT.jar /home/IPFS-0.0.1-SNAPSHOT.jar
CMD ["java",:"-jar","/home/IPFS-0.0.1-SNAPSHOT.jar"]