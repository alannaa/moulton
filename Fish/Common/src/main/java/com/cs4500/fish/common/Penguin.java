package com.cs4500.fish.common;

/**
 * Holds information associated with a Penguin in a Fish Game.
 * NOTE the class is _immutable_
 */
public final class Penguin {
  private final PlayerColor color; // Owner's Color
  private final Position position;

  public Penguin(PlayerColor color, Position position) {
    this.color = color;
    this.position = position;
  }

  public Penguin setPosition(Position position) {
    return new Penguin(color, position);
  }

  public Position getPosition() {
    return position;
  }

  public PlayerColor getPlayerColor() {
    return this.color;
  }
}
