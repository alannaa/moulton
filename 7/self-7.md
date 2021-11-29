## Self-Evaluation Form for Milestone 7

Please respond to the following items with

1. the item in your `todo` file that addresses the points below.
    It is possible that you had "perfect" data definitions/interpretations
    (purpose statement, unit tests, etc) and/or responded to feedback in a 
    timely manner. In that case, explain why you didn't have to add this to
    your `todo` list.

2. a link to a git commit (or set of commits) and/or git diffs the resolve
   bugs/implement rewrites: 

These questions are taken from the rubric and represent some of the most
critical elements of the project, though by no means all of them.

(No, not even your sw arch. delivers perfect code.)

### Board

- a data definition and an interpretation for the game _board_

  - We were originally critizied for our interpretation of tiles, but that
   criticism was incorrect. we're not going to keep repeating this, but we 
   spent twice as much time
   designing the code than implementing it. That's the main justification we
   'll be giving.

- a purpose statement for the "reachable tiles" functionality on the board representation

  - We were never criticized on our implementation of the reachable tiles
   functionality

- two unit tests for the "reachable tiles" functionality

  - We were critized about this, but immediately addressed it before the next 
  milestone

### Game States 


- a data definition and an interpretation for the game _state_

    - We were never criticized on our gameState Interpretation

- a purpose statement for the "take turn" functionality on states

    - We were never criticized on the take turn functionality

- two unit tests for the "take turn" functionality 

    - We were criticized originally for not having enough unit tests, but
     immediately after receiving feedback we added those tests.


### Trees and Strategies


- a data definition including an interpretation for _tree_ that represent entire games

    - We were criticized on not differentiating between game over and player
     is stuck clearly as well as not mentioning how `skip`s are handled, we took
      this feedback
      and better explained our design
      in the method purpose statement immediately.

- a purpose statement for the "maximin strategy" functionality on trees

    - We were not criticied on this.

- two unit tests for the "maximin" functionality 

    - We were not criticized on this.

### General Issues

Point to at least two of the following three points of remediation: 


- the replacement of `null` for the representation of holes with an actual representation 

    - We did not run into this issue, and were never criticized on it. We
     chose to represent tiles as integers, with 0 being a hole.

- one name refactoring that replaces a misleading name with a self-explanatory name

    - We did not rename anything as part of this reworking, as our names are
     already very verbose and self-explanatory.

- a "debugging session" starting from a failed integration test:
  - the failed integration test
  - its translation into a unit test (or several unit tests)
  - its fix
  - bonus: deriving additional unit tests from the initial ones 
  
- Comment
    - We failed some integration tests as part of milestone 5, but
     immediately addressed that the problem was our code not enforcing a
      minimum of 2 players. We were allowing games with more than 2 players
      . Aside from that there was nothing that we let get through the next
       assignment.


### Bonus

Explain your favorite "debt removal" action via a paragraph with
supporting evidence (i.e. citations to git commit links, todo, `bug.md`
and/or `reworked.md`).

- We really enjoyed learning about the singleton pattern from advice given to
 us in the most recent codewalk before pattern switching. It was something we
  hadn't thought of before, and cleaned up the code base quite nicely. We
   implemented it for the `Skip` action, because we only ever called `new
    Skip()` and we would be creating tens of thousands of object instances
     when we only needed to return a reference to one. It was very satisfying
      to reduce memory usage in such a simple way. [Here](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/f393a67975baa6c50b2cb9101e5b23689720785a/Fish/Common/src/main/java/com/cs4500/fish/common/Skip.java#L9-L14) is a link to the
       commit where we made the change, and [here](https://github.ccs.neu.edu/CS4500-F20/richlandsprings/blob/797bd6c6bbd05d887d1465030c724e220a46366d/7/todo.md) is a link to the todo list
        item.
