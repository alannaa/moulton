package com.cs4500.fish.player;

import com.cs4500.fish.common.*;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

public class AIPlayerTest {

  //Example
  private Player playerD2;
  private Player playerD3;

  @Before
  public void setUp() {
    this.playerD2 = new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(2));
    this.playerD3 = new AIPlayer( new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(3));
  }

  //Methods that do not require anything in return:
  @Test
  public void testInformMethods() {
    assertTrue(this.playerD2.assignColor(PlayerColor.WHITE));
    assertTrue(this.playerD2.informOpponentColors(
        Arrays.asList(PlayerColor.values()).subList(0,3)));
    assertTrue(this.playerD2.informTournamentStatus(true));
    assertTrue(this.playerD2.informTournamentStatus(false));
    assertTrue(this.playerD2.informGameResult(true));
    assertTrue(this.playerD2.informGameResult(false));
    assertTrue(this.playerD2.disqualifyPlayer("you cheated"));
  }

  @Test
  public void testRequestPlacePenguin() throws DeserializationException {
    /* <0,0>   <hol>   <hol>
     *     <hol>   <1,1>   <1,2>
     */
    Position redPos = new Position(0, 0);
    Position whitepos = new Position(1, 1);
    Position hole1 = new Position(0,1);
    Position hole2 = new Position(0, 2);
    Position hole3 = new Position(1, 0);

    List<Position> holes = new ArrayList<>();
    holes.add(hole1);
    holes.add(hole2);
    holes.add(hole3);

    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(2).setOneFishTileMin(0).setDefaultFish(2)
            .setHoles(holes);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors);

    playerD2.assignColor(PlayerColor.RED);
    try {
      assertEquals(redPos, playerD2.requestPenguinPlacement(state));
    } catch (PlayerCommunicationException e) {
      fail();
    }
    state = state.placePenguin(PlayerColor.RED, redPos).advancePlayer();
    playerD2.assignColor(PlayerColor.WHITE);
    try {
      assertEquals(whitepos, playerD2.requestPenguinPlacement(state));
    } catch (PlayerCommunicationException e) {
      fail();
    }
    state = state.placePenguin(PlayerColor.WHITE, new Position(1,1));
    state = state.placePenguin(PlayerColor.WHITE, new Position(1,2));

    try {
      playerD2.requestPenguinPlacement(state);
      fail();
    } catch (PlayerCommunicationException e) {
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Can't place penguin anywhere", e.getMessage());
    }
  }

  @Test
  public void testRequestActionAvoidTrap() throws DeserializationException {
    /* <0, 0>   <0,1>   <0,2>
     *     <red>   <1,1>   <1,2>
     */
    Position redPos = new Position(1, 0);
    Position target = new Position(0, 1);

    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(2).setOneFishTileMin(0).setDefaultFish(2);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    GameState state = new GameState(board, colors);

    playerD2.assignColor(PlayerColor.RED);

    state = state.placePenguin(PlayerColor.RED, redPos);
    Move move = new Move(redPos, target);


    try {
      assertEquals(move, playerD2.requestAction(new GameTree(state)));
    } catch (PlayerCommunicationException e) {
      fail();
    }
  }

  @Test
  public void testRequestActionTrapped() throws DeserializationException {
    /* <hol>   <whi>   <0,2>
     *     <red>   <1,1>   <1,2>
     */
    Position redPos = new Position(1, 0);
    Position whiPos = new Position(0, 1);
    Position hole1 = new Position(0,0);

    List<Position> holes = new ArrayList<>();
    holes.add(hole1);

    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(2).setOneFishTileMin(0).setDefaultFish(2)
      .setHoles(holes);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors);

    playerD2.assignColor(PlayerColor.RED);

    state = state.placePenguin(PlayerColor.RED, redPos)
            .placePenguin(PlayerColor.WHITE, whiPos);
    try {
      assertEquals(Skip.getInstance(), playerD2.requestAction(new GameTree(state)));
    } catch (PlayerCommunicationException e) {
      fail();
    }
  }

  @Test
  public void testRequestActionTieBreakWithMultiplayer() throws DeserializationException {
    /* <hol>   <rta>   <0,2>
     *     <red>   <wta>   <1,2>
     * <2,0>   <2,1>   <whi>
     * The purpose of this test is to show that firstly, red will choose
     * the tiebreaker move first, which is red to rta, and then secondly to show
     * that white's move after that will be from whi to wta. This is important
     * because with a search depth of 2, white will move to 0,2 instead because
     * of the tiebreaker functionality. Adding that additional search depth
     * allows white to see that wta the move that would increase it's score
     * the most.
     *
     */
    Position redPos = new Position(1, 0);
    Position whiPos = new Position(2, 2);
    Position hole1 = new Position(0,0);
    Position redTarg = new Position(0,1);
    Position whiTarg1 = new Position(0, 2);
    Position whiTarg2 = new Position(1,1);

    List<Position> holes = new ArrayList<>();
    holes.add(hole1);

    BoardConfig conf = new BoardConfig();
    conf.setWidth(3).setHeight(3).setOneFishTileMin(0).setDefaultFish(2)
            .setHoles(holes);
    Board board = new Board(conf);
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.WHITE);
    GameState state = new GameState(board, colors);

    state = state.placePenguin(PlayerColor.RED, redPos)
            .placePenguin(PlayerColor.WHITE, whiPos);
    Action redMove = new Move(redPos, redTarg);
    Action whiMove1 = new Move(whiPos, whiTarg1);
    Action whiMove2 = new Move(whiPos, whiTarg2);

    playerD3.assignColor(PlayerColor.RED);
    try {
      assertEquals(redMove, playerD3.requestAction(new GameTree(state)));
    } catch (PlayerCommunicationException e) {
      fail();
    }
    state = state.movePenguin(redPos, redTarg);
    // Search with 2
    playerD2.assignColor(PlayerColor.WHITE);
    try {
      assertEquals(whiMove1,
          playerD2.requestAction(new GameTree(state.advancePlayer())));
    } catch (PlayerCommunicationException e) {
      fail();
    }
    // Search with 3
    playerD3.assignColor(PlayerColor.WHITE);
    try {
      assertEquals(whiMove2,
          playerD3.requestAction(new GameTree(state.advancePlayer())));
    } catch (PlayerCommunicationException e) {
      fail();
    }
  }
}
