package de.simone;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of nodes used by the stateful StatefulSolver
 * to solve a Markov Decision Process (MDP).
 *
 * This type contains several convenience properties for implementing
 * a stateful MDP solver.
 *
 * @param <S> the type representing states of the MDP
 * @param <A> the type representing actions of the MDP
 */
public class StateNode<S, A> extends Node<A, StateNode<S, A>> {

    private final S state;
    private final Collection<A> validActions;
    private final boolean terminal;

    private final Map<A, StateNode<S, A>> children = new HashMap<>();

    /**
     * Constructor.
     *
     * @param parent         the parent node
     * @param inducingAction the action that led to this node
     * @param state          the state represented by this node
     * @param validActions   actions available from this state
     * @param isTerminal     whether this node is terminal
     */
    public StateNode(StateNode<S, A> parent, A inducingAction, S state, Collection<A> validActions,
            boolean isTerminal) {
        super(parent, inducingAction);

        this.state = state;
        this.validActions = validActions;
        this.terminal = isTerminal;
    }

    /**
     * Returns the state represented by this node.
     */
    public S getState() {
        return state;
    }

    /**
     * Returns valid actions from this node.
     */
    public Collection<A> getValidActions() {
        return validActions;
    }

    /**
     * Returns whether this node is terminal.
     */
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public void addChild(StateNode<S, A> child) {
        A action = child.getInducingAction();

        if (action == null) {
            throw new RuntimeException("Inducing action must be set on child");
        }

        if (children.containsKey(action)) {
            throw new RuntimeException("A child with the same inducing action has already been added");
        }

        children.put(action, child);
    }

    @Override
    public Collection<StateNode<S, A>> getChildren(A action) {
        if (action == null) {
            return children.values();
        }

        StateNode<S, A> child = children.get(action);

        if (child == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(child);
    }

    /**
     * Returns all actions that have been explored from this node.
     */
    public Collection<A> exploredActions() {
        return children.keySet();
    }

    @Override
    public String toString() {
        return String.format("State: %s, Max Reward: %.5f", state, getMaxReward());
    }
}
