package cs4500;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.StringReader;

/**
 * Tests for the JSON program.
 */
public class JsonMainTest {

	@Test
	public void testCountAndReverseWorking() {

		String input = "1 \"a\" \ntrue";
		StringBuilder output = new StringBuilder();
		StringReader reader = new StringReader(input);
		String expected = "{\"count\":3,\"seq\":[1,\"a\",true]}\n[3,true,\"a\",1]\n";

		JsonMain.countAndReverse(reader, output);
		assertEquals(expected, output.toString());
	}

	@Test
	public void tesNullCountAndReverse() {

		String input = "";
		StringBuilder output = new StringBuilder();
		StringReader reader = new StringReader(input);
		String expected = "{\"count\":0,\"seq\":[]}\n" + "[0]\n";

		JsonMain.countAndReverse(reader, output);
		assertEquals(expected, output.toString());
	}

	@Test
	public void testNestedCountAndReverse() {

		String input = "1 \"a\" \ntrue \n[1, [\"b\", \"c\", \"d\"], {\"count\":3,\"seq\":[true,\"a\",1]}] [] {}";
		StringBuilder output = new StringBuilder();
		StringReader reader = new StringReader(input);
		String expected =
      "{\"count\":6,\"seq\":[1,\"a\",true," +
      "[1,[\"b\",\"c\",\"d\"],{\"count\":3,\"seq\":[true,\"a\",1]}],[],{}]}\n" +
      "[6,{},[],[1,[\"b\",\"c\",\"d\"],{\"count\":3,\"seq\":[true,\"a\",1]}],true,\"a\",1]\n";

		JsonMain.countAndReverse(reader, output);
		assertEquals(expected, output.toString());
	}
}
