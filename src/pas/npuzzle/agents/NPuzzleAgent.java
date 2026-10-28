package src.pas.npuzzle.agents;


// SYSTEM IMPORTS
import edu.bu.pas.npuzzle.Action;
import edu.bu.pas.npuzzle.Board;
import edu.bu.pas.npuzzle.BoardView;
import edu.bu.pas.npuzzle.Game;
import edu.bu.pas.npuzzle.GameView;
import edu.bu.pas.npuzzle.agents.Agent;
import edu.bu.pas.npuzzle.enums.Direction;
import edu.bu.pas.npuzzle.graph.Edge;
import edu.bu.pas.npuzzle.graph.Path;
import edu.bu.pas.npuzzle.graph.Vertex;
import edu.bu.pas.npuzzle.planners.Heuristic;
import edu.bu.pas.npuzzle.planners.Planner;
import edu.bu.pas.npuzzle.utils.Coordinate;

import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;


// JAVA PROJECT IMPORTS
import src.pas.npuzzle.planners.AStarPlanner;  // responsible for solving the game


/**
 * Represents an {@link Agent} that solves the N-puzzle game via the A* algorithm.
 *
 * <p>This agent contains an instance of a {@link AStarPlanner} which is what actually calculates a shortest path
 * of vertices that take the original state of the game to one of the goal states (there may be more than one).
 * This agent is responsible for using the {@link AStarPlanner} to construct the plan, converting that path of vertices
 * into an actionable plan of {@link Action} objects and then executing that plan by returning the specific
 * {@link Action} per turn of the game.
 *
 * <p>The (really, ungodly) slow way for your agent to function would be to invoke the A* algorithm *every time* the
 * 'getAction' method is called. This way is so slow that you will time out any autograder I will make. The faster way
 * (which requires more logic) is for you to create the plan *once* and then cache it. Every time 'getAction' is called
 * by the game loop, you can do some sanity checking that your plan is still valid to follow, and then if it is, carve
 * off the next action from it and return it to the game.
 *
 * <p>I will (probably) call your 'makePlan' method myself in my infrastructure but do not rely on me doing so. When
 * your 'getAction' method is called by the game loop (which it will be), if you do *not* have a plan then make one
 * and cache it! If you already do, then do some sanity checking, chop off the next action to do from your plan,
 * and return it!
 *
 */
public class NPuzzleAgent implements Agent {

    /**
     * Represents a {@link Heuristic} function specifically tailored to the N-puzzle.
     */
    public static class NPuzzleHeuristic implements Heuristic {

        /**
         * Returns an admissible and consistent guess of the shortest path cost from v to dst.
         *
         * @param v     The {@link Vertex} to get the shortest path cost guess of
         * @param dst   The destination of the shortest path this {@link Heuristic} is guessing the cost of.
         * @return An admissible and consistent guess.
         */
        @Override
        public double getValue(final Vertex v, final Vertex dst) {
            double value = 0.0;
            // TODO: come up with a heuristic value! Right now this is just Dijkstra!
            return value;
        }
    }

    private final Random    random;
    private final Planner   planner;
    private Queue<Action>   plan;

    public NPuzzleAgent() {
        this.random = new Random();
        this.planner = new AStarPlanner(new NPuzzleHeuristic());
        this.plan = new LinkedList<>();
    }

    public final Random getRandom() { return this.random; }
    public final Planner getPlanner() { return this.planner; }

    // don't *ever* let this be <code>null</code>
    public final Queue<Action> getPlan() { return this.plan; }

    // the argument to this should *never* be <code>null</code>
    protected void setPlan(Queue<Action> q) { this.plan = q; }

    // requires that 'getPlan' never returns <code>null</code>
    @Override
    public boolean hasCachedPlan() {
        return this.getPlan().size() > 0;
    }

    // don't *ever* let this return <code>null</code>
    @Override
    public Queue<Action> makePlan(final GameView game) {
        // Build all of the destination vertices
        Set<Vertex> dsts = new HashSet<>();
        for(BoardView board : game.getDesiredConfigs()) {
            Game mutableGame = new Game(this.getRandom(), new Board(board), this, game.getDesiredConfigs());
            dsts.add(new GameView(mutableGame)); // don't need to mutate so make immutable
        }

        Path<Vertex> vertexPlan = this.getPlanner().plan(game, dsts);
        Queue<Action> actionPlan = new LinkedList<>();
        for(Edge e : this.getPlanner().toEdges(vertexPlan)) {
            actionPlan.add((Action)e); // the returned Edge should be an Action!
        }
        this.setPlan(actionPlan); // cache it so we can use it when getAction() gets called!

        return actionPlan;
    }

    @Override
    public Action getAction(final GameView game) {
        /**
         * TODO: if you already have a plan cached, the action at the beginning of the plan should be
         *       a sane action to make in that game (e.g., if we look at the coordinate + direction in the action
         *       there should be a non-empty tile at that coord and the empty tile should be at the coordinate
         *       we would get if we went in the specified direction from the non-empty coordinate).
         *
         *       If this is a sane move to do then do it! If not something likely went wrong and you should replan??
         */
        return null;
    }
}
