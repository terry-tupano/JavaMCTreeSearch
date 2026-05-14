package de.simone;

public enum TestStochasticAction {
    LEFT,
    RIGHT;

    @Override
    public String toString() {
        switch (this) {
            case LEFT:
                return "←";
            case RIGHT:
                return "→";
            default:
                return super.toString();
        }
    }
}
