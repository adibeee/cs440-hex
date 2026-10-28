package src.labs.routing.agents;



// SYSTEM IMPORTS
import edu.bu.labs.routing.utils.Coordinate;
import edu.bu.labs.routing.enums.Direction;
import edu.bu.labs.routing.graph.Path;
import edu.bu.labs.routing.State.StateView;
import edu.bu.labs.routing.maze.Tile;
import edu.bu.labs.routing.agents.MazeAgent;


import java.util.Collection;
import java.util.HashSet;       // will need for bfs
import java.util.Queue;         // will need for bfs
import java.util.LinkedList;    // will need for bfs
import java.util.Set;


// JAVA PROJECT IMPORTS


public class BFSMazeAgent extends MazeAgent {

    public BFSMazeAgent(final int agentId) {
        super(agentId);
    }

    @Override
    public void initializeFromState(final StateView stateView) {
        // find the FINISH tile
        Coordinate finishCoord = null;
        for(int rowIdx = 0; rowIdx < stateView.getNumRows(); ++rowIdx) {
            for(int colIdx = 0; colIdx < stateView.getNumCols(); ++colIdx) {
                if(stateView.getTileState(new Coordinate(rowIdx, colIdx)) == Tile.State.FINISH) {
                    finishCoord = new Coordinate(rowIdx, colIdx);
                }
            }
        }
        this.setFinishCoordinate(finishCoord);

        // make sure to call the super-class' version!
        super.initializeFromState(stateView);
    }

    @Override
    public boolean shouldReplacePlan(final StateView stateView) {
        return false;
    }

    @Override
    public Path<Coordinate> search(final Coordinate src, final Coordinate goal, final StateView stateView) {
        // TODO: complete me!
        Queue<Path<Coordinate>> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        // start with a path containing just the src
        queue.add(new Path<Coordinate>(src));
        visited.add(src);

        while (!queue.isEmpty()) {
            Path<Coordinate> currentPath = queue.poll();
            Coordinate current = currentPath.current();

            // found the goal, return the path that got us here
            if (current.equals(goal)) {
                return currentPath;
            }

            // try each of the 4 cardinal directions
            for (Direction dir : Direction.values()) {
                Coordinate neighbor = current.getNeighbor(dir);
                // skip if off the map, a wall, or already seen
                if (!stateView.isInBounds(neighbor)) continue;
                if (!stateView.getTileState(neighbor).canPassThrough()) continue;
                if (visited.contains(neighbor)) continue;

                visited.add(neighbor);
                queue.add(new Path<Coordinate>(currentPath, neighbor, 1d));
            }
        }

        // no path exists
        return null;
    }

}
