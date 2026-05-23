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

import java.util.Collection;

/**
 * A representation of tree nodes used for Monte Carlo Tree Search (MCTS).
 *
 * This abstract type contains no logic but includes the definitions of
 * functions that must be supported for each tree node as well as commonly used
 * values including depth, number of visits, current reward and the max reward
 * among its children.
 *
 * @param <A>        the type that represents the actions that can be taken
 *                   in the MDP
 * @param <SelfType> a convenience parameter to represent the type itself
 */
public abstract class Node<A, SelfType extends Node<A, SelfType>> {

    private final SelfType parent;
    private final A inducingAction;
    private final int depth;

    /**
     * The number of visits to the node.
     */
    private int n = 0;

    /**
     * The reward value of the node.
     */
    private double reward = 0.0;

    /**
     * The max reward value among the children of this node.
     */
    private double maxReward = 0.0;

    /**
     * Constructor.
     *
     * @param parent         the parent node
     * @param inducingAction the action that led to this node
     */
    protected Node(SelfType parent, A inducingAction) {
        this.parent = parent;
        this.inducingAction = inducingAction;
        this.depth = (parent == null) ? 0 : parent.getDepth() + 1;
    }

    /**
     * Get the parent node.
     */
    public SelfType getParent() {
        return parent;
    }

    /**
     * Get the action that led to this node.
     */
    public A getInducingAction() {
        return inducingAction;
    }

    /**
     * Get the depth of the node.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Get the number of visits.
     */
    public int getN() {
        return n;
    }

    /**
     * Set the number of visits.
     */
    public void setN(int n) {
        this.n = n;
    }

    /**
     * Get the reward value.
     */
    public double getReward() {
        return reward;
    }

    /**
     * Set the reward value.
     */
    public void setReward(double reward) {
        this.reward = reward;
    }

    /**
     * Get the max reward value.
     */
    public double getMaxReward() {
        return maxReward;
    }

    /**
     * Set the max reward value.
     */
    public void setMaxReward(double maxReward) {
        this.maxReward = maxReward;
    }

    /**
     * Add a child to the current node.
     */
    public abstract void addChild(SelfType child);

    /**
     * Get all the children of the current node.
     */
    public abstract Collection<SelfType> getChildren(A action);

    /**
     * Convenience overload equivalent to Kotlin default parameter.
     */
    public Collection<SelfType> getChildren() {
        return getChildren(null);
    }
}