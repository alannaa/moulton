package cs4500;

import java.util.Optional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * A program that consumes a sequence of well-formed JSON values from the input
 * side of a TCP connection and delivers JSON to the output side of a TCP
 * connection after the input side is closed.
 */
public class TcpMain {

  // How long to wait for the first connection before time out
  private static final int ACCEPT_TIMEOUT_MS = 3000;
  // Default port for server to listen on
  private static final int DEFAULT_LISTEN_PORT = 4567;

  /**
   * Set up a server which reads in JSON values from a client and echos summary
   * JSON values to the client. 
   * @param args either contains 1 integer (the port number) or nothing.
   * @param output Where to output error message.
   */
  static void runServer(String[] args, Appendable output) {
    // parse command line arguments
    Optional<Integer> optInt = parsePort(args);
    if (! optInt.isPresent()) {
      append(output, "Usage: ./xtcp <port-number>\n");
      return;
    }
    int port = optInt.get();

    try {
      ServerSocket serverSock = new ServerSocket(port);
      serverSock.setSoTimeout(ACCEPT_TIMEOUT_MS);
      Socket clientSock = serverSock.accept(); // get a client connection
      Reader in = new InputStreamReader(clientSock.getInputStream());
      OutputStreamWriter out = new OutputStreamWriter(clientSock.getOutputStream());
      JsonMain.countAndReverse(in, out); // do the work
      out.flush(); // make sure output is sent
      // clean up
      clientSock.close();
      serverSock.close();

    } catch (SocketTimeoutException timeout) {
      append(output, "No client connected after " + ACCEPT_TIMEOUT_MS + " ms\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Append `s` to `out` and throw RuntimeException on IOException.
   */
  private static void append(Appendable out, String s) {
    try {
      out.append(s);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses the input port from `args`, default to `DEFAULT_LISTEN_PORT`
   * if no argument is specified.
   * @return Optional.empty() on invalid input args.
   */
  private static Optional<Integer> parsePort(String[] args) {
    try {
      if (args.length == 0) {
        return Optional.of(DEFAULT_LISTEN_PORT);
      } else if (args.length == 1) {
        return Optional.of(Integer.parseInt(args[0]));
      }
      return Optional.empty();

    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * Program entry.
   */
  public static void main(String[] args) {
    runServer(args, System.out);
  }
}
