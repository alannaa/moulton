package com.cs4500.fish.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Holds information for a tile on a board in a Fish game.
 * No fish means a tile is removed.
 * NOTE the class is _immutable_
 */
public class Tile {

  // INVARIANT: `numFish` >= 0.
	private final int numFish;

	public Tile(int numFish) {
    if (numFish < 0) {
      String msg = "Number of fish on a tile must be positive";
      throw new IllegalArgumentException(msg);
    }
		this.numFish = numFish;
	}

  public int getNumFish() {
    return this.numFish;
  }

  // a penguin has left this tile and collected all of its fish
  public boolean isRemoved() {
    return this.numFish == 0;
  }

  public Tile remove() {
    return new Tile(0);
  }

  // Serialize into and deserialize from json.
  // A Tile with (numFish) -> "numFish"
  public static Tile deserialize(JsonElement e)
          throws DeserializationException {
    if (! e.isJsonPrimitive() || ! e.getAsJsonPrimitive().isNumber()) {
      throw new DeserializationException("Expected: Number");
    }
    int fish = e.getAsInt();
    if (fish < 0) {
      String msg = "Number of fish on a tile must be positive";
      throw new DeserializationException(msg);
    }
    return new Tile(fish);
  }

  public JsonElement serialize() {
    return new JsonPrimitive(this.numFish);
  }
}
