package cs4500;
import java.util.Arrays;
import java.io.IOException;

/**
 * Driver for a program which repeatedly echos command line arguments, unless
 * explicitly limited.
 */
public class EchoMain {

  // If echoing is limited to finite repetitions, this is the # of repetitions.
  private static final int    REPETITION_LIMIT = 20;
  // If no command line argument is supplied, use this as output.
  private static final String DEFAULT_MESSAGE  = "hello world";

  /**
   * Based on the inputs in `args`, either finitely or infinitely print 
   * the string concatenated from `args` to `output`.
   * If args[0] == "-limit", print only REPETITION_LIMIT number of times.
   * If no argument (excluding "-limit") is specified, print DEFAULT_MESSAGE.
   * @param args The input command line arguments as an array of String.
   * @param output Where to direct the output.
   */
  static void echo(String[] args, Appendable output) {
    String msg = DEFAULT_MESSAGE;
    boolean limited = false;

    // If `-limit` flag is specified, handle it.
    if (args.length > 0 && args[0].equals("-limit")) {
      limited = true;
      args = Arrays.copyOfRange(args, 1, args.length);
    }

    // If there are more arguments, replace message with them.
    if (args.length > 0) {
      msg = String.join(" ", args);
    }

    // print a message for a finite or inifite amount of times based on the 
    // limited flag
    for (int i = 0; i < REPETITION_LIMIT || !limited; i += 1) {
      try {
        output.append(msg);
        output.append('\n');
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }

  /**
   * Driver entry.
   */
  public static void main(String[] args) {
    echo(args, System.out);
  }
}
