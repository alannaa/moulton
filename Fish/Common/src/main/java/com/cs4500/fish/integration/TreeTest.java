package com.cs4500.fish.integration;

import com.cs4500.fish.common.Board.Direction;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.GameTree;
import com.cs4500.fish.common.Move;
import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.Position;
import com.cs4500.fish.common.DeserializationException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A program that serves as integration test for GameState.
 */
public class TreeTest {

  // Constants for Json Parsing
  private static final String STATE_FIELD = "state";
  private static final String FROM_FIELD = "from";
  private static final String TO_FIELD = "to";

  // When choosing a move, favor the ones earlier directions.
  private static final Direction[] DIRECTION_PREFERENCE = {
    Direction.UP, Direction.UPRIGHT, Direction.DOWNRIGHT,
    Direction.DOWN, Direction.DOWNLEFT, Direction.UPLEFT,
  };

  /**
   * Parse in a Move-Response-Query and then it outputs the action obtained from
   * `getNextPlayerResponse`, or "false" if not possible.
   * -  Move-Response-Query is a JSON Object with 3 fields :
   *  - { "state" : State,
   *      "from"  : Position
   *      "to"    : Position }
   * - INTERPRETATION The object describes the current state and the move that
   *   the currently active player picked. CONSTRAINT The object is invalid, if
   *   the specified move is illegal in the given state.
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
   * -  {state : { "players" : [{ "color" : "white",
   *                              "score" : 42,
   *                              "places" : [[1, 2], [1, 1]] }],
   *               "board"   : [[1,  2,  3,  1],
   *                              [2,  2,  2,  2],
   *                            [1,  1,  1,  1]] }
   *     from  : [1, 2]
   *     to    : [2, 2] }
   * Output:
   * - [[1, 1], [0, 2]]
   */
  public static void outputNextPlayerResponse(Reader input, Appendable output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    JsonObject moveResponseQuery = parser.next().getAsJsonObject();

    try {
      GameState state = GameState.deserialize(moveResponseQuery.get(STATE_FIELD));
      Position from = Position.deserialize(moveResponseQuery.get(FROM_FIELD));
      Position to = Position.deserialize(moveResponseQuery.get(TO_FIELD));
      Optional<Move> move = getNextPlayerResponse(state, new Move(from, to));

      JsonElement result = move.isPresent() ?
        move.get().serialize() : new JsonPrimitive(false);
      output.append(result.toString());
    } catch (DeserializationException e) {
      System.err.println("Invalid json");
      e.printStackTrace();
      return;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Outputs action that the next player can take to get a penguin to a place
  // that neighbors the one that the current player moved to, or "false" if not
  // possible; The neighbor tiles are searched based on `DIRECTION_PREFERENCE`.
  // Return `Optional.empty()` if no such move exists.
  private static Optional<Move> 
    getNextPlayerResponse(GameState state, Move move) {
    GameTree tree = new GameTree(state);
    if (! tree.getSubtrees().containsKey(move)) {
      return Optional.empty();
    }
    GameTree subtree = tree.getSubtrees().get(move);
    List<Move> moves = subtree.getSubtrees().keySet()
            .stream().filter(Action::isMove)
            .map(Action::getAsMove).sorted(Move::compareTo)
            .collect(Collectors.toList());
    // sort in advance to resolve ties, only `from` position matters.
    for (Direction d : DIRECTION_PREFERENCE) {
      Position target = d.stepFrom(move.getTo());
      for (Move opponentMove : moves) {
        if (target.equals(opponentMove.getTo())) {
          return Optional.of(opponentMove);
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
    outputNextPlayerResponse(in, out);
  }
}
