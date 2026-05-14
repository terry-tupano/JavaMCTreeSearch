package de.simone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A set of unit tests to test the mctreesearch4j package.
 *
 * A simple MDP is designed in TestStochasticMDP.
 *
 * A set of tests is designed for the GenericSolver and the base
 * Solver class to ensure proper implementation of the vital classes.
 */
public class CoreLibraryTests {

    private final TestStochasticMDP testMDP = new TestStochasticMDP();

    private final int depthLimit = 29;
    private final double exploreConstant = 0.4;
    private final double rewardDiscount = 0.01;
    private final boolean verbose = false;

    private final GenericSolver<TestStochasticState, TestStochasticAction> solver = new GenericSolver<>(testMDP,
            depthLimit, exploreConstant, rewardDiscount, verbose);

    // private final ActionNode<TestStochasticState, TestStochasticAction> testRoot = solver.root;
    private final ActionNode<TestStochasticState, TestStochasticAction> testRoot = solver.getRoot();

    /**
     * Tests the GenericSolver.expand method functionality
     * without failure.
     */
    @Test
    public void coreLibraryTestExpandMethod() {
        solver.expand(testRoot);
    }

    /**
     * Tests the GenericSolver.select method functionality
     * without failure.
     */
    @Test
    public void coreLibraryTestSelectMethod() {
        solver.select(testRoot);
    }

    /**
     * Tests the GenericSolver.simulate method functionality
     * without failure.
     */
    @Test
    public void coreLibraryTestSimulation() {
        solver.simulate(testRoot);
    }

    /**
     * Tests both expand and select functionality working together.
     */
    @Test
    public void coreLibraryTestExpandAndSelect() {
        solver.select(coreLibraryTestExpand());
    }

    /**
     * Tests to ensure expand works properly and that
     * correct meta-info is transferred from state-to-state.
     */
    @Test
    public ActionNode<TestStochasticState, TestStochasticAction> coreLibraryTestExpand() {

        ActionNode<TestStochasticState, TestStochasticAction> nextNode = solver.expand(testRoot);

        int iterC = 2 + (int) (Math.random() * 98);

        for (int i = 1; i <= iterC; i++) {
            nextNode = solver.expand(nextNode);
        }

        assertTrue(nextNode.getState().counter == iterC + 1, "Test Search Tree Expansion");

        return nextNode;
    }

    /**
     * Tests to ensure backpropagation propagates values properly.
     *
     * In this test the monotonic relation of n visits
     * from child to parent is always maintained.
     */
    @Test
    public void coreLibraryTestBackpropagation() {
        ActionNode<TestStochasticState, TestStochasticAction> node = testRoot;

        int n1 = node.getN();

        solver.backpropagate(testRoot, 20.0);

        int n2 = node.getN();

        assertTrue(n2 >= n1, "Monotonic guarantee of n child <= n parent in single Backpropagate");
    }

    /**
     * Test all key mechanisms by running a single iteration.
     */
    @Test
    public void coreLibraryTestSingleIteration() {
        solver.runTreeSearchIteration();
    }

    /**
     * Tests the MCTS algorithm as a whole.
     *
     * Ensures a monotonic relationship where
     * child visit count <= parent visit count.
     */
    @Test
    public void coreLibraryTestMCTS() {
        solver.runTreeSearch(99);

        int rootN = testRoot.getN();

        System.out.println("n root: " + rootN);

        var nextNodes = testRoot.getChildren();

        int n1 = testRoot.getN();

        while (!nextNodes.isEmpty()) {

            int randomIndex = (int) (Math.random() * nextNodes.size());

            ActionNode<TestStochasticState, TestStochasticAction> nextNode = nextNodes.stream()
                    .skip(randomIndex)
                    .findFirst()
                    .orElseThrow();

            int n2 = nextNode.getN();

            nextNodes = nextNode.getChildren();

            assertTrue(n2 < n1, "Monotonic guarantee of n child <= n parent in MCTS");

            n1 = n2;
        }
    }
}
