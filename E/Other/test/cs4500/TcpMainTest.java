package cs4500;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.StringWriter;

/**
 * Tests for the Tcp program.
 */
public class TcpMainTest {

  void testTcpServerFail(String[] args) {
    StringWriter output = new StringWriter();
    String expected = "Usage: ./xtcp <port-number>\n";
    TcpMain.runServer(args, output);
    assertEquals(expected, output.toString());
  }

	@Test
	public void testInputTooManyArgs() {
	String[] input = {"1234", "42"};
	testTcpServerFail(input);
	}

	@Test
	public void testInputNotAnInt() {
    String[] input = {"42foobar"};
    testTcpServerFail(input);
	}
}
