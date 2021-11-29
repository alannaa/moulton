package com.cs4500.fish.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.HashSet;

public class BoardConfigTest {

  // Test basic Constructor
  @Test
  public void testConfigConstruction() {
    BoardConfig conf = new BoardConfig();

    assertEquals(conf.WIDTH, conf.getWidth());
    assertEquals(conf.HEIGHT, conf.getHeight());
    assertEquals(conf.DEFAULT_FISH, conf.getDefaultFish());
    assertEquals(conf.ONE_FISH_TILE_MIN, conf.getOneFishTileMin());
    assertEquals(new HashSet<>(), conf.getHoles());
  }

  @Test
  public void testConfigSettersAndGetters() {
    BoardConfig conf = new BoardConfig();
    HashSet<Position> holes = new HashSet<>();
    holes.add(new Position(10, 10));
    holes.add(new Position(5, 5));
    conf.setWidth(5).setHeight(5).setDefaultFish(2).setOneFishTileMin(6).setHoles(holes);

    assertEquals(5, conf.getWidth());
    assertEquals(5, conf.getHeight());
    assertEquals(2, conf.getDefaultFish());
    assertEquals(6, conf.getOneFishTileMin());
    assertEquals(holes, conf.getHoles());

    conf.setWidth(10);
    assertEquals(10, conf.getWidth());
    assertEquals(5, conf.getHeight());
    assertEquals(2, conf.getDefaultFish());
    assertEquals(6, conf.getOneFishTileMin());
    assertEquals(holes, conf.getHoles());
  }

}
