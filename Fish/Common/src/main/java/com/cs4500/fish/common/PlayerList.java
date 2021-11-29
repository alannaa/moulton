package com.cs4500.fish.common;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class is a representation of all of the data regarding players.
 * It is intended to be expandable if we need to represent more information
 * about players in the future.
 * NOTE the class is _immutable_
 * INVARIANT: 
 * - colors of penguins must be used by `orderedPlayers`
 * - Positions of the penguins are distinct
 * - List is ordered by time of placement (intra, not inter player),
 *   or the order of penguins in players from deserialization.
 * - they always have distinct colors.
 * - there is at least 1 player
 */
class PlayerList {
  // for json (de)serialization
  public static final String PENGUIN_POS_FIELD = "places";
  // This represents the locations of all penguins on a board
  private final List<Penguin> penguinList;
  // This list is ordered according to player turn.
  private final List<PlayerState> orderedPlayers;

  /**
   * Construct a list of player state with given colors.
   */
  PlayerList(List<PlayerColor> colors) {
    if (new HashSet<>(colors).size() != colors.size()) {
      throw new IllegalArgumentException("Given colors must have no duplicates");
    }
    this.penguinList = new ArrayList<>();
    this.orderedPlayers = new ArrayList<>();
    // initialize the list of orderedPlayers with the age included
    // age is equivalent to the index the player has in the init ordered list
    colors.forEach(c -> orderedPlayers.add(new PlayerState(c,
        colors.indexOf(c))));
  }

  // for implementation convenience
	private PlayerList(List<PlayerState> players, List<Penguin> penguins) {
    this.orderedPlayers = players;
    this.penguinList = penguins;
	}


  PlayerColor getCurrentPlayerColor() {
    return this.orderedPlayers.get(0).getPlayerColor();
  }

  /**
   * @throws IllegalArgumentException if no player has `color`.
   */
  PlayerState getPlayerStateWithColor(PlayerColor color) {
    for (PlayerState player : this.orderedPlayers) {
      if (player.getPlayerColor().equals(color)) {
        return player;
      }
    }
    throw new IllegalStateException("My player color is not in the game state");
  }

  // advance the current player to the next player in order.
  PlayerList advancePlayer() {
    //note : This does mutate orderedPlayers
    List<PlayerState> nextOrdering = 
      orderedPlayers.subList(1, orderedPlayers.size());
    nextOrdering.add(orderedPlayers.get(0));
    return new PlayerList(nextOrdering, this.penguinList);
  }

  // removes the player and its penguins with the color
  // if color does not exist in this game, throw illegal state exc
  // if 1 player remains, return Optional.emppty
  Optional<PlayerList> removePlayerWithColor(PlayerColor color) {
    // effectively does not mutate the current state, so if this method
    // call returns optional.empty, fall back on the previous state
    if (this.orderedPlayers.size() == 1) {
      return Optional.empty();
    }
    // remove the playerState:
    List<PlayerState> remainingPlayers = new ArrayList<>(this.orderedPlayers);
    int index = getPlayerIndexWithColor(color);
    remainingPlayers.remove(index);
    // remove the player's penguins:
    List<Penguin> remainingPenguins = new ArrayList<>(this.penguinList);
    remainingPenguins.removeIf(p -> p.getPlayerColor() == color);
    return Optional.of(new PlayerList(remainingPlayers, remainingPenguins));
  }

  // create a new penguin for player with `color` at `pos`
  PlayerList placePenguin(PlayerColor color, Position pos) {
    if (orderedPlayers.stream().noneMatch(p -> p.getPlayerColor() == color)) {
      String msg ="Cannot place penguin with unassigned color";
      throw new IllegalArgumentException(msg);

    } else if (getPenguinIndexAt(pos).isPresent()) {
      String msg = "Cannot place penguin where another penguin already exists";
      throw new IllegalArgumentException(msg);
    }
    List<Penguin> penguins = new ArrayList<>(this.penguinList);
    List<PlayerState> players = new ArrayList<>(this.orderedPlayers);
    penguins.add(new Penguin(color, pos));
    return new PlayerList(players, penguins);
  }

  // increment the penguin owner's score by `fishAtFrom` 
  PlayerList movePenguin(Position from, Position to, int fishAtFrom) {
    Optional<Integer> index = getPenguinIndexAt(from);
    if (! index.isPresent()) {
      String msg = "No penguin exists at " + from + ".";
      throw new IllegalArgumentException(msg);

    } else if (getPenguinIndexAt(to).isPresent()) {
      String s = "Cannot move penguin to where another penguin already exists";
      throw new IllegalArgumentException(s);
    }
    Penguin p = this.penguinList.get(index.get()); // the penguin at `from`
    List<PlayerState> updatedPlayers = incrementScore(p.getPlayerColor(), fishAtFrom);
    List<Penguin> updatedPenguins = new ArrayList<>(this.penguinList);
    updatedPenguins.set(index.get(), p.setPosition(to));
    return new PlayerList(updatedPlayers, updatedPenguins);
  }

