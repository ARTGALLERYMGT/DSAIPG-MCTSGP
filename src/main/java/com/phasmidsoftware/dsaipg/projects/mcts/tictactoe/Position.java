package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import java.util.*;

public class Position {
    public Position move(int player, int x, int y) {
        if (full()) throw new RuntimeException("Position is full");
        if (player == last) throw new RuntimeException("consecutive moves by same player: " + player);
        int[][] matrix = copyGrid();
        if (matrix[x][y] < 0) {
            matrix[x][y] = player;
            return new Position(matrix, count + 1, player);
        }
        throw new RuntimeException("Position is occupied: " + x + ", " + y);
    }

    public List<int[]> moves(int player) {
        if (player == last) throw new RuntimeException("consecutive moves by same player: " + player);
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                if (grid[i][j] < 0)
                    result.add(new int[]{i, j});
        return result;
    }

    boolean threeInARow() {
        return anyLineMatches(projectRow(0)) || anyLineMatches(projectRow(1)) || anyLineMatches(projectRow(2)) ||
                anyLineMatches(projectCol(0)) || anyLineMatches(projectCol(1)) || anyLineMatches(projectCol(2)) ||
                anyLineMatches(projectDiag(true)) || anyLineMatches(projectDiag(false));
    }

    private boolean anyLineMatches(int[] line) {
        return Arrays.equals(line, xxx);
    }

    static Position parsePosition(final String grid, final int last) {
        int[][] matrix = new int[gridSize][gridSize];
        int count = 0;
        String[] rows = grid.split("\\n", gridSize);
        for (int i = 0; i < gridSize; i++) {
            String[] cells = rows[i].split(" ", gridSize);
            for (int j = 0; j < gridSize; j++) {
                int cell = parseCell(cells[j].trim());
                if (cell >= 0) count++;
                matrix[i][j] = cell;
            }
        }
        return new Position(matrix, count, last);
    }

    static int parseCell(String cell) {
        return switch (cell.toUpperCase()) {
            case "O", "0" -> 0;
            case "X", "1" -> 1;
            default -> -1;
        };
    }

    public Optional<Integer> winner() {
        if (count > 4 && threeInARow()) return Optional.of(last);
        return Optional.empty();
    }

    boolean full() {
        return count == 9;
    }

    int[] projectRow(int i) {
        return grid[i];
    }

    int[] projectCol(int j) {
        int[] result = new int[gridSize];
        for (int i = 0; i < gridSize; i++)
            result[i] = grid[i][j];
        return result;
    }

    int[] projectDiag(boolean b) {
        int[] result = new int[gridSize];
        for (int j = 0; j < gridSize; j++) {
            int i = b ? j : gridSize - j - 1;
            result[j] = grid[i][j];
        }
        return result;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        int cellNumber = 1;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                sb.append(render(grid[i][j], cellNumber++));
                if (j < gridSize - 1) sb.append(" | ");
            }
            if (i < gridSize - 1) sb.append("\n---------\n");
        }
        return sb.toString();
    }


    private int[][] copyGrid() {
        int[][] result = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++)
            result[i] = Arrays.copyOf(grid[i], gridSize);
        return result;
    }

    private char render(int x, int cellNumber) {
        return switch (x) {
            case 0 -> 'O';
            case 1 -> 'X';
            default -> (char) ('0' + cellNumber);
        };
    }


    private void swap(int[][] matrix, int i1, int j1, int i2, int j2) {
        int temp = matrix[i1][j1];
        matrix[i1][j1] = matrix[i2][j2];
        matrix[i2][j2] = temp;
    }

    public Position rotate() {
        int[][] matrix = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                matrix[i][j] = grid[j][gridSize - i - 1];
        return new Position(matrix, count, last);
    }

    public Position reflect(int axis) {
        int[][] matrix = copyGrid();
        switch (axis) {
            case 0 -> { for (int j = 0; j < gridSize; j++) swap(matrix, 0, j, 2, j); }
            case 1 -> { for (int i = 0; i < gridSize; i++) swap(matrix, i, 0, i, 2); }
            default -> throw new RuntimeException("reflect not implemented for " + axis);
        }
        return new Position(matrix, count, last);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return Arrays.deepEquals(grid, position.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                sb.append(grid[i][j]);
                if (j < gridSize - 1) sb.append(',');
            }
            if (i < gridSize - 1) sb.append('\n');
        }
        return sb.toString();
    }

    Position(int[][] grid, int count, int last) {
        this.grid = grid;
        this.count = count;
        this.last = last;
        this.xxx = new int[]{last, last, last};
    }

    private final int[][] grid;
    final int last;
    private final int count;
    private static final int gridSize = 3;
    private final int[] xxx;
}