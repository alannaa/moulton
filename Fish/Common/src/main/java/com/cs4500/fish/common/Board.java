package com.cs4500.fish.common;

import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

/**
 * Represents state of a board in a Fish game.
 * The coordinate system for hexagon grid is like this where (row, col):
 * (0, 0)  (0, 1) (0, 2) ...
 *     (1, 0)  (1, 1)    ...
 * (2, 0)  (2, 1) (2, 2) ...
 *     (3, 0)  (3, 1)    ...
 * .....
 */
public class Board {

  /**
   * Utility enum class to facilitate implementation.
   */
  public enum Direction {
    UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT;

    // Return the row offset required for moving in a direction.
    int getOffsetRow() {
      switch (this) {
        case DOWNLEFT:    return 1;
        case DOWNRIGHT:   return 1;
        case DOWN:        return 2;
        case UPLEFT:      return -1;
        case UPRIGHT:     return -1;
        default /* UP */: return -2;
      }
    }

    // Return the row offset required for moving from (row, col) in a direction.
    int getOffsetCol(int row) {
      switch (this) {
        case DOWNLEFT:    return (row % 2 == 0) ? -1 : 0;
        case DOWNRIGHT:   return (row % 2 == 0) ? 0 : 1;
        case DOWN:        return 0;
        case UPLEFT:      return (row % 2 == 0) ? -1 : 0;
        case UPRIGHT:     return (row % 2 == 0) ? 0 : 1;
        default /* UP */: return 0;
      }
    }

    public Position stepFrom(Position pos) {
      int col = pos.getCol();
      int row = pos.getRow();
      return new Position(row + getOffsetRow(),
              col + getOffsetCol(row));
    }
  }

  // This holds all tiles, where each `tiles[row][col]`
  // is the tile located at position (row, col). Refer to class documentation
  // for interpretation of position
  // INVARIANT: width and height are positive
  private Tile[][] tiles;

  /**
   * Construct a board based on the given configuration.
   */
  public Board(BoardConfig conf) {
    if (conf.getHeight() <= 0 || conf.getWidth() <= 0) {
      String msg = "Board width and height must be positive";
      throw new IllegalArgumentException(msg);
    }
    this.tiles = new Tile[conf.getHeight()][conf.getWidth()];
    int oneFishTiles = conf.getOneFishTileMin();
    for (int row = 0; row < conf.getHeight(); row += 1) {
      for (int col = 0; col < conf.getWidth(); col += 1) {
        this.tiles[row][col] = new Tile(conf.getDefaultFish());
        if (conf.getHoles().contains(new Position(row, col))) {
          this.tiles[row][col] = this.tiles[row][col].remove();
        } else if (oneFishTiles > 0) {
          this.tiles[row][col] = new Tile(1);
          oneFishTiles -= 1;
        }
      }
    }
  }

  /**
   * Construct a board with default configuration.
   */
	public Board() {
    this(new BoardConfig());
	}

  public int getHeight() {
    return this.tiles.length;
  }

  public int getWidth() {
    return (this.tiles.length == 0) ? 0 : (this.tiles[0].length);
  }

  public Position getTopleftPos() {
    return new Position(0, 0);
  }

  /**
   * Return the tile located at (row, col).
   * @throws IllegalArgumentException if (row, col) is outside the board
   */
  public Tile getTileAt(int row, int col) {
    enforcePosInBounds(row, col);
    return tiles[row][col];
  }

  // Convenience method for getTileAt
  public Tile getTileAt(Position pos) {
    return this.getTileAt(pos.getRow(), pos.getCol());
  }

  /**
   * Return a map of all the positions reachable if one starts at `pos`
   * via straight lines. Map from Direction to the Positions accessible in that
   * direction, ordered by distance from `pos`. This does not account for
   * penguins being on the board, only for holes.
   */
  public Map<Direction, List<Position>> getReachableFrom(Position pos) {
    int originRow = pos.getRow();
    int originCol = pos.getCol();
    Map<Direction, List<Position>> reachables = new HashMap<>();
    if (! pos.withinBounds(this.getHeight(), this.getWidth())) {
      return reachables;
    }
    // explore as much as possible in each direction
    for (Direction dir : Direction.values()) {
      List<Position> positions = new ArrayList<>();
      // skip the origin position
      int row = originRow + dir.getOffsetRow(); //getOffset provides relative position
      int col = originCol + dir.getOffsetCol(originRow);
      while (this.isPosInbound(row, col) && !this.tiles[row][col].isRemoved()) {
        positions.add(new Position(row, col));
        col += dir.getOffsetCol(row);
        row += dir.getOffsetRow();
      }
      if (! positions.isEmpty()) {
        reachables.put(dir, positions);
      }
    }
    return reachables;
  }

  // Returns a copy of the board
  Board copy() {
    Board copy = new Board();
    copy.tiles = new Tile[this.getHeight()][this.getWidth()];
    for (int row = 0; row < this.getHeight(); row+=1) {
      for (int col = 0; col < this.getWidth(); col+=1) {
        copy.tiles[row][col] = this.tiles[row][col];
      }
    }
    return copy;
  }

  // return whether (row, col) is within the board.
  public boolean isPosInbound(int row, int col) {
    return 0 <= row && row < this.getHeight() &&
           0 <= col && col < this.getWidth();
  }
  public boolean isPosInbound(Position pos) {
    return this.isPosInbound(pos.getRow(), pos.getCol());
  }

  /**
   * Remove the tile at given position.
   * EFFECT: Clear the fish count and occupancy at the tile.
   * @throws IllegalArgumentException if `pos` is outside the board
   */
  public void removeTile(Position pos) {
    int row = pos.getRow();
    int col = pos.getCol();
    enforcePosInBounds(row, col);
    this.tiles[row][col] = this.tiles[row][col].remove();
  }

  // Returns the number of tiles (that are NOT holes) on the board
  public int numNonHoleTilesOnBoard() {
    int nonHoleTiles = 0;
    for (int row = 0; row < this.getHeight(); row += 1) {
      for (int col = 0; col < this.getWidth(); col += 1) {
        if (! this.getTileAt(row, col).isRemoved()) {
          nonHoleTiles += 1;
        }
      }
    }
    return nonHoleTiles;
  }

  // if (row, col) is not within the board, throw IllegalArgumentException.
  private void enforcePosInBounds(int row, int col) {
    int width = this.getWidth();
    int height = this.getHeight();
    if (! isPosInbound(row, col)) {
      String msg = String.format(
          "Position (%d, %d) is outside board with dimension (%d x %d)",
          row, col, height, width);
      throw new IllegalArgumentException(msg);
    }
  }

  // Serialize into and deserialize from json.
  // A Board -> "[[Tile, Tile, ...], ...]"
  // Note that the rows may have different length. The width always equals
  // the row with maximum length, and rows that are shorter are padded with
  // removed tiles.
  public static Board deserialize(JsonElement e) throws DeserializationException {
    if (! e.isJsonArray()) {
      throw new DeserializationException("Board must be a json array");
    }
    JsonArray boardArr = e.getAsJsonArray();
    int height = boardArr.size();
    int width = 0; // width = row with max length
    for (int i = 0; i < height; i += 1) {
      if (! boardArr.get(i).isJsonArray()) {
        String msg = "Board must be a json array of json array";
        throw new DeserializationException(msg);
      }
      JsonArray row = boardArr.get(i).getAsJsonArray();
      width = Integer.max(width, row.size());
    }
    if (height <= 0 || width <= 0) {
      String msg = "Board width and height must be positive";
      throw new DeserializationException(msg);
    }
    BoardConfig config = new BoardConfig().setWidth(width).setHeight(height);
    Board board = new Board(config); // a board with correct dimension
    // deserialize each tile
    for (int row = 0; row < height; row += 1) {
      JsonArray rowArr = boardArr.get(row).getAsJsonArray();
      for (int col = 0; col < width; col += 1) {
          board.tiles[row][col] =
            col >= rowArr.size() ? // pad with empty tile
            new Tile(0) : Tile.deserialize(rowArr.get(col));
      }
    }
    return board;
  }

  public JsonElement serialize() {
    JsonArray board = new JsonArray();
    for (Tile[] tile : this.tiles) {
      JsonArray rowArr = new JsonArray();
      for (int col = 0; col < this.tiles[0].length; col += 1) {
        rowArr.add(tile[col].serialize());
      }
      board.add(rowArr);
    }
    return board;
  }
}
