package cs4500;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.StringWriter;

/**
 * Tests for the Echo program.
 */
public class EchoMainTest {

  @Test
  // with limit flag and no other argument
  public void testLimitedDefault() {
    StringWriter output = new StringWriter();
    String[] args = {"-limit"};
    EchoMain.echo(args, output);
    String expect = "";
    for (int i = 0; i < 20; i += 1) {
      expect += "hello world\n";
    }
    assertEquals(expect, output.toString());
  }

  @Test
  // with limit flag and more arguments
  public void testLimitedCustom() {
    StringWriter output = new StringWriter();
    String[] args = {"-limit", "-unlimit", "foo\n"};
    EchoMain.echo(args, output);
    String expect = "";
    for (int i = 0; i < 20; i += 1) {
      expect += "-unlimit foo\n\n";
    }
    assertEquals(expect, output.toString());
  }
}
