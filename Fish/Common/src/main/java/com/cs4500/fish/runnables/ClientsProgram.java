package com.cs4500.fish.runnables;

import com.cs4500.fish.player.AIPlayer;
import com.cs4500.fish.player.MinimaxTurnActionStrategy;
import com.cs4500.fish.player.PlacementStrategy;
import com.cs4500.fish.player.Player;
import com.cs4500.fish.player.ScanPlacementStrategy;
import com.cs4500.fish.player.TurnActionStrategy;
import com.cs4500.fish.remote.Client;

public class ClientsProgram {

  private static final String DEFAULT_IP_ADDRESS = "127.0.0.1";

  public static void main(String[] args) {

    if (args.length != 3 && args.length != 2) {
      System.out.print("Usage: ./xclients [number of clients] [port-number] <ip-address>\n");
      return;
    }

    int numClients = Integer.parseInt(args[0]);
    int port = Integer.parseInt(args[1]);
    String ip = getIPAddress(args);

    PlacementStrategy placementStrategy = new ScanPlacementStrategy();
    TurnActionStrategy turnActionStrategy = new MinimaxTurnActionStrategy(1);

    for (int i = 0; i < numClients; i++) {
      Player p = new AIPlayer(placementStrategy, turnActionStrategy);
      new Thread(new Client(port, ip, p)::runClient).start();
    }

  }

  private static String getIPAddress(String[] args) {
    if (args.length == 3) {
      return args[2];
    } else {
      return DEFAULT_IP_ADDRESS;
    }
  }
}
