package src.labs.routing.agents;

// SYSTEM IMPORTS
import edu.bu.labs.routing.utils.Coordinate;
import edu.bu.labs.routing.enums.Direction;
import edu.bu.labs.routing.graph.Path;
import edu.bu.labs.routing.State.StateView;
import edu.bu.labs.routing.maze.Tile;
import edu.bu.labs.routing.agents.MazeAgent;

import java.util.Collection;
import java.util.HashSet;   // will need for dfs
import java.util.Stack;     // will need for dfs
import java.util.Set;       // will need for dfs


// JAVA PROJECT IMPORTS


public class DFSMazeAgent extends MazeAgent {

    public DFSMazeAgent(final int agentId) {
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

        Stack<Path<Coordinate>> stack = new Stack<>();
    Set<Coordinate> visited = new HashSet<>();

    stack.push(new Path<Coordinate>(src));

    while (!stack.isEmpty()) {
        Path<Coordinate> currentPath = stack.pop();
        Coordinate current = currentPath.current();

        // skip if we already explored this tile
        if (visited.contains(current)) continue;
        visited.add(current);

        if (current.equals(goal)) {
            return currentPath;
        }

        for (Direction dir : Direction.values()) {
            Coordinate neighbor = current.getNeighbor(dir);
            if (!stateView.isInBounds(neighbor)) continue;
            if (!stateView.getTileState(neighbor).canPassThrough()) continue;
            if (visited.contains(neighbor)) continue;

            stack.push(new Path<Coordinate>(currentPath, neighbor, 1d));
        }
    }

    return null;
    }
}
