# Folder Structure

Due to the nature of our programming language, all classes are found in `Fish/Common/src/main/java/com/cs4500/fish`. 
Specifically, remote components can be found in the `remote` folder.

### Server side
- `server` provides a TCP connection for our tournament manager and referees to interact with remote players. It collects all remote player connections, 
creates proxy players for each of them and passes them to the tournament manager to run a Fish tournament, if there are enough players.
- `proxyPlayer` represents a single remote player on our server side. It serializes and deserializes JSON commands and return values during the interaction of requesting for the remote player's moves by communicating with the `proxyAdmin` on the client side through the TCP connection.

### Client side
- `client` provides a TCP connection for our remote players to sign up and join our Fish tournament. 
- `proxyAdmin` represents an administrator that serialize and deserialize JSON commands and return values for the player it is in charge of. It lives on the client side and communicates with the `proxyPlayer` on the server side through TCP connection in order for our remote players to interact with our Admins (Tournament manager and Referees) on the server side.

## Interactions
All remote interactions can be found [here](https://felleisen.org/matthias/4500-f20/remote.html) and all logical interactions can be found [here](https://felleisen.org/matthias/4500-f20/local_protocol.html). 

## Code Modifications
The runRournament() method in the tournament class originally only returned a list of Player which were the tournament winners. Therefore, there was no way to count the tournament filaures or cheaters. The tournament manager was modified to return a hashmap of "PlayerOutcome" (one of: WINNER, CHEATER, FAILURE, or LOSER) to list of Player, where all of the possible outcomes are captured.

Previously, the referee class employed method chaining so rigorously that no method could be altered without breaking another. Sometimes, however, the method chaining would result in complete arbitrary purpose statements. For example, the assignColor() method used to take in as arguments a BoardCongif and list of Players, and output a GameState. What significance were the arguments and output? Well, the method, in addition to sending the players a message with their color, alo constructed the referee's initial GameState to track the game. This was modified to be able to incorporate also sending players opponents' colors. Now, the assignColor and informPlayerOpponentColors are simply void and do what their names say they do and nothing more, while the GameState set up takes place in the main game running loop. 

Player names were previously unkept. We added a field to the Player class to hold a String representing the player name.

Lastly, the codebase needed rigous testing to be done as there were originally only 2-3 unit tests per class. Significant tesing was added to the code base at every level and will continue to be added to the code base until the final code walk. 
