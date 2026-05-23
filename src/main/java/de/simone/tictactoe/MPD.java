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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.simone.MDP;
import de.simone.tictactoe.TicTacToe.GameState;

/**
 * This class implements the Markov Decision Process (MDP) for the Tic Tac Toe game.
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
