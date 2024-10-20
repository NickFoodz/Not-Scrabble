public class Position {
    private int row; // row index
    private int col; // column

    private char tile; // scrabble tile letter for representation
    private boolean occupied; // true if position is occupied, false otherwise


    public Position(int row, int col) {
        this.row = row;
        this.col = col;
        this.tile = ' ';
        this.occupied = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getTile() {
        return tile;
    }

    public void setTile(char tile) {
        this.tile = tile;
        this.occupied = true;
    }

    public boolean isOccupied() {
        return occupied;
    }

    @Override
    public String toString() {
        char colLetter = (char) ('A' + col);
        return (row + 1) + "" + colLetter;
    }
}
