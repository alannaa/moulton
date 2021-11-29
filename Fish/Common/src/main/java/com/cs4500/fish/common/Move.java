package com.cs4500.fish.common;

import java.util.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;

/**
 * A move represents a action from a player move one of his/her penguin from
 * one position to another in a Fish game.
 * NOTE the class is _immutable_
 */
public final class Move implements Action {
  private final Position from;
  private final Position to;

  public Move(Position from, Position to) {
    this.from = from;
    this.to = to;
  }

  public Position getFrom() {
    return this.from;
  }

  public Position getTo() {
    return this.to;
  }

  @Override
  public boolean isSkip() {
    return false;
  }

  @Override
  public boolean isMove() {
    return true;
  }

  @Override
  public Move getAsMove() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Move)) {
      return false;
    }
    Move other = (Move) o;
    return this.from.equals(other.from) && this.to.equals(other.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public String toString() {
    return String.format("[%s to %s]", from ,to);
  }

  // Compare this move with `other` lexicographically, i.e., return negative
  // number if `this` is smaller, 0 if they are equal, positive otherwise.
  public int compareTo(Move other) {
    int fromCmp = this.from.compareTo(other.from);
		return fromCmp != 0 ? fromCmp : this.to.compareTo(other.to);
  }

  // Serialize into and deserialize from json.
  // A move with (src, dst) -> "[src, dst]"
  public static Move deserialize(JsonElement e) throws DeserializationException {
    JsonArray arr = e.getAsJsonArray();
    Position src = Position.deserialize(arr.get(0));
    Position dst = Position.deserialize(arr.get(1));
    return new Move(src, dst);
  }

  public JsonElement serialize() {
    JsonArray arr = new JsonArray();
    arr.add(this.from.serialize());
    arr.add(this.to.serialize());
    return arr;
  }
}
