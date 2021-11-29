package com.cs4500.fish.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class TileTest {

  @Test
  public void testTileConstruction() {
    Tile t = new Tile(42);
    assertEquals(42, t.getNumFish());
    assertFalse(t.isRemoved());

    t = new Tile(0);
    assertEquals(0, t.getNumFish());
    assertTrue(t.isRemoved());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegFishConstructor() {
    Tile t = new Tile(-42);
  }

  @Test
  public void testRemove() {
    Tile t = new Tile(42);
    Tile removed = t.remove();
    // should be no side effect on `t`
    assertEquals(42, t.getNumFish());
    assertFalse(t.isRemoved());

    assertEquals(0, removed.getNumFish());
    assertTrue(removed.isRemoved());
  }
}
