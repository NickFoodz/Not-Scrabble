public class Position {
    private int row; // row index
    private int col; // column

    private Tile tile; // scrabble tile letter for representation
    private boolean occupied; // true if position is occupied, false otherwise


    public Position(int row, int col) {
        this.row = row;
        this.col = col;
        this.tile = null;
        this.occupied = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        this.occupied = (tile != null);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public char displayTile() {
        return tile != null ? tile.getLetter() : ' ';
    }

    @Override
    public String toString() {
        char colLetter = (char) ('A' + col);
        return (row + 1) + "" + colLetter;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Position position = (Position) obj;
        return this.row == position.row && this.col == position.col;
    }
}
