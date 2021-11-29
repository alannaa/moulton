# Referee
Author: Christian Hauser & Xiangxi Guo (Ryan)
Repo: Mabank

## The Relevant Information 

- a `GameResult` is a `([List Player], [List Player], [List Player], [List Player]`
  - interpretation: The lists represent cheating players, failed players,
    winners and all the other players.
- a `BoardConfig` is a `(int, int, int, int, [Set Position])
  - interpretation: The width, height, default # of fish on each tile, minimum #
    of tiles with 1 single fish, and the positions that should be holes.
- a `Position` is a `(int, int)`
  - interpretation: The 2 integers represent a position on the fish game board,
    indexed by row and column.


## The Use Cases

The Referee API is useful for the Tournament Manager, who can use it to:

- create, set up, and manage a single Fish game, with a given list of players.
- add game observers, to whom the Referee will update progress of the Game.


## The External Interface

A `Referee` keeps track of all the `GameObserver`s added via the
`addGameObserver` method, and periodically update them with game progress.

```java
public interface Referee {

  /**
   * Construct a board using `config`, assign each player in `players` a unique
   * color, and create a game state with the board and player colors. The order
   * of players in `players` correspond to their turn order in the actual game.
   * It starts the game with the penguin placement phase, Each player receives 6 - N
   * penguins where N is the number of players. It will request penguin
   * placement from players based on their order in `players`, and run as many
   * rounds as there are penguins per player. If a player attempts to place a 
   * penguin outside the board, onto a hole or an occupied tile, register it as
   * a cheater.
   */
  void setupGame(BoardConfig config, List<Player> players);

  /**
   * ASSUME `setupGame` has been called and finished.
   * Handles players taking turn. Each player will be asked to provide an
   * action, which this referee will then validate against the gametree, and
   * then update then traverse the gametree if the move is valid. The game will
   * stop running when the game tree has no more subtrees, or if all the players
   * have either failed or cheated, in which case there is no winners. Return
   * the result of this game asynchronously. Failed players are removed from the 
   * game, and kept track of. At the end of
   * the game, these players are reported upwards. 
   * A `failed` player can be described as someone who has lost connection, or 
   * has failed to respond to a request within
   * an acceptable time.
   */
  Future<GameResult> runGame();

  /**
   * Keep track of `observer` and periodically update them with game progress
   * from either the `setupGame` phase, or the actual `runGame` phase.
   */
  void addGameObserver(GameObserver observer);
}
```

The sub-contractor can assume the following interfaces:

```java

public interface Player {

  /**
   * Inform this player that it has been assigned the given color.
   */
  void assignColor(PlayerColor color);

  /**
   * Asks the player to send a position in, representing their penguin
   * placement.
   */
  Position requestPenguinPlacement(GameState state);

  /**
   * Asks the player to send in an action that represents their turn.
   */
  Action requestAction(GameState state);

  /**
   * Informs the player of the end and result of their current game.
   */
  void informGameResult(GameResult result);

  /**
   * Informs the player of the end and result of the tournament
   */
  void informTournamentResult(TournamentResult result);

  /**
   * Disqualifies the player with given reason.
   */
  void disqualifyPlayer(String reason);

  /**
   * Inform players waiting for their next turn about changes in the game
   * state. (e.g., another player has made a move).
   */
  void informTurn(GameState state);
}
```
