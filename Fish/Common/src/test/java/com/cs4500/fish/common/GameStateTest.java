package com.cs4500.fish.common;

import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameStateTest {

  // Check that `r.run()` results in `IllegalArgumentException` with `msg` as
  // message.
  private void checkIllegalArgExn(Runnable r, String msg) {
    try {
      r.run();
    } catch (IllegalArgumentException e) {
      assertEquals(msg, e.getMessage());
      return;
    }
    fail("Expected IllegalArgumentException[" + msg + "]");
  }


  @Test
  public void testGameStateConstruction() {
    Board board = new Board();
    GameState state = new GameState(board, new ArrayList<>());
    Board copy = state.getBoardCopy();
    for (int row = 0; row < board.getHeight(); row += 1) {
      for (int col = 0; col < board.getWidth(); col += 1) {
        assertEquals(board.getTileAt(row, col), copy.getTileAt(row, col));
      }
    }
  }

  @Test
  public void testGetBoardCopy() {
    Board board = new Board();
    GameState state = new GameState(board, new ArrayList<>());
    Board copy = state.getBoardCopy();
    assertNotSame(copy, board);
  }

  //Test that removing the last player results in an empty playerList
  @Test
  public void testRemovePlayerOnePlayerLeft() {
    GameState twoPlayerGS = new GameState(new Board(),
        Arrays.asList(PlayerColor.values()).subList(0,2));

    assertEquals(2, twoPlayerGS.getOrderedPlayers().size());

    GameState onePlayerGs =
        twoPlayerGS.removePlayerWithColor(PlayerColor.WHITE);

    assertEquals(1, onePlayerGs.getOrderedPlayers().size());

    GameState zeroPlayerGs =
        onePlayerGs.removePlayerWithColor(PlayerColor.WHITE);

    assertEquals(0, zeroPlayerGs.getOrderedPlayers().size());
  }

  //Test that removing a player also removes its penguins
  @Test
  public void testRemovePlayerAndItsPenguins() {
    GameState twoPlayerGS = new GameState(new Board(),
        Arrays.asList(PlayerColor.values()).subList(0,2));

    GameState stateOne = twoPlayerGS.placePenguin(PlayerColor.RED,
        new Position(0,0));
    GameState stateTwo = stateOne.placePenguin(PlayerColor.WHITE,
        new Position(0,1));
    GameState stateThree = stateTwo.placePenguin(PlayerColor.RED,
        new Position(0,2));
    GameState stateFour = stateThree.placePenguin(PlayerColor.WHITE,
        new Position(0,3));

    assertEquals(2, stateFour.getOrderedPlayers().size());
    assertEquals(4, stateFour.getPenguinList().size());
    GameState stateFiveRemoveWhite =
        stateFour.removePlayerWithColor(PlayerColor.WHITE);
    assertEquals(2, stateFiveRemoveWhite.getPenguinList().size());
    assertEquals(1, stateFiveRemoveWhite.getOrderedPlayers().size());
  }

  @Test
  public void testPlacePenguin() {
    Position p1 = new Position(1,1);
    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(3).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    Position holePos = new Position(2, 0);
    board.removeTile(holePos);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.BLACK);
    colors.add(PlayerColor.WHITE);
    GameState s0 = new GameState(board, colors);

    // penguin placement is reflected in the penguinlist from resuling state
    GameState s1 = s0.placePenguin(PlayerColor.BLACK, p1);
    assertEquals(1, s1.getPenguinList().size());
    Penguin peng = s1.getPenguinList().get(0);
    assertEquals(PlayerColor.BLACK, peng.getPlayerColor());

    // no effect on s0
    assertEquals(0, s0.getPenguinList().size());

    checkIllegalArgExn(
        () -> { s1.placePenguin(PlayerColor.WHITE, p1); },
        "Cannot place penguin where another penguin already exists");
    checkIllegalArgExn(
        () -> { s1.placePenguin(PlayerColor.RED, new Position(2, 2)); },
        "Cannot place penguin with unassigned color");
    checkIllegalArgExn(
        () -> { s1.placePenguin(PlayerColor.RED, new Position(5, 5)); },
        "Position (5, 5) is outside board with dimension (3 x 3)");
    checkIllegalArgExn(
        () -> { s1.placePenguin(PlayerColor.RED, holePos); },
        "Cannot place penguin onto a hole");
  }

  @Test
  public void testMovePenguin() {
    // a game with
    // - 5x5 board, 2 fish everywhere, except for a hole and a tile with 3 fish
    // - 2 players (black and white)
    Position p1 = new Position(1,1);
    Position p2 = new Position(2,2);
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    Position holePos = new Position(2, 1);
    board.removeTile(holePos);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.BLACK);
    colors.add(PlayerColor.WHITE);
    GameState s0 = new GameState(board, colors);

    // Checking that placing a penguin updates the board and penguin
    // list accordingly
    GameState s1 =
      s0.placePenguin(PlayerColor.BLACK, p1).movePenguin(p1, p2);
    assertEquals(0, s1.getBoardCopy().getTileAt(p1).getNumFish());
    assertEquals(1, s1.getPenguinList().size());
    Penguin peng = s1.getPenguinList().get(0);
    assertSame(peng.getPlayerColor(), PlayerColor.BLACK);

    // no effect on s0
    assertEquals(2, s0.getOrderedPlayers().size());
    assertEquals(0, s0.getPenguinList().size());

    // player score needs to be updated
    assertEquals(2, s1.getOrderedPlayers().size());
    assertEquals(PlayerColor.BLACK, s1.getOrderedPlayers().get(0).getPlayerColor());
    assertEquals(2, s1.getOrderedPlayers().get(0).getScore());

    // check for error
    final GameState s2 = s0.placePenguin(PlayerColor.WHITE, p1)
                           .placePenguin(PlayerColor.BLACK, p2);

    // out of bound
    checkIllegalArgExn(
        () -> { s2.movePenguin(p1, new Position(10, 10)); },
        "(1, 1) or (10, 10) is not within the bounds of the board (w:5, h:5)");
    // another penguin already exists
    checkIllegalArgExn(
        () -> { s2.movePenguin(p1, p2); },
        "Cannot move penguin to where another penguin already exists");
    // no penguin exists at source
    checkIllegalArgExn(
        () -> { s2.movePenguin(new Position(0, 0), new Position(2, 2)); },
        "No penguin exists at (0, 0).");
    // onto hole
    checkIllegalArgExn(
        () -> { s2.movePenguin(p1, holePos); },
        "Cannot place penguin onto a hole");
  }
}
