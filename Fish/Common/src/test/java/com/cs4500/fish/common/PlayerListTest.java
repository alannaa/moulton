package com.cs4500.fish.common;

import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.ArrayList;

public class PlayerListTest {


  //Examples
  private PlayerList twoPlayers;
  private PlayerList fourPlayers;

  @Before
  public void setUp() {
    this.twoPlayers =
        new PlayerList(Arrays.asList(PlayerColor.values()).subList(0,2));
    this.fourPlayers = new PlayerList(Arrays.asList(PlayerColor.values()));
  }

  @Test
  public void testPlayerListConstruction() {
    assertTrue(this.twoPlayers.getPenguinList().isEmpty());
    List<PlayerState> players = this.twoPlayers.getOrderedPlayers();
    List<PlayerColor> colors = Arrays.asList(PlayerColor.values()).subList(0,2);

    assertEquals(2, players.size());
    assertEquals(2, colors.size());

    for (int i = 0; i < players.size(); i += 1) {
      PlayerState aPlayer = players.get(i);
      assertEquals(colors.get(i), aPlayer.getPlayerColor());
      assertEquals(0, aPlayer.getScore());
      assertEquals(i, aPlayer.getAge());
    }
  }

  @Test
  public void testConstructorWithEmptyColorList() {
    List<PlayerColor> empty = new ArrayList<>();
    PlayerList emptyPL = new PlayerList(empty);
  }

  @Test
  public void testConstructorWithOneColorList() {
    PlayerList onePl =
        new PlayerList(Arrays.asList(PlayerColor.values()).subList(0,1));
  }


  @Test (expected = IndexOutOfBoundsException.class)
  public void testConstructorWithEmptyColorListPlusMethodCalling() {
    List<PlayerColor> empty = new ArrayList<>();
    PlayerList emptyPL = new PlayerList(empty);
    //Throws exception because the list is empty:
    emptyPL.getCurrentPlayerColor();
  }

  @Test
  public void testAllGetters() {
    // - getCurrentPlayerColor()
    // - getPlayerStatewithcolor(Color) -- can be used to get score, age
    // - getOrderedPlayers()
    // - getPenguinList()

    assertEquals(PlayerColor.RED, this.fourPlayers.getCurrentPlayerColor());

    PlayerState player0 = new PlayerState(PlayerColor.RED, 0);
    PlayerState player1 = new PlayerState(PlayerColor.WHITE, 1);
    PlayerState player2 = new PlayerState(PlayerColor.BROWN, 2);
    PlayerState player3 = new PlayerState(PlayerColor.BLACK, 3);

    assertEquals(player0,
        this.fourPlayers.getPlayerStateWithColor(PlayerColor.RED));
    assertEquals(player1,
        this.fourPlayers.getPlayerStateWithColor(PlayerColor.WHITE));
    assertEquals(player2,
        this.fourPlayers.getPlayerStateWithColor(PlayerColor.BROWN));
    assertEquals(player3,
        this.fourPlayers.getPlayerStateWithColor(PlayerColor.BLACK));

    List<PlayerState> orderedPlayers = new ArrayList<>();
    orderedPlayers.add(player0);
    orderedPlayers.add(player1);
    orderedPlayers.add(player2);
    orderedPlayers.add(player3);

    assertEquals(orderedPlayers, this.fourPlayers.getOrderedPlayers());

    PlayerList advancePL = this.fourPlayers.advancePlayer();
    assertNotEquals(orderedPlayers, advancePL.getOrderedPlayers());

    assertEquals(new ArrayList<>(), this.fourPlayers.getPenguinList());
  }

  @Test
  public void testAdvancePlayer() {
    // Note: If you call advance player twice on one object,
    // you get a concurrentModificationException
    assertEquals(PlayerColor.RED, this.twoPlayers.getCurrentPlayerColor());

    PlayerList plWithPlayerAdvanced1x = this.twoPlayers.advancePlayer();

    // ensure the original object did not get mutated:
    assertEquals(PlayerColor.RED, this.twoPlayers.getCurrentPlayerColor());
    // check that the new object did:
    assertEquals(PlayerColor.WHITE, plWithPlayerAdvanced1x.getCurrentPlayerColor());

    PlayerList plWithPlayerAdvanced2x = plWithPlayerAdvanced1x.advancePlayer();

    // ensure the original object did not get mutated:
    assertEquals(PlayerColor.RED, this.twoPlayers.getCurrentPlayerColor());
    // check that the second object did not get mutated again:
    assertEquals(PlayerColor.WHITE, plWithPlayerAdvanced1x.getCurrentPlayerColor());
    // check that the new object did form as planned:
    assertEquals(PlayerColor.RED, plWithPlayerAdvanced2x.getCurrentPlayerColor());
  }


  @Test
  public void testRemovePlayerWithColor() {
    // 1. Test that after removal the current player is RED
    PlayerList listAfterRemoval =
        this.twoPlayers.removePlayerWithColor(PlayerColor.WHITE).get();
    assertEquals(PlayerColor.RED, listAfterRemoval.getCurrentPlayerColor());

    // 2. Test that after removal, you cannot remove anything more because
    // there can never be less than 1 player in the PlayerList
    assertEquals(1, listAfterRemoval.getOrderedPlayers().size());
    assertEquals(Optional.empty(),
        listAfterRemoval.removePlayerWithColor(PlayerColor.RED));
  }

  @Test
  public void testRemovePlayerOnePlayerLeft() {
    PlayerList onePlayerLeft =
        this.twoPlayers.removePlayerWithColor(PlayerColor.WHITE).get();

    assertEquals(Optional.empty(),
        onePlayerLeft.removePlayerWithColor(PlayerColor.RED));
  }

  @Test(expected = IllegalStateException.class)
  public void testRemovePlayerColorNotFound() {
    this.twoPlayers.removePlayerWithColor(PlayerColor.BROWN);
  }

  @Test(expected = IllegalStateException.class)
  public void testRemovePlayerZeroPlayers() {
    PlayerList zeroPlayers = new PlayerList(new ArrayList<>());
    zeroPlayers.removePlayerWithColor(PlayerColor.BROWN);
  }

  //Test that removing a player also removes its penguins
  @Test
  public void testRemovePlayerAndItsPenguins() {
    PlayerList stateOne = this.twoPlayers.placePenguin(PlayerColor.RED,
        new Position(0,0));
    PlayerList stateTwo = stateOne.placePenguin(PlayerColor.WHITE,
        new Position(0,1));
    PlayerList stateThree = stateTwo.placePenguin(PlayerColor.RED,
        new Position(0,2));
    PlayerList stateFour = stateThree.placePenguin(PlayerColor.WHITE,
        new Position(0,3));

    assertEquals(4, stateFour.getPenguinList().size());
    PlayerList stateFiveRemoveWhite =
        stateFour.removePlayerWithColor(PlayerColor.WHITE).get();
    assertEquals(2, stateFiveRemoveWhite.getPenguinList().size());
  }

  @Test
  public void testPlaceAndMovePenguin() {
    List<PlayerColor> colors = new ArrayList<>();
    colors.add(PlayerColor.RED);
    colors.add(PlayerColor.BLACK);
    colors.add(PlayerColor.BROWN);
    PlayerList pl1 = new PlayerList(colors);
    assertTrue(pl1.getPenguinList().isEmpty());

    // test effect by checking penguin map + player state list
    PlayerList pl2 = pl1.placePenguin(PlayerColor.RED, new Position(3, 3));
    List<Penguin> penguins = pl2.getPenguinList();
    List<PlayerState> players = pl2.getOrderedPlayers();
    assertEquals(1, penguins.size());
    Penguin p = penguins.get(0);
    assertEquals(new Position(3, 3), p.getPosition());
    assertEquals(PlayerColor.RED, p.getPlayerColor());
    assertEquals(colors.size(), players.size());
    // no effect on original pl1
    assertEquals(0, pl1.getPenguinList().size());
    assertEquals(colors.size(), pl1.getOrderedPlayers().size());

    // test effect by checking penguin map + player state list
    PlayerList pl3 = pl2.movePenguin(new Position(3, 3), new Position(1, 0), 7);
    penguins = pl3.getPenguinList();
    players = pl3.getOrderedPlayers();
    assertEquals(1, penguins.size());
    p = penguins.get(0);
    assertEquals(PlayerColor.RED, p.getPlayerColor());
    assertEquals(new Position(1, 0), p.getPosition());
    assertEquals(colors.size(), players.size());
    // score increases after the penguin moved away from a tile
    assertEquals(7, players.get(0).getScore());

    // no effect on original pl2
    penguins = pl2.getPenguinList();
    players = pl2.getOrderedPlayers();
    assertEquals(1, penguins.size());
    p = penguins.get(0);
    assertEquals(new Position(3, 3), p.getPosition());
    assertEquals(PlayerColor.RED, p.getPlayerColor());
    assertEquals(colors.size(), players.size());
  }
}
