package src.pas.npuzzle.planners;


// SYSTEM IMPORTS
import edu.bu.pas.npuzzle.graph.Edge;
import edu.bu.pas.npuzzle.graph.Path;
import edu.bu.pas.npuzzle.graph.Vertex;
import edu.bu.pas.npuzzle.planners.Heuristic;
import edu.bu.pas.npuzzle.planners.Planner;

import java.util.Collection;
import java.util.Queue;


// JAVA PROJECT IMPORTS


public class AStarPlanner implements Planner {

    // object that represents the heuristic function used by this AStarPlanner
    private final Heuristic heuristic;

    public AStarPlanner(final Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public Heuristic getHeuristic() { return this.heuristic; }

    public Path<Vertex> plan(final Vertex src, final Collection<Vertex> dsts) {
        // TODO: use the A* algorithm to find the shortest path from 'src' to the closest vertex in 'dsts'.
        //       Specifically (see the documentation for Planner::plan for more details), if there are n vertices
        //       in 'dsts' then the path your return from this method should be the smallest of the n shortest paths
        //       (one shortest path from 'src' per vertex in 'dsts').
        return null;
    }

    public Queue<Edge> toEdges(final Path<Vertex> path) {
        // TODO: convert the Path of vertices into an executable queue of actions.
        //       Specifically (see the documentation for Planner::toEdges and the Path datatype), the queue of actions
        //       you return should, if executed in the order defined by the queue (e.g. iterating over the queue)
        //       take one from the source of the path to the destination of that path.
        return null;
    }

}

