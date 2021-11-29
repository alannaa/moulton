package com.cs4500.fish.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import com.cs4500.fish.common.Action;
import com.cs4500.fish.common.DeserializationException;
import com.cs4500.fish.common.GameState;
import com.cs4500.fish.common.GameTree;
import com.cs4500.fish.common.Move;
import com.cs4500.fish.common.PlayerColor;
import com.cs4500.fish.common.Position;
import com.cs4500.fish.player.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Represents a single remote player on our server side. It serializes and
 * deserializes JSON commands and return values during the interaction of
 * requesting for the remote player's moves by communicating
 * with the proxyAdmin on the client side through the TCP connection.
 */
public class ProxyPlayer implements Player {

  private InputStream in;
  private OutputStream out;

  public ProxyPlayer(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  @Override
  public Position requestPenguinPlacement(GameState state) throws DeserializationException {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("setup");
    JsonArray arguments = new JsonArray();
    arguments.add(state.serialize());
    outputToClient.add(arguments);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonArray position = input.next().getAsJsonArray();
    return Position.deserialize(position);
  }

  @Override
  public Action requestAction(GameTree state) throws DeserializationException {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("take-turn");
    JsonArray arguments = new JsonArray();
    arguments.add(state.getState().serialize());
    arguments.add(new JsonArray());
    outputToClient.add(arguments);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonArray action = input.next().getAsJsonArray();
    return Move.deserialize(action);
  }

  @Override
  public boolean assignColor(PlayerColor color) {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("playing-as");
    JsonArray arguments = new JsonArray();
    arguments.add(color.serialize());
    outputToClient.add(arguments);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonPrimitive voidMsg = input.next().getAsJsonPrimitive();
    return true;
  }

  @Override
  public boolean informOpponentColors(List<PlayerColor> colors) {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("playing-with");
    JsonArray outterArray = new JsonArray();
    JsonArray arguments = new JsonArray();
    for (PlayerColor pc : colors) {
      arguments.add(pc.serialize());
    }
    outterArray.add(arguments);
    outputToClient.add(outterArray);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonPrimitive voidMsg = input.next().getAsJsonPrimitive();
    return true;
  }

  @Override
  public boolean informTournamentStatus(boolean tStatus) {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("start");
    JsonArray arguments = new JsonArray();
    arguments.add(new JsonPrimitive(true));
    outputToClient.add(arguments);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonPrimitive voidMsg = input.next().getAsJsonPrimitive();
    return true;
  }

  @Override
  public boolean informGameResult(boolean wonGame) {
    //Send out request to client
    JsonArray outputToClient = new JsonArray();
    outputToClient.add("end");
    JsonArray arguments = new JsonArray();
    arguments.add(new JsonPrimitive(wonGame));
    outputToClient.add(arguments);
    this.sendToClient(outputToClient);
    //Wait for response from client
    JsonStreamParser input = new JsonStreamParser(new InputStreamReader(this.in));
    JsonPrimitive voidMsg = input.next().getAsJsonPrimitive();
    return true;
  }

  /**
   * Sends the output onto the output stream.
   */
  private void sendToClient(JsonArray outputToClient) {
    try {
      new OutputStreamWriter(this.out).append(outputToClient.toString()).flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
