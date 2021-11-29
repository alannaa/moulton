package com.cs4500.fish.remote;

import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.Position;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.cs4500.fish.common.DeserializationException;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.GameTree;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.player.Player;
import com.cs4500.fish.player.PlayerCommunicationException;

import java.util.Optional;

/**
 * Represents an administrator that serialize and deserialize JSON commands and return values for the
 * player it is in charge of. It lives on the client side and communicates with the proxyPlayer on the
 * server side through TCP connection in order for our remote players to interact with our Admins
 * (Tournament manager and Referees) on the server side.
 */
public class ProxyAdmin {

  private Player playerInChargeOf;

  public ProxyAdmin(Player player) {
    this.playerInChargeOf = player;
  }

  /**
   * Parses the command and executes it accordingly.
   */
  public JsonElement parseAndExecuteCommand(JsonArray jsonCommand)
          throws DeserializationException, PlayerCommunicationException {
    String commandName = jsonCommand.get(0).getAsString();
    JsonArray arguments = (JsonArray) jsonCommand.get(1);
    JsonElement outputForServer = executeCommand(commandName, arguments);
    return outputForServer;
  }

  /**
   * Executes the given command according to Remote Interaction given on the website.
   */
  private JsonElement executeCommand(String commandName, JsonArray arguments)
          throws DeserializationException, PlayerCommunicationException {
    switch (commandName) {
      case "start":
        boolean startArg = arguments.getAsBoolean();
        this.playerInChargeOf.informTournamentStatus(startArg);
        return new JsonPrimitive("void");

      case "playing-as":
        PlayerColor color = PlayerColor.deserialize(arguments);
        this.playerInChargeOf.assignColor(color);
        return new JsonPrimitive("void");

      case "playing-with":
        // do nothing cause proxyadmin does not need to know
        return new JsonPrimitive("void");

      case "setup":
        GameState setUpState = GameState.deserialize(arguments.get(0).getAsJsonObject());
        Position ps = this.playerInChargeOf.requestPenguinPlacement(setUpState);
        return ps.serialize();

      case "take-turn":
        GameState takeTurnState = GameState.deserialize(arguments.get(0).getAsJsonObject());
        Action ac =
            this.playerInChargeOf.requestAction(new GameTree(takeTurnState));
        return ac.getAsMove().serialize();
        
      case "end":
        boolean endArg = arguments.getAsBoolean();
        this.playerInChargeOf.informGameResult(endArg);
        return new JsonPrimitive("void");
      default:
        throw new IllegalArgumentException("No such command");
    }
  }
}


