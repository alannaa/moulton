package com.cs4500.fish.player;

import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.Position;

/**
 * This represents a strategy for choosing penguin placement.
 */
public interface PlacementStrategy {
  /**
   * Return a position that represents a penguin placement on the baord in
   * `state`.
   * @throws IllegalArgumentException if there isn't a choice.
   */
  Position apply(GameState state);
}
