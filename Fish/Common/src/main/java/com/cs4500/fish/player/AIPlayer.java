package com.cs4500.fish.player;

import com.cs4500.fish.admin.GameResult;
import com.cs4500.fish.common.*;

import java.util.Optional;

/**
 * A simple AI player that relies on the minimax search algorithm.
 */
public class AIPlayer implements Player {
  private Optional<PlayerColor> myColor;
  private final PlacementStrategy placementStrategy;
  private final TurnActionStrategy turnActionStrategy;

  // Construct an AI player with given strategy
  public AIPlayer(PlacementStrategy placementStrategy,
                  TurnActionStrategy turnActionStrategy) {
    this.placementStrategy = placementStrategy;
    this.turnActionStrategy = turnActionStrategy;
  }

  @Override
  public Position requestPenguinPlacement(GameState state) {
    //this.checkIsMyTurn(state);
    return this.placementStrategy.apply(state);
  }

  @Override
  public Action requestAction(GameTree tree) {
    //this.checkIsMyTurn(tree.getState());
    return this.turnActionStrategy.apply(tree);
  }

  // TODO: Why is this needed?
  private void checkIsMyTurn(GameState state) {
    if (! this.myColor.isPresent()) {
      String str = "This player hasn't been assigned a color yet";
      throw new IllegalStateException(str);
    }
    if (! state.getCurrentPlayerColor().equals(this.myColor.get())) {
      throw new IllegalArgumentException("It's not this player's turn");
    }
  }

  // AI player doesn't need to respond to these following events,
  // but may override these methods if they want to use the information
  // given in the arguments

  // AIPlayer has no notion of "what its color is". It plans on behalf of
  // whoever the current player is
  @Override
  public boolean assignColor(PlayerColor color) {
    this.myColor = Optional.of(color);
    return true;
  }

  @Override
  public String toString() {
    String msg = "Player Color (If assigned, otherwise null: " + myColor +"\n";
    return msg;
  }
}
