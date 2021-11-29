package com.cs4500.fish.player;

import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.GameTree;

/**
 * This represents a strategy for choosing a player's action during its turn.
 */
public interface TurnActionStrategy {
  /**
   * Return an action that represents an action the current player in `tree`
   * makes during its turn.
   */
  Action apply(GameTree tree);
}
