package com.cs4500.fish.common;

/**
 * A Skip represents an action of player skipping its turn.
 * NOTE the class is _immutable_
 */
public final class Skip implements Action {

  private static final Skip instance = new Skip();

  //Singleton pattern
  public static Skip getInstance() {
    return instance;
  }

  @Override
  public boolean isSkip() {
    return true;
  }

  @Override
  public boolean isMove() {
    return false;
  }

  @Override
  public Move getAsMove() {
    throw new IllegalArgumentException("Not a move");
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Skip);
  }

  @Override
  public int hashCode() {
    return 42;
  }

  @Override
  public String toString() {
    return "[skip]";
  }
}
