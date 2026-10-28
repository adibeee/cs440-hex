package src.pas.hex.agents;


// SYSTEM IMPORTS
import edu.bu.pas.hex.*;
import edu.bu.pas.hex.agents.ATPZSMCTSAgent;
import edu.bu.pas.hex.enums.*;
import edu.bu.pas.hex.exceptions.*;
import edu.bu.pas.hex.tree.*;
import edu.bu.pas.hex.utils.*;
import edu.bu.pas.hex.agents.Agent;
import edu.bu.pas.hex.agents.RandomAgent;

import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;



// import javax.swing.Action;

// import org.w3c.dom.Node;

// edu.bu.pas.hex.agents.EasyAgent
// src.pas.hex.agents.UCTAgent
// correctness: C = sqrt(2)
// easy: C = sqrt(2)
// medium: C = sqrt(1)
// hard: C = sqrt(1)



// JAVA PROJECT IMPORTS


/**
 * Represents an {@link Agent} that solves the Hex game (an Alternating Two-Player Zero-Sum) game using a particular
 * flavor of the Monte-Carlo Tree-Search (MCTS) algorithm called Upper-Confidence Tree (UCT) algorithm.
 *
 * <p>Rather than have you develop a generic UCT solver and then adapt it specifically for the game of Hex, I have
 * decided for you to focus on the game of Hex to begin with. Even though I have provided enough abstraction
 * through datatypes like the {@link GenericGame} interface, tailoring the UCT algorithm to perform well on Hex
 * will be challenging enough.
 *
 * <p>Your agent is implementing a flavor of MCTS called UCT. The way any MCTS algorithm functions can be found in
 * the documentation for the {@link ATPZSMCTSAgent}, so I would really consider taking a look at it!
 *
 * <p>The way this agnet is designed is that the four methods <code>selectArtificialLeaf</code>,
 * <code>chooseChild</code>, <code>getRolloutOutcome</code>, and <code>backupStatistics</code> implement the four
 * parts of a single MCTS iteration, while your <code>treeSearch</code> method is where the main logic of othe MCTS
 * algorithm will take place.
 *
 */
public class UCTAgent extends ATPZSMCTSAgent<ATPZSQNode> {

    private static final double C = Math.sqrt(1);
    private final int myPlayerId;
    private final long thinkingTimeInMs;
    private final Agent[] proxyAgents;

    public UCTAgent(final int agentId, final long maxThinkingTimeInMs) {
        super(agentId, maxThinkingTimeInMs);
        // RandomAgent wasn't running without adding final int agentId as a parameter. 
        this.myPlayerId = agentId;
        this.thinkingTimeInMs = maxThinkingTimeInMs;
        this.proxyAgents = new Agent[] {
            new RandomAgent(0, maxThinkingTimeInMs),
            new RandomAgent(1, maxThinkingTimeInMs)
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ATPZSQNode toNode(final GameView game) {
        // you shouldn't need to change this
        return new ATPZSQNode(game, ATPZSNode.PlayerType.MAXIMIZING_PLAYER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ATPZSQNode selectArtificialLeaf(final ATPZSQNode rootNode) {
        // TODO: choose an artificial leaf node in the tree! Remember that whenever you arrive at a nonterminal
        //       who'se action set has yet to be fully visited, you are required to choose that node! If you
        //       encounter a nonterminal in your walk who'se action set has been enumerated you should choose
        //       a child who'se "worth it score" (defined by the Upper-Confidence Bound) is maximized.
       ATPZSQNode node = rootNode;
        while (!node.isTerminal()) {
        // untried move left -> this is the artificial leaf
        if (node.getChildList().size() < node.getState().getNumActions()) {
            return node;
        }
        // otherwise move to the child with the best UCB score
        ATPZSQNode bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Node c : node.getChildList()) {
            ATPZSQNode child = (ATPZSQNode) c;
            Action action = child.getLastAction();
            double q = node.getAverageQValue(action);
            if (node.getPlayerType() != ATPZSNode.PlayerType.MAXIMIZING_PLAYER) {
                q = -q;   // opponent wants LOW values
            }
            double score = q + C * Math.sqrt(Math.log(node.getNumSamples()) / node.getNumSamples(action));
            if (score > bestScore) {
                bestScore = score;
                bestChild = child;
            }
        }
        node = bestChild;
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ATPZSQNode chooseChild(final ATPZSQNode artificialLeafNode) {
        // TODO: choose a child node from the leaf node. Since there is at least one action that has yet to be
        //       explored in order for this node to have been picked as the artificial leaf node than you need
        //       to choose one of the unexplored actions!
        for (Action action : artificialLeafNode.getState().getActions()) {
        if (artificialLeafNode.getNumSamples(action) == 0) {
            try {
                ATPZSQNode child = (ATPZSQNode) artificialLeafNode.getChild(action);
                artificialLeafNode.addChild(child);   // cache it
                return child;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRolloutOutcome(final ATPZSQNode leafNode) {
        // TODO: perform a rollout to sample an outcome from the true leaves under this node and return its utility.
        //       remember that if this node is terminal you can just ask the utility function otherwise you need
        //       to perform a full rollout (by playing the game until completion). See
        //       {@link ATPZSMCTSAgent}'s documentation on this.
        // return Double.POSITIVE_INFINITY;
        if (leafNode.isTerminal()) {
            return leafNode.getState().getTerminalUtility(myPlayerId);
        }
        try {
            GenericGame game = leafNode.getState().getMutableInstance(proxyAgents);
            return game.playUntilCompletion().getTerminalUtility(myPlayerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void backupStatistics(final ATPZSQNode leafNode, double rolloutOutcomeUtility) {
        // TODO: walk back up the path from your new leaf to the root updating statistics for each node in the path
        //       on the way. See {@link ATPZSMCTSAgent}'s documentation on this.
        ATPZSQNode node = leafNode;
        while (node.getParent() != null) {
            ATPZSQNode parent = (ATPZSQNode) node.getParent();
            parent.addSample(node.getLastAction(), rolloutOutcomeUtility);
            node = parent;
        }
    }

    /**
     * This is where your Upper-Confidenct Tree (UCT) MCTS algorithm will go. When called, you should repeatedly
     * perform the four steps of the MCTS algorithm until you run out of resources.
     *
     * @param   rootNode    The root of the game tree
     * @return  A child of the root. {@link Node} instances contain incoming edges as discussed in class, so you want
     *          to return the child of the root that contains the action you want your {@link Agent} to emit.
     */
    @Override
    public ATPZSQNode treeSearch(final ATPZSQNode rootNode) {
        // TODO: as the javadoc string says, perform the four steps of MCTS until you run out of resources.
        //       Make sure you return the best child of the root for you!
        long start = System.currentTimeMillis();
        long limit = (long) (thinkingTimeInMs * 0.9);

        while (System.currentTimeMillis() - start < limit) {
            ATPZSQNode leaf = selectArtificialLeaf(rootNode);
            ATPZSQNode child = leaf;
            if (!leaf.isTerminal()) {
                child = chooseChild(leaf);
            }
            double utility = getRolloutOutcome(child);
            backupStatistics(child, utility);
        }

        // play the most-visited move
        ATPZSQNode bestChild = null;
        int mostVisits = -1;
        for (Node c : rootNode.getChildList()) {
            int visits = rootNode.getNumSamples(c.getLastAction());
            if (visits > mostVisits) {
                mostVisits = visits;
                bestChild = (ATPZSQNode) c;
            }
        }
        return bestChild;
        }
}
