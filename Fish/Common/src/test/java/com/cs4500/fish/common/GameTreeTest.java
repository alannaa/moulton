package com.cs4500.fish.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameTreeTest {

  @Test
  public void testConstruction() {
    // 3 x 3 board, 2 fish on all tiles
    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(3).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.BLACK);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors);
    GameTree tree = new GameTree(state);
    assertEquals(state, tree.getState());
    assertEquals(PlayerColor.BLACK, tree.getState().getCurrentPlayerColor());
  }

  @Test
  public void testMapNextStates() {
    /* <0<0>   <0,1>   <0,2>   <0,3>   <0,4>
     *     <1,0>   <1,1>   <1,2>   <1,3>   <1,4>
     * <2,0>   <2,1>   <red>   <2,3>   <2,4>
     *     <3,0>   <3,1>   <3,2>   <3,3>   <3,4>
     * <4,0>   <4,1>   <4,2>   <4,3>   <4,4>
     */
    BoardConfig conf = new BoardConfig();
    conf.setWidth(5).setHeight(5).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors)
                          .placePenguin(PlayerColor.RED, new Position(2, 2));

    GameTree tree = new GameTree(state);
    int penguinSumInSubtrees =
      tree.mapSubTrees(s -> s.getState().getPenguinList().size())
      .stream().reduce(0, Integer::sum);
    assertEquals(10, penguinSumInSubtrees); // 10 next states, each with 1 penguin

    tree = new GameTree(state.advancePlayer()); // only skip action is legal
    penguinSumInSubtrees =
      tree.mapSubTrees(s -> s.getState().getPenguinList().size())
      .stream().reduce(0, Integer::sum);
    assertEquals(1, penguinSumInSubtrees);
  }

  @Test
  public void testAttemptMove() {
    /* <0<0>   <0,1>   <0,2>
     *     <1,0>   <whi>   <hol>
     * <2,0>   <2,1>   <red>
     */
    Position holePos = new Position(1,2);
    Position redPos = new Position(2, 2);
    Position pos02 = new Position(0, 2);
    Position whitepos = new Position(1,1);

    BoardConfig conf = new BoardConfig();
    ArrayList<Position> holes = new ArrayList<>();
    holes.add(holePos);
    conf.setWidth(3).setHeight(3).setOneFishTileMin(0).setDefaultFish(2)
            .setHoles(holes);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors)
            .placePenguin(PlayerColor.RED, new Position(2, 2))
            .placePenguin(PlayerColor.WHITE, new Position(1, 1));

    Move moveValid = new Move(redPos, pos02);
    Move moveHolePos = new Move(redPos, holePos);
    Move moveWhitePos = new Move(redPos, whitepos);

    GameTree tree = new GameTree(state);
    Optional<GameTree> treeOpt = tree.attemptAction(moveValid);
    assertTrue(treeOpt.isPresent());
    GameTree newTree = treeOpt.get();

    // Test that current player is now white
    assertEquals(PlayerColor.WHITE,
        newTree.getState().getCurrentPlayerColor());

    // Test that the number of penguins and players is the same
    assertEquals(2, newTree.getState().getPenguinList().size());
    assertEquals(2, newTree.getState().getOrderedPlayers().size());

    // Test that all penguins are in the correct place
    for (Penguin p : newTree.getState().getPenguinList()) {
      if (p.getPlayerColor().equals(PlayerColor.RED)) {
        assertEquals(pos02, p.getPosition());
      }
      else {
        assertEquals(whitepos, p.getPosition());
      }
    }

    // Test that the player's scores increased
    assertEquals(2,
        newTree.getState().getPlayerStateWithColor(PlayerColor.RED).getScore());
    assertEquals(0,
        newTree.getState().getPlayerStateWithColor(PlayerColor.WHITE).getScore());

    // Test invalid move
    assertEquals(Optional.empty(), tree.attemptAction(moveHolePos));
    assertEquals(Optional.empty(), tree.attemptAction(moveWhitePos));
  }

  @Test
  public void testGetNextPossibleMoves() {
    /* <0<0>   <0,1>   <0,2>
     *     <1,0>   <whi>   <1,2>
     * <2,0>   <2,1>   <red>
     */
    Position redPos = new Position(2, 2);
    Position pos02 = new Position(0, 2);
    Position whitepos = new Position(1,1);
    Position pos12 = new Position(1, 2);

    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(3).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors)
            .placePenguin(PlayerColor.RED, new Position(2, 2))
            .placePenguin(PlayerColor.WHITE, new Position(1, 1));

    Move moveValid = new Move(redPos, pos02);
    Move moveValid2 = new Move(redPos, pos12);

    GameTree tree = new GameTree(state);
    Map<Action, GameTree> gTMap = tree.getSubtrees();

    //Test that all of the moves exist in the map
    assertTrue(gTMap.containsKey(moveValid));
    assertTrue(gTMap.containsKey(moveValid2));
    assertEquals(2, gTMap.size());

    GameTree newTree = gTMap.get(moveValid);

    // Test that current player is now white
    assertEquals(PlayerColor.WHITE,
        newTree.getState().getCurrentPlayerColor());

    // Test that the number of penguins and players is the same
    assertEquals(2, newTree.getState().getPenguinList().size());
    assertEquals(2, newTree.getState().getOrderedPlayers().size());

    // Test that all penguins are in the correct place
    for (Penguin p : newTree.getState().getPenguinList()) {
      if (p.getPlayerColor().equals(PlayerColor.RED)) {
        assertEquals(pos02, p.getPosition());
      }
      else {
        assertEquals(whitepos, p.getPosition());
      }
    }

    // Test that the player's scores increased
    for (PlayerState s : newTree.getState().getOrderedPlayers()) {
      if (s.getPlayerColor().equals(PlayerColor.RED)) {
        assertEquals(2, s.getScore());
      } else if (s.getPlayerColor().equals(PlayerColor.WHITE)){
        assertEquals(0, s.getScore());

      }
    }

    GameTree newTree2 = gTMap.get(moveValid2);

    // Test that current player is now white
    assertEquals(PlayerColor.WHITE,
        newTree2.getState().getCurrentPlayerColor());

    // Test that the number of penguins and players is the same
    assertEquals(2, newTree2.getState().getPenguinList().size());
    assertEquals(2, newTree2.getState().getOrderedPlayers().size());

    // Test that all penguins are in the correct place
    for (Penguin p : newTree2.getState().getPenguinList()) {
      if (p.getPlayerColor().equals(PlayerColor.RED)) {
        assertEquals(pos12, p.getPosition());
      }
      else {
        assertEquals(whitepos, p.getPosition());
      }
    }

    // Test that the player's scores increased
    assertEquals(2,
        newTree.getState().getPlayerStateWithColor(PlayerColor.RED).getScore());
    assertEquals(0,
        newTree.getState().getPlayerStateWithColor(PlayerColor.WHITE).getScore());
  }
}
