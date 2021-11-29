# TournamentManager interface protocol

## How to use it

0. Continuously add players via `addPlayer` who will be participating in the
   tournament as they come in from the sign-up manager. Then go to (1).

1. Optionally add tournament observers before starting the tournament via
   `addTournamentObserver`. Then go to (2).

2. Start the tournament via `runTournament` Then optionally repeat the following
   until the tournament ends:
   - add more player via `addPlayer`
   - add more tournament observers  via `addTournamentObserver`
   - add observers for specific games via `addGameObserver`


## Tournament Observers

- When an observer is first added to an object, it can expect to receive a list
  of IDs of the currently active games.

- Upon each new round of games starting in the tournament, the tournament
  observer will be informed via `informNewRoundStart`.

- Upon each new game starting, the tournament observer will be informed via
  `informStartGame`.

- Upon the end of the tournament, the tournament observer will be informed via
  `informTournamentResult`


## Game Observers

- When an observer is first added to an object, it can expect to receive a copy 
  of the most recent `GameState` via `onRegister`.
  
- The `GameObserver` will receive updates on various events that happen within the
  the game, until the game has finished.

- At the end of the game, the `GameObserver` will stop receiving information.
