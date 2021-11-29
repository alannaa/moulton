## Self-Evaluation Form for Milestone 8

Indicate below where your TAs can find the following elements in your strategy and/or player-interface modules:

1. did you organize the main function/method for the manager around
the 3 parts of its specifications --- point to the main function

    - The manager handles
        - [Checking if the game is over](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/TournamentManager.java#L24)
        - [Assigning Players to Games](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/TournamentManager.java#L29)
        - They main method does not do directly inform players of tournament
         results
        , but players can
         infer this information from being informed that they have won a game
         , and then being disconnected from the server 
        [here
        ](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/TournamentManager.java#L55)
    - [Signature Link](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/TournamentManager.java#L19)

2. did you factor out a function/method for informing players about
the beginning and the end of the tournament? Does this function catch
players that fail to communicate? --- point to the respective pieces

    - While we don't have a particular method for informing the player that
     the game is over, we expect them to infer that information from a set of
      defined criteria. When a player receives their first color assignment
       during the first game, they can assume that the tournament has started
       . When they are informed that they have won a game, and then are
        promptly disconnected from the server, they can infer that they have
         won the tournament. We do handle failures to communicate [here](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/PlayerSystemInteraction.java#L17)

3. did you factor out the main loop for running the (possibly 10s of
thousands of) games until the tournament is over? --- point to this
function.

    - We did not factor out this main loop, however we only call one function
     in the loop that handles all of the individual games [here](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish/Common/src/main/java/com/cs4500/fish/admin/TournamentManager.java#L30)

**Please use GitHub perma-links to the range of lines in specific
file or a collection of files for each of the above bullet points.**


  WARNING: all perma-links must point to your commit "d349cf6815f0e233d3abce0289746b019a4b2d6c".
  Any bad links will be penalized.
  Here is an example link:
    <https://github.ccs.neu.edu/CS4500-F20/richlandsprings/tree/d349cf6815f0e233d3abce0289746b019a4b2d6c/Fish>

