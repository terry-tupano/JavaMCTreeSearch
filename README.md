# Monte carlo tree seach for Java
JavaMCTreeSearch is a Monte Carlo Tree Search library written in Java. It is designed with a clear separation of the core concepts of the algorithm, such as `Node`, `Action`, `MDP`and `Solver`. JavaMCTreeSearch utilizes class inheritance and generic types to standardize custom algorithm definitions. In addition, key class abstractions are designed for the library to flexibly adapt to any well-defined Markov Decision Process.

This project is a Java implementation inspired by an existing Kotlin library found [here](https://github.com/aqtech-ca/mctreesearch4j). The project serves as an experimental and educational implementation focused on understanding the architecture and mechanics of MCTS systems, intended for future integration into another personal project. 

### Open Journals paper
[here](https://www.theoj.org/joss-papers/joss.03804/10.21105.joss.03804.pdf) you can find the paper submitted by the original developers of the kotlin version.


## Example Code
### MDP of TicTacToe
```java
package de.simone.tictactoe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.simone.MDP;
import de.simone.tictactoe.TicTacToe.GameState;

/**
 * This class implements the Markov Decision Process for the Tic Tac Toe game.
 */
public class MPD extends MDP<Grid, Action> {
    private char mySeed;
    private char opponentSeed;
    private Grid root;

    protected MPD() {
        this(new Grid());
    }

    public MPD(Grid initialState) {
        this.mySeed = TicTacToe.PLAYER_O;
        this.opponentSeed = TicTacToe.PLAYER_X;
        this.root = new Grid(initialState);
    }

    @Override
    public boolean isTerminal(Grid state) {
        return Utils.isGameOver(state);
    }

    @Override
    public double reward(Grid state, Action action, Grid newState) {
        double reward = 0.0;

        // Win
        reward = Utils.hasWinner(newState, mySeed) ? 1.0 : reward;

        // Loss
        reward = Utils.hasWinner(newState, opponentSeed) ? -1.0 : reward;

        // Tie
        reward = Utils.isFull(newState) ? 0.5 : reward;

        return reward;
    }

    @Override
    public Grid transition(Grid state, Action action) {
        Grid newState = new Grid(state);

        GameState gState = Utils.makeMove(newState, action);
        
        // should never happen
        if (gState == GameState.ILLEGAL_MOVE) {
            throw new IllegalArgumentException("Illegal move: " + action.row + ", " + action.col);
        }

        return newState;
    }

    @Override
    public Grid initialState() {
        return root;
    }

    @Override
    public Collection<Action> actions(Grid state) {
        int myMoves = 0;
        int opponentMoves = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.getCell(i, j) == TicTacToe.PLAYER_O) {
                    myMoves++;
                }
                if (state.getCell(i, j) == TicTacToe.PLAYER_X) {
                    opponentMoves++;
                }
            }
        }
        char seed = (myMoves < opponentMoves) ? TicTacToe.PLAYER_O : TicTacToe.PLAYER_X;
        List<Action> validActions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.getCell(i, j) == ' ') {
                    validActions.add(new Action(i, j, seed));

                }
            }
        }
        return validActions;
    }
}
```
### And the Game

``` java
package de.simone.tictactoe;

import java.util.Scanner;

import de.simone.GenericSolver;

/**
 * This class represents the Tic Tac Toe game.
 */
public class TicTacToe {
    // The grid where the game is played, represented as a 2D array
    private static Grid grid;

    // The players, represented as X and O
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';

    // The current player
    private static char currentPlayer;

    public enum GameState {
        IN_PROGRESS,
        X_WINS,
        O_WINS,
        TIE,
        ILLEGAL_MOVE,
    }

    /**
     * This method uses @link{GenericSolver} to determine the best move for the AI player (O).
     * @param grid - the grid
     * @return the move
     */
    private static Action getAiMove(Grid grid) {
        MPD mdp = new MPD(grid);
        GenericSolver<Grid, Action> aiPlayer = new GenericSolver<>(mdp, 10, 0.25, 0.5, true);
        aiPlayer.runTreeSearch(50);
        Action action = aiPlayer.extractOptimalAction();
        return action;
    }

    private static GameState playerMove(int row, int col) {
        GameState state = GameState.IN_PROGRESS;
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || !grid.isCellEmpty(row, col)) {
            return GameState.ILLEGAL_MOVE;
        }

        // Update the grid with the player's move
        grid.setCell(row, col, currentPlayer);

        // Check if the game is over
        if (Utils.isGameOver(grid)) {
            if (Utils.hasWinner(grid, currentPlayer)) {
                state = (currentPlayer == PLAYER_X) ? GameState.X_WINS : GameState.O_WINS;
            } else {
                state = GameState.TIE;
            }
        }

        // Switch to the other player
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        return state;
    }

    public static void main(String[] args) {
        grid = new Grid();
        currentPlayer = PLAYER_X;
        // Start the game loop
        while (true) {
            System.out.println(grid);

            int row, col;
            if (currentPlayer == PLAYER_O) {
                System.out.println("Player O's turn (GA Player)");
                Action move = getAiMove(grid);
                row = move.row;
                col = move.col;
            } else {
                System.out.println(
                        "Player " + currentPlayer + ", enter your move (row col). -1 to restart, -2 to quit: ");
                Scanner scanner = new Scanner(System.in);
                row = scanner.nextInt();
                if (row == -1) {
                    grid = new Grid();
                    continue;
                }
                if (row == -2) {
                    System.out.println("Quitting the game.");
                    break;
                }
                col = scanner.nextInt();
                scanner.close();
            }

            // Update the grid with the player's move
            GameState state = playerMove(row, col);
            if (state == GameState.ILLEGAL_MOVE) {
                System.out.println("Illegal move, try again.");
                continue;
            }

            if (state == GameState.X_WINS)
                System.out.println("Player X wins!");

            if (state == GameState.O_WINS)
                System.out.println("Player O wins!");

            if (state == GameState.TIE)
                System.out.println("It's a tie!");

            if (state != GameState.IN_PROGRESS) {
                System.out.println(grid);
                break;
            }

        }

        System.out.println("Game over.");
    }
}
```

## Building and running demo
- git clone repository
- mvn jetty:run


## License & Author

JavaMCTreeSearch is manteined by Terry Tupano.

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See LICENSE file.

	Copyright 2026 Terry Tupano

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.