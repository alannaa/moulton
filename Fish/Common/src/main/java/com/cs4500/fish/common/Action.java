package com.cs4500.fish.common;

/**
 * An `Action` represents any Action from a Player in a Fish game, when it takes
 * its turn.
 */
public interface Action {

  boolean isSkip();

  boolean isMove();

  Move getAsMove();

  /* NOTE: The implementation class must override `equals` and `hashCode` to
   * enable structural equality.  */
}
