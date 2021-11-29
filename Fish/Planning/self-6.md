## Self-Evaluation Form for Milestone 6

Indicate below where your TAs can find the following elements in your strategy and/or player-interface modules:

The implementation of the "steady state" phase of a board game
typically calls for several different pieces: playing a *complete
game*, the *start up* phase, playing one *round* of the game, playing a *turn*, 
each with different demands. The design recipe from the prerequisite courses call
for at least three pieces of functionality implemented as separate
functions or methods:

- the functionality for "place all penguins"

We don't have a single function that does this, it's handled as a part of the `setupGame` function. [Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L105-L112)

- a unit test for the "place all penguins" funtionality 

Because we only expose `runGame` publicly, we don't have unit test for `placeAllPenguins`. We do have unit tests that do test this functionality however.
[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L56-L57)


- the "loop till final game state"  function

[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L68-L80)

- this function must initialize the game tree for the players that survived the start-up phase

[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L66)


- a unit test for the "loop till final game state"  function

[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L133-L134)

- the "one-round loop" function

We didn't think it's necessary to implement the concept of round in this game. We simply handle this within `runGame`. This is not really specified anywhere in the spec or our plan either. 
[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L68)

- a unit test for the "one-round loop" function

Same thing as above, we don't have that individual function.
[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L133-L134)

- the "one-turn" per player function

[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L68)

- a unit test for the "one-turn per player" function with a well-behaved player 

This unit test includes two players that behave properly, and take turns until game completion.
[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L148-L153)

- a unit test for the "one-turn" function with a cheating player


[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L100-L105)

- a unit test for the "one-turn" function with an failing player 

[Here](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/test/java/com/cs4500/fish/admin/RefereeTest.java#L106-L107)

- for documenting which abnormal conditions the referee addresses 

[Input Validation,](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L52-L57)
[Failed Players,](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L49-L51)
[and Cheaters](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L38-L43)

- the place where the referee re-initializes the game tree when a player is kicked out for cheating and/or failing 

[Kicked for Failing,](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L211)
[Kicked for Cheating,](https://github.ccs.neu.edu/CS4500-F20/mabank/blob/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish/Common/src/main/java/com/cs4500/fish/admin/Referee.java#L216)


**Please use GitHub perma-links to the range of lines in specific
file or a collection of files for each of the above bullet points.**

  WARNING: all perma-links must point to your commit "a94202c6d4e8615ac62b42f5c14185d40c2c6f2c".
  Any bad links will be penalized.
  Here is an example link:
    <https://github.ccs.neu.edu/CS4500-F20/mabank/tree/a94202c6d4e8615ac62b42f5c14185d40c2c6f2c/Fish>

