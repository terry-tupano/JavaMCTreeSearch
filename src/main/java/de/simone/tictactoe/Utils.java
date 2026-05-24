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

import de.simone.tictactoe.TicTacToe.GameState;

public class Utils {

    // Check if the game is over (i.e. someone has won or there are no more empty
    // spaces)
    protected static boolean isGameOver(Grid grid) {
        return hasWinner(grid, TicTacToe.PLAYER_X) || hasWinner(grid, TicTacToe.PLAYER_O) || isFull(grid);
    }

    // Check if there is a winner (i.e. someone has three marks in a row)
    protected static boolean hasWinner(Grid grid, char seed) {
        // Check for horizontal wins
        for (int i = 0; i < 3; i++) {
            if (isRowWin(grid, i, seed)) {
                return true;
            }
        }

        // Check for vertical wins
        for (int i = 0; i < 3; i++) {
            if (isColWin(grid, i, seed)) {
                return true;
            }
        }

        // Check for diagonal wins
        if (isDiag1Win(grid, seed) || isDiag2Win(grid, seed)) {
            return true;
        }

        // If none of the above checks passed, there is no winner
        return false;
    }

    public static GameState makeMove(Grid grid, Action action) {
        GameState state = GameState.IN_PROGRESS;

        if (action.row < 0 || action.row >= 3 || action.col < 0 || action.col >= 3
                || !grid.isCellEmpty(action.row, action.col)) {
            return GameState.ILLEGAL_MOVE;
        }

        // Update the grid with the player's move
        grid.setCell(action.row, action.col, action.seed);

        // Check if the game is over
        if (Utils.isGameOver(grid)) {
            if (Utils.hasWinner(grid, action.seed)) {
                state = (action.seed == TicTacToe.PLAYER_X) ? GameState.X_WINS : GameState.O_WINS;
            } else {
                state = GameState.TIE;
            }
        }

        return state;
    }

    /**
     * Check if there are no more empty spaces in the grid
     */
    protected static boolean isFull(Grid grid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid.isCellEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the given row has a winning combination
     */
    private static boolean isRowWin(Grid grid, int row, char seed) {
        return (grid.getCell(row, 0) == seed && grid.getCell(row, 1) == seed && grid.getCell(row, 2) == seed);
    }

    /**
     * Check if the given column has a winning combination
     */
    private static boolean isColWin(Grid grid, int col, char seed) {
        return (grid.getCell(0, col) == seed && grid.getCell(1, col) == seed && grid.getCell(2, col) == seed);
    }

    /**
     * Check if the first diagonal has a winning combination
     */
    private static boolean isDiag1Win(Grid grid, char seed) {
        return (grid.getCell(0, 0) == seed && grid.getCell(1, 1) == seed && grid.getCell(2, 2) == seed);
    }

    /**
     * Check if the second diagonal has a winning combination
     */
    private static boolean isDiag2Win(Grid grid, char seed) {
        return (grid.getCell(0, 2) == seed && grid.getCell(1, 1) == seed && grid.getCell(2, 0) == seed);
    }

}