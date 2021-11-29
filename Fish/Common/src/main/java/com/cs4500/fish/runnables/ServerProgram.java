package com.cs4500.fish.runnables;

import com.cs4500.fish.admin.PlayerOutcome;
import com.cs4500.fish.player.Player;
import com.google.gson.JsonArray;

import com.cs4500.fish.remote.Server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServerProgram {

  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.print("Usage: ./xserver [port number]\n");
      return;
    }
    int port = Integer.parseInt(args[0]);

    try {
      Server server = new Server(port);

      Map<PlayerOutcome, List<Player>> result = server.runServer();

      JsonArray output = new JsonArray();
      output.add(result.get(PlayerOutcome.WINNER).size());
      output.add(result.get(PlayerOutcome.CHEATER).size() + result.get(PlayerOutcome.FAILURE).size());
      System.out.print(output);
      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
