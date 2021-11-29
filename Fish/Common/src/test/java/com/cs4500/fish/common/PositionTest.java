package com.cs4500.fish.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.util.Set;
import java.util.HashSet;

public class PositionTest {

  @Test
  public void testConstruction() {
    Position pos = new Position(3, 5);
    assertEquals(3, pos.getRow());
    assertEquals(5, pos.getCol());

    pos = new Position(4, 2);
    assertEquals(4, pos.getRow());
    assertEquals(2, pos.getCol());
  }

  @Test
  public void testWithinBounds() {
    Position p35 = new Position(3, 5);
    assertFalse(p35.withinBounds(0, 0));
    assertFalse(p35.withinBounds(3, 5));
    assertFalse(p35.withinBounds(4, 5));
    assertFalse(p35.withinBounds(3, 6));
    assertTrue(p35.withinBounds(4, 6));
  }

  ////Equals and Hash

  @Test
  public void testEquality() {
    Position p15 = new Position(1, 5);
    Position p13 = new Position(1, 3);
    Position p35 = new Position(3, 5);
    Position p15dup = new Position(1, 5);
    assertNotEquals(p15, p13);
    assertNotEquals(p15, p35);
    assertNotEquals(p13, p35);
    assertEquals(p15, p15);
    assertEquals(p13, p13);
    assertEquals(p35, p35);
    assertEquals(p15, p15dup);
  }

  @Test
  public void testHashCode() {
    Position p15 = new Position(1, 5);
    Position p13 = new Position(1, 3);
    Position p35 = new Position(3, 5);
    Set<Position> set1 = new HashSet<>();
    set1.add(p15);
    set1.add(p13);
    set1.add(p35);
    Set<Position> set2 = new HashSet<>();
    set2.add(p15);
    set2.add(p13);
    set2.add(p35);
    set2.add(p15);
    set2.add(p13);
    set2.add(p35);
    assertEquals(set1, set2);
  }

  ////Deserialize and Serialize
  @Test
  public void testDeserialize() throws DeserializationException {
    JsonElement input = new JsonArray();
    JsonArray inputArr = input.getAsJsonArray();
    inputArr.add(1);
    inputArr.add(2);

    assertEquals(new Position(1,2), Position.deserialize(input));
  }

  @Test(expected = DeserializationException.class)
  public void testTooLongInputDeserializationError() throws DeserializationException {
    JsonElement input = new JsonArray();
    JsonArray tooLongInput = input.getAsJsonArray();
    tooLongInput.add(1);
    tooLongInput.add(2);
    tooLongInput.add(3);

    Position.deserialize(input);
  }

  @Test(expected = DeserializationException.class)
  public void testWrongPrimInputDeserializationError() throws DeserializationException {
    JsonElement input = new JsonArray();
    JsonArray wrongPrimitiveInput = input.getAsJsonArray();
    wrongPrimitiveInput.add("one");
    wrongPrimitiveInput.add("two");

    Position.deserialize(input);
  }

  @Test
  public void testSerialize() {
    Position p = new Position(1,2);
    JsonElement output = new JsonArray();
    JsonArray outputArr = output.getAsJsonArray();
    outputArr.add(1);
    outputArr.add(2);

    assertEquals(output, p.serialize());
  }
}
