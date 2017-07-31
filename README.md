## Calculator assignment
### Scope

Develop a calculator webapplication using websockets.

### Application

Websocket for computing operations;

REST API for handling results;

Simple UI for demo

1. Build *mvn install* - builds and creates Dockerfile
2. Test *mvn test* - integration and unit tests
3. Run *docker-compose up*
4. Index *http://localhost:8080/*
5. Swagger API *http://localhost:8080/swagger-ui.html*
6. HealthCheck *http://localhost:8080/health*

### At Most Once Delivery

Given the request to deliver the message at most once, the problem appears when the server publishes a message 
and no one is listening on the topic. The default brocker used didn't permit ACK headers, for that we could use another 
queue (RabbitMQ) this means that a message is requeued if there is no ACK. This could lead to a growing queue with
messages that will not be read soon (user is logout).
The solution chosen was to create an acknowledge endpoint where the client can validate the message he received.
This requires that the messages to be stored on server, in this particular case I used a cache from the Guava library
( this could also be stored in a db ). When the user acknowledges a message it is removed from the cache, and it can't 
be retrieved again by the user.

To scale the application we either replace the local cache with a distributed cache (Hazelcast) or save the results in 
a database, that will be accessed from multiple services.





