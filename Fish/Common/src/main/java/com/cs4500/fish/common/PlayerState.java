package com.cs4500.fish.common;

import com.cs4500.fish.admin.PlayerSystemInteraction;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Objects;

/**
 * Contains all the information about a player in a Fish game.
 * NOTE the class is _immutable_
 */
public class PlayerState {
  // for json (de)serialization
  public static final String COLOR_FIELD = "color";
  public static final String SCORE_FIELD = "score";

  private final PlayerColor color;
  private final int age;
  private final int score;

  public PlayerState(PlayerColor color) {
    this(color, 0);
  }

  // used when initializing new players in real game
  // age represents the index this player has in the init age-ordered playerList
  // score initialized to zero at start of game
  public PlayerState(PlayerColor color, int age) {
    this.color = color;
    this.age = age;
    this.score = 0;
  }

  // used internally
  private PlayerState(PlayerColor color, int age, int score) {
    if (score < 0) {
      String msg = "[PlayerState]: score can't be negative: " + score;
      throw new IllegalArgumentException(msg);
    }
    this.color = color;
    this.age = age; // will never change
    this.score = score;
  }

  public PlayerState addScore(int score) {
    return new PlayerState(this.color, this.age, this.score + score);
  }

  public PlayerColor getPlayerColor() {
    return this.color;
  }

  public int getAge() { return this.age; }

  public int getScore() {
    return this.score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlayerState playerState = (PlayerState) o;
    return color == playerState.color
        && age == playerState.age
        && score == playerState.score;
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, score);
  }

  // Serialize into and deserialize from json.
  // Player ->
  //  - { "color" : Color,
  //      "score" : Natural }
  public static PlayerState deserialize(JsonElement e)
          throws DeserializationException {
    if (! (e.isJsonObject() && e.getAsJsonObject().has(COLOR_FIELD) &&
            e.getAsJsonObject().has(COLOR_FIELD)) ) {
      String msg = String.format("Expected: JsonObject with %s and %s.",
              COLOR_FIELD, SCORE_FIELD);
      throw new DeserializationException(msg);
    }
    JsonObject playerObj = e.getAsJsonObject();
    PlayerColor color = PlayerColor.deserialize(playerObj.get(COLOR_FIELD));
    int score = deserializeScore(playerObj.get(SCORE_FIELD));
    return new PlayerState(color, score);
  }

  private static int deserializeScore(JsonElement e)
          throws DeserializationException {
    if (! e.isJsonPrimitive() || ! e.getAsJsonPrimitive().isNumber()) {
      throw new DeserializationException("Expected: Number");
    }
    int score = e.getAsInt();
    if (score < 0) {
      String msg = "Score must be non-negative";
      throw new DeserializationException(msg);
    }
    return score;
  }

  public JsonObject serialize() {
    JsonObject playerObj = new JsonObject();
    playerObj.add(COLOR_FIELD, this.color.serialize());
    playerObj.add(SCORE_FIELD, new JsonPrimitive(this.score));
    return playerObj;
  }

  @Override
  public String toString() {
    return String.format("(color : %s, age %d, score %d)", color, age, score);
  }
}
