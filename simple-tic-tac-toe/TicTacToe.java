

package tictactoe;

import java.util.Scanner;

public class TicTacToe {

    public void startGame() {
        char[][] board = buildBoard();
        boolean xTurn = true;
        printBoard(board);

        while (true) {

            Scanner scanner = new Scanner(System.in);

            if (scanner.hasNextInt()) {
                int x = scanner.nextInt();
                int y = scanner.nextInt();

                if (x < 1 || x > 3 || y < 1 || y > 3) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else if (board[x - 1][y - 1] != ' ') {
                    System.out.println("This cell is occupied! Choose another one!");
                } else if (xTurn) {
                    board[x - 1][y - 1] = 'X';
                    xTurn = false;
                    printBoard(board);
                } else {
                    board[x - 1][y - 1] = 'O';
                    xTurn = true;
                    printBoard(board);
                }

                if (!analyzeGame(board).equals("Game not finished")) {
                    System.out.println(analyzeGame(board));
                    break;
                }

            } else {
                System.out.println("You should enter numbers!");
            }
        }
    }

    public char[][] buildBoard() {
        char[][] board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        return board;
    }

    public String analyzeGame(char[][] board) {
        int occupied = 0;
        boolean winX = false;
        boolean winO = false;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != ' ') {
                    occupied++;
                }
            }

            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == 'X') {
                    winX = true;
                } else if (board[i][0] == 'O') {
                    winO = true;
                }
            }

            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == 'X') {
                    winX = true;
                } else if (board[0][i] == 'O') {
                    winO = true;
                }
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[1][1] == 'X') {
                winX = true;
            } else if (board[1][1] == 'O') {
                winO = true;
            }
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[1][1] == 'X') {
                winX = true;
            } else if (board[1][1] == 'O') {
                winO = true;
            }
        }

        if (winX) {
            return "X wins";
        }
        if (winO) {
            return "O wins";
        }
        if (occupied == 9) {
            return "Draw";
        }

        return "Game not finished";
    }

    public void printBoard(char[][] board) {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }
}
