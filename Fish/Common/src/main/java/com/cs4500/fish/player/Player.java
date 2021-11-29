package com.cs4500.fish.player;

import com.cs4500.fish.common.*;
import java.util.List;

/**
 * This class represents all potential players. The referee will communicate
 * with the players via this interface. How requests made of the player are
 * answered are implementation specific.
 */
public interface Player {

  /**
   * Inform this player that it has been assigned the given color.
   * This also signals the beginning of a game.
   */
  default boolean assignColor(PlayerColor color) {return true;}

  /**
   * Inform this player of all the other players' colors (not including their
   * own).
   */
  default boolean informOpponentColors(List<PlayerColor> colors) {return true;}

  /**
   * Asks the player to send a position in, representing their penguin
   * placement.
   */
  Position requestPenguinPlacement(GameState state)
          throws PlayerCommunicationException, DeserializationException;

  /**
   * Asks the player to send in an action that represents their turn.
   */
  Action requestAction(GameTree state)
          throws PlayerCommunicationException, DeserializationException;

  /**
   * Informs the player of the tournament status.
   * true: The tournament has started and is ongoing
   * false: The tournament is not running, for example it has ended or has
   * not started.
   */
  default boolean informTournamentStatus(boolean tStatus) {return true;}

  /**
   * Disqualifies the player with given reason.
   */
  default boolean disqualifyPlayer(String reason) {return true;}

  /**
   * Informs player of game result.
   */
  default boolean informGameResult(boolean wonGame) {return true;}


}