  // Always return a copy
  List<PlayerState> getOrderedPlayers() {
    return new ArrayList<>(this.orderedPlayers);
  }

  // Always return a copy
  List<Penguin> getPenguinList() {
    return new ArrayList<>(this.penguinList);
  }

  private int getPlayerIndexWithColor(PlayerColor color) {
    for (int i = 0; i < this.orderedPlayers.size(); i += 1) {
      PlayerState state = this.orderedPlayers.get(i);
      if (color == state.getPlayerColor()) {
        return i;
      }
    }
    String msg = "No player has given color: " + color;
    throw new IllegalStateException(msg); // internal error, should not happen
  }

  // Return an new list of player states from `orderedPlayers` in which the
  // score of player that has `color` is incremented by `score`.
  private List<PlayerState> incrementScore(PlayerColor color, int score) {
    List<PlayerState> players = new ArrayList<>(this.orderedPlayers);
    int index = getPlayerIndexWithColor(color);
    PlayerState state = players.get(index);
    players.set(index, state.addScore(score));
    return players;
  }

  // Returns Optional.empty if no penguin is at `pos`
  private Optional<Integer> getPenguinIndexAt(Position pos) {
    for (int i = 0; i < penguinList.size(); i += 1) {
      if (penguinList.get(i).getPosition().equals(pos)) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }

  // Serialize into and deserialize from json.
  // PlayerList -> [Player, ..., Player]
  // Player ->
  //  - { Player, # serialize components from Player
  //      "places" : [Position, ..., Position] }
  public static PlayerList deserialize(JsonElement e)
          throws DeserializationException {
    // deserialize player, then construct penguin list from the extra "places"
    // field in player json object.
    List<PlayerState> players = new ArrayList<>();
    List<Penguin> penguins = new ArrayList<>();
    if (! e.isJsonArray()) {
      throw new DeserializationException("PlayerList should be a JsonArray");
    }
    for (JsonElement obj : e.getAsJsonArray()) {
      if (! (obj.isJsonObject() && 
            obj.getAsJsonObject().has(PENGUIN_POS_FIELD))) {
        String msg = String.format("Expected: JsonObject with %s",
                PENGUIN_POS_FIELD);
        throw new DeserializationException(msg);
      }
      JsonObject playerObj = obj.getAsJsonObject();
      PlayerState player = PlayerState.deserialize(playerObj);
      players.add(player);
      if (! playerObj.get(PENGUIN_POS_FIELD).isJsonArray()) {
        String msg = "Players should have a list of penguin positions";
        throw new DeserializationException(msg);
      }
      JsonElement penguinPosArr = playerObj.get(PENGUIN_POS_FIELD);
      for (JsonElement pos : penguinPosArr.getAsJsonArray()) {
        PlayerColor color = player.getPlayerColor();
        Penguin p = new Penguin(color, Position.deserialize(pos));
        penguins.add(p);
      }
    }
    return constructFrom(players, penguins);
  }

  // Check all the invariants and construct a player list.
  private static PlayerList constructFrom(List<PlayerState> players,
                                          List<Penguin> penguins)
          throws DeserializationException {

    Set<PlayerColor> colors = new HashSet<>();
    players.forEach(ps -> colors.add(ps.getPlayerColor()));
    if (colors.size() != players.size()) {
      String msg = "Players in PlayerList must have distinct colors";
      throw new DeserializationException(msg);
    }
    Set<Position> positions = new HashSet<>();
    penguins.forEach(peng -> positions.add(peng.getPosition()));
    if (positions.size() != penguins.size()) {
      String msg = "Penguins in PlayerList must have distinct positions";
      throw new DeserializationException(msg);
    }
    return new PlayerList(players, penguins);
  }

  public JsonElement serialize() {
    // Categorize penguin positions based on owner color, then serialize players
    // with those position information added to the "places" field.
    JsonArray playerArr = new JsonArray();
    Map<PlayerColor, List<JsonElement>> colorToPlaces = new HashMap<>();
    for (Penguin p : this.penguinList) {
      colorToPlaces.computeIfAbsent(p.getPlayerColor(), c -> new ArrayList<>())
        .add(p.getPosition().serialize());
    }
    for (PlayerState player : this.orderedPlayers) {
      JsonObject playerObj = player.serialize();
      JsonArray placesArr = new JsonArray();
      PlayerColor color = player.getPlayerColor();
      colorToPlaces.getOrDefault(color, new ArrayList<>()).forEach(placesArr::add);
      playerObj.add(PENGUIN_POS_FIELD, placesArr);
      playerArr.add(playerObj);
    }
    return playerArr;
  }

  @Override
  public String toString() {
    return this.orderedPlayers.toString();
  }
}
