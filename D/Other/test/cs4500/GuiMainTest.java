package cs4500;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.StringWriter;

/**
 * Tests for the Hexagon GUI program.
 */
public class GuiMainTest {

  public void testBadInput(String[] input) {
    StringWriter output = new StringWriter();
    String expected = "usage: ./xgui positive-integer\n";
    GuiMain.drawHexagon(input, output);
    assertEquals(expected, output.toString());
  }

	@Test
	public void testInputNoArgs() {
    String[] input = {};
    testBadInput(input);
	}

	@Test
	public void testInputTooManyArgs() {
    String[] input = {"20", "42"};
    testBadInput(input);
	}

	@Test
	public void testInputNotAnInt() {
    String[] input = {"42foobar"};
    testBadInput(input);
	}

	@Test
	public void testInputOutOfRangeSize() {
    String[] input = {"-10"};
    testBadInput(input);
	}
}
