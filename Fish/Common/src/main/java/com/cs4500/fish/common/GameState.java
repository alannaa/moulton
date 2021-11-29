package com.cs4500.fish.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;

/**
 * Packages together all information about the state of a Fish game, including:
 * - state of the board (holes, fish, etc.)
 * - state of all the players (count, color, scores, etc.)
 * - the order in which players take turn, and current player.
 * NOTE: 
 * - It does not include the rules.
 * - The class is _immutable_
 * INVARIANTS:
 * - No penguin exists on a hole, occupied tile, or outside the board.
 * - Each penguin must have one of given player color.
 * - All players have distinct colors.
 * - There is at least 1 player.
 */
public class GameState {
  // constants for json (de)serialization
  public static final String PLAYERS_FIELD = "players";
  public static final String BOARD_FIELD = "board";

  // current state of the board
	private final Board board;
  // current state of all the players in order
	private final PlayerList playerList;

  /**
   * Construct a GameState with `board` and players represented as colors.
   * The order of player colors represents the turn order of corresponding
   * players. Order of players is determined by whoever is passing in
	 * `playerColors`.
	 * @param board could be in any state, including completely empty.
   */
	public GameState(Board board, List<PlayerColor> playerColors) {
		this(board, new PlayerList(playerColors));
	}

  // for implementation convenience
	private GameState(Board board, PlayerList playerList) {
		this.board = board;
		this.playerList = playerList;
	}

	/**
   * Returns a copy of the board
   */
  public Board getBoardCopy() {
    return this.board.copy();
  }

  public PlayerColor getCurrentPlayerColor() {
    return this.playerList.getCurrentPlayerColor();
  }

  /**
   * @throws IllegalArgumentException if no player has `color` in this state.
   */
  public PlayerState getPlayerStateWithColor(PlayerColor color) {
    return this.playerList.getPlayerStateWithColor(color);
  }

	/**
	 * Takes a player color and creates and places a penguin on the given
	 * position.
   * @throws IllegalArgumentException if board has a hole at `pos`.
	 */
  public GameState placePenguin(PlayerColor color, Position pos) {
    this.checkTileNotHole(pos);
		PlayerList newPlayerList = this.playerList.placePenguin(color, pos);
    return new GameState(board, newPlayerList);
	}

	/**
	 * If a penguin exists on the first position, it moves it to the second
	 * position.
	 * @throws IllegalArgumentException if
		* - either position is invalid
	 * - there is no penguin at the from position
		* - the move is illegal (onto a penguin/hole),
	 */
	public GameState movePenguin(Position from, Position to) {
		int height = this.board.getHeight();
		int width = this.board.getWidth();
		// Check that the requested move is within bounds
		if (!from.withinBounds(height, width) || !to.withinBounds(height, width)) {
			String msg = String.format(
            "%s or %s is not within the bounds of the board (w:%d, h:%d)",
            from, to, width, height);
			throw new IllegalArgumentException(msg);
		}
		this.checkTileNotHole(to);
		int fromFish = this.board.getTileAt(from).getNumFish();
		PlayerList newPlayerList = this.playerList.movePenguin(from, to, fromFish);
		Board newBoard = this.board.copy();
		newBoard.removeTile(from);
    return new GameState(newBoard, newPlayerList);
	}

  // advance the current player to the next player in order.
  public GameState advancePlayer() {
    return new GameState(this.board, this.playerList.advancePlayer());
  }

  /**
   * Return a copy of map that associates the current position of each penguin
   * in the game to the penguins themselves.
   */
  public List<Penguin> getPenguinList() {
    return this.playerList.getPenguinList();
  }

  /**
   * Return a copy of ordered PlayerState list.
   * The order corresponds to player's turn in a Fish game.
   */
  public List<PlayerState> getOrderedPlayers() {
    return this.playerList.getOrderedPlayers();
  }

  public GameState removePlayerWithColor(PlayerColor color) {
    //If called on a gameState with zero players, throws IllegalStateException
    Optional<PlayerList> optPlayers = this.playerList.removePlayerWithColor(color);
    if (! optPlayers.isPresent()) {
      return new GameState(this.board, new ArrayList<>());
    }
    return new GameState(this.board, optPlayers.get());
  }

  public boolean hasEveryoneBeenKicked() {
    return this.getOrderedPlayers().size() == 0;
  }

  private void checkTileNotHole(Position pos) {
    Tile tile = this.board.getTileAt(pos);
    if (tile.isRemoved()) {
      String msg = "Cannot place penguin onto a hole";
      throw new IllegalArgumentException(msg);
    }
  }

  // Serialize into and deserialize from json.
  // GameState ->
		//  - { "players" : PlayerList,
		//      "board"   : Board }
  // NOTE serialized board should doesn't have penguin tiles removed
  public static GameState deserialize(JsonElement e) throws DeserializationException {
    if (! e.isJsonObject()) {
      throw new DeserializationException("GameState must be a json object");
    }
    // - No penguin exists on a hole, or outside the board.
    JsonObject stateObj = e.getAsJsonObject();
    Board board = Board.deserialize(stateObj.get(BOARD_FIELD));
    PlayerList playerList = PlayerList.deserialize(stateObj.get(PLAYERS_FIELD));
    if (playerList.getPenguinList().stream().map(Penguin::getPosition)
        .anyMatch(pos -> !board.isPosInbound(pos) || board.getTileAt(pos).isRemoved())) {
      String msg = "Penguin must reside on non-hole position within the board";
      throw new DeserializationException(msg);
    }
    return new GameState(board, playerList);
  }

  public JsonElement serialize() {
    JsonObject gameStateObj = new JsonObject();
    gameStateObj.add(BOARD_FIELD, board.serialize());
    gameStateObj.add(PLAYERS_FIELD, this.playerList.serialize());
    return gameStateObj;
  }

  @Override
  public String toString() {
    return String.format("{ board: %s\n,  players: %s\n}\n", board, playerList);
  }
}