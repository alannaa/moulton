package com.cs4500.fish.player;

import java.util.Optional;

import com.cs4500.fish.common.Board;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.Position;

public class ScanPlacementStrategy implements PlacementStrategy {

  private static Optional<Position> getLeftmostNonHolePosInRow(
      Board board, Position rowStart) {
    Position pos = rowStart;
    while (board.isPosInbound(pos)) {
      if (! board.getTileAt(pos).isRemoved()) {
        return Optional.of(pos);
      }
      // You cannot move directly to the right, so to retrieve the tile to the
      // right travel down right and back up right
      pos = Board.Direction.DOWNRIGHT.stepFrom(pos);
      pos = Board.Direction.UPRIGHT.stepFrom(pos);
    }
    return Optional.empty();
  }

  /**
   * Return the lexicographically smallest position within the board in
   * `state`, where the tile is unoccupied and not a hole.
   */
  public Position apply(GameState state) {
    Board board = state.getBoardCopy();
    //make penguin tiles unavailable:
    state.getPenguinList().forEach(p -> board.removeTile(p.getPosition()));
    // pos = starts as top left most posn
    Position pos = board.getTopleftPos();
    //scanning the entire within bounds board
    while (board.isPosInbound(pos)) {
      //this next line moves the search one tile to the right at a time
      Optional<Position> curRowRes = getLeftmostNonHolePosInRow(board, pos);
      if (curRowRes.isPresent()) {
        //if the current tile IS present (eg its not a hole and no peng on it)
        return curRowRes.get();
      }
      //if the current tile is NOT present (eg its a hole or peng is on it)
      // then that means that the entire ROW was not available because it
      // could not return any valid tile of that row.
      // you want to go to the next row:
      Position nextRowStart = Board.Direction.DOWNRIGHT.stepFrom(pos);
      Optional<Position> nextRowRes = 
        getLeftmostNonHolePosInRow(board, nextRowStart);
      //Now repeat the same, and because the rows are off set in two
      // different ways you need to do this maneuver twice before you can
      // start at the top of the while loop again.
      if (nextRowRes.isPresent()) {
        return nextRowRes.get();
      }
      pos = Board.Direction.DOWNLEFT.stepFrom(nextRowStart);
    }
    throw new IllegalArgumentException("Can't place penguin anywhere");
  }
}
