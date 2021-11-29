# Fish

## Project Structure
- `Common`: source code and auxiliary files.
  - `src`: java source code
    - `main/java/com/cs4500/fish`: package for the fish game.
      - `game`: package for game state implementations.
      - `view`: package for rendering the game.
      - `demo`: package for demos programs.
      - `integration`: package for integration test harness implementation.
    - `test/java/com/cs4500/fish`: package for unit tests
      - The internals follows the same structure as the implementation source code
        directory.
  - `pom.xml`: Maven configuration file.
  - `demo`: Script to run demo for project Milestone 2.
  - `Makefile`: Makefile to compile the java program.
- `xtest`: Script to run the unit tests.
- `README.md`: This file.


## Roadmap
Start by looking at the `Common/src/main/java/com/cs4500/demo/RenderGameDemo.java`.
It provides a demo which demonstrates the existing functionality at a high level.

From that point, one should explore top-down into the `game` package, starting
from `GameState.java`, `PlayerList.java`, `Board.java` and then `Tile.java`,
etc. When reading through each class, one may also want to look at the
corresponding unit tests in `Common/src/test/...`

After understanding much of the `game` package, one should look into the `view`
package, to see how the game state is used and rendered.


## Unit Tests
Please run the following command to run the unit tests.
```
./xtest
```