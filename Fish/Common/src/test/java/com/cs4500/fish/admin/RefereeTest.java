package com.cs4500.fish.admin;

import com.cs4500.fish.common.*;
import com.cs4500.fish.player.*;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RefereeTest {

  // Check that `r.run()` results in `IllegalArgumentException`
  // with `msg` as message.
  private void checkIllegalArgExn(Runnable r, String msg) {
    try {
      r.run();
    } catch (IllegalArgumentException e) {
      assertEquals(msg, e.getMessage());
      return;
    }
    fail("Expected IllegalArgumentException[" + msg + "]");
  }

  //Examples
  private Referee twoPlGame;
  private Referee threePlGame;

  private BoardConfig defaultConf;
  private Player player;
  private List<Player> initPlayers2;
  private List<Player> initPlayers3;


  @Before
  public void setUp() {
    defaultConf = new BoardConfig().setDefaultFish(2);
    player = new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1));

    initPlayers2 = new ArrayList<>();
    initPlayers2.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(2)));
    initPlayers2.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(2)));

    initPlayers3 = new ArrayList<>();
    initPlayers3.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));
    initPlayers3.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));
    initPlayers3.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));

    this.twoPlGame = new Referee();
    this.threePlGame = new Referee();
  }


  @Test
  public void testRunGameInvalidInputs() {
    List<Player> list = new ArrayList<>();
    list.add(player);
    BoardConfig conf = new BoardConfig();
    Referee ref = new Referee();

    //one player:
    checkIllegalArgExn(() -> ref.runGame(conf, list),
            "Invalid number of players");

    list.add(player);
    list.add(player);
    list.add(player);
    list.add(player);

    //five players:
    checkIllegalArgExn(() -> ref.runGame(conf, list),
            "Invalid number of players");

    //A correct number of players (4) but insufficient tiles to play game:
    list.remove(list.size() - 1);
    conf.setWidth(2).setHeight(2);
    checkIllegalArgExn(() -> ref.runGame(conf, list),
            "Insufficient non-hole tiles to place penguins");
  }

  @Test
  public void testSetUpGame() {
    GameState state = twoPlGame.setupGame(defaultConf, initPlayers2);
    assertEquals(2, state.getOrderedPlayers().size());
  }

  @Test
  public void testAssignColors() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(player);
    playerList.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));

    GameState state = twoPlGame.setupGame(defaultConf,
        playerList);
    //test with our good implementation of AI player (no timeouts)
    GameState stateAfterAssignCols = twoPlGame.assignPlayerColors(state);
    assertEquals(2, stateAfterAssignCols.getOrderedPlayers().size());
  }

  @Test
  public void testAssignColorsPlayerTimeout() {
    List<Player> timeoutList = new ArrayList<>();
    timeoutList.add(player);
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    GameState state = twoPlGame.setupGame(defaultConf, timeoutList);
    state = twoPlGame.assignPlayerColors(state);
    //Check that the state has been updated:
    assertEquals(1, state.getOrderedPlayers().size());
  }

  @Test
  public void testAssignColorsTwoPlayerTimeouts() {
    List<Player> timeoutList = new ArrayList<>();
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    GameState state = twoPlGame.setupGame(defaultConf, timeoutList);
    state = twoPlGame.assignPlayerColors(state);
    //Check that the state has been updated:
    assertEquals(0, state.getOrderedPlayers().size());
  }

  @Test
  public void testInformOpponentColors() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(player);
    playerList.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));

    GameState state = twoPlGame.setupGame(defaultConf, playerList);
    //test with our good implementation of AI player (no timeouts)
    GameState stateAfterAssignCols = twoPlGame.informPlayerOpponentColors(state);
    assertEquals(2, stateAfterAssignCols.getOrderedPlayers().size());
  }

  @Test
  public void testInformOpponentColorsPlayerTimeout() {
    List<Player> timeoutList = new ArrayList<>();
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    timeoutList.add(player);
    GameState state = twoPlGame.setupGame(defaultConf, timeoutList);
    state = twoPlGame.informPlayerOpponentColors(state);
    //Check that the state has been updated:
    assertEquals(1, state.getOrderedPlayers().size());
  }

  @Test
  public void testInformOpponentColorsTwoPlayerTimeouts() {
    List<Player> timeoutList = new ArrayList<>();
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    GameState state = twoPlGame.setupGame(defaultConf, timeoutList);
    state = twoPlGame.informPlayerOpponentColors(state);
    //Check that the state has been updated:
    assertEquals(0, state.getOrderedPlayers().size());
  }

  //Knowing the individual set up and informing methods work, make sure that
  //runGame will stop the game should all players fail during the beginning
  //set up of the game:

  @Test
  public void testAllPlayersFailDuringSetUp() {
    List<Player> timeoutList = new ArrayList<>();
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    timeoutList.add(new PlayerImplementsInfiniteLoop());
    GameResult gr = twoPlGame.runGame(defaultConf, timeoutList);
    assertEquals(2, gr.getFailedPlayers().size());
    assertEquals(0, gr.getCheaters().size());
    assertEquals(0, gr.getWinners().size());
    assertEquals(0, gr.getOthers().size());
  }

  /////TESTING PLACING PENGUINS PHASE/////
  @Test
  public void testPlaceCurrentPlayerPenguin(){
    List<Player> playerList = new ArrayList<>();
    playerList.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));
    playerList.add(new AIPlayer(new ScanPlacementStrategy(),
        new MinimaxTurnActionStrategy(1)));

    GameState state = twoPlGame.setupGame(defaultConf, playerList);
    state = twoPlGame.assignPlayerColors(state);
    state = twoPlGame.informPlayerOpponentColors(state);
    assertTrue(twoPlGame.anyPlayerHasPenguinToPlace(state, 4));

    // the red player places one penguin on posn (0,0)
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);

    assertEquals(PlayerColor.WHITE, state.getCurrentPlayerColor());
    assertEquals(1, state.getPenguinList().size());
    assertEquals(new Position(0,0),
        state.getPenguinList().get(0).getPosition());
    assertEquals(PlayerColor.RED,
        state.getPenguinList().get(0).getPlayerColor());

    assertTrue(twoPlGame.anyPlayerHasPenguinToPlace(state, 4));

    GameState secondPlacementState =
        twoPlGame.handleCurrentPlayerPenguinPlacement(state);

    assertEquals(PlayerColor.RED, secondPlacementState.getCurrentPlayerColor());
    assertEquals(2, secondPlacementState.getPenguinList().size());
    assertEquals(new Position(0,1),
        secondPlacementState.getPenguinList().get(1).getPosition());
    assertEquals(PlayerColor.WHITE,
        secondPlacementState.getPenguinList().get(1).getPlayerColor());
  }

  //Tests for:
  // player implements infinite loop
  // player tries to move to a hole/occupied tile
  @Test
  public void testPlayerImplementsInfiniteLoopInPlacement() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(player);
    playerList.add(new PlayerImplementsInfiniteLoop());

    GameState state = twoPlGame.setupGame(defaultConf, playerList);
    assertTrue(twoPlGame.anyPlayerHasPenguinToPlace(state, 4));
    //first player places correctly
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(2, state.getOrderedPlayers().size());
    //second one times out, leaving one player in the game
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(1, state.getOrderedPlayers().size());
  }

  @Test
  public void testPlayerMovesToOccupiedPlace() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(player);
    playerList.add(new PlayerCheatsAtPlacement());

    GameState state = twoPlGame.setupGame(defaultConf, playerList);
    assertTrue(twoPlGame.anyPlayerHasPenguinToPlace(state, 4));
    //first player places correctly
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(2, state.getOrderedPlayers().size());
    //second one tries to place on top of player1, kick them out:
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(1, state.getOrderedPlayers().size());
  }

  //Test that if everyone fails or cheats during placing pengs,
  //the method returns a state thats at gameover back up to the main method
  @Test
  public void testEveryoneFailsDuringPlacement() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(new PlayerImplementsInfiniteLoop());
    playerList.add(new PlayerImplementsInfiniteLoop());

    GameState state = twoPlGame.setupGame(defaultConf, playerList);
    assertTrue(twoPlGame.anyPlayerHasPenguinToPlace(state, 4));
    //first player gets kicked for timing out:
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(1, state.getOrderedPlayers().size());
    //second player gets kicked for timing out:
    state = twoPlGame.handleCurrentPlayerPenguinPlacement(state);
    assertEquals(0, state.getOrderedPlayers().size());
  }

  //Test for a properly working full round of penguin placement:
  @Test
  public void testPenguinPlacementGood() {
    GameState state = threePlGame.setupGame(defaultConf, initPlayers3);
    state = threePlGame.assignPlayerColors(state);
    state = threePlGame.informPlayerOpponentColors(state);
    state = threePlGame.placeAllPenguins(state);
    assertFalse(state.hasEveryoneBeenKicked());
    assertEquals(3, state.getOrderedPlayers().size());
    assertEquals(9, state.getPenguinList().size());
    assertEquals(PlayerColor.RED, state.getCurrentPlayerColor());
    //Uncomment for a quick glance at the penguin positions:
    //state.getPenguinList().forEach((p) ->
        //System.out.println("Color: " + p.getPlayerColor() + " posn: " + p
        // .getPosition()));
  }


  //Test for a full round of penguin placement where EVERYONE fails/cheats
  @Test
  public void testEveryoneFailsRoundOfPlacement () {
    List<Player> playerList = new ArrayList<>();
    playerList.add(new PlayerCheatsAtPlacement()); //cheater
    playerList.add(new PlayerImplementsInfiniteLoop()); //failure
    //Because both players cheat/fail the game will be cut short
    //right when the placing penguins phase is over:
    GameResult bigResult = twoPlGame.runGame(defaultConf, playerList);
    assertEquals(1, bigResult.getFailedPlayers().size());
    assertEquals(1, bigResult.getCheaters().size());
    assertEquals(0, bigResult.getWinners().size());
    assertEquals(0, bigResult.getOthers().size());
  }

  //Test for a full round of penguin placement where ALL BUT 1 fails/cheats
  //(game should still end after placing penguins)
  @Test
  public void testAllButOneFailsRoundOfPlacement () {
    List<Player> playerList = new ArrayList<>();
    playerList.add(new PlayerCheatsAtPlacement()); //cheater
    playerList.add(player); //good player
    GameResult bigResult = twoPlGame.runGame(defaultConf, playerList);
    assertEquals(0, bigResult.getFailedPlayers().size());
    assertEquals(1, bigResult.getCheaters().size());
    assertEquals(1, bigResult.getWinners().size());
    assertEquals(0, bigResult.getOthers().size());
  }


  /* `Rem` stands for "removed"
   * <RED>  <WHT>  <BRN>  <BLK>  <RED>
   *    <WHT>  <BRN>  <BLK>  <1,3>  <1,4>
   * <2,0>  <2,1>  <rem>  <2,3>  <2,4>
   *    <3,0>  <3,1>  <3,2>   <3,3>  <3,4>
   * <4,0>  <4,1>  <4,2>  <4,3>  <4,4>
   */
  @Test
  public void testRunGameFailAtTurn() {
    List<Player> players = new ArrayList<>();
    Player playerMovesToOOB =
        new AIPlayer(new ScanPlacementStrategy(),
            (r) -> new Move(new Position(0, 4), new Position(2, 5)));
    Player playerMovesToOccupied =
            new AIPlayer(new ScanPlacementStrategy(),
                (w) -> new Move(new Position(0, 1), new Position(1, 1)));
    Player playerMovesToHole =
        new AIPlayer(new ScanPlacementStrategy(),
            (w) -> new Move(new Position(1, 1), new Position(2, 2)));
    Player playerTimeout = new PlayerTimoutAtAction();

    players.add(playerMovesToOOB);
    players.add(playerMovesToOccupied);
    players.add(playerMovesToHole);
    players.add(playerTimeout);

    List<Position> holes = new ArrayList<>();
    Position hole = new Position(2, 2);
    holes.add(hole);
    defaultConf.setHoles(holes);
    Referee ref = new Referee();

    GameResult result = ref.runGame(defaultConf, players);

    assertEquals(playerTimeout, result.getFailedPlayers().get(0));
    assertTrue(result.getCheaters().contains(playerMovesToHole));
    assertTrue(result.getCheaters().contains(playerMovesToOccupied));
    assertTrue(result.getCheaters().contains(playerMovesToOOB));
    assertEquals(new ArrayList<>(), result.getOthers());
    assertEquals(new ArrayList<>(), result.getWinners());
  }

  @Test
  public void testRunGameCompleteWithSomeCheaterAndFailedPlayer() {
    // tests
    // 1. smart player who can't make since its penguins are all blocked
    //    this shows that referee handles "turn skipping"
    // 2. player who failed during placement
    // 3. player who cheated during turn taking
    // 4. a final winner
    List<Player> players = new ArrayList<>();
    List<Position> holes = new ArrayList<>();
    holes.add(new Position(1, 0));
    holes.add(new Position(2, 0));
    holes.add(new Position(1, 2));
    holes.add(new Position(1, 3));
    holes.add(new Position(2, 3));
    Player dumbPlayer =
      new AIPlayer(new ScanPlacementStrategy(),
          new MinimaxTurnActionStrategy(1));
    Player smartPlayer =
      new AIPlayer(new ScanPlacementStrategy(),
          new MinimaxTurnActionStrategy(2));
    Player placementFailedPlayer = new PlayerImplementsInfiniteLoop();
    Player playerMovesToOutside =
            new AIPlayer(new ScanPlacementStrategy(),
                (t) -> new Move(new Position(0, 2), new Position(0, 1)));

    players.add(smartPlayer);
    players.add(dumbPlayer);
    players.add(placementFailedPlayer);
    players.add(playerMovesToOutside);

    defaultConf.setHoles(holes);
    Referee ref = new Referee();
    GameResult result = ref.runGame(defaultConf, players);

    List<Player> cheaters = new ArrayList<>();
    cheaters.add(playerMovesToOutside);
    List<Player> failedPlayers = new ArrayList<>();
    failedPlayers.add(placementFailedPlayer);
    List<Player> winners = new ArrayList<>();
    winners.add(dumbPlayer);
    List<Player> others = new ArrayList<>();
    others.add(smartPlayer);

    assertEquals(failedPlayers, result.getFailedPlayers());
    assertEquals(cheaters, result.getCheaters());
    assertEquals(winners, result.getWinners());
    assertEquals(others, result.getOthers());
  }

  @Test
  public void testGoodGame() {
    GameResult result = this.threePlGame.runGame(defaultConf, initPlayers3);

    assertEquals(0, result.getFailedPlayers().size());
    assertEquals(0, result.getCheaters().size());
    assertEquals(1, result.getWinners().size());
    assertEquals(2, result.getOthers().size());
  }

  // The following classes are used to mock players in order to test exception
  // handling in referee. For example these exceptions would be thrown if
  // the given player had disconnected from the server, or failed to respond
  // in time.

  private static class PlayerImplementsInfiniteLoop implements Player {
    @Override
    public boolean assignColor(PlayerColor color) {
      while(true){
        //waste time
      }
    }

    @Override
    public boolean informOpponentColors(List<PlayerColor> colors) {
      while(true){
        //waste time
      }
    }

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      while(true){
        //waste time
      }
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return null;
    }
  }

  private static class PlayerCheatsAtPlacement implements Player {
    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new Position(0,0);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new Move(new Position(0,0), new Position(0,1));
    }
  }

  private static class PlayerTimoutAtAction implements Player {

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      while(true){
        //waste time
      }
    }
  }

}
