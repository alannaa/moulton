Reworked Commit Links

Referee

[] need unit test that specifically addresses penguin placement only

Unfortunately didn't get to this item in time, we started onboarding late due,
to technical issues.

[x] separate function to handle avatar placement phase

[link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/commit/8e23c48ae55a8323cfc6d9a196ecba4db9580b53)

[x] separate function to handle a turn taking phase

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/commit/50636e2412767d995afe9a36129480ad0bc7b396?branch=50636e2412767d995afe9a36129480ad0bc7b396&diff=split)

[x] add to referee documentation the specific details around referee data mutation (there is none)

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/ce5865f30feaf8ff9ff8fc938332563865a2c449/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L53)

[X] handle the magic number 6 when calculating the number of penguins per player

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/f393a67975baa6c50b2cb9101e5b23689720785a/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L27)

[x] handle the potential of players timing out for ALL calls to player

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/commit/b50a6e3f1ef3925718e58d4a40172541b52f37f6)

[x] implement singleton pattern instead of creating a new skip

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/f393a67975baa6c50b2cb9101e5b23689720785a/Fish/Common/src/main/java/com/cs4500/fish/common/Skip.java#L7)

[x] numNonHoleTilesOnBoard belongs in board class, not referee class

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/f393a67975baa6c50b2cb9101e5b23689720785a/Fish/Common/src/main/java/com/cs4500/fish/common/Board.java#L184)


GameResult
[x] document that the others' list of players is ordered and implement that
the players will have associated scores

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/ce5865f30feaf8ff9ff8fc938332563865a2c449/Fish/Common/src/main/java/com/cs4500/fish/admin/GameResult.java#L16)

PlayerList
[x] Enforce that deserialized boards have at least two players

[Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/3969873dc30290fb0cce0008cca9e962a195e7df/Fish/Common/src/main/java/com/cs4500/fish/common/PlayerList.java#L204-L207)