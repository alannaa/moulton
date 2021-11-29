package com.cs4500.fish.common;

import java.io.IOException;
import java.util.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Utility class that represents a position on a game board.
 * NOTE the class is _immutable_
 */
public class Position {

	private final int row;
	private final int col;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

  // Return whether this position is within the rectangle whose diagonal is
  // (0, 0) and (height - 1, width - 1)
	public boolean withinBounds(int height, int width) {
		return 0 <= row && row < height && 0 <= col && col < width;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return row == position.row &&
						col == position.col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

  @Override
  public String toString() {
    return String.format("(%d, %d)", row, col);
  }

  // Compare this position with `other` lexicographically, i.e., return negative
  // number if `this` is smaller, 0 if they are equal, positive otherwise.
  public int compareTo(Position other) {
    int rowCmp = Integer.compare(this.row, other.row);
		return rowCmp != 0 ? rowCmp : Integer.compare(this.col, other.col);
  }

  // Serialize into and deserialize from json.
  // A position with (row, col) -> "[row, col]"
  public static Position deserialize(JsonElement e)
					throws DeserializationException {
		if (! e.isJsonArray() || e.getAsJsonArray().size() != 2) {
			String msg = "Position should be a Json Array containing two things";
			throw new DeserializationException(msg);
		}
		JsonArray arr = e.getAsJsonArray();

		for (JsonElement j : arr) {
			if (! j.isJsonPrimitive() || ! j.getAsJsonPrimitive().isNumber()) {
				throw new DeserializationException("Expected: Number");
			}
		}
		int row = arr.get(0).getAsInt();
		int col = arr.get(1).getAsInt();
    return new Position(row, col);
  }

  public JsonElement serialize() {
    JsonArray arr = new JsonArray();
    arr.add(this.row);
    arr.add(this.col);
    return arr;
  }
}
