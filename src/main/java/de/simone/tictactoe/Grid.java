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

import java.util.Arrays;

public class Grid {
    private char[][] grid;

    public Grid() {
        this.grid = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid[i][j] = ' ';
            }
        }
    }

    public Grid(Grid initialState) {
        this.grid = new char[3][3];
        for (int i = 0; i < 3; i++) {
            this.grid[i] = Arrays.copyOf(initialState.grid[i], 3);
        }
    }

    public boolean isCellEmpty(int row, int col) {
        return grid[row][col] == ' ';
    }

    public void setCell(int row, int col, char seed) {
        grid[row][col] = seed;
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(" " + getCell(i, j) + " ");
                if (j < 2) {
                    sb.append("|");
                }
            }
            sb.append("\n");
            if (i < 2) {
                sb.append("---+---+---\n");
            }
        }

        return sb.toString();
    }
}