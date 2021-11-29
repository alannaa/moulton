package com.cs4500.fish.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PenguinTest {

  @Test
  public void testPenguinConstruction() {
    Penguin p = new Penguin(PlayerColor.BROWN, new Position(2, 3));
    assertEquals(new Position(2, 3), p.getPosition());
    assertEquals(PlayerColor.BROWN, p.getPlayerColor());
  }

  @Test
  public void testPenguinSetPosition() {
    Penguin p00 = new Penguin(PlayerColor.BLACK, new Position(0, 0));

    // update methods should not modify existing penguin
    Penguin p23 = p00.setPosition(new Position(2, 3));
    assertEquals(PlayerColor.BLACK, p00.getPlayerColor());
    assertEquals(new Position(0, 0), p00.getPosition());

    assertEquals(PlayerColor.BLACK, p23.getPlayerColor());
    assertEquals(new Position(2, 3), p23.getPosition());
  }
}
