package com.cs4500.fish.player;

import com.cs4500.fish.common.*;

import java.util.Optional;
import java.util.Map;
import java.util.List;

public class MinimaxTurnActionStrategy implements TurnActionStrategy {

  // maximum # of turns it will look ahead.
  private final int maxDepth;

  public MinimaxTurnActionStrategy(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  /**
   * Returns the best action that the current player in state can take, using
   * the minimax search algorithm. If we don't have a best move, then return
   * a skip, otherwise return the best move.
   */
  public Action apply(GameTree tree) {
    Action bestMove = Skip.getInstance();
    int bestScore = Integer.MIN_VALUE;
    PlayerColor myColor = tree.getState().getCurrentPlayerColor();
    Map<Action, GameTree> subtrees = tree.getSubtrees();
    // rank the subtree resulted from each possible move
    for (Map.Entry<Action, GameTree> entry : subtrees.entrySet()) {
      if (entry.getKey().isMove()) {
        Move move = entry.getKey().getAsMove();
        GameTree subtree = entry.getValue();
        int score = this.evaluateTree(subtree, myColor, 1);
        if (score > bestScore || // better score or a more "top-left" move
            (score == bestScore && isFirstBetter(move, bestMove))) {
          bestMove = move;
          bestScore = score;
        }
      }
    }
    return bestMove;
  }

  // Evaluates the `tree` from the perspective of player with `myColor`. If the
  // currently active player is of `myColor` then return the maximum score of
  // the this tree's subtrees. Otherwise it returns the minimum of those scores.
  // If there are no subtrees or `depth` is at `maxDepth` then return the score
  // at this tree's gamestate. If two of the subtree's scores are identical,
  // return the score who's corresponding move's `from` field is
  // lexicographically less.
  private int evaluateTree(GameTree tree, PlayerColor myColor, int depth) {
    if (depth == this.maxDepth) {
      return getPlayerScore(tree.getState(), myColor);
    }
    PlayerColor currentPlayer = tree.getState().getCurrentPlayerColor();
    boolean isMyTurn = myColor.equals(currentPlayer);
    int nextDepth = isMyTurn ? depth + 1 : depth;
    List<Integer> subtreeScores =
      tree.mapSubTrees(subtree -> evaluateTree(subtree, myColor, nextDepth));
    Optional<Integer> scoreOpt = isMyTurn ?
      subtreeScores.stream().max(Integer::compare) :
      subtreeScores.stream().min(Integer::compare);
    return scoreOpt.orElseGet(() -> getPlayerScore(tree.getState(), myColor));
  }

  private static int getPlayerScore(GameState state, PlayerColor myColor) {
    return state.getPlayerStateWithColor(myColor).getScore();
  }

  // Move is better than all other actions, and moves themselves are
  // ranked reversed lexicalgraphically. (i.e., top-left moves are better).
  private static boolean isFirstBetter(Action first, Action second) {
    if (!second.isMove()) {
      return true;
    }
    if (! first.isMove()) {
      return false;
    }
    return first.getAsMove().compareTo(second.getAsMove()) < 0;
  }
}
