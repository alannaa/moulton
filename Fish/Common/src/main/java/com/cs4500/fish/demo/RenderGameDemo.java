package com.cs4500.fish.demo;

import com.cs4500.fish.common.BoardConfig;
import com.cs4500.fish.common.Board;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.common.Position;
import com.cs4500.fish.view.SwingGameView;
import java.util.List;
import java.util.ArrayList;

/**
 * A Demo program that shows how a Fish Game Board is constructed and rendered.
 */
public class RenderGameDemo {

  public static void main(String[] args) {

    BoardConfig config = new BoardConfig();
    config.setHeight(4).setWidth(3);
    config.setDefaultFish(4).setOneFishTileMin(3);
    List<Position> holes = new ArrayList<>();
    holes.add(new Position(0, 0));
    holes.add(new Position(1, 1));
    config.setHoles(holes);

    Board board = new Board(config.setWidth(5).setHeight(8));
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.BLACK);
    colors.add(PlayerColor.RED);
    GameState state = new GameState(board, colors);
    state.placePenguin(PlayerColor.BLACK, new Position(4, 1));
    state.placePenguin(PlayerColor.RED, new Position(3, 3));
    new SwingGameView(state);
  }
}
