package com.cs4500.fish.common;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Contains configuration for creating a board.
 * - width and height specify dimension of the board
 * - defaultFish specifies the default # of fish on a tile
 * - oneFishTileMin specifies the _preferred_ minimum tiles with 1 single fish,
 * - holes specify tiles that will be removed
 *   i.e. priority should be given to other configuration parameters.
 */
public class BoardConfig {

	public final int WIDTH = 5;
	public final int HEIGHT = 5;
	public final int DEFAULT_FISH = 1;
	public final int ONE_FISH_TILE_MIN = 0;

	private int width = WIDTH;
	private int height = HEIGHT;
	private int defaultFish = DEFAULT_FISH;
	private int oneFishTileMin = ONE_FISH_TILE_MIN;
	private Set<Position> holes = new HashSet<>();

	public BoardConfig setWidth(int width) {
		this.width = width;
		return this;
	}

  public int getWidth() {
    return width;
	}

	public BoardConfig setHeight(int height) {
		this.height = height;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public BoardConfig setOneFishTileMin(int oneFishTileMin) {
		this.oneFishTileMin = oneFishTileMin;
		return this;
	}

	public int getOneFishTileMin() {
		return oneFishTileMin;
	}

	public BoardConfig setDefaultFish(int defaultFish) {
		this.defaultFish = defaultFish;
		return this;
	}

	public int getDefaultFish() {
		return defaultFish;
	}

	public BoardConfig setHoles(Collection<Position> holes) {
		this.holes = new HashSet<>(holes);
		return this;
	}

	public Set<Position> getHoles() {
		return new HashSet<>(this.holes);
	}

}
