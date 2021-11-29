package cs4500;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

/**
 * Driver for a program which consumes JSON values from STDIN and print out 2
 * summary JSON values to STDOUT:
 * - { "count" : # of input JSON values, 
 *     "seq"   : Array of input JSON values in order }
 * - [# of input JSON values, followed by the input JSON values in reversed order].
 *
 * Example output one: {count: 3 , seq: [3, false, 1]}
 *                     {count: 5 , seq: [3, d, 3, q, 1]}
 *
 * Example output two: [3, 3, false, 1]
 *                     [5, 3, d, 3, q, 1]
 */
public class JsonMain {

  // Constants for JSON output object fields
  private static final String COUNT_FIELD = "count";
  private static final String SEQ_FIELD   = "seq";

  /**
   * Return whether `stream` has encountered EOF.
   */
  static boolean isEOF(Reader stream) throws IOException {
    // not all Reader implementations support `mark`
    assert(stream.markSupported());
    stream.mark(1);
    int c = stream.read();
    stream.reset(); // reset to marked position above.
    return c == -1;
  }

  /**
   * Parse JSON values from `input` and
   * Print 2 summary JSON values to `output` based on specs from class doc.
   */
  static void countAndReverse(Reader input, Appendable output) {
    if (!input.markSupported()) {
      input = new BufferedReader(input); // avoid reference to original `input`
    }
    ArrayList<JsonElement> inputList = new ArrayList<>();
    try {
      // The parser can't parse an empty stream, so we explicitly
      // check that case, since it shouldn't result in error.
      // Still can't handle empty stream with white spaces, will handle
      // this if it becomes critical.
      if (!isEOF(input)) {
        new JsonStreamParser(input).forEachRemaining(inputList::add);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Write to output
    try {
      output.append(buildFirstJsonValue(inputList) + "\n");
      output.append(buildSecondJsonValue(inputList) + "\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Takes a list of JSON inputs and outputs in the following format:
   * - { "count" : # of input JSON values,
   *     "seq"   : Array of input JSON values in order }
   *
   * @param inputList a list of parsed JSON inputs
   * @return a formated Json summary of the given inputs
   */
  private static JsonElement buildFirstJsonValue(List<JsonElement> inputList) {
    JsonArray values = new JsonArray();
    inputList.forEach(values::add);

    // First output object
    JsonObject jObject = new JsonObject();
    jObject.addProperty(COUNT_FIELD, inputList.size());
    jObject.add(SEQ_FIELD, values);

    return jObject;
  }

  /**
   * Takes a list of JSON inputs and outputs in the following format:
   * - [# of input JSON values, followed by the input JSON values in reversed order]
   *
   * @param inputList a list of parsed JSON inputs
   * @return a formated Json summary of the given inputs
   */
  private static JsonElement buildSecondJsonValue(List<JsonElement> inputList) {
    inputList = new ArrayList<>(inputList); // Create a copy of inputList
    Collections.reverse(inputList); // Reverse the copied inputList rather than original
    JsonArray reversedValues = new JsonArray();
    inputList.forEach(reversedValues::add);

    // Second output array
    JsonArray jArray = new JsonArray();
    jArray.add(inputList.size());
    jArray.addAll(reversedValues);

    return jArray;
  }

  /**
   * Driver entry.
   */
  public static void main(String[] args) {
    Reader in = new InputStreamReader(System.in);
    Appendable out = System.out;
    countAndReverse(in, out);
  }
}
