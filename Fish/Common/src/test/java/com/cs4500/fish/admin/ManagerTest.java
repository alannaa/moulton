package com.cs4500.fish.admin;

import com.cs4500.fish.common.*;
import com.cs4500.fish.player.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ManagerTest {

		// Check that `r.run()` results in `IllegalArgumentException` with `msg` as
		// message.
		private void checkIllegalArgExn(Runnable r, String msg) {
				try {
						r.run();
				} catch (IllegalArgumentException e) {
						assertEquals(msg, e.getMessage());
						return;
				}
				fail("Expected IllegalArgumentException[" + msg + "]");
		}

		private List<Player> getNPlayers(Integer n) {
				List<Player> players = new ArrayList<>();
				for (int i = 0; i < n; i += 1) {
						Player p = new AIPlayer(new ScanPlacementStrategy(),
														new MinimaxTurnActionStrategy(1));
						players.add(p);
				}
				return players;
		}


		@Test
		public void testAssignPlayersToGames() {
			List<Player> players = getNPlayers(9);
			TournamentManager tm = new TournamentManager();
			List<List<Player>> allocatedGames = tm.assignPlayersToGames(players);
			assertEquals(3, allocatedGames.size());
			assertEquals(4, allocatedGames.get(0).size());
			assertEquals(3, allocatedGames.get(1).size());
			assertEquals(2, allocatedGames.get(2).size());
		}

		@Test
		public void testRunOneRound() {
			List<Player> players = getNPlayers(9);
			TournamentManager tm = new TournamentManager();
			List<List<Player>> allocatedGames = tm.assignPlayersToGames(players);
			tm.runRound(allocatedGames);

			assertEquals(4, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(5, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.FAILURE).size());

			List<List<Player>> nextAllocatedGames =
					tm.assignPlayersToGames(tm.constructResults().get(PlayerOutcome.WINNER));
			tm.runRound(nextAllocatedGames);

			assertEquals(2, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(7, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

		//test connect 5 good players
		@Test
		public void testRunningTournamentFiveGoodPlayers(){
			List<Player> players = getNPlayers(5);
			TournamentManager tm = new TournamentManager();
			tm.runTournament(players);
			assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(4, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

			//test connect 6 players, one times out
		@Test
		public void testRunningTournamentSixPlayers(){
			List<Player> players = getNPlayers(5);
			players.add(new PlayerTimesOutInTournamentStatus());
			TournamentManager tm = new TournamentManager();
			tm.runTournament(players);
			assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(4, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(1, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

		//test connect 10 good players
		@Test
		public void testRunningTournamentTenPlayers(){
			List<Player> players = getNPlayers(10);
			TournamentManager tm = new TournamentManager();
			tm.runTournament(players);
			assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(9, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

		//test connect 5 good players; 5 bad players, one per call
		@Test
		public void testRunningTournamentFiveBadPlayers(){
			List<Player> players = getNPlayers(5);
			players.add(new PlayerTimesOutInTournamentStatus());
			players.add(new PlayerTimesOutInPlayingAs());
			players.add(new PlayerTimesOutInPlayingWith());
			players.add(new PlayerCheatsInPengPlacement());
			players.add(new PlayerCheatsInTakingTurns());


			TournamentManager tm = new TournamentManager();
			tm.runTournament(players);
			assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(4, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(2, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(3, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

		//test connect 10 players but one times out during informPlayers
		@Test
		public void testRunningTournamentOneTimeout(){
			List<Player> players = getNPlayers(9);
			players.add(new PlayerTimesOutInTournamentStatus());
			TournamentManager tm = new TournamentManager();
			tm.runTournament(players);
			assertEquals(2, tm.constructResults().get(PlayerOutcome.WINNER).size());
			assertEquals(7, tm.constructResults().get(PlayerOutcome.LOSER).size());
			assertEquals(0, tm.constructResults().get(PlayerOutcome.CHEATER).size());
			assertEquals(1, tm.constructResults().get(PlayerOutcome.FAILURE).size());
		}

	//test connect 2 good, 4 bad ones players, including end of tournament
	@Test
	public void testRunningTournamentFourBadPlayers(){
		List<Player> players = getNPlayers(2);
		players.add(new PlayerTimesOutInPlayingAs());
		players.add(new PlayerTimesOutInPlayingWith());
		players.add(new PlayerCheatsInPengPlacement());
		players.add(new PlayerTimesOutInEndTournament());

		TournamentManager tm = new TournamentManager();
		tm.runTournament(players);
		assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.LOSER).size());
		assertEquals(1, tm.constructResults().get(PlayerOutcome.CHEATER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.FAILURE).size());
	}

	//test connect 6 good players; 4 bad players, one per call
	@Test
	public void testRunningTournamentSixGoodFourBadPlayers(){
		List<Player> players = new ArrayList<>(getNPlayers(3));
		players.add(new PlayerTimesOutInPlayingWith());
		players.add(new PlayerTimesOutInPlayingAs());
		players.add(new PlayerCheatsInPengPlacement());
		players.add(new PlayerCheatsInTakingTurns());
		players.addAll(getNPlayers(3));

		TournamentManager tm = new TournamentManager();
		List<List<Player>> allocatedGames = tm.assignPlayersToGames(players);
		tm.runRound(allocatedGames);

		assertEquals(3, tm.constructResults().get(PlayerOutcome.WINNER).size());
		assertEquals(3, tm.constructResults().get(PlayerOutcome.LOSER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.CHEATER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.FAILURE).size());

		List<List<Player>> nextAllocatedGames =
				tm.assignPlayersToGames(tm.constructResults().get(PlayerOutcome.WINNER));
		tm.runRound(nextAllocatedGames);

		assertEquals(1, tm.constructResults().get(PlayerOutcome.WINNER).size());
		assertEquals(5, tm.constructResults().get(PlayerOutcome.LOSER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.CHEATER).size());
		assertEquals(2, tm.constructResults().get(PlayerOutcome.FAILURE).size());
	}


		@Test
		public void testAssignPlayersThrowIllegalArg() {
				BoardConfig conf = new BoardConfig();
				conf.setHeight(5).setWidth(5).setDefaultFish(2);
				TournamentManager manager = new TournamentManager();
				manager.setBoardConfig(conf);
				List<Player> players = new ArrayList<>();
				checkIllegalArgExn(() -> manager.assignPlayersToGames(players),
												"There are too few players for a game.");
		}




		private class PlayerTimesOutInTournamentStatus implements Player {

			@Override
			public boolean informTournamentStatus(boolean status) {
				while(true){
					//do nothing
				}
			}

			@Override
			public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
				return new ScanPlacementStrategy().apply(state);
			}

			@Override
			public Action requestAction(GameTree state) throws PlayerCommunicationException {
				return new MinimaxTurnActionStrategy(1).apply(state);
			}
		}

	private class PlayerTimesOutInPlayingAs implements Player {

		@Override
		public boolean assignColor(PlayerColor color) {
			while(true){
				//do nothing
			}
		}

		@Override
		public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
			return new ScanPlacementStrategy().apply(state);
		}

		@Override
		public Action requestAction(GameTree state) throws PlayerCommunicationException {
			return new MinimaxTurnActionStrategy(1).apply(state);
		}
	}

	private class PlayerTimesOutInPlayingWith implements Player {

		@Override
		public boolean informOpponentColors(List<PlayerColor> colors) {
			while(true){
				//do nothing
			}
		}

		@Override
		public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
			return new ScanPlacementStrategy().apply(state);
		}

		@Override
		public Action requestAction(GameTree state) throws PlayerCommunicationException {
			return new MinimaxTurnActionStrategy(1).apply(state);
		}
	}

	private class PlayerCheatsInPengPlacement implements Player {

		@Override
		public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
			return new Position(0,0);
		}

		@Override
		public Action requestAction(GameTree state) throws PlayerCommunicationException {
			return new MinimaxTurnActionStrategy(1).apply(state);
		}
	}

	private class PlayerCheatsInTakingTurns implements Player {

		@Override
		public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
			return new ScanPlacementStrategy().apply(state);
		}

		@Override
		public Action requestAction(GameTree state) throws PlayerCommunicationException {
			return new Move(new Position(0,0), new Position(0,1));
		}
	}

	private class PlayerTimesOutInEndTournament implements Player {

		@Override
		public boolean informGameResult(boolean won) {
			while(true){
				//do nothing
			}
		}

		@Override
		public Position requestPenguinPlacement(GameState state) throws PlayerCommunicationException {
			return new ScanPlacementStrategy().apply(state);
		}

		@Override
		public Action requestAction(GameTree state) throws PlayerCommunicationException {
			return new MinimaxTurnActionStrategy(1).apply(state);
		}
	}


}
