package com.cs4500.fish.remote;

import com.google.gson.JsonStreamParser;

import com.cs4500.fish.admin.PlayerOutcome;
import com.cs4500.fish.common.BoardConfig;

import com.cs4500.fish.admin.PlayerSystemInteraction;
import com.cs4500.fish.admin.TournamentManager;
import com.cs4500.fish.player.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Server class that provides a TCP connection for our tournament manager and referees to interact
 * with remote players. It collects all remote player connections, creates proxy players for each of
 * them and passes them to the tournament manager to run a Fish tournament, if there are enough players.
 */
public class Server {

  private static final int MIN_NUM_PLAYERS = 5;
  private static final int MAX_NUM_PLAYERS = 10;
  private static final int GET_NAME_TIME_S = 10;
  private static final int MAX_NAME_LENGTH = 12;
  //30 sec, wait for clients
  private static final int WAIT_TIME_MS = 30000;
  //num times left to reenter waiting for clients:
  private static int REENTER_WAIT_INTERVAL = 1;

  private final ServerSocket serverSocket;
  private List<Player> proxyPlayerList;

  /**
   * Uses the given port to create server sockets in order for the players to connect into.
   */
  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    this.proxyPlayerList = new ArrayList<>();
  }

  /**
   * Runs the server to first wait for TCP connections to accept clients.
   * If there are enough players connected, it will start running the
   * tournament.
   */
  public Map<PlayerOutcome, List<Player>> runServer() {
    // accept clients, wait TWO interval waiting periods if needed
    this.acceptClients();
    //IF after two waiting intervals there are still not enough players:
    this.closeServerAndExitIfNeeded();

    return this.runTournament(this.proxyPlayerList);
  }

  /**
   * Waits for some time for at least a minimum number of remote clients to connect. The server will reenter
   * waiting state (only once) if there are not enough clients connected. Collects the player into its list of
   * connection if the player sends its name in 10s.
   */
  private void acceptClients() {
    while (REENTER_WAIT_INTERVAL >= 0 && this.proxyPlayerList.size() < MIN_NUM_PLAYERS) {
      int leftoverWaitTime = WAIT_TIME_MS;
      while (this.proxyPlayerList.size() < MAX_NUM_PLAYERS) {
        try {
          this.serverSocket.setSoTimeout(leftoverWaitTime);
          Long startTime = System.currentTimeMillis();
          Socket socket = this.serverSocket.accept();
          acceptPlayerIfValidName(socket);
          leftoverWaitTime =
              leftoverWaitTime - (int) (System.currentTimeMillis() - startTime);
        } catch (SocketTimeoutException e) {
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      REENTER_WAIT_INTERVAL -= 1;
    }
  }

  private void closeServerAndExitIfNeeded() {
    if (this.proxyPlayerList.size() < MIN_NUM_PLAYERS) {
      try {
        serverSocket.close();
        System.exit(0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  Map<PlayerOutcome, List<Player>> runTournament(List<Player> proxyPlayerList) {
    BoardConfig conf = new BoardConfig().setWidth(5).setHeight(5).setDefaultFish(2);
    TournamentManager manager = new TournamentManager();
    manager.setBoardConfig(conf);
    Map<PlayerOutcome, List<Player>> tournamentOutcome =
        manager.runTournament(proxyPlayerList);
    // close server once done
    try {
      this.serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return tournamentOutcome;
  }

  /**
   * Collects the player into its list of connection if the player sends its name in 10s.
   */
  private void acceptPlayerIfValidName(Socket socket) throws IOException {
    //get input and output streams
    InputStream inputStream = socket.getInputStream();
    OutputStream outputStream = socket.getOutputStream();
    JsonStreamParser inputAsJson = new JsonStreamParser(new InputStreamReader(inputStream));

    //Keep listening for name to come in on inputStream for 10 seconds:
    Optional<String> name =
        PlayerSystemInteraction.requestResponseTimeout(() ->
            inputAsJson.next().getAsString(), GET_NAME_TIME_S);

    if (name.isPresent()
        && name.get().length() != 0
        && name.get().length() <= MAX_NAME_LENGTH) {
      //Add player at index zero to keep the list in ascending age-order
      proxyPlayerList.add(0, new ProxyPlayer(inputStream, outputStream));
    } else {
      socket.close();
    }
  }

}
