package com.cs4500.fish.integration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import java.util.List;
import java.util.Map;

import com.cs4500.fish.common.Board;
import com.cs4500.fish.common.Board.Direction;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.Penguin;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.common.PlayerState;
import com.cs4500.fish.common.Position;
import com.cs4500.fish.common.DeserializationException;

import com.google.gson.JsonStreamParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * A program that serves as integration test for GameState.
 */
public class StateTest {

  // When choosing a move, favor the ones earlier directions.
  private static final Direction[] DIRECTION_PREFERENCE = {
    Direction.UP, Direction.UPRIGHT, Direction.DOWNRIGHT,
    Direction.DOWN, Direction.DOWNLEFT, Direction.UPLEFT,
  };


  /**
   * Parse in a `State`, and then output the result from
   * moving the first player's first penguin one step in the first available
   * direction, ordered in `DIRECTION_PREFERENCE`.
   *
   * - State is a Json Object that contains two fields
   *  - { "players" : Player*,
   *      "board" : Board }
   * - Player* is a Json array of Player
   *  - [Player, ..., Player]
   * - Player is a Json Object
   *  - { "color" : Color,
   *      "score" : Natural,
   *      "places" : [Position, ..., Position] }
   * - Board is a JSON array of JSON arrays where each element is
   *   either 0 or a number between 1 and 5. (0 means a hole)
   * - Position is a JSON array that contains two natural numbers:
   *   [board-row, board-column].
   * - Color is one of
   *  - ["red", "white", "brown", "black"]
   * Ex:
   * Input:
   * -  { "players" : [{ "color" : "white",
   *                     "score" : 42,
   *                     "places" : [[2, 3], [1, 1]] }],
   *      "board" : [[1,  2,  3,  1],
   *                   [2,  2,  2,  2],
   *                 [1,  1,  1,  1]] }
   * Output:
   * - { "players" : [{ "color" : "white",
   *                    "score" : 43,
   *                    "places" : [[0, 3], [1, 1]] }],
   *     "board" : [[1,  2,  3,  1],
   *                  [2,  2,  2,  2],
   *                [1,  1,  1,  1]] }
   */
  public static void sillyPlayerMove(Reader input, Appendable output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    try {
      GameState state = GameState.deserialize(parser.next().getAsJsonObject());
      Optional<GameState> newState = applyFirstMove(state);

      JsonElement result = newState.isPresent() ?
        newState.get().serialize() : new JsonPrimitive(false);
      output.append(result.toString());
    } catch (DeserializationException e) {
      System.err.println("Invalid json");
      e.printStackTrace();
      return;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the resulting state of applying the first move available to the
   * first player's first penguin, based on `DIRECTION_PREFERENCE`.
   * Return empty if state has no player, or first player has no penguin, or the
   * penguin can't move.
   */
  private static Optional<GameState> applyFirstMove(GameState state) {
    return getFirstPenguin(state).flatMap(p -> {
      Board board = state.getBoardCopy();
      state.getPenguinList().forEach(pg -> board.removeTile(pg.getPosition()));
      Position src = p.getPosition();
      Map<Direction, List<Position>> map = board.getReachableFrom(src);
      for (Board.Direction dir : DIRECTION_PREFERENCE) {
        if (map.containsKey(dir)) {
          Position dst = map.get(dir).get(0);
          return Optional.of(state.movePenguin(src, dst));
        }
      }
      return Optional.empty();
    });
  }


  /**
   * Return the first-placed penguin of the first-to-take-turn player in
   * `state`.
   * Return empty if `state` has no player or the 1st player has no penguin.
   */
  private static Optional<Penguin> getFirstPenguin(GameState state) {
    List<PlayerState> players = state.getOrderedPlayers();
    if (!players.isEmpty()) {
      PlayerColor color = players.get(0).getPlayerColor();
      for (Penguin p : state.getPenguinList()) {
        if (p.getPlayerColor().equals(color)) {
          return Optional.of(p);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Program entry.
   */
  public static void main(String[] args) {
    Reader in = new InputStreamReader(System.in);
    Appendable out = System.out;
    sillyPlayerMove(in, out);
  }
}
