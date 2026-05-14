package de.simone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A representation of nodes used by the stateless GenericSolver to solve a
 * Markov Decision Process (MDP).
 *
 * This type contains several convenience properties for implementing a
 * stateless MDP solver.
 *
 * @param <S> the type that represents the states of the MDP
 * @param <A> the type that represents the actions of the MDP
 */
public class ActionNode<S, A> extends Node<A, ActionNode<S, A>> {

    private S state;
    private Iterable<A> validActions;

    private final List<ActionNode<S, A>> children = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param parent         the parent node
     * @param inducingAction the action that led to this node
     */
    public ActionNode(ActionNode<S, A> parent, A inducingAction) {
        super(parent, inducingAction);
    }

    /**
     * The state at this node.
     * This is only available if a simulation has run.
     */
    public S getState() {
        if (state == null) {
            throw new RuntimeException("Simulation not run at depth: " + getDepth());
        }
        return state;
    }

    public void setState(S state) {
        this.state = state;
    }

    /**
     * A list of actions that can be taken from this node.
     * This is only available if a simulation has run.
     */
    public Iterable<A> getValidActions() {
        if (validActions == null) {
            throw new RuntimeException("Simulation not run");
        }
        return validActions;
    }

    public void setValidActions(Iterable<A> validActions) {
        this.validActions = validActions;
    }

    @Override
    public void addChild(ActionNode<S, A> child) {
        children.add(child);
    }

    @Override
    public Collection<ActionNode<S, A>> getChildren(A action) {
        if (action == null) {
            return children;
        }

        return children.stream()
                .filter(c -> {
                    A childAction = c.getInducingAction();
                    return action.equals(childAction);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("Action: %s, Max Reward: %.5f", getInducingAction(), getMaxReward());
    }
}
