package com.cs4500.fish.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * All possible colors a Penguin can have.
 */
public enum PlayerColor {
  RED, WHITE, BROWN, BLACK;

  // Serialize into and deserialize from json.
  public static PlayerColor deserialize(JsonElement e) throws DeserializationException {
    switch (e.getAsString()) {
      case "red" : return RED;
      case "white" : return WHITE;
      case "brown" : return BROWN;
      case "black" : return BLACK;
    }
    throw new DeserializationException("unrecognized color: " + e);
  }

  public JsonElement serialize() {
    switch (this) {
      case RED:   return new JsonPrimitive("red");
      case WHITE: return new JsonPrimitive("white");
      case BROWN: return new JsonPrimitive("brown");
      default:    return new JsonPrimitive("black");
    }
  }
}