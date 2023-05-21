# Compile the code

1. Open a terminal
2. Navigate to the src directory of the project
3. To compile all the code for the server and the clients: ```javac */*.java```


# Running the server

1. Open a terminal
2. Navigate to the src directory of the project
3. To run the server execute the following command: ```java server.Server 8080```
4. The server should now be running on port 8080


# Running the clients

1. Open a new terminal for each client
2. Navigate to the src directory of the project
3. To login a client execute the following command: ```java client.Client localhost 8080 -l <username> <password>```
4. To register a client execute the following command ```java client.Client localhost 8080 -r <username> <password>```