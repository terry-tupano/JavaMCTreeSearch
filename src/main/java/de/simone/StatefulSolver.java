package de.simone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A stateful solver for a Markov Decision Process (MDP).
 *
 * This solver permanently stores states at each node in the tree.
 * Simulations are run once throughout the entire MCTS run.
 *
 * This may not work well in stochastic MDPs with high branching factors, but
 * may improve performance for deterministic MDPs with lower branching factors.
 *
 * @param <S> the type representing states in the MDP
 * @param <A> the type representing actions in the MDP
 */
public class StatefulSolver<S, A> extends Solver<A, StateNode<S, A>> {

    protected final MDP<S, A> mdp;
    protected final int simulationDepthLimit;
    protected final double rewardDiscountFactor;
    protected StateNode<S, A> root;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public StatefulSolver(MDP<S, A> mdp, int simulationDepthLimit, double explorationConstant,
            double rewardDiscountFactor, boolean verbose) {
        super(verbose, explorationConstant);

        this.mdp = mdp;
        this.simulationDepthLimit = simulationDepthLimit;
        this.rewardDiscountFactor = rewardDiscountFactor;

        this.root = createNode(null, null, mdp.initialState());
    }

    @Override
    public StateNode<S, A> getRoot() {
        return root;
    }

    @Override
    public void setRoot(StateNode<S, A> root) {
        this.root = root;
    }

    @Override
    public StateNode<S, A> select(StateNode<S, A> node) {
        StateNode<S, A> currentNode = node;

        while (true) {
            // If node is terminal, return it
            if (mdp.isTerminal(currentNode.getState())) {
                return currentNode;
            }

            Collection<A> exploredActions = currentNode.exploredActions();

            assert currentNode.getValidActions().size() >= exploredActions.size();

            // This state has not been fully explored
            if (currentNode.getValidActions().size() > exploredActions.size()) {
                return currentNode;
            }

            // This state has been fully explored
            Collection<StateNode<S, A>> children = currentNode.getChildren();

            StateNode<S, A> bestChild = null;
            double bestUCT = Double.NEGATIVE_INFINITY;

            for (StateNode<S, A> child : children) {
                double uct = calculateUCT(child);
                if (uct > bestUCT) {
                    bestUCT = uct;
                    bestChild = child;
                }
            }

            if (bestChild == null) {
                throw new RuntimeException("There were no children for explored node");
            }

            currentNode = bestChild;
        }
    }

    @Override
    public StateNode<S, A> expand(StateNode<S, A> node) {
        // If node is terminal, return it
        if (node.isTerminal()) {
            return node;
        }

        List<A> exploredActions = new ArrayList<>();

        for (StateNode<S, A> child : node.getChildren()) {
            exploredActions.add(child.getInducingAction());
        }

        List<A> unexploredActions = new ArrayList<>();

        for (A action : node.getValidActions()) {
            if (!exploredActions.contains(action)
                    && !unexploredActions.contains(action)) {
                unexploredActions.add(action);
            }
        }

        if (unexploredActions.isEmpty()) {
            throw new RuntimeException(
                    "No unexplored actions available");
        }

        A actionTaken = unexploredActions.get(random.nextInt(unexploredActions.size()));

        // Transition to new state
        S newState = mdp.transition(node.getState(), actionTaken);

        return createNode(node, actionTaken, newState);
    }

    @Override
    public double simulate(StateNode<S, A> node) {
        traceln("Simulation:");

        // If terminal, reward is defined by MDP
        if (node.isTerminal()) {
            traceln("Terminal state reached");

            return mdp.reward(
                    node.getParent() != null
                            ? node.getParent().getState()
                            : null,
                    node.getInducingAction(),
                    node.getState());
        }

        int depth = 0;
        S currentState = node.getState();
        double discount = rewardDiscountFactor;

        while (true) {
            List<A> validActions = new ArrayList<>();

            for (A action : mdp.actions(currentState)) {
                validActions.add(action);
            }

            A randomAction = validActions.get(
                    random.nextInt(validActions.size()));

            S newState = mdp.transition(currentState, randomAction);
            trace("-> " + randomAction + " ");
            trace("-> " + newState + " ");

            if (mdp.isTerminal(newState)) {
                double reward = mdp.reward(currentState, randomAction, newState) * discount;
                traceln("-> Terminal state reached : " + reward);

                return reward;
            }

            currentState = newState;
            depth++;
            discount *= rewardDiscountFactor;

            if (depth > simulationDepthLimit) {
                double reward = mdp.reward(currentState, randomAction, newState) * discount;
                traceln("-> Depth limit reached: " + reward);

                return reward;
            }
        }
    }

    @Override
    public void backpropagate(StateNode<S, A> node, double reward) {
        StateNode<S, A> currentStateNode = node;
        double currentReward = reward;

        while (true) {
            currentStateNode.setMaxReward(Math.max(currentReward, currentStateNode.getMaxReward()));
            currentStateNode.setReward(currentStateNode.getReward() + currentReward);
            currentStateNode.setN(currentStateNode.getN() + 1);
            if (currentStateNode.getParent() == null) {
                break;
            }

            currentStateNode = currentStateNode.getParent();
            currentReward *= rewardDiscountFactor;
        }
    }

    // Utilities

    private StateNode<S, A> createNode(StateNode<S, A> parent,
            A inducingAction, S state) {
        List<A> validActions = new ArrayList<>(
                mdp.actions(state));

        boolean isTerminal = mdp.isTerminal(state);

        StateNode<S, A> stateNode = new StateNode<>(parent, inducingAction, state, validActions,
                isTerminal);

        if (parent != null) {
            parent.addChild(stateNode);
        }

        return stateNode;
    }
}