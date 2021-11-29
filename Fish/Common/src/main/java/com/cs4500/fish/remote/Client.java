package com.cs4500.fish.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import com.cs4500.fish.common.DeserializationException;
import com.cs4500.fish.player.Player;
import com.cs4500.fish.player.PlayerCommunicationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Client class that provides a TCP connection for our remote players to sign up and join our Fish tournament.
 * Uses provided IP, port to create a socket and will be designated for the given player.
 * The client will pass the player to the proxy admin that will play as a proxy "referee" and act according
 * to the JSON commands that comes through the socket, calling methods on the designated player accordingly.
 */
public class Client {

  private final int port;
  private final String ip;
  private JsonStreamParser input;
  private OutputStreamWriter output;
  private final ProxyAdmin proxyAdmin;
  private static final int TRIES = 10;

  /**
   * Uses the given port and IP to create sockets in order for the given player
   * to connect into the game.
   */
  public Client(int port, String ip, Player player) {
    this.port = port;
    this.ip = ip;
    this.proxyAdmin = new ProxyAdmin(player);
    this.createConnection();
  }

  //Set up input and output stream
  private void createConnection() {
    for (int count = 0; count <= TRIES; count++) {
      try {
        Socket socket = new Socket(this.ip, this.port);
        input = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        output = new OutputStreamWriter(socket.getOutputStream());
        output.append("\"name\"").flush();
        break;
      } catch (UnknownHostException e) {
        System.out.println("host not found: " + e.getMessage());
      } catch (IOException e) {
        System.out.println("I/O: " + e.getMessage());
      } try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {}
      if (count == TRIES) {
        throw new RuntimeException("");
      }
    }
  }

  /**
   * Sends the name of the player to the server in order to sign up and
   * continuously read for JSON commands that comes through the socket and
   * passes it to the proxy admin. When there is a reply, send it back through
   * the socket. The socket will close when the player has received the "end"
   * JSON command.
   */
  public void runClient() {
    try {
      while (input.hasNext()) {
        JsonArray commandFromServer = input.next().getAsJsonArray();
        JsonElement outputToServer = this.proxyAdmin
            .parseAndExecuteCommand(commandFromServer);
        output.append(outputToServer.toString()).flush();
      }
    } catch (IOException | DeserializationException | PlayerCommunicationException e) {
      e.printStackTrace();
    }
  }
}
