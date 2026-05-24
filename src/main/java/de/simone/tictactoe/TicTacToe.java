/*-
 * #%L
 * JavaMCTreeSearch
 * %%
 * Copyright (C) 2026 Terry Tupano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
