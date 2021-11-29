//package com.cs4500.fish.integration;
//
//import com.cs4500.fish.admin.GameResult;
//import com.cs4500.fish.admin.Referee;
//import com.cs4500.fish.common.BoardConfig;
//import com.cs4500.fish.player.AIPlayer;
//import com.cs4500.fish.player.MinimaxTurnActionStrategy;
//import com.cs4500.fish.player.PlacementStrategy;
//import com.cs4500.fish.player.Player;
//import com.cs4500.fish.player.ScanPlacementStrategy;
//import com.cs4500.fish.player.TurnActionStrategy;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonStreamParser;
//
//import java.io.*;
//import java.util.*;
//
///**
//	* A program that serves as an integration test for the referee component
//	*/
//public class RefereeTest {
//
//		private static final String ROW_FIELD = "row";
//		private static final String COLUMN_FIELD = "column";
//		private static final String PLAYERS_FIELD = "players";
//		private static final String FISH_FIELD = "fish";
//
//
//		/**
//			* Parse in a Game Description, and run a game based on the information it
//			* provides. The output will be a list of Strings, representing the winners
//			* of the game.
//			* - GameDescription is a Json Object that contains four fields
//			*  -  { "row"     : Natural in [2,5],
//	  *       "column"  : Natural in [2,5],
//  	*       "players" : [[String, D], ,,,, [String, D]]
//	  *       "fish"    : Natural in [1,5] }
//			* - D is an integer, representing the depth that each player should use to
//			* 			make their decisions
//			* Ex:
//			* Input:
//			* -  { "row"     : 2,
//			* 	    "column"  : 5,
//			*   	  "players" : [["jkl", 2], ["asdf", 1]]]
//			* 	    "fish"    : 1 }
//			*    ]
//			* Output:
//			* - The best move "white" can make, e.g., [[1, 2], [1, 0]]
//			*/
//		static void playAGame(Reader input, Appendable output) {
//				JsonStreamParser parser = new JsonStreamParser(input);
//				JsonObject gameStateElement = parser.next().getAsJsonObject();
//				PlacementStrategy placeStrat = new ScanPlacementStrategy();
//
//				try {
//						int row = gameStateElement.get(ROW_FIELD).getAsInt();
//						int column = gameStateElement.get(COLUMN_FIELD).getAsInt();
//						int fish = gameStateElement.get(FISH_FIELD).getAsInt();
//						Map<String, Integer> playerDepthMap = createPlayerMap(gameStateElement);
//						Map<Player, String> AIPlayerMap = new HashMap<>();
//
//						BoardConfig conf = new BoardConfig().setDefaultFish(fish)
//														.setHeight(row).setWidth(column);
//						List<Player> players = new ArrayList<>();
//
//						for (String s : playerDepthMap.keySet()) {
//								TurnActionStrategy turnStrat =
//																new MinimaxTurnActionStrategy(playerDepthMap.get(s));
//								Player p = new AIPlayer("name", placeStrat, turnStrat);
//								players.add(p);
//								AIPlayerMap.put(p, s);
//						}
//
//						Referee ref = new Referee();
//						GameResult result = ref.runGame(conf, players);
//						List<String> nameList = new ArrayList<>();
//
//						for (Player p : result.getWinners()) {
//								nameList.add(AIPlayerMap.get(p));
//						}
//						JsonArray outputArray = new JsonArray();
//						Collections.sort(nameList);
//						output.append(nameList.toString());
//				} catch (IOException e) {
//						throw new RuntimeException(e);
//				}
//		}
//
//		private static Map<String, Integer> createPlayerMap(JsonObject gameStateElement) {
//				Map<String, Integer> playerMap = new HashMap<>();
//				for (JsonElement a : gameStateElement.get(PLAYERS_FIELD).getAsJsonArray()) {
//						JsonArray playerArray = a.getAsJsonArray();
//						playerMap.put(playerArray.get(0).getAsString(),
//														playerArray.get(1).getAsInt());
//				}
//				return playerMap;
//		}
//
//		/**
//			* Program entry.
//			*/
//		public static void main(String[] args) {
//				Reader in = new InputStreamReader(System.in);
//				Appendable out = System.out;
//				playAGame(in, out);
//		}
//}
