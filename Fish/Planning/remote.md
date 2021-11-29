# Remote Protocol
## Running a Tournament

1. Players will connect to the server via the signup handler. Once
 connected, players will wait for all other players to be connected, and then
  the signup handler will package all of these connected players into
   ProxyPlayer, an implementation of the Player interface. This list of
    ProxyPlayers will then be handled to the Tournament Manager. Continue to (2)

2. The Tournament Mananger will then divvy up all of these ProxyPlayers into
 game groups, and will then hand off the groups to individual Referees to
  handle each game. This should all happen within 60 seconds. Continue to (3)

3. The remote player can then expect to receive a JSON element containing the
 color it will be playing for a game. This can be considered as the signaling
  call for the remote player to anticipate playing a game.
  The manager will make this call
  on the Proxy Player class, which will then handle distributing this
   information to its associated remote player. This communication should be
    handled within 10 seconds. Continue to (4)
    
4. The remote player can now expect to receive one of four calls from the
 Proxy Player, either a request for a action,  a request for a penguin
  placement, being informed of an opponent's action, or being informed of having
   been disqualified from the
    game. All of these calls except for being informed of an opponent's action
     require a response within 10 seconds otherwise the player will be
      disqualified. Continue to (5) if the player has Lost, or if 60 seconds
       have passed after being informed of winning without any other
        communication. Continue to (3) if the player has won, and then
         received another call informing them of a new player color
      
5. If the player has lost, then they will be disconnected from the
 game by the Tournament Manager. If
  the player has won their most recent game, and does not receive any more
   calls after 60 seconds, then they can assume that they have won the
    tournament. 
    
## In Prose

The general interactions between the server and the players are handled
 through JSON arrays and elements. Remote Players will connect with a
  currently undetermined method to the SignupHandler, who will then package
   up these remote player connections into a Proxy Player object. This proxy
    player object implements our Player Interface, and contains all the
     calls necessary for the proxy player to interact with our server and
      participate in the tournament.

The proxy player can expect to receive different calls, with all of these
 calls expect responses within 10 seconds. This 10 second buffer period is
  generall more than the player will need if its owners has programmed it
   correctly, but the buffer period exists to check if a proxy player has
    lost connection to its remote player. In the event that this time period
     expires and there has been no response, then a call will be made to the
      proxy player to disqualify the remote player, which said proxy player
       will then handle. 
       
The five calls that the Proxy Player has are
    
    - requestPenguinPlacement
    - requestAction
    - disqualifyPlayer
    - informGameResult
    - assignColor
    
The types of information that the proxy player expects to receive for each of
 these calls in order is as
 follows
 
    - JSON Single coordinate
        - In the style of [row, col]
    - JSON set of 2 coordinates signifying a move
        - In the style of [[row, col], [row, col]]
    - JSON boolean
        - In the style of [true]
    - JSON boolean
        - In the style of [true]
    - JSON boolean
        - In the style of [true]
        
Each of these calls to the Proxy Player also correspond to the Proxy Player
 making a call to its remote player, though using a different method. The
  Proxy Player will request information or inform the remote player by using
   JSON elements in the following style for each call. 
   ```
    - {Communication: "Placement", State:}
    - {Communication: "Action", State:}
    - {Communication: "Disqualify", Result: true}
    - {Communication: "gameResult", Result: true}
    - {Communication: "Color", Color: "White" }
   ```
`State` in these calls represents this JSON formatting, where this is a
 representation of the game.
```
- State is a Json Object that contains two fields
   - { "players" : Player*,
       "board" : Board }
    - Player* is a Json array of Player
   - [Player, ..., Player]
   - Player is a Json Object
    - { "color" : Color,
        "score" : Natural,
        "places" : [Position, ..., Position] }
   - Board is a JSON array of JSON arrays where each element is
     either 0 or a number between 1 and 5. (0 means a hole)
   - Position is a JSON array that contains two natural numbers:
     [board-row, board-column].
   - Color is one of
    - ["red", "white", "brown", "black"]
  ```
  
Each remote player can expect to participate in at least one game, barring
 they don't fail to receive their player color properly. Once a number of
  rounds has been completed, the final winners will simply be informed that
   they have won their most recent game, and then they will be disconnected
    from the game. This signifies that this player(s) have won the tournament
    , and can expect to receive their prize in the mail 6-10 business days
     later.