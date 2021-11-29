package com.cs4500.fish.remote;


import static org.junit.Assert.assertEquals;

import com.cs4500.fish.admin.PlayerOutcome;
import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.GameTree;
import com.cs4500.fish.common.Move;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.common.Position;
import com.cs4500.fish.player.AIPlayer;
import com.cs4500.fish.player.MinimaxTurnActionStrategy;
import com.cs4500.fish.player.Player;
import com.cs4500.fish.player.PlayerCommunicationException;
import com.cs4500.fish.player.ScanPlacementStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

  //Example
  Server server;

  @Before
  public void setUp() {
    try {
      this.server = new Server(55555);
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  private List<Player> getNPlayers(Integer n) {
    List<Player> players = new ArrayList<>();
    for (int i = 0; i < n; i += 1) {
      Player p = new AIPlayer(new ScanPlacementStrategy(),
          new MinimaxTurnActionStrategy(1));
      players.add(p);
    }
    return players;
  }

  //(the scenario sets up  6 players, one fails to send a name)
  @Test
  public void testRunTournamentFiveOrdinaryPlayers() {
    List<Player> players = getNPlayers(5);

    Map<PlayerOutcome, List<Player>> results =
        this.server.runTournament(players);

    assertEquals(1, results.get(PlayerOutcome.WINNER).size());
    assertEquals(4, results.get(PlayerOutcome.LOSER).size());
    assertEquals(0, results.get(PlayerOutcome.CHEATER).size());
    assertEquals(0, results.get(PlayerOutcome.FAILURE).size());
  }

  //(the scenario sets up 6 players, one send invalid Json)
  //Unsure how to mimic invalid Json sending
  @Test
  public void testRunTournamentSixPlayers() {
    List<Player> players = getNPlayers(5);
    players.add(new PlayerCheatsInPengPlacement());

    Map<PlayerOutcome, List<Player>> results =
        this.server.runTournament(players);

    assertEquals(1, results.get(PlayerOutcome.WINNER).size());
    assertEquals(4, results.get(PlayerOutcome.LOSER).size());
    assertEquals(1, results.get(PlayerOutcome.CHEATER).size());
    assertEquals(0, results.get(PlayerOutcome.FAILURE).size());
  }

  //(the scenario sets up  5 ordinary players; 5 bad ones, one per call)
  @Test
  public void testRunTournamentHalfBadPlayers() {
    List<Player> players = getNPlayers(5);
    players.add(new PlayerTimesOutInTournamentStatus());
    players.add(new PlayerTimesOutInPlayingAs());
    players.add(new PlayerTimesOutInPlayingWith());
    players.add(new PlayerCheatsInPengPlacement());
    players.add(new PlayerCheatsInTakingTurns());

    Map<PlayerOutcome, List<Player>> results =
        this.server.runTournament(players);

    //assertEquals(2, results.get(PlayerOutcome.WINNER).size());
    //assertEquals(3, results.get(PlayerOutcome.LOSER).size());
    assertEquals(2, results.get(PlayerOutcome.CHEATER).size());
    assertEquals(3, results.get(PlayerOutcome.FAILURE).size());
  }

  //(the scenario sets up 10 ordinary players)
  @Test
  public void testRunTournamentTenOrdinaryPlayers() {
    List<Player> players = getNPlayers(10);

    Map<PlayerOutcome, List<Player>> results =
        this.server.runTournament(players);

    assertEquals(1, results.get(PlayerOutcome.WINNER).size());
    assertEquals(9, results.get(PlayerOutcome.LOSER).size());
    assertEquals(0, results.get(PlayerOutcome.CHEATER).size());
    assertEquals(0, results.get(PlayerOutcome.FAILURE).size());
  }



  private class PlayerTimesOutInTournamentStatus implements Player {

    @Override
    public boolean informTournamentStatus(boolean status) {
      while(true){
        //do nothing
      }
    }

    @Override
    public Position requestPenguinPlacement(
        GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new MinimaxTurnActionStrategy(1).apply(state);
    }
  }

  private class PlayerTimesOutInPlayingAs implements Player {

    @Override
    public boolean assignColor(PlayerColor color) {
      while(true){
        //do nothing
      }
    }

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new MinimaxTurnActionStrategy(1).apply(state);
    }
  }

  private class PlayerTimesOutInPlayingWith implements Player {

    @Override
    public boolean informOpponentColors(List<PlayerColor> colors) {
      while(true){
        //do nothing
      }
    }

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new MinimaxTurnActionStrategy(1).apply(state);
    }
  }

  private class PlayerCheatsInPengPlacement implements Player {

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new Position(0,0);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new MinimaxTurnActionStrategy(1).apply(state);
    }
  }

  private class PlayerCheatsInTakingTurns implements Player {

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new Move(new Position(0,0), new Position(0,1));
    }
  }

  private class PlayerTimesOutInEndTournament implements Player {

    @Override
    public boolean informGameResult(boolean won) {
      while(true){
        //do nothing
      }
    }

    @Override
    public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
      return new ScanPlacementStrategy().apply(state);
    }

    @Override
    public Action requestAction(GameTree state) throws PlayerCommunicationException {
      return new MinimaxTurnActionStrategy(1).apply(state);
    }
  }


}