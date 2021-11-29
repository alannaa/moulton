# Player interface protocol

## Start of a game

0. The Player will connect to a server port to join the tournament, and then it
   proceeds to (1).

1. The Player should expect a `requestPenguinPlacement` call with an updated
   `GameState`, to which the Player should return a `Position` that represents
   where the Player wishes to place its penguin on the board.

2. After (1), the Player should expect to receive either
  - a `requestPenguinPlacement` call, for which it goes back to (1), or 
  - a `requestMove` call, for which it proceeds to (3).
  - a `informTurn` call, which comes with a new game state resulted from another
    player's penguin placement. Then it repeats (2).

3. Along with the `requestMove` call, the Player can expect an updated
   `GameState`, and a list of valid moves it can make. The Player can either
   provide a move immediately.

4. After (3), the Player should expect to receive either 
  - a `requestMove` call, for which it goes back to (3), or 
  - a `informGameResult` call with information about the result of this game. 
    This also signals the end of the game, and it proceeds to (5).
  - a `informTurn` call, which comes with a new game state resulted from another
    player's penguin movement. Then it repeats (4).

5. After (4), the Player should expect to receive a `informTournamentResult`
   call with information about the result of the entire tournament, and it will
   proceed (6). Otherwise, it will receive a `requestPenguinPlacement` call, for
   which it proceeds to (1).

6. The Player will be disconnected, i.e., the Player will no longer receive any
   call on the interface.

### On Player Misbehavior

The Player coulld be disqualified after any request due to misbehavior, e.g.,
delay in response, invalid moves, etc. If this occurs, the Player will receive a
`disqualifyPlayer` call with a reason, and will proceed to (6).

