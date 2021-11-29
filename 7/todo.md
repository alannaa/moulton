


To do list:

Referee
[] separate function to handle avatar placement phase

[] need unit test that specifically addresses penguin placement only

[x] separate function to handle a turn taking phase

[x] add to referee documentation the specific details around referee data mutation (there is none)

[X] handle the magic number 6 when calculating the number of penguins per player

[x] handle the potential of players timWing out for ALL calls to player

[x] implement singleton pattern instead of creating a new skip

[x] numNonHoleTilesOnBoard belongs in board class, not referee class


GameResult
[x] document that the others' list of players is ordered and implement that
the players will have associated scores


PlayerList
[x] Enforce that deserialized boards have at least two players

