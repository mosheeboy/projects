import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// SudokuSolver class to read the input file, solve each puzzle and display the solutions
public class SudokuSolver {
    public static void main(String[] args) {
        try {
            // Start timer
            long startTime = System.currentTimeMillis();

            // Read puzzles from input file into list of puzzles
            SudokuPuzzle[] puzzles = readPuzzlesFromFile("p096_sudoku.txt");

            // Solve each puzzle and display the solution
            for (int i = 0; i < puzzles.length; i++) {
                System.out.println("Grid " + (i + 1));
                puzzles[i].solve();
            }

            // Stop timer and display total run time
            long endTime = System.currentTimeMillis();
            System.out.println("Total Run Time "+(endTime - startTime) + " ms");

        } catch (IOException e) {

            // Catch any IO exceptions reading the input file
            System.out.println("Error reading the input file.");
            e.printStackTrace();
        }
    }

    // Method to read the input file and return a list of puzzles
    public static SudokuPuzzle[] readPuzzlesFromFile(String filename) throws IOException {

        // Use Java Scanner class to read the input file line by line
        Scanner scanner = new Scanner(new File(filename));

        // Create a list of puzzles to return and SudokuPuzzle object to store the current puzzle
        List<SudokuPuzzle> puzzles = new ArrayList<>();
        SudokuPuzzle currentPuzzle = null;

        // Loop through the 50 puzzles in the input file
        for(int i = 0; i < 50; i++){
            // Skip the first line of each puzzle (Grid XX)
            scanner.nextLine();
            // Create a new SudokuPuzzle object and initialize grid to store the current puzzle
            currentPuzzle = new SudokuPuzzle();
            currentPuzzle.grid = new int[9][9];

            // Loop through the 9 lines of each puzzle representing each sub-grid
            for(int j = 0; j < 9; j++){
                // Read each line of the sub-grid and store into character array
                char [] line = scanner.nextLine().toCharArray();
                // Loop through the 9 characters in the line and store into the grid
                for(int k = 0; k < 9; k++){
                    currentPuzzle.grid[j][k] = Character.getNumericValue(line[k]);
                }
            }
            // Add the current puzzle to the list of puzzles
            puzzles.add(currentPuzzle);
        }
        // Return the list of puzzles as an array
        return puzzles.toArray(new SudokuPuzzle[puzzles.size()]);
    }
}

class SudokuPuzzle {

    // 2D array to store the puzzle grid
    public int[][] grid;
    
    // Default constructor
    public SudokuPuzzle() {
    }

    // Public solve method to solve the puzzle and display the solution if there is one
    public void solve() {
        // Print the unsolved puzzle for quick reference
        System.out.println("Unsolved");
        displayGrid();
        // Create a list of blank cells and populate it with all cells that need to be filled
        List<int[]> blankCells = new ArrayList<>();
        getBlankCells(grid, blankCells);
        // Call the recursive solveSudoku method to solve the puzzle, starting with the first blank cell
        if(solveSudoku(grid, blankCells, 0)){
            // If the puzzle is solved, display the solution grid and the sum of the top-left 3 digits, otherwise display "Not solved"
            System.out.println("Solved");
            displayGrid();
            displaySum();
        } else {
            System.out.println("Not solved");
        }
    }

    // Private recursive method to solve the puzzle by passing in list of blank cells and index
    private boolean solveSudoku(int[][] board, List<int[]> emptyCells, int index) {

        // Base case: If all blank cells have been filled, the puzzle is solved and return true
        if (index == emptyCells.size()) {
            return true;
        }

        // Get the row and column of the current blank cell to try to fill
        int[] cell = emptyCells.get(index);
        int row = cell[0];
        int col = cell[1];

        // Try numbers 1-9 in the current cell
        for (int num = 1; num <= 9; num++) {

            // If number is valid, update the grid with the number
            if (isValid(board, row, col, num)) {
                board[row][col] = num;

                // Recursively call solveSudoku with the next blank cell to be filled and return true if the puzzle is solved
                if (solveSudoku(board, emptyCells, index + 1)) {
                    return true;
                }
                
                // If the puzzle is not solved, backtrack by setting the current cell back to 0 and try the next number
                board[row][col] = 0; // Backtrack
            }
        }

        // If no valid number is found, return false to backtrack to the next blank cell to try to fill
        return false;
    }

    // Private method to check if a number is valid in a cell according to Sudoku rules
    private boolean isValid(int[][] board, int row, int col, int num) {

        // Check if the number already exists in the row or column of current blank cell
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false; 
            }
        }

        // Check if the number already exists in the 3x3 subgrid of current blank cell
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        // If the current number is not found in the row, column or subgrid of blank cell, return true
        return true; 
    }   

    // Private method to populate the list of blank cells with all cells that need to be filled (are 0)
    private void getBlankCells(int[][] board, List<int[]> blankCells) {
        // Loop through all grid columns and rows and dd the cell to the list if it is zero
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    blankCells.add(new int[]{i, j});
                }
            }
        }
    }

    // Public method to display the sum of the top-left 3 digits
    public void displaySum() {
        int sum = 0;
        // Loop through the first 3 columns of the first row, add the digits to the sum and print
        for (int i = 0; i < 3; i++) {
            sum += grid[0][i];
        }
        System.out.println("Sum of 3 digits in top-left corner: " + sum);
    }

    // Public method to display the grid of the solved puzzle with ASCII art formatting
    public void displayGrid() {
        for (int i = 0; i < 9; i++) {
            // Every 3 rows, print a horizontal line (including first row)
            if (i % 3 == 0) {
                System.out.print("+-----+-----+-----+\n");
            }
            // Loop through each column in the row
            for (int j = 0; j < 9; j++) {
                // If it is first columns in sub-grid, print a vertical line and print the number with a space afterwards
                if (j % 3 == 0) {
                    System.out.print("|");
                    System.out.print(grid[i][j] + " ");
                }
                // Print the last column of each sub-grid with a vertical line and no space
                else if (j == 2 || j == 5) {
                    System.out.print(grid[i][j]);
                }
                // Print the last column of the last sub-grid with a vertical line and no space
                else if (j == 8){
                    System.out.print(grid[i][j] + "|");
                }
                // Print all other numberd with a space afterwards
                else{
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
        // Print the last horizontal line
        System.out.print("+-----+-----+-----+\n");

    }
}
