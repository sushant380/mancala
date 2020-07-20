
  
# Mancala  

[![Build Status](https://travis-ci.org/sushant380/mancala.svg?branch=master)](https://travis-ci.org/sushant380/mancala)
[![Code Coverage](https://codecov.io/github/sushant380/mancala/coverage.svg)](https://codecov.io/gh/sushant380/mancala)

  
### Java, Maven, Spring Boot, Spring Security, Rest API, Swagger, Embedded Mongo DB  
  
Mancala is a family of board games played around the world, sometimes called "sowing" games, or "count-and-capture" games, which describes the gameplay. This application provides api to implement mancala game.  
  
## Tech Stack  
  
1. Java 11  
  
2. Maven   
  
3. Spring Boot  
  
4. Swagger   
  
5. Lombok   
  
6. Security  
  
7. Docker  
  
8. Mongo DB  
  
## Steps to Setup  
  
**1. Install the application to fetch dependencies.**  
```bash  
mvn install  
```  
In case you are facing compilation errors for domain beans, Please check the **lombok dependency** supported in your IDE. This should not be a problem from command line.  
  
**2. Run the app using maven**  
  
```bash  
mvn spring-boot:run  
```  
  
The app will start running at <http://localhost:9080>.  
You can use inbuilt swagger ui to test the app or you go for the postman/curl clients.  
  
Swagger: <http://localhost:9080/swagger-ui.html>.  
Swagger-docs:<http://localhost:9080/v2/api-docs>.  
  
## Explore Rest APIs  
  
The app defines following APIs.  
      
 - **`GET`** localhost:9080/games (fetch all games) 
 - **`GET`** localhost:9080/games/{gameId} (fetch game by id) 
 - **`PUT`** localhost:9080/game/{gameId}/pits/{pitid} (make move by game id and pit id) 
 - **`POST`** localhost:9080/games (Create game) PUT localhost:9080/games/{gameid}/join (Join game)
 - **`DELETE`** localhost:9080/games/{gameid} (Delete game)    
  
You can test them using postman or any other rest client.  
  
 You must add Basic Auth in your rest client.   
+ open `RestSecurityConfiguration`and you can find 3 roles and credentials  
      
    Authorized users:   
        1. admin/admin  
        2. player1/player1  
        3. player2/player2  
      
    Unauthorized user: user/user  
 ## Game Play
 Actions:
+ Call **`POST`** `/games` with one player id.
+ Call **`PUT`** `/games/{gameid}/join` with another player id.
+ Call **`PUT`** `localhost:9080/game/{gameId}/pits/{pitid}` with player 1 id.
+ Check the `nextPlayer` id in the response.
+ Based on nextPlayer, call **`PUT`** `localhost:9080/game/{gameId}/pits/{pitid}` with respective player id till you get the winner
+ Keep checking Game `status`.
+ `winnerPlayer` will display winning players id after game ends.
+ Use `admin` user to delete the game.
## Key points to note  
 
+ API implementation and validation of parameters.  
+ Supports Security `/games**` requests  
+ Using lombok library   
+ Mongo DB integration  
  
## Possible improvements  
  
+ Registration api.  
+ Logs.
