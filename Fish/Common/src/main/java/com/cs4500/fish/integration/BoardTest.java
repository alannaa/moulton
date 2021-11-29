package com.cs4500.fish.integration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.cs4500.fish.common.Board;
import com.cs4500.fish.common.DeserializationException;
import com.cs4500.fish.common.Position;

import com.google.gson.JsonStreamParser;
import com.google.gson.JsonObject;

/**
 * A program that serves as integration test for Board.
 */
public class BoardTest {

  public static final String POSITION_FIELD = "position";
  public static final String BOARD_FIELD = "board";

  /**
   * Parse a Board-Posn json object from `input` and print out the # of
   * reachable positions to `output`.
   * - Board-Posn is
   *  { "position" : Position,
   *    "board"    : Board}
   * - Position is a JSON array that contains two natural numbers:
   *   [board-row, board-column].
   * - Board is a JSON array of JSON arrays where each element is
   *   either 0 or a number between 1 and 5. (0 means a hole)
   * Ex:
   * Input:
   * - { "position" : [0, 0],
   *     "board"    : [[1, 2, 3],
   *                   [4, 0, 5],
   *                   [1, 1, 0]] }
   * Output:
   * - 3
   */
  public static void countReachablePos(Reader input, Appendable output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    JsonObject boardPosn = parser.next().getAsJsonObject();
    try {
      Position pos = Position.deserialize(boardPosn.get(POSITION_FIELD));
      Board board  = Board.deserialize(boardPosn.get(BOARD_FIELD));
      int reachablePosCount = 0;
      for (List<Position> list : board.getReachableFrom(pos).values()) {
        reachablePosCount += list.size();
      }
      output.append(Integer.toString(reachablePosCount));
    } catch (DeserializationException e) {
      System.err.println("Invalid json");
      e.printStackTrace();
      return;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    Reader in = new InputStreamReader(System.in);
    Appendable out = System.out;
    countReachablePos(in, out);
  }
}
