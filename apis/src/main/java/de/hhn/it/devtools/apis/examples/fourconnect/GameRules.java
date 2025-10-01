package de.hhn.it.devtools.apis.examples.fourconnect;

public record GameRules(int rows, int columns) {

    
    public boolean checkWinner(char[][] board, char player) {
        return false;
        
    }

    
    private boolean checkHorizontal(char[][] board, char player) {
        return false;
       
    }

    
    private boolean checkVertical(char[][] board, char player) {
        return false;
     
    }

    
    private boolean checkDiagonal(char[][] board, char player) {
        return false;
      
    }

  
}
