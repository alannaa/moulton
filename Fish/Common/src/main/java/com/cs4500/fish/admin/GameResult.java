package com.cs4500.fish.admin;

import com.cs4500.fish.player.Player;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * A class that packages the results and analytics from a complete Fish game.
 * It contains:
 * - the players who cheated in a game
 * - the players who failed during communication in a game
 * - the player(s) who won a game
 * - all the other players
 *   - other players is ordered according to finishing place ie second, third...
 */
public class GameResult {
  private List<Player> cheaters = new ArrayList<>();
  private List<Player> failedPlayers = new ArrayList<>();
  private List<Player> winners = new ArrayList<>();
  private List<Player> others = new ArrayList<>();
  private Map<Player, Integer> playerScores = new HashMap<>();

  public GameResult addCheater(Player cheater, int score) {
    playerScores.put(cheater, score);
    this.cheaters.add(cheater);
    return this;
  }
  public GameResult addFailedPlayer(Player failedPlayer, int score) {
    playerScores.put(failedPlayer, score);
    this.failedPlayers.add(failedPlayer);
    return this;
  }
  public GameResult addWinner(Player winner, int score) {
    playerScores.put(winner, score);
    this.winners.add(winner);
    return this;
  }
  public GameResult addOtherPlayer(Player otherPlayer, int score) {
    playerScores.put(otherPlayer, score);
    this.others.add(otherPlayer);
    return this;
  }

  public List<Player> getCheaters() {
    return new ArrayList<>(cheaters);
  }
  public List<Player> getFailedPlayers() {
    return new ArrayList<>(failedPlayers);
  }
  public List<Player> getWinners() { return new ArrayList<>(winners); }
  public List<Player> getOthers() {
    return new ArrayList<>(others);
  }
  public Map<Player, Integer> getPlayerScores() {return playerScores;}

  @Override
  public String toString() {
    return "GameResult{" +
            "cheaters=" + cheaters +
            ", failedPlayers=" + failedPlayers +
            ", winners=" + winners +
            ", others=" + others +
            '}';
  }
}
