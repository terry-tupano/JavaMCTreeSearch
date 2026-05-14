package de.simone;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TestStochasticMDP extends MDP<TestStochasticState, TestStochasticAction> {

    private final double bias;

    private final int maxCounter = 10;

    private final List<TestStochasticAction> allActions = Arrays.asList(TestStochasticAction.values());

    public TestStochasticMDP() {
        this(0.75);
    }

    public TestStochasticMDP(double bias) {
        this.bias = bias;
    }

    @Override
    public TestStochasticState initialState() {
        return new TestStochasticState(0);
    }

    @Override
    public boolean isTerminal(TestStochasticState state) {
        return false;
    }

    @Override
    public double reward(TestStochasticState previousState, TestStochasticAction action, TestStochasticState state) {
        return state.stateIndex * 2.0;
    }

    @Override
    public TestStochasticState transition(TestStochasticState state, TestStochasticAction action) {
        int directionIndex = 0;

        if (Math.random() < bias) {
            if (action == TestStochasticAction.LEFT) {
                directionIndex = state.stateIndex - 1;
            }

            if (action == TestStochasticAction.RIGHT) {
                directionIndex = state.stateIndex + 1;
            }
        } else {
            if (action == TestStochasticAction.LEFT) {
                directionIndex = state.stateIndex + 1;
            }

            if (action == TestStochasticAction.RIGHT) {
                directionIndex = state.stateIndex - 1;
            }
        }

        return new TestStochasticState(directionIndex, state.counter + 1);
    }

    @Override
    public Collection<TestStochasticAction> actions(TestStochasticState state) {
        return allActions;
    }
}
