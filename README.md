-------------------------------------------------------------------------------
API Details :

1. API TO UPLOAD FILE
URL : localhost:8090/upload
METHOD : POST
BODY : Formdata:
key:multiPartFile
value : {
            "fileName": "Justin.pdf",
            "fileData":"bW9jayBieXRlIGRhdGE="
        }
HEADER : APP/JSON
RESPONSE : {
               "fileHash": "",
               "fileSize": 63,
               "fileName": "Justin.pdf",
               "fileData": null,
               "message": null
           }
           
2. API TO DOWNLOAD FILE
URL : localhost:8090/getfile
METHOD : GET
BODY : null
HEADER1 : Content-Type - APP/JSON
HEADER2 : key:fileHash
value : QmZJ8KFNiedyXLK2ZhvnEWq8cKs2jkE9EFftwFY8caVRpP-justin.pdf
RESPONSE : {
                "fileHash" : null,
                "fileSize" : null,
                "fileName" : "justin.pdf",
                "fileData" : "bW9jayBieXRlIGRhdGE=",
                "message" : null
           }
           
-------------------------------------------------------------------------------
Steps to Run IPFS Nodes using Docker :

1. docker network create mynetwork

2. docker-compose -f docker-compose-ipfs-nodes.yml down --volumes --remove-orphans

3. docker-compose -f docker-compose-ipfs-nodes.yml up -d

--------------------------------------------------------------------------------
Steps to Build Java JAR :

1. Start the ipfs Nodes (Locally or Docker)

2. Run the main application using ./gradlew bootRun command in terminal

3. ./gradlew clean build in a different terminal

4. Stop the main application after build is successful

---------------------------------------------------------------------------------
Steps to Run Private ipfs Service :

1. Build the Java jar

2. Remove the running ipfs node containers using docker rm -f $(docker ps -aq) and docker rmi -f private-ipfs

3. docker-compose -f docker-compose.yml down --volumes --remove-orphans

4. docker-compose -f docker-compose.yml up -d