# Assignment 1
Team: Ryan Guo and Christian Hauser
What: A generalized echo program.

We have included a makefile for this project, please run this before attempting to test xyes.

## Project Structure
- `Other`: source code and auxiliary files.
  - `src`: java source code
    - `cs4500`: package
      - `EchoMain.java`: Main source code for the echo program.
  - `test`: test source code
    - `cs4500`: package
      - `EchoMainTest.java`: Unit tests for the echo program.
  - `app.jar`: The assembled jar file.
  - `pom.xml`: Maven configuration file.
  - `xtest`: Script to run all the unit tests.
- `README.md`: This file.
- `Makefile`: Makefile to compile the program and build a jar file.
- `xyes`: Script to run the echo program.

## Roadmap

Start by looking at the `Other/src/cs4500/EchoMain.java`, and then `Other/test/cs4500/EchoMainTest.java`.

## Tests

Navigate to `Other/B` and execute `Other/xtest`. This will execute all unit
tests located in the `B/Other/test` folder.
