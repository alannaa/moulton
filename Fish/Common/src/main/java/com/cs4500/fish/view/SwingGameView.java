package com.cs4500.fish.view;

import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.common.Board;
import com.cs4500.fish.common.Penguin;
import com.cs4500.fish.common.Tile;
import com.cs4500.fish.common.Position;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;

/**
 * An implementation of visual representation for the Fish Game.
 */
public class SwingGameView implements GameView {
  private static final String FRAME_TITLE = "Fish";
  private JFrame window;
  private BoardPanel boardPanel;

  /**
   * Rendering of given state immediately starts after constructor.
   */
  public SwingGameView(GameState state) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      this.window = new JFrame(FRAME_TITLE);
      this.boardPanel = new BoardPanel(state);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.add(this.boardPanel);
      window.pack(); // let layout TournamentManager handle component/frame sizes
      window.setVisible(true);
    });
  }

  public void updateGameState(GameState state) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      this.boardPanel.updateGameState(state);
      this.window.repaint();
      window.pack();
    });
  }

  /**
   * A panel that renders the board.
   * The rendering of a tile is as follows:
   *    A      B
   *    /------\
   * F /        \ C
   *   \        /
   *    \------/
   *    E      D
   * A = (1, 0), B = (2, 0), C = (3, 1)
   * D = (2, 2), E = (1, 2), F = (0, 1)
   * where the displayed coordinates of (x, y) is (x * size, y * size),
   * and `size` is the # of pixels by which this image scales.
   * The coordinate system for hexagon grid is like this:
   * (0, 0)  (0, 1)
   *     (1, 0)  (1, 1)
   * (2, 0)  (2, 1)
   *     (3, 0)  (3, 1)
   * TODO may end up managing mouse event as well.
   */
  private static class BoardPanel extends JPanel {
    private static final int SIZE_SCALE = 30; // in pixels
    private static final Color POLYGON_COLOR = Color.ORANGE;
    // We start from the top left point, and move clockwise.
    // These points are relative to SIZE_SCALE.
    private static final int[] xArray = {1, 2, 3, 2, 1, 0};
    private static final int[] yArray = {0, 0, 1, 2, 2, 1};

    private Polygon[][] polygons;
    private GameState state;

    BoardPanel(GameState state) {
      this.updateGameState(state);
    }

    // update internal state to synchronize with given `state`
    void updateGameState(GameState state) {
      this.state = state;
      Board board = state.getBoardCopy();
      this.updatePanelDimension(board);
      this.polygons = this.createHexagons(board.getHeight(), board.getWidth());
    }

    // update panel dimension (for rendering) based on dimension of the board
    private void updatePanelDimension(Board board) {
      int height = board.getHeight(); // # of hexagons
      int width  = board.getWidth();  // # of hexagons
      int minHeight = (1 + height) * SIZE_SCALE;
      int minWidth = (1 + 4 * width) * SIZE_SCALE;
      this.setPreferredSize(new Dimension(minWidth, minHeight));
    }

    // create a 2d grid (with dimension of height X width) of Polygons.
    private Polygon[][] createHexagons(int height, int width) {
      Polygon[][] grid = new Polygon[height][width];
      for (int row = 0; row < height; row += 1) {
        for (int col = 0; col < width; col += 1) {
          Polygon p = new Polygon();
          for (int n = 0; n < Integer.min(xArray.length, yArray.length); n += 1) {
            Position relativeOrigin = this.getTopLeftBoundaryPos(row, col);
            int x = relativeOrigin.getCol() + xArray[n];
            int y = relativeOrigin.getRow() + yArray[n];
            p.addPoint(x * SIZE_SCALE, y * SIZE_SCALE);
          }
          grid[row][col] = p;
        }
      }
      return grid;
    }

    // for the hexagon located at (row, col), return the unscaled position of
    // the topleft point of the smallest rectangle enclosing the hexagon.
    // i.e. relative to this position, the hexagon at (row, col) is always at
    // top-left corner (0, 0) of the board.
    private Position getTopLeftBoundaryPos(int row, int col) {
      int colOffset = (row % 2) == 1 ? 2 : 0;
      return new Position(row, colOffset + 4 * col);
    }

    @Override
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      this.paintBoard(g, this.state.getBoardCopy());
      this.paintPenguins(g, this.state.getPenguinList());
    }

    // Render all the `penguins`
    private void paintPenguins(Graphics g, List<Penguin> penguins) {
      for (Penguin p : penguins) {
        int row = p.getPosition().getRow();
        int col = p.getPosition().getCol();
        Position relativeOrigin = this.getTopLeftBoundaryPos(row, col);
        g.drawImage(
            this.getPenguinImage(p.getPlayerColor()),
            (relativeOrigin.getCol() + 1) * SIZE_SCALE,
            relativeOrigin.getRow() * SIZE_SCALE,
            null); // no image observer
      }
    }

    // Render the `board` (i.e. the tiles and fish on it)
    private void paintBoard(Graphics g, Board board) {
      for (int row = 0; row < board.getHeight(); row += 1) {
        for (int col = 0; col < board.getWidth(); col += 1) {
          this.paintTile(g, row, col);
        }
      }
    }

    // render the tile at (row, col), along with the fish on it (if any).
    private void paintTile(Graphics g, int row, int col) {
      Board board = this.state.getBoardCopy();
      Tile tile = board.getTileAt(row, col);
      Polygon p = this.polygons[row][col];
      g.drawPolygon(p); // render an empty hexagon skeleton
      if (!tile.isRemoved()) {
        Color originalColor = g.getColor();
        g.setColor(POLYGON_COLOR);
        g.fillPolygon(p);
        g.setColor(originalColor);

        Position relativeOrigin = this.getTopLeftBoundaryPos(row, col);
        g.drawImage(
            this.getFishImage(tile.getNumFish()),
            (relativeOrigin.getCol() + 1) * SIZE_SCALE,
            relativeOrigin.getRow() * SIZE_SCALE,
            null); // no image observer
      }
    }

    // returned image has size (width = SIZE_SCALE, height = 2 * SIZE_SCALE)
    private Image getPenguinImage(PlayerColor color) {
      String path;
      switch (color) {
        case RED:   path = "/red-penguin.png"; break;
        case BLACK: path = "/black-penguin.png"; break;
        case WHITE: path = "/white-penguin.png"; break;
        default:    path = "/brown-penguin.png"; break;
      }
      return this.loadImageAndScale(path);
    }

    // returned image has size (width = SIZE_SCALE, height = 2 * SIZE_SCALE)
    private Image getFishImage(int fishNum) {
      String path = (fishNum < 5) ? "/" + fishNum + "fish.png" : "/too-many-fish.png";
      return this.loadImageAndScale(path);
    }

    // load image from `path`, where root is the resources directory,
    // and scale it to (width = SIZE_SCALE, height = 2 * SIZE_SCALE)
    private Image loadImageAndScale(String path) {
      try {
        Image img = ImageIO.read(BoardPanel.class.getResource(path));
        return img.getScaledInstance(SIZE_SCALE, 2 * SIZE_SCALE, Image.SCALE_SMOOTH);
      } catch (Exception e) {
        System.err.println("Cannot load resource image from: " + path);
        throw new RuntimeException(e);
      }
    }
  }
}
