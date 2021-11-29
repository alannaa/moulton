package com.cs4500.fish.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.util.Set;
import java.util.HashSet;

public class MoveTest {

  @Test
  public void testConstruction() {
    Position p00 = new Position(0, 0);
    Position p20 = new Position(2, 0);
    Move move = new Move(p00, p20);
    assertEquals(p00, move.getFrom());
    assertEquals(p20, move.getTo());
  }

  @Test
  public void testEquality() {
    Position p00 = new Position(0, 0);
    Position p13 = new Position(1, 3);
    Position p35 = new Position(3, 5);
    assertNotEquals(new Move(p00, p13), new Move(p00, p35));
    assertNotEquals(new Move(p00, p13), new Move(p35, p13));
    assertEquals(new Move(p00, p13), new Move(p00, p13));
    assertEquals(new Move(p13, p13), new Move(p13, p13));
  }

  @Test
  public void testHashCode() {
    Position p15 = new Position(1, 5);
    Position p13 = new Position(1, 3);
    Position p35 = new Position(3, 5);
    Set<Move> set1 = new HashSet<>();
    set1.add(new Move(p15, p15));
    set1.add(new Move(p13, p15));
    set1.add(new Move(p15, p35));
    Set<Move> set2 = new HashSet<>();
    set2.add(new Move(p13, p15));
    set2.add(new Move(p15, p15));
    set2.add(new Move(p13, p15));
    set2.add(new Move(p15, p35));
    set2.add(new Move(p15, p15));
    set2.add(new Move(p15, p35));
    assertEquals(set1, set2);
  }
}
