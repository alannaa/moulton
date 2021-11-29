package com.cs4500.fish.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerStateTest {

  @Test
  public void testPlayerStateConstruction() {
    PlayerState p = new PlayerState(PlayerColor.BROWN);
    assertEquals(0, p.getScore());
    assertEquals(PlayerColor.BROWN, p.getPlayerColor());
  }

  @Test
  public void testPlayerStateConstructionWithAge() {
    PlayerState p = new PlayerState(PlayerColor.BROWN, 2);
    assertEquals(PlayerColor.BROWN, p.getPlayerColor());
    assertEquals(0, p.getScore());
    assertEquals(2, p.getAge());
  }

  @Test
  public void testPlayerStateScoreGetterSetter() {
    PlayerState p0 = new PlayerState(PlayerColor.BLACK);
    assertEquals(0, p0.getScore());

    // update methods should not modify existing player state
    PlayerState p5 = p0.addScore(5);
    assertEquals(0, p0.getScore());
    assertEquals(5, p5.getScore());
    PlayerState p12 = p5.addScore(7);
    assertEquals(0, p0.getScore());
    assertEquals(5, p5.getScore());
    assertEquals(12, p12.getScore());
  }

  @Test
  public void testPlayerStateScoreGetterSetterWithAge() {
    PlayerState p0 = new PlayerState(PlayerColor.BLACK, 2);
    assertEquals(0, p0.getScore());
    assertEquals(2, p0.getAge());

    // update methods should not modify existing player state
    // but do make sure the age carries over correctly
    PlayerState p5 = p0.addScore(5);
    assertEquals(0, p0.getScore());
    assertEquals(2, p0.getAge());
    assertEquals(5, p5.getScore());
    assertEquals(2, p5.getAge());
    PlayerState p12 = p5.addScore(7);
    assertEquals(0, p0.getScore());
    assertEquals(5, p5.getScore());
    assertEquals(12, p12.getScore());
    assertEquals(2, p0.getAge());
    assertEquals(2, p5.getAge());
    assertEquals(2, p12.getAge());
  }
}
