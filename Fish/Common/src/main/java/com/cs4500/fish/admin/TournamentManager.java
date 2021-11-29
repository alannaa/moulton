package com.cs4500.fish.admin;

import com.cs4500.fish.common.BoardConfig;
import com.cs4500.fish.player.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TournamentManager {

  private static final int MINIMUM_PLAYERS = 2;
  private static final int MAXIMUM_PLAYERS = 4;
  private static final int TIMEOUT_THRESHOLD_ONE_S = 1;

  private BoardConfig conf =
      new BoardConfig().setWidth(5).setHeight(5).setDefaultFish(2);

  private List<Player> winners = new ArrayList<>();
  private List<Player> losers = new ArrayList<>();
  private List<Player> cheaters = new ArrayList<>();
  private List<Player> failures = new ArrayList<>();

  public Map<PlayerOutcome, List<Player>> runTournament(List<Player> initialPlayers) {
    // players are always the players of the current game
    // winners are always the winners of the previous game (hence why its set
    //  to the initPlayers to begin with)
    List<Player> players = new ArrayList<>();
    informPlayersTournamentStart(initialPlayers);//may kick out some players
    List<Player> winners = initialPlayers;
    while(! tournamentIsOver(players, winners)) {
      //Set the new current players to be winners from the last round
      players = winners;
      //group together the players playing in each game:
      List<List<Player>> games = assignPlayersToGames(players);
      runRound(games);
      winners = this.winners;
      if (games.size() == 1) {
        //if the tournament gets down to one proper game, play it and
        // then break (at this point it's already been played)
        break;
      }
    }
    informPlayersTournamentOutcome();
    return constructResults();
  }

  //Sends communication to player re the tournament starting
  // if player fails to respond in 1 second, they are kicked
  void informPlayersTournamentStart(List<Player> playersToNotify) {
    List<Player> playersCopy = new ArrayList<>(playersToNotify);
    for (Player p : playersCopy) {
      Optional<Boolean> optResponse =
          PlayerSystemInteraction.requestResponseTimeout(() ->
              p.informTournamentStatus(true), TIMEOUT_THRESHOLD_ONE_S);
      if (! optResponse.isPresent()) {
        this.failures.add(p);
        playersToNotify.remove(p);
      }
    }
  }

  /**
   * Takes in a list of players, and breaks it down into the correct number
   * of players to play a game. If there is an uneven number of players, then
   * the two last games will have less than the optimal number of players.
   * THROWS: IllegalArgumentException if there are too few players
   */
  List<List<Player>> assignPlayersToGames(List<Player> initialPlayers) {
    List<List<Player>> allGroups = new ArrayList<>();
    List<Player> aGroup = new ArrayList<>();
    if (initialPlayers.size() < MINIMUM_PLAYERS) {
      String msg = "There are too few players for a game.";
      throw new IllegalArgumentException(msg);
    }
    for (Player p : initialPlayers) {
      if (aGroup.size() == MAXIMUM_PLAYERS) {
        allGroups.add(aGroup);
        aGroup = new ArrayList<>();
      }
      aGroup.add(p);
    }

    if (aGroup.size() < MINIMUM_PLAYERS) {
      List<Player> finalGroup = allGroups.get(allGroups.size() - 1);
      Player p = finalGroup.get(finalGroup.size() - 1);
      aGroup.add(p);
      finalGroup.remove(p);
      allGroups.add(aGroup);
    } else {
      allGroups.add(aGroup);
    }
    return allGroups;

  }

  // Runs one round of games
  // accumulates the losers, cheaters, failures
  // updates the winners list with only the winners from the most recent round
  void runRound(List<List<Player>> games) {
    GameResult result;
    List<Player> winners = new ArrayList<>();

    for (List<Player> game : games) {
      Referee ref = new Referee();
      result = ref.runGame(conf, game);
      winners.addAll(result.getWinners());
      this.losers.addAll(result.getOthers());
      this.cheaters.addAll(result.getCheaters());
      this.failures.addAll(result.getFailedPlayers());
    }
    this.winners = winners;
  }

  // true == player won
  // false == player lost (not fail or cheat)
  void informPlayersTournamentOutcome() {
    List<Player> winnersCopy = new ArrayList<>(this.winners);
    for (Player resultWinner : winnersCopy) {
      Optional<Boolean> response =
          PlayerSystemInteraction.requestResponseTimeout(() ->
                  resultWinner.informGameResult(true),
              TIMEOUT_THRESHOLD_ONE_S);
      if (! response.isPresent()) {
        winners.remove(resultWinner);
        this.failures.add(resultWinner);
      }
    }
    for (Player resultLoser : this.losers) {
      PlayerSystemInteraction.requestResponseTimeout(() ->
              resultLoser.informGameResult(false),
          TIMEOUT_THRESHOLD_ONE_S);
    }
  }

  // returns true when either two games in a row occur with the same winners or
  // when there are not enough players remaining to play a game (eg 1)
  // winners list is always output in order by age, so order will not affect
  // the equality comparison.
  private boolean tournamentIsOver(List<Player> winnersLastGame, List<Player> winners) {
    return winnersLastGame.equals(winners) || winners.size() < MINIMUM_PLAYERS;
  }

  // using the outcome fields in this class, construct results
  Map<PlayerOutcome, List<Player>> constructResults() {
    Map<PlayerOutcome, List<Player>> results = new HashMap<>();
    results.put(PlayerOutcome.WINNER, this.winners);
    results.put(PlayerOutcome.LOSER, this.losers);
    results.put(PlayerOutcome.FAILURE, this.failures);
    results.put(PlayerOutcome.CHEATER, this.cheaters);
    return results;
  }

  public void setBoardConfig(BoardConfig conf) {
    this.conf = conf;
  }
}
