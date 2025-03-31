package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

import java.util.Scanner;

public class MCTS {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain;

        do {
            TicTacToe game = new TicTacToe();
            TicTacToe.TicTacToeState state = game.new TicTacToeState();

            System.out.println("\n Welcome to Tic Tac Toe");
            System.out.println("Choose your symbol:");
            System.out.println("1. X (Player 1, goes first)");
            System.out.println("2. O (Player 2, goes second)");
            System.out.print("Enter 1 or 2: ");
            int choice = getValidatedChoice(scanner, 1, 2);
            int humanPlayer = (choice == 1) ? 1 : 0;
            int botPlayer = 1 - humanPlayer;

            int moveNumber = 0;

            while (!state.isTerminal()) {
                System.out.println("\nMove #" + (++moveNumber));
                System.out.println("Current Board:");
                System.out.println(state.position().render());

                int currentPlayer = state.player();

                if (currentPlayer == humanPlayer) {
                    int moveIndex;
                    while (true) {
                        System.out.print("Enter your move (1-9): ");
                        moveIndex = getValidatedChoice(scanner, 1, 9) - 1;
                        int row = moveIndex / 3;
                        int col = moveIndex % 3;

                        try {
                            state = (TicTacToe.TicTacToeState) state.next(new TicTacToe.TicTacToeMove(humanPlayer, row, col));
                            break;
                        } catch (Exception e) {
                            System.out.println(" Invalid move. Try again.");
                        }
                    }
                } else {
                    System.out.println("\n Bot is thinking...");
                    Node<TicTacToe> root = new TicTacToeNode(state);
                    MCTS mcts = new MCTS(root);
                    Node<TicTacToe> best = mcts.run(1000);
                    state = (TicTacToe.TicTacToeState) best.state();
                }
            }

            // Final board
            System.out.println("\n Final Board:");
            System.out.println(state.position().render());

            // Result
            state.winner().ifPresentOrElse(
                    w -> System.out.println(" " + (w == humanPlayer ? "You win!" : "Bot wins!")),
                    () -> System.out.println("It's a draw!")
            );

            System.out.print("\nPlay again? (y/n): ");
            playAgain = scanner.next().trim().equalsIgnoreCase("y");

        } while (playAgain);

        System.out.println(" Thanks for playing!");
        scanner.close();
    }


