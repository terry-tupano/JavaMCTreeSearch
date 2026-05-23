/*-
 * #%L
 * JavaMCTreeSearch
 * %%
 * Copyright (C) 2026 Terry Tupano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.simone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A stateless solver for a Markov Decision Process (MDP).
 *
 * This solver does not permanently store states at each of the nodes in the
 * tree. Instead simulations are rerun for each MCTS iteration starting from the
 * root node. This allows maximum flexibility in handling a variety of MDPs but
 * may be slower due to the repeated simulations.
 *
 * @param <S> the type that represents the states of the MDP
 * @param <A> the type that represents the actions of the MDP
 */
public class GenericSolver<S, A> extends Solver<A, ActionNode<S, A>> {

    private final MDP<S, A> mdp;
    private final int simulationDepthLimit;
    private final double rewardDiscountFactor;
    private final Random random = new Random();
    private ActionNode<S, A> root;

    /**
     * Constructor.
     * 
     * @param mdp - the MDP to solve
     * @param simulationDepthLimit - the maximum depth for simulations
     * @param explorationConstant - the constant to use for UCT calculation
     * @param rewardDiscountFactor - the discount factor for rewards 
     * @param verbose - true to print statistics
     */
    public GenericSolver(MDP<S, A> mdp, int simulationDepthLimit, double explorationConstant,
            double rewardDiscountFactor, boolean verbose) {
        super(verbose, explorationConstant);

        this.root = new ActionNode<>(null, null);
        this.mdp = mdp;
        this.simulationDepthLimit = simulationDepthLimit;
        this.rewardDiscountFactor = rewardDiscountFactor;

        simulateActions(root);
    }

    @Override
    public ActionNode<S, A> getRoot() {
        return root;
    }

    @Override
    public void setRoot(ActionNode<S, A> root) {
        this.root = root;
    }

    @Override
    public ActionNode<S, A> select(ActionNode<S, A> node) {
        // If this node is a leaf node, return it
        if (node.getChildren().isEmpty()) {
            return node;
        }

        ActionNode<S, A> currentNode = node;
        simulateActions(node);

        // Run a simulation greedily
        while (true) {
            if (mdp.isTerminal(currentNode.getState())) {
                return currentNode;
            }

            Collection<ActionNode<S, A>> currentChildren = currentNode.getChildren();

            List<A> exploredActions = new ArrayList<>();
            for (ActionNode<S, A> child : currentChildren) {
                exploredActions.add(child.getInducingAction());
            }

            boolean hasUnexplored = false;
            for (A action : currentNode.getValidActions()) {
                if (!exploredActions.contains(action)) {
                    hasUnexplored = true;
                    break;
                }
            }

            // There are unexplored actions
            if (hasUnexplored) {
                return currentNode;
            }

            // All actions have been explored, choose best one
            ActionNode<S, A> bestChild = null;
            double bestUCT = Double.NEGATIVE_INFINITY;

            for (ActionNode<S, A> child : currentChildren) {
                double uct = calculateUCT(child);

                if (uct > bestUCT) {
                    bestUCT = uct;
                    bestChild = child;
                }
            }

            if (bestChild == null) {
                throw new RuntimeException(
                        "There were no children for explored node");
            }

            currentNode = bestChild;
            simulateActions(currentNode);
        }
    }

    @Override
    public ActionNode<S, A> expand(ActionNode<S, A> node) {
        // If the node is terminal, return it
        if (mdp.isTerminal(node.getState())) {
            return node;
        }

        List<A> exploredActions = new ArrayList<>();
        for (ActionNode<S, A> child : node.getChildren()) {
            exploredActions.add(child.getInducingAction());
        }

        List<A> unexploredActions = new ArrayList<>();
        for (A action : node.getValidActions()) {
            if (!exploredActions.contains(action)) {
                unexploredActions.add(action);
            }
        }

        if (unexploredActions.isEmpty()) {
            throw new RuntimeException("No unexplored actions available");
        }

        // Choose random unexplored action
        A actionTaken = unexploredActions.get(random.nextInt(unexploredActions.size()));

        // Transition to new state for given action
        ActionNode<S, A> newNode = new ActionNode<>(node, actionTaken);

        node.addChild(newNode);

        simulateActions(newNode);

        return newNode;
    }

    @Override
    public double simulate(ActionNode<S, A> node) {
        traceln("Simulation:");

        // If state is terminal, the reward is defined by MDP
        if (mdp.isTerminal(node.getState())) {
            traceln("Terminal state reached");
            S prevState = node.getParent() != null ? node.getParent().getState() : null;
            A action = node.getInducingAction();
            return mdp.reward(prevState, action, node.getState());
        }

        int depth = 0;
        S currentState = node.getState();
        double discount = rewardDiscountFactor;

        while (true) {
            List<A> validActions = new ArrayList<>();

            for (A action : mdp.actions(currentState)) {
                validActions.add(action);
            }

            A randomAction = validActions.get(random.nextInt(validActions.size()));

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
    public void backpropagate(ActionNode<S, A> node, double reward) {
        ActionNode<S, A> currentStateNode = node;
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

    private void simulateActions(ActionNode<S, A> node) {
        ActionNode<S, A> parent = node.getParent();

        if (parent == null) {
            S initialState = mdp.initialState();

            node.setState(initialState);
            node.setValidActions(mdp.actions(initialState));

            return;
        }

        // Parent simulation must be run before current simulation can proceed
        S parentState = parent.getState();

        A parentAction = node.getInducingAction();

        if (parentAction == null) {
            throw new RuntimeException("Action was null for non-null parent");
        }

        S state = mdp.transition(parentState, parentAction);

        node.setState(state);
        node.setValidActions(mdp.actions(state));
    }
}