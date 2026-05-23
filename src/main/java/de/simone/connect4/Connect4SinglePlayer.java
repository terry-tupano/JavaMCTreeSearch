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
package de.simone.connect4;

import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Connect4SinglePlayer {
    public static void main(String[] args) {
        final long GIVEN_TIME = TimeUnit.SECONDS.toNanos(args.length > 0 ? Integer.parseInt(args[0]) : 2);
        Scanner in = new Scanner(System.in);
        Connect4Board board = new Connect4Board();
        boolean turn = Connect4Board.PLAYER_1_TURN;
        Connect4AI ai = new Connect4AI(board, GIVEN_TIME);
        while (board.currentGameState() == Connect4Board.ONGOING) {
            System.out.println("\n\n" + board);
            int moveColumn;
            do {
                if (board.getNextTurn() == Connect4Board.PLAYER_1_TURN) {
                    System.out.printf("Enter your move: ", board.getNextTurn() == Connect4Board.PLAYER_1_TURN ? 1 : 2);
                    moveColumn = in.nextInt();
                } else {
                    System.out.print("AI determining move: ");
                    moveColumn = ai.getOptimalMove();
                    System.out.println(moveColumn);
                }
            } while (!board.canPlace(moveColumn));
            board.place(moveColumn);
            ai.update(moveColumn);
        }
        int gameState = board.currentGameState();
        System.out.println("\n\n\n\n\n");
        System.out.println(board);
        switch (gameState) {
            case Connect4Board.PLAYER_1_WON:
                System.out.println("You won.\n");
                break;
            case Connect4Board.PLAYER_2_WON:
                System.out.println("AI won.\n");
                break;
            default:
                System.out.println("Tie.\n");
                break;
        }
    }
}