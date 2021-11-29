# High Level Representation
- A GameState is (Board)
  - Interpretation: Board holds all the information in a Fish game
- A Board is a (Array Tiles)
  - Interpretation: A 2d array of tiles, whose positions are their indices.
- Tiles is a (Int, `Optional<Penguin>`))
  - Interpretation: Number of fish on the tile, and penguin (if any)
- A Penguin is a (int, PlayerColor)
  - Interpretation: Amount of fish a penguin has collected and its color
- PlayerColor is one of [Red, Brown, Black, White]

# Game State External Interface
```java
interface GameState {
  /**
   * Returns a direct reference to the board. 
   */
  Board getBoard();
}
``` 
The referee will distribute copies of his GameState to players so that they may
interact with them as they please. The referee will then accept move requests
from the players and carry them out ccording to the rules. Because the referee
is accessing a direct reference to he board, there are no additional methods
necessary to interact with the board. 

- Why we don’t explicitly represent Player with a class
  Currently there are only 3 pieces of information associated with a player, its
  penguins, its score, and its color. This could either be represented in a
  Player class, or by the Penguin a player owns. Since the former complicates
  the rendering process, we decided to represent a player completely by the
  penguin it owns.

- How does one use GameState for rule checking
  To check whether an action complies with game rules, one can query the GameState
  to check the state of the board. For instance, if a player requested to make a
  move from position (1, 1) to (2, 2), one needs to check (a). The positions are
  both within the board, (b). A penguin exists at (1, 1) and is owned by the
  player. (c). The move is in a straight line, and crosses valid tiles. The Board
  class provides methods for all such queries.

- How does one use GameState for strategic
  For strategic planning, the player needs to look at the state of the board,
  which can be done by querying the GameState. For more advanced AI players, they
  might need to simulate the games for a few turns. In this case, the Referee will
  hand a _copy_ of the GameState to the player, and the player can modify it to
  simulate the game, search and select the optimal move.