package cs4500;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;
import java.io.IOException;

/**
 * A class to open up a window and draw a hexgon, based on size specified via 
 * command line input.
 */
public class GuiMain {

  private static final String FRAME_TITLE = "Hexagon";

  /**
   * Driver entry.
   */
  public static void main(String[] args) {
    drawHexagon(args, System.out);
  }

  /**
   * Parse the command line arguments, open up a window and draw a hexgon based
   * on size.
   */
  static void drawHexagon(String[] args, Appendable out) {
    // parse command line arguments
    int size;
    Optional<Integer> optInt;
    if (args.length != 1 ||
        !(optInt = parseInt(args[0])).isPresent() ||
        (size = optInt.get()) <= 0 ) {
      try {
        out.append("usage: ./xgui positive-integer").append('\n');
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return;
    }
    // open up a window and draw hexagon
    javax.swing.SwingUtilities.invokeLater(() -> {
      final JFrame frame = new JFrame(FRAME_TITLE);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(new HexagonPanel(size, frame::dispose));
      frame.pack(); // let layout manager handle component/frame sizes
      frame.setVisible(true);
    });
  }

  /**
   * Parses an integer from `str`.
   * @return Optional.empty() if there was an error in parsing.
   */
  private static Optional<Integer> parseInt(String str) {
    try {
      return Optional.of(Integer.parseInt(str));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * A listener to handle high level Hexagon GUI events.
   */
  private interface HexagonGuiListener {
    void onClose();
  }

  /**
   *    A      B
   *    /------\
   * F /        \ C
   *   \        /
   *    \------/
   *    E      D
   * A = (1, 0), B = (2, 0), C = (3, 1)
   * D = (2, 2), E = (1, 2), F = (0, 1)
   * where the displayed coordinates of (x, y) is (x * size, y * size).
   */
  private static class HexagonPanel extends JPanel implements MouseListener {
    private final Polygon p;
    private final HexagonGuiListener listener;
    // We start from the top left point, and move clockwise. These points are relative to size.
    private static final int[] xArray = {1, 2, 3, 2, 1, 0};
    private static final int[] yArray = {0, 0, 1, 2, 2, 1};

    /**
     * @param size is the size as explaiend in class documentation.
     * @param listener is the listener for high level close event.
     */
    public HexagonPanel(int size, HexagonGuiListener listener){
      this.p = new Polygon();
      this.listener = listener;
      this.setPreferredSize(new Dimension(size * 3, size * 2));
      this.addMouseListener(this);
      for (int i = 0; i < Integer.min(xArray.length, yArray.length); i++) {
        p.addPoint(xArray[i] * size, yArray[i] * size);
      }
    }

    @Override
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      g.drawPolygon(p);
    }

    // Required methods for MouseListener, but we only care about `click`
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override 
    public void mouseClicked(MouseEvent e) {
      Point clicked = e.getPoint();
      if (p.contains(clicked)) {
        this.listener.onClose();
      }
    }
  }
}
