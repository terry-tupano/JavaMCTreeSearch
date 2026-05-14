package de.simone;

import java.util.Collection;

/**
 * A representation of Markov Decision Processes (MDPs).
 *
 * This abstract type contains no logic but defines the functions
 * that must be implemented for a valid MDP.
 *
 * @param <S>  the type that represents states in the MDP
 * @param <A> the type that represents actions in the MDP
 */
public abstract class MDP<S, A> {

    /**
     * Represents a transition of MDP state.
     *
     * @param state  the current state
     * @param action the action to take
     * @return the new state
     */
    public abstract S transition(S state, A action);

    /**
     * Represents the reward function of the MDP.
     *
     * @param previousState the previous state (may be null)
     * @param action        the action taken (may be null)
     * @param state         the resulting terminal state
     * @return the reward score
     */
    public abstract double reward(S previousState, A action, S state);

    /**
     * Returns the initial state of the MDP.
     */
    public abstract S initialState();

    /**
     * Determines whether the given state is terminal.
     */
    public abstract boolean isTerminal(S state);

    /**
     * Returns the actions available for the given state.
     */
    public abstract Collection<A> actions(S state);
}
