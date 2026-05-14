package de.simone.tictactoe;

public class Action {
    int row;
    int col;
    char seed;

    public Action(int row, int col, char playerSeed) {
        this.row = row;
        this.col = col;
        this.seed = playerSeed;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ") =" + seed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Action action = (Action) obj;
        return row == action.row && col == action.col && seed == action.seed;
    }
}
