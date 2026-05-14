package de.simone;

public class TestStochasticState {

    public final int stateIndex;
    public final int counter;

    public TestStochasticState(int stateIndexInput) {
        this(stateIndexInput, 0);
    }

    public TestStochasticState(int stateIndexInput, int counterInput) {
        this.stateIndex = stateIndexInput;
        this.counter = counterInput;
    }
}