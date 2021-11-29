package com.cs4500.fish.common;

import com.cs4500.fish.common.Board.Direction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.*;

public class BoardTest {

  //Examples
  private Board defaultBoard;
  private Board configBoard;

  @Before
  public void setUp() throws Exception {
    this.defaultBoard = new Board();

    Set<Position> holes = new HashSet<>();
    holes.add(new Position(0,0));
    BoardConfig conf =
        new BoardConfig().setWidth(10).setHeight(8).setDefaultFish(2)
            .setOneFishTileMin(1).setHoles(holes);
    //Will result in an 8x10 board where 0,0 is a hole, 0,1 has 1 fish,
    //and the rest of the tiles on the entire board have 2 fish.
    this.configBoard = new Board(conf);
  }

  ////Tests for Constructor////
  @Test
  public void testConstructor() {
    for (int iRow = 0; iRow < this.defaultBoard.getHeight(); iRow++) {
      for (int iCol = 0; iCol < this.defaultBoard.getWidth(); iCol++) {
        assertEquals(1, this.defaultBoard.getTileAt(iRow, iCol).getNumFish());
      }
    }

    assertTrue(this.configBoard.getTileAt(0,0).isRemoved());
    assertEquals(1, this.configBoard.getTileAt(0,1).getNumFish());

    for (int iRow = 0; iRow < this.configBoard.getHeight(); iRow++) {
      //For simplicity, start iCol at 2 since we know the first two cols of the
      //first row are tested above and do not == 2
      for (int iCol = 2; iCol < this.configBoard.getWidth(); iCol++) {
        assertEquals(2, this.configBoard.getTileAt(iRow, iCol).getNumFish());
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegWidth() {
    BoardConfig badConfig = new BoardConfig().setWidth(-2);
    Board badBoard = new Board(badConfig);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegHeight() {
    BoardConfig badConfig = new BoardConfig().setHeight(-2);
    Board badBoard = new Board(badConfig);
  }

  @Test
  public void testHoleOutOfBounds() {
    Set<Position> holes = new HashSet<>();
    holes.add(new Position(-10,0));
    holes.add(new Position(0,10));

    BoardConfig conf = new BoardConfig().setHoles(holes);
    Board board = new Board(conf);
    //results in a board with NO holes because the bad holes are ignored
    for (int iRow = 0; iRow < this.configBoard.getHeight(); iRow++) {
      for (int iCol = 2; iCol < this.configBoard.getWidth(); iCol++) {
        assertFalse(this.configBoard.getTileAt(iRow, iCol).isRemoved());
      }
    }
  }

  ////Tests for Getting, Removing Tiles, and Counting Holes////

  @Test(expected = IllegalArgumentException.class)
  public void testGetTileOOBNeg() {
    this.defaultBoard.getTileAt(-3,0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetTileOOB() {
    this.defaultBoard.getTileAt(20,20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetTileOOBNegWithPosn() {
    this.defaultBoard.getTileAt(new Position(-3,0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetTileOOBWithPosn () {
    this.defaultBoard.getTileAt(new Position(20,20));
  }

  @Test
  public void testRemoveTile() {
    assertFalse(this.defaultBoard.getTileAt(1, 1).isRemoved());
    assertFalse(this.defaultBoard.getTileAt(new Position(1, 1)).isRemoved());
    this.defaultBoard.removeTile(new Position(1, 1));
    assertTrue(this.defaultBoard.getTileAt(1, 1).isRemoved());
    assertTrue(this.defaultBoard.getTileAt(new Position(1, 1)).isRemoved());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveTileOOBNeg() {
    this.defaultBoard.removeTile(new Position(-3,0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveTileOOB () {
    this.defaultBoard.removeTile(new Position(20,20));
  }

  @Test
  public void testNumNonHoleTilesOnBoard() {
    assertEquals(25, this.defaultBoard.numNonHoleTilesOnBoard());
    assertEquals(79, this.configBoard.numNonHoleTilesOnBoard());
  }


  //Test for accurate and defensive copies//
  @Test
  public void testCopyAccurate() {
    BoardConfig complexConfig =
        new BoardConfig()
            .setWidth(1).setHeight(10)
            .setDefaultFish(new Random().nextInt(10))
            .setOneFishTileMin(5);

    Board complexBoard = new Board(complexConfig);
    Board complexCopy = complexBoard.copy();

    for (int iRow = 0; iRow < this.configBoard.getHeight(); iRow++) {
      assertEquals(complexBoard.getTileAt(iRow,0).getNumFish(),
          complexCopy.getTileAt(iRow,0).getNumFish());
    }
  }

  @Test
  public void testCopyExclusiveMutation() {
    //Default board constructed in the @before setup method above
    Board defaultBoardCopy = this.defaultBoard.copy();

    assertFalse(this.defaultBoard.getTileAt(1, 1).isRemoved());
    assertFalse(defaultBoardCopy.getTileAt(1, 1).isRemoved());

    this.defaultBoard.removeTile(new Position(1, 1));

    // mutating `defaultBoard` does not affect its copy
    assertFalse(defaultBoardCopy.getTileAt(1, 1).isRemoved());
    assertTrue(this.defaultBoard.getTileAt(1, 1).isRemoved());

    //and vice versa
    defaultBoardCopy.removeTile(new Position(3, 3));
    assertFalse(this.defaultBoard.getTileAt(3, 3).isRemoved());
    assertTrue(defaultBoardCopy.getTileAt(3, 3).isRemoved());

  }

  //Testing for getTilesReachableFrom
  //NOTE The method in the board class does NOT account for penguins in the way
  // Only a hole stops a tile from being reachable from the given posn

  /* <0,0>  <0,1>  <0,2>  <0,3>  <0,4>
   *    <1,0>  <1,1>  <1,2>  <1,3>  <1,4>
   * <2,0>  <2,1>  <2,2>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <3,2>  <3,3>  <3,4>
   * <4,0>  <4,1>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testGetReachableFrom() {
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setHoles(new HashSet<>()).setDefaultFish(3);
    Board board = new Board(conf);
    Map<Board.Direction, List<Position>> reachable = new HashMap<>();
    reachable.put(Board.Direction.UP, new ArrayList<>());
    reachable.put(Board.Direction.DOWNLEFT, new ArrayList<>());
    reachable.put(Board.Direction.UPRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.DOWN, new ArrayList<>());
    reachable.put(Board.Direction.UPLEFT, new ArrayList<>());
    reachable.put(Board.Direction.DOWNRIGHT, new ArrayList<>());

    reachable.get(Board.Direction.UP).add(new Position(0, 2));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(1, 2));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(0, 3));
    reachable.get(Board.Direction.DOWN).add(new Position(4, 2));
    reachable.get(Board.Direction.DOWNLEFT).add(new Position(3, 1));
    reachable.get(Board.Direction.DOWNLEFT).add(new Position(4, 1));
    reachable.get(Board.Direction.UPLEFT).add(new Position(1, 1));
    reachable.get(Board.Direction.UPLEFT).add(new Position(0, 1));
    reachable.get(Board.Direction.DOWNRIGHT).add(new Position(3, 2));
    reachable.get(Board.Direction.DOWNRIGHT).add(new Position(4, 3));
    assertEquals(reachable, board.getReachableFrom(new Position(2, 2)));
  }

  /* `Rem` stands for "removed"
   * <0,0>  <0,1>  <0,2>  <0,3>  <0,4>
   *    <1,0>  <Rem>  <1,2>  <1,3>  <1,4>
   * <2,0>  <2,1>  <2,2>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <Rem>  <3,3>  <3,4>
   * <4,0>  <Rem>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testGetReachableFromWithHoles() {
      BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(1);
    ArrayList<Position> holes = new ArrayList<>();
    holes.add(new Position(1, 1));
    holes.add(new Position(3, 2));
    holes.add(new Position(4, 1));
    conf.setHoles(holes);
    Board board = new Board(conf);

    Map<Board.Direction, List<Position>> reachable = new HashMap<>();
    reachable.put(Board.Direction.UP, new ArrayList<>());
    reachable.put(Board.Direction.DOWNLEFT, new ArrayList<>());
    reachable.put(Board.Direction.UPRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.DOWN, new ArrayList<>());

    reachable.get(Board.Direction.UP).add(new Position(0, 2));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(1, 2));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(0, 3));
    reachable.get(Board.Direction.DOWN).add(new Position(4, 2));
    reachable.get(Board.Direction.DOWNLEFT).add(new Position(3, 1));
    assertEquals(reachable, board.getReachableFrom(new Position( 2, 2)));
  }

  /* `Rem` stands for "removed"
   * <0,0>  <0,1>  <0,2>  <0,3>  <0,4>
   *    <1,0>  <Rem>  <1,2>  <1,3>  <1,4>
   * <2,0>  <2,1>  <2,2>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <Rem>  <3,3>  <3,4>
   * <4,0>  <Rem>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testGetReachableFromEdgeTile() {
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(1);
    ArrayList<Position> holes = new ArrayList<>();
    holes.add(new Position(1, 1));
    holes.add(new Position(3, 2));
    holes.add(new Position(4, 1));
    conf.setHoles(holes);
    Board board = new Board(conf);

    Map<Board.Direction, List<Position>> reachable = new HashMap<>();
    reachable.put(Board.Direction.UP, new ArrayList<>());
    reachable.put(Board.Direction.DOWNRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.UPRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.DOWN, new ArrayList<>());

    reachable.get(Board.Direction.UP).add(new Position(0, 0));
    reachable.get(Board.Direction.DOWN).add(new Position(4, 0));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(1, 0));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(0, 1));
    reachable.get(Board.Direction.DOWNRIGHT).add(new Position(3, 0));
    assertEquals(reachable, board.getReachableFrom(new Position( 2, 0)));
  }

  /* `Rem` stands for "removed"
   * <0,0>  <0,1>  <0,2>  <0,3>  <0,4>
   *    <1,0>  <Rem>  <1,2>  <1,3>  <1,4>
   * <2,0>  <2,1>  <2,2>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <Rem>  <3,3>  <3,4>
   * <4,0>  <Rem>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testGetReachableFromChangesAfterRemovingATile() {
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(1);
    ArrayList<Position> holes = new ArrayList<>();
    holes.add(new Position(1, 1));
    holes.add(new Position(3, 2));
    holes.add(new Position(4, 1));
    conf.setHoles(holes);
    Board board = new Board(conf);

    Map<Board.Direction, List<Position>> reachable = new HashMap<>();
    reachable.put(Board.Direction.UP, new ArrayList<>());
    reachable.put(Board.Direction.DOWNRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.UPRIGHT, new ArrayList<>());
    reachable.put(Board.Direction.DOWN, new ArrayList<>());

    reachable.get(Board.Direction.UP).add(new Position(0, 0));
    reachable.get(Board.Direction.DOWN).add(new Position(4, 0));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(1, 0));
    reachable.get(Board.Direction.UPRIGHT).add(new Position(0, 1));
    reachable.get(Board.Direction.DOWNRIGHT).add(new Position(3, 0));
    assertEquals(reachable, board.getReachableFrom(new Position( 2, 0)));
    //remove a tile and check the second time it's called it returns the
    // adjusted hashmap
    board.removeTile(new Position(1,0));
    reachable.remove(Direction.UPRIGHT);
    assertEquals(reachable, board.getReachableFrom(new Position( 2, 0)));
  }

  /* `Rem` stands for "removed"
   * <0,0>  <Rem>  <0,2>  <0,3>  <0,4>
   *    <Rem>  <1,1>  <1,2>  <1,3>  <1,4>
   * <Rem>  <2,1>  <2,2>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <3,2>  <3,3>  <3,4>
   * <4,0>  <4,1>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testGetReachableFromNoReachableTiles() {
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(1);
    ArrayList<Position> holes = new ArrayList<>();
    holes.add(new Position(0, 1));
    holes.add(new Position(1, 0));
    holes.add(new Position(2, 0));
    conf.setHoles(holes);
    Board board = new Board(conf);

    Map<Board.Direction, List<Position>> reachable = new HashMap<>();
    assertEquals(reachable, board.getReachableFrom(new Position( 0, 0)));
  }
}
