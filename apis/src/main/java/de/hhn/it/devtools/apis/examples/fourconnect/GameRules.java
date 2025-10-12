package de.hhn.it.devtools.apis.examples.fourconnect;

public class GameRules {

    private final int rows;
    private final int columns;

    public GameRules(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    
    public boolean checkWinner(Player[][] board, Player player) {
        return checkHorizontal(board, player)
            || checkVertical(board, player)
            || checkDiagonal(board, player);
    }

    
    private boolean checkHorizontal(Player[][] board, Player player) {
       
        return false;
    }

    
    private boolean checkVertical(Player[][] board, Player player) {
      
        return false;
    }

   
    private boolean checkDiagonal(Player[][] board, Player player) {
      
        return false;
    }

    
}
