package com.cs4500.fish.admin;

import com.cs4500.fish.common.*;
import com.cs4500.fish.player.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class Referee {
  private final static List<PlayerColor> initColors =
          Arrays.asList(PlayerColor.values());

  private final static int TIMEOUT_THRESHOLD_ONE_S = 1;
  private final static int TIMEOUT_THRESHOLD_THREE_S = 3;
  private final static int NUM_PENGUIN_SUBTRACTOR = 6;
  private final static int MIN_NUM_PLAYERS = 2;
  private final static int MAX_NUM_PLAYERS = initColors.size();

  private final Map<Player, Integer> cheaters = new HashMap<>();
  private final Map<Player, Integer> failures = new HashMap<>();
  private final Map<PlayerColor, Player> colorToPlayer = new HashMap<>();

  /**
   * Construct a board using `config`,
   *  assign each player in `players` a unique color, and
   *  create a game state with the board and player colors. The order
   * of players in `players` correspond to their turn order in the actual game.
   * It starts the game with the penguin placement phase, Each player receives
   * 6 - N penguins where N is the number of players. It will request penguin
   * placement from players based on their order in `players`, and run as many
   * rounds as there are penguins per player. If a player attempts to place a
   * penguin outside the board, onto a hole or an occupied tile, register it as
   * a cheater.
   * Handles players taking turn. Each player will be asked to provide an
   * action, which this referee will then validate against the gametree, and
   * then update then traverse the gametree if the move is valid. The game will
   * stop running when the game tree has no more subtrees, or if all the players
   * have either failed or cheated, in which case there is no winners. Return
   * the result of this game asynchronously. Failed players are removed from the
   * game, and kept track of. At the end of
   * the game, these players are reported upwards.
   * A `failed` player can be described as someone who has lost connection, or
   * has failed to respond to a request within
   * an acceptable time.
   * At no point is the player ever passed a mutable object.
   * @throws IllegalArgumentException if
   * - number of players exceeds `MAX_NUM_PLAYERS` or is less than
   *    `MIN_NUMBER_PLAYERS`
   * - the board specified by the config does not have enough non-hole tiles
   *    for the required amount of penguins to be placed
   */
  public GameResult runGame(BoardConfig config, List<Player> players) {

    //Check inputs:
    if (players.size() > MAX_NUM_PLAYERS || players.size() < MIN_NUM_PLAYERS) {
      throw new IllegalArgumentException("Invalid number of players");
    }
    GameState state = setupGame(config, players);

    state = assignPlayerColors(state);
    if (allPlayersDisqualified(state)) {
      return collectResult(state);
    }
    state = informPlayerOpponentColors(state);
    if (allPlayersDisqualified(state)) {
      return collectResult(state);
    }

    state = placeAllPenguins(state);
    if (allPlayersDisqualified(state)) {
      return collectResult(state);
    }

    GameTree tree = new GameTree(state);
    state = takeTurnsMovingPenguins(tree);
    return collectResult(state);
  }


  // Sets up administrative tasks needed to start a game
  // EFFECT: Populate `colorToPlayer`
  // THROWS: IllegalArgumentException if there is not enough non-hole tiles for
  //         every penguin
  // Returns the gameState set up with all players and dimensions
  GameState setupGame(BoardConfig config, List<Player> players) {
    // populate colorToPlayer
    List<PlayerColor> colors = initColors.subList(0, players.size());
    IntStream.range(0, colors.size())
        .forEach(i -> colorToPlayer.put(colors.get(i), players.get(i)));

    // check there is enough tiles
    Board board = new Board(config);
    int penguinsPerPlayer = NUM_PENGUIN_SUBTRACTOR - players.size();
    if ((penguinsPerPlayer * players.size()) > board.numNonHoleTilesOnBoard()) {
      String msg = "Insufficient non-hole tiles to place penguins";
      throw new IllegalArgumentException(msg);
    }

    //return the gameState all set up with players and dimensions
    return new GameState(new Board(config),
        new ArrayList<>(colors));
  }


  // sends communication to player re their own playerColor
  GameState assignPlayerColors(GameState state) {
    //must traverse through a copy of the hashmap since the
    //handleFailedPlayer method modifies the original during the iteration
    Map<PlayerColor, Player> ctpCopy = new HashMap<>(this.colorToPlayer);
    for (PlayerColor pc : ctpCopy.keySet()) {
      Player player = ctpCopy.get(pc);
      Optional<Boolean> optResponse =
          PlayerSystemInteraction.requestResponseTimeout(() ->
              player.assignColor(pc), TIMEOUT_THRESHOLD_ONE_S);
      if (! optResponse.isPresent()) {
        state = handleFailedPlayer(state, pc);
      }
    }
    return state;
  }

  // sends communication to player re their opponents' playerColors
  GameState informPlayerOpponentColors(GameState state ) {
    Map<PlayerColor, Player> ctpCopy = new HashMap<>(this.colorToPlayer);
    for (PlayerColor pc : ctpCopy.keySet()) {
      List<PlayerColor> colorsToSend = new ArrayList<>(ctpCopy.keySet());
      colorsToSend.remove(pc);
      Player player = ctpCopy.get(pc);
      Optional<Boolean> optResponse =
          PlayerSystemInteraction.requestResponseTimeout(() ->
              player.informOpponentColors(colorsToSend), TIMEOUT_THRESHOLD_ONE_S);
      if (! optResponse.isPresent()) {
        state = handleFailedPlayer(state, pc);
      }
    }
    return state;
  }


  //////// PLACING PENGUINS PHASE ////////

  //while any player has pengs left, place them.
  //once all players run out of pengs, return the state
  //if all players get disqualified during the phase, cut short and send
  //back the GameState with no players left in it.
  GameState placeAllPenguins(GameState state) {
    int penguinsPerPlayer = NUM_PENGUIN_SUBTRACTOR - colorToPlayer.keySet().size();
    while (anyPlayerHasPenguinToPlace(state, penguinsPerPlayer)) {
      state = this.handleCurrentPlayerPenguinPlacement(state);
      if (allPlayersDisqualified(state)) {
        return state;
      }
    }
    return state;
  }

  boolean anyPlayerHasPenguinToPlace(GameState state, int penguinsPerPlayer) {
    for (PlayerState ps : state.getOrderedPlayers()) {
      int count = (int) state.getPenguinList().stream()
              .filter(p -> p.getPlayerColor().equals(ps.getPlayerColor()))
              .count();
      if (count != penguinsPerPlayer ) {
        return true;
      }
    }
    return false;
  }

  /* Return the resulting game state from placing one penguin at the position
   * specified by the single current player.*/
  GameState handleCurrentPlayerPenguinPlacement(GameState state) {
    PlayerColor color = state.getCurrentPlayerColor();
    Player currentPlayer = colorToPlayer.get(color);
    Optional<Position> optPos =
        PlayerSystemInteraction.requestResponseTimeout(() ->
            currentPlayer.requestPenguinPlacement(state), TIMEOUT_THRESHOLD_ONE_S);
    //Handle if player timed out or got disconnected
    if (! optPos.isPresent()) {
      return this.handleFailedPlayer(state, color);
    }
    Position pos = optPos.get();
    Board board = state.getBoardCopy();
    state.getPenguinList().forEach(p -> board.removeTile(p.getPosition()));
    if (board.isPosInbound(pos) && ! board.getTileAt(pos).isRemoved()) {
      return state.placePenguin(color, pos).advancePlayer();
    } else {
      return this.handleCheater(state, color);
    }
  }


  /// METHODS HANDLING MOVEMENT PHASE ////
  // main game loop of actionable, penguin-moving phase
  private GameState takeTurnsMovingPenguins(GameTree tree) {
    int callCount = 0;
    while (! allPlayersDisqualified(tree.getState()) && ! tree.getSubtrees().isEmpty()) {
      Map<Action, GameTree> subTrees = tree.getSubtrees();
      // If the current player cannot make a move, skip them
      if (subTrees.size() == 1 && subTrees.containsKey(Skip.getInstance())) {
        tree = subTrees.get(Skip.getInstance());
        continue;
      }
      int timeoutToUse = callCount == 0 ?
          TIMEOUT_THRESHOLD_THREE_S :
          TIMEOUT_THRESHOLD_ONE_S;
      callCount = 1;
      tree = handleCurrentPlayerAction(tree, timeoutToUse, subTrees);
    }
    return tree.getState();
  }

  /* Return the resulting game tree from taking the action specified by the
   * current player.
   * Return Optional.empty if the current player cheated or failed, and it is
   * the last player in the current gamestate. */
  private GameTree handleCurrentPlayerAction(GameTree tree, int timeoutToUse,
      Map<Action, GameTree> subTrees) {
    GameState state = tree.getState();
    PlayerColor color = state.getCurrentPlayerColor();
    Player player = colorToPlayer.get(color);

    Optional<Action> optAct =
        PlayerSystemInteraction.requestResponseTimeout(() ->
            player.requestAction(tree), timeoutToUse);
    // kick player for failing
    if (! optAct.isPresent()) {
      GameState nextState = this.handleFailedPlayer(state, color);
      return new GameTree(nextState);
    }
    Action action = optAct.get();
    // kick player for cheating
    if (!subTrees.containsKey(action)) {
      GameState nextState = this.handleCheater(state, color);
      return new GameTree(nextState);
    }
    return subTrees.get(action);
  }


  ////METHODS FOR HANDLING KICKING PLAYERS OUT/////
  // both methods return the updated state with the player removed
  // if this is the last player in the game, will return optional.empty()
  private GameState handleFailedPlayer(GameState state, PlayerColor color) {
    this.failures.put(colorToPlayer.get(color),
        state.getPlayerStateWithColor(color).getScore());
    String msg = "You failed to communicate.";
    PlayerSystemInteraction.requestResponseTimeout(()
        -> colorToPlayer.get(color).disqualifyPlayer(msg), TIMEOUT_THRESHOLD_ONE_S);
    this.colorToPlayer.remove(color);
    return state.removePlayerWithColor(color);
  }

  private GameState handleCheater(GameState state, PlayerColor color) {
    this.cheaters.put(colorToPlayer.get(color),
        state.getPlayerStateWithColor(color).getScore());
    String msg = "You cheated and have been removed from the game.";
    PlayerSystemInteraction.requestResponseTimeout(() ->
        colorToPlayer.get(color).disqualifyPlayer(msg), TIMEOUT_THRESHOLD_ONE_S);
    this.colorToPlayer.remove(color);
    return state.removePlayerWithColor(color);
  }


  ////METHODS FOR HANDLING THE ENDING OF A GAME/////
  private boolean allPlayersDisqualified(GameState state) {
    return colorToPlayer.keySet().size() == 0 || state.hasEveryoneBeenKicked();
  }

  //Gather the game results and put them in a GameResult
  private GameResult collectResult(GameState state) {
    GameResult result = new GameResult();
    for (Player ch : this.cheaters.keySet()) {
      result = result.addCheater(ch, cheaters.get(ch));
    }
    for (Player fl : this.failures.keySet()) {
      result = result.addFailedPlayer(fl, failures.get(fl));
    }
    if (allPlayersDisqualified(state)) {
      return result;
    }
    // the state only has information concerning the player colors, not
    // external players. So first populate the winning player colors:
    List<PlayerColor> winners = new ArrayList<>();
    List<PlayerColor> others = new ArrayList<>();
    this.populateWinnersAndOthers(state, winners, others);

    for (PlayerColor otherColor : others) {
      result = result.addOtherPlayer(colorToPlayer.get(otherColor),
          state.getPlayerStateWithColor(otherColor).getScore());
    }
    // adding winners is a special case because they need to be in order by age
    // in order for the Tournament manager to set up the next game correctly:
    List<Player> ageOrderedWinners = orderPlayersByAge(state, winners);
    for (Player p : ageOrderedWinners) {
      result = result.addWinner(p, givenPlayerGetScore(state, p));
    }

    return result;
  }

  // update `winners` and `others` based on `state`
  private void populateWinnersAndOthers(
      GameState state, List<PlayerColor> winners, List<PlayerColor> others) {
    int maxScoreSoFar = Integer.MIN_VALUE;
    for (PlayerState ps : state.getOrderedPlayers()) {
      if (ps.getScore() == maxScoreSoFar) {
        winners.add(ps.getPlayerColor());
      } else if (ps.getScore() > maxScoreSoFar) {
        others.addAll(winners);
        winners.clear();
        winners.add(ps.getPlayerColor());
        maxScoreSoFar = ps.getScore();

      } else {
        others.add(ps.getPlayerColor());
      }
    }
  }

  // given the winning colors, return a list of ext players in order by age
  private List<Player> orderPlayersByAge(GameState state,
      List<PlayerColor> unorderedWinners) {
    List<Player> ageOrderedPlayers = new ArrayList<>();
    //gather the winnerStates
    List<PlayerState> winnerStates = new ArrayList<>();
    for (PlayerState ps : state.getOrderedPlayers()) {
      if (unorderedWinners.contains(ps.getPlayerColor())) {
        winnerStates.add(ps);
      }
    }
    //sort WinnerStates by age
    winnerStates.sort(Comparator.comparing(PlayerState::getAge));
    //convert into a list of corresponding External Players
    winnerStates.forEach((ws) -> ageOrderedPlayers
        .add(colorToPlayer.get(ws.getPlayerColor())));
    return ageOrderedPlayers;
  }

  private int givenPlayerGetScore(GameState state, Player player) {
    int score = 0;
    for (PlayerColor pc : colorToPlayer.keySet()) {
      if (colorToPlayer.get(pc) == player) {
        score = state.getPlayerStateWithColor(pc).getScore();
      }
    }
    return score;
  }

}
