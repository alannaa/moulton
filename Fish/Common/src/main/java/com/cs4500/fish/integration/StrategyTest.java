package com.cs4500.fish.integration;

import com.cs4500.fish.common.GameState;
import com.cs4500.fish.player.MinimaxTurnActionStrategy;
import com.cs4500.fish.common.GameTree;
import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.DeserializationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * A program that serves as integration test for the player strategy component.
 */
public class StrategyTest {

  /**
   * Parse in a Depth-State object and outputs the best turn Action for the
   * given depth D and State that the next (first in array) player can take
   * according to the MinimaxTurnActionStrategy.
   * Depth-State is [D, State] where D (short for depth) is a Natural in [1,2]
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
   * -  [1,
   *     { "players" : [{ "color" : "white",
   *                                "score" : 42,
   *                                "places" : [[1, 2], [1, 1]] }],
   *                      "board"   : [[1,  2,  3,  1],
   *                                     [2,  2,  2,  2],
   *                                   [1,  1,  1,  1]] }
   *    ]
   * Output:
   * - The best move "white" can make, e.g., [[1, 2], [1, 0]]
   */
  public static void outputBestAction(Reader input, Appendable output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    JsonArray depthStateArr = parser.next().getAsJsonArray();
    int maxDepth = depthStateArr.get(0).getAsInt();
    try {
      GameState state = GameState.deserialize(depthStateArr.get(1));
      Action bestAct = 
        new MinimaxTurnActionStrategy(maxDepth).apply(new GameTree(state));
      JsonElement e = bestAct.isMove() ? 
        bestAct.getAsMove().serialize() : new JsonPrimitive(false);
      output.append(e.toString());
    } catch (DeserializationException e) {
      System.err.println("Invalid json");
      e.printStackTrace();
      return;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Program entry.
   */
  public static void main(String[] args) {
    Reader in = new InputStreamReader(System.in);
    Appendable out = System.out;
    outputBestAction(in, out);
  }
}
