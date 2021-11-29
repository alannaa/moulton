package com.cs4500.fish.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.HashMap;

/**
 * A game tree represents all possible game states reachable from a starting
 * state and player. This also serves as the "rule book" for GameState. We
 * impose rules on actions that can be made by players on a GameState. If no
 * player can make a valid move, then this `GameTree` has no subtrees, otherwise
 * if the current player cannot make a move, then there is only one subtree
 * mapped to a skip with the the current player advanced but with no other
 * changes made to the `GameState` in that subtree.
 * NOTE that the subsequent states are lazily generated upon request.
 * The class is _externally_ pure and immutable.
 */
public class GameTree {
  private GameState state; // The current state of the game.
  // The following field is cached once they are computed.
  // It's intialized to `Optional.empty()`, meaning not computed yet.
  // `subtrees` represents lazily generated sub-trees from this root tree node.
  private Optional<Map<Action, GameTree>> subtrees;

  /**
   * Constructor.
   */
  public GameTree(GameState state) {
    this.state = state;
    this.subtrees = Optional.empty();
  }

  // Getter methods
  public GameState getState() {
    return this.state;
  }

  /**
   * Return a map from each possible action to the resulting sub game tree.
   */
  public Map<Action, GameTree> getSubtrees() {
    if (! this.subtrees.isPresent()) { // lazily generate subtree if we haven't
      this.subtrees = Optional.of(this.computeSubtrees());
    }
    return this.subtrees.get();
  }

  /**
   * Return the GameTree that would result from carrying out an `action` for
   * current player in this GameTree.
   * Return empty if the move is illegal.
   */
  public Optional<GameTree> attemptAction(Action action) {
    Map<Action, GameTree> tree = this.getSubtrees();
    if (tree.containsKey(action)) {
      GameState nextState = tree.get(action).getState();
      return Optional.of(new GameTree(nextState));
    }
    return Optional.empty();
  }

  /**
   * Applies `func` to all sub game tree directly reachable from this game tree.
   */
  public <T>  List<T> mapSubTrees(Function<GameTree, T> func) {
    List<T> result =  new ArrayList<>();
    for (GameTree t : this.getSubtrees().values()) {
      result.add(func.apply(t));
    }
    return result;
  }

  // Compute all legal _moves_ of `currentPlayer` and return a map from each
  // legal move to the resulting GameTree. If there is there is no possible
  // subtree, then the map would be empty.
  private Map<Action, GameTree> computeSubtrees() {
    Map<Action, GameTree> result = new HashMap<>();
    PlayerColor currentPlayer = this.state.getCurrentPlayerColor();
    List<Move> moves = this.getAllPossibleMovesForPlayer(currentPlayer);
    for (Move m : moves) {
      GameState nextState = 
        state.movePenguin(m.getFrom(), m.getTo()).advancePlayer();
      result.put(m, new GameTree(nextState));
    }
    if (result.isEmpty() &&
            this.state.getOrderedPlayers().stream()
            .anyMatch(p -> !this.getAllPossibleMovesForPlayer(p.getPlayerColor())
            .isEmpty())) {
      result.put(Skip.getInstance(), new GameTree(this.state.advancePlayer()));
    }
    return result;
  }

  // Return a list of all possible moves for the penguins of player with given
  // color in `this.state`.
  private List<Move> getAllPossibleMovesForPlayer(PlayerColor color) {
    Board board = state.getBoardCopy();
    state.getPenguinList().forEach(p -> board.removeTile(p.getPosition()));
    // The REASON line 102 exists is to turn all tiles with a penguin
    //  into a hole. The getReachableFrom method ONLY takes into
    //  consideration holes as blockers, not penguins. So by doing this, this
    //  line effectively allows penguins to be taken into consideration as
    //  blockers.
    List<Move> moves = new ArrayList<>();
    // collect all possible moves for each penguin of `currentPlayer`
    for (Penguin p : this.state.getPenguinList()) {
      if (p.getPlayerColor() == color) {
        Position from = p.getPosition();
        for (List<Position> positions :
            board.getReachableFrom(from).values()) {
          positions.forEach(to -> moves.add(new Move(from, to)));
        }
      }
    }
    return moves;
  }
}
