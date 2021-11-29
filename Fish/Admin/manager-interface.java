interface TournamentManager {

  /**
   * Allocates players to games, creates referees to run games, and
   * return collected tournament statistics once the tournament ends.
   * Games will be created continuously using `Referee` in "rounds".  Once all
   * of the players have been partitioned and handed off to referees for this
   * round, the tournament TournamentManager will wait for all games to be completed,
   * before restarting this process of generating games. This process will
   * repeat for only winning players from previous games, until only one winning
   * player remains. 
   * During these games, 
   * - if players fail or are caught cheating, they will be ejected from the tournament. 
   * - Any observers that have been added to this tournament will be
   *   periodically updated with informatio.
   */
  TournamentResult runTournament();

  // The following methods can be called at anytime as long as the tournmanet
  // has not completed.
  void addPlayer(Player players);
  // Add a tournament observer for the entire tournament
  void addTournamentObserver(TournamentObserver observer);
  // Add a game observer for the particular game with `gameId`.
  // Ignore the request if `gameId` is not found in the tournament TournamentManager.
  void addGameObserver(GameObserver observer, int gameId);
}

interface TournamentObserver {

  // When this observer is added to an object, send it the list
  // of IDs of the currently active games.
  void onRegister(List<Integer> gameIDs);
  // Inform observer of any game that just started.
  void informStartGame(int gameId);
  // Inform observer that an entire round of games is starting. This can also
  // imply that the previous round of games has finished.
  void informNewRoundStart();
  // Inform observer of the result of the tournament that just finished.
  void informTournamentResult(TournamentResult result);
}

interface GameObserver {

  // When this observer is added to an object, send it the most recent gamestate
  // from this point on the observer will only be notified of events.
  void onRegister(GameState currentState);
  // Inform this observer that the player with `color` has just taken `action`
  void informAction(Action action, PlayerColor color);
  // Inform this observer that the player with `color` has just cheated
  void informCheater(PlayerColor color);
  // Inform this observer that the player with `color` has just failed to
  // respond
  void informFailedPlayer(PlayerColor color);
  // Inform this observer that the player with `color` has placed a penguin at
  // `pos`
  void informPenguinPlacement(Position pos, PlayerColor color);
  // Inform observer of the result of this game, which just finished.
  void informGameResult(GameResult result);
}
