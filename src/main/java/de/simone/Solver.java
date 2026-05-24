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

import static java.lang.Math.log;
import static java.lang.Math.sqrt;

/**
 * A representation of Markov Decision Process (MDP) solvers
 * using Monte Carlo Tree Search (MCTS) methods.
 *
 * This abstract type contains four functions that must be implemented
 * for a valid solver. Additional utility functions are provided for
 * convenience but can be overridden.
 *
 * @param <A>        the type that represents actions in the MDP
 * @param <N> the type that represents nodes in the tree
 */
public abstract class Solver<A, N extends Node<A, N>> {

    protected final boolean verbose;
    protected final double explorationConstant;

    /**
     * Constructor.
     * @param verbose - true to print statistics
     * @param explorationConstant - the constant to use for UCT calculation. (standard c=sqrt(2))
     */
    protected Solver(boolean verbose, double explorationConstant) {
        this.verbose = verbose;
        this.explorationConstant = explorationConstant;
    }

    /**
     * The root node of the tree.
     */
    public abstract N getRoot();

    public abstract void setRoot(N root);

    /**
     * Returns a leaf node in the tree given a starting node.
     */
    public abstract N select(N node);

    /**
     * Creates and returns a new child node given a leaf node.
     */
    public abstract N expand(N node);

    /**
     * Runs a simulation from the given leaf node and computes a score.
     */
    public abstract double simulate(N node);

    /**
     * Propagates the reward for the given node to the root.
     */
    public abstract void backpropagate(N node, double reward);

    /**
     * Runs a given number of iterations of MCTS.
     */
    public void runTreeSearch(int iterations) {
        for (int i = 0; i <= iterations; i++) {
            traceln("");
            traceln("New iteration " + i);
            traceln("=============");

            runTreeSearchIteration();
        }
    }

    /**
     * Runs a single iteration of MCTS.
     *
     * Default order:
     * select -> expand -> simulate -> backpropagate
     */
    public void runTreeSearchIteration() {
        // Selection
        N best = select(getRoot());

        if (verbose) {
            traceln("Selected:");
            displayNode(best);
        }

        // Expansion
        N expanded = expand(best);

        if (verbose) {
            traceln("Expanding:");
            displayNode(expanded);
        }

        // Simulation
        double simulatedReward = simulate(expanded);

        traceln("Simulated Reward: " + simulatedReward);

        // Backpropagation
        backpropagate(expanded, simulatedReward);
    }

    // Utilities

    /**
     * Calculates the UCT score of a node.
     */
    protected double calculateUCT(N node) {
        int parentN = (node.getParent() != null) ? node.getParent().getN() : node.getN();

        return calculateUCT(parentN, node.getN(), node.getReward(), explorationConstant);
    }

    /**
     * Calculates the UCT score using explicit parameters.
     */
    protected double calculateUCT(int parentN, int n, double reward, double explorationConstant) {
        return reward / n + explorationConstant * sqrt(log((double) parentN) / n);
    }

    /**
     * Returns the best action from the root by selecting the child with the highest
     * number of visits.
     */
    public A extractOptimalAction() {
        Collection<N> children = getRoot().getChildren();

        N bestChild = null;
        int bestVisits = Integer.MIN_VALUE;

        for (N child : children) {
            if (child.getN() > bestVisits) {
                bestVisits = child.getN();
                bestChild = child;
            }
        }

        return bestChild != null
                ? bestChild.getInducingAction()
                : null;
    }

    // Debug and Diagnostics

    /**
     * Prints the string with a newline if verbose is enabled.
     */
    protected void traceln(String string) {
        if (verbose) {
            System.out.println(string);
        }
    }

    /**
     * Prints the string if verbose is enabled.
     */
    protected void trace(String string) {
        if (verbose) {
            System.out.print(string);
        }
    }

    /**
     * Formats a node into a string.
     */
    protected String formatNode(N node) {
        return node.toString();
    }

    /**
     * Prints the path from root to the given node.
     */
    public void displayNode(N node) {
        if (node.getParent() != null) {
            displayNode(node.getParent());
        }

        if (node.getDepth() > 0) {
            System.out.print(" ".repeat((node.getDepth() - 1) * 2) + " └");
        }

        System.out.println(formatNode(node));
    }

    /**
     * Prints the tree starting from the root up to a given depth.
     */
    public void displayTree() {
        displayTree(3);
    }

    /**
     * Prints the tree starting from the root up to a given depth.
     */
    public void displayTree(int depthLimit) {
        displayTree(depthLimit, getRoot(), "");
    }

    private void displayTree(int depthLimit, N node, String indent) {
        if (node == null) {
            return;
        }

        if (node.getDepth() > depthLimit) {
            return;
        }

        String line = new StringBuilder()
                .append(indent)
                .append(" ")
                .append(formatNode(node))
                .append(String.format(
                        " (n: %d, reward: %.5f, UCT: %.5f)", node.getN(), node.getReward(), calculateUCT(node)))
                .toString();

        System.out.println(line);

        Collection<N> childrenCollection = node.getChildren();

        if (childrenCollection.isEmpty()) {
            return;
        }

        List<N> children = new ArrayList<>(childrenCollection);

        for (int i = 0; i < children.size() - 1; i++) {
            displayTree(depthLimit, children.get(i), generateIndent(indent) + " ├");
        }

        displayTree(depthLimit, children.get(children.size() - 1),
                generateIndent(indent) + " └");
    }

    private String generateIndent(String indent) {
        return indent
                .replace('├', '│')
                .replace('└', ' ');
    }
}
