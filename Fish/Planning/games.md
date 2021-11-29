# High Level Representation
- A `GameTree` is a (`GameState`, `PlayerColor`)
  - Interpretation: The current GameState and who’s turn it is next.
- A `Move` is a (`Position`, `Position`)
  - Interpretation: A potential penguin move from the first position to the second.
- A `GameState` is (`Board`, `PlayerList`)
  - Interpretation: Board holds all the information in a Fish game
- A `PlayerList` is (`[Map Position Penguin]`, `[List PlayerState]`)
  - Interpretation: A map from penguin positions on the board to the penguins,
    and an ordered list of PlayerState that represents turn order.
- A `Board` is a (`[Array [Array Tiles]]`)
  - Interpretation: A 2d array of tiles, whose positions are their indices.
- A `Tiles` is a (`Nat`)
  - Interpretation: Number of fish on the tile.
- A `Penguin` is a (`Nat`, `PlayerColor`)
  - Interpretation: Amount of fish a penguin is holding and its color
- A `PlayerState` is a (`Nat`, `PlayerColor`)
  - Interpretation: Total player Score, and color
- A `PlayerColor` is one of (`Red | Brown | Black | White`)
  - Interpretation: Each player has a unique color in a Fish game.

# GameTree

## Interface
```java
/**
 * An incremental representation of a Game Tree.
 * It's incremental in the sense that
 * - it doesn't explicitly build and hold the entire game tree.
 * - it can generate the next layer of game tree.
 */
class GameTree {
    // data
    private GameState state;
    private PlayerColor nextPlayer;

    // constructor
    public GameTree(GameState state, PlayerColor nextPlayer);

    // getter methods
    public GameState getState();
    public PlayerColor getNextPlayer();

    /**
     * Returns a map that contains all the next valid move that can be made by
     * `nextPlayer`, and the corresponding next subtree.
     */
    public Map<Move, GameTree> getNextPossibleMoves();
}
```


## How Referee uses GameTree
A Referee can use the game tree to verify moves from players by checking whether
the attempted move is in the map returned by `gameTree.getNextPossibleMoves()`.

If the referee needs to remove a player, it updates the game state, finds the
next player, then construct a new GameTree. (this shows how the Referee can
override who's playing next, even though the information is maintained by
GameTree)

## How Player uses GameTree
An AI player can use the game tree to theorize its next move by looking ahead
into future game states, ranking the immediate next moves, and picking the
optimal one from those.
