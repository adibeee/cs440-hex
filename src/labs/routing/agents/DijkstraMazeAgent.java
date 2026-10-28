package src.labs.routing.agents;

// SYSTEM IMPORTS
import edu.bu.labs.routing.utils.Coordinate;
import edu.bu.labs.routing.enums.Direction;
import edu.bu.labs.routing.graph.Path;
import edu.bu.labs.routing.State.StateView;
import edu.bu.labs.routing.maze.Tile;
import edu.bu.labs.routing.agents.MazeAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue; // heap in java
import java.util.Set;


// JAVA PROJECT IMPORTS


public class DijkstraMazeAgent extends MazeAgent {
    // enemies see 2 tiles by default, stay at least 3 away
    private static final int DANGER_RADIUS = 3;
    
    public DijkstraMazeAgent(final int agentId) {
        super(agentId);
    }

    // get the positions of all enemy units
    private List<Coordinate> getEnemyPositions(final StateView stateView) {
        List<Coordinate> enemies = new ArrayList<>();
        for (Integer agentId : stateView.getAgentIds()) {
            if (agentId != this.getAgentId()) {
                for (Unit.UnitView unit : stateView.getUnitViews(agentId)) {
                    enemies.add(unit.currentPosition());
                }
            }
        }
        return enemies;
    }

     // true if c is within DANGER_RADIUS of any enemy
    private boolean isDangerous(final Coordinate c, final StateView stateView) {
        for (Coordinate e : getEnemyPositions(stateView)) {
            int rowDist = Math.abs(c.row() - e.row());
            int colDist = Math.abs(c.col() - e.col());
            if (rowDist <= DANGER_RADIUS && colDist <= DANGER_RADIUS) {
                return true;
            }
        }
        return false;
    }


    public double getEdgeWeight(final Coordinate src, final Coordinate dst, final StateView stateView) {
        // TODO: alter me to account for risk!
        if (isDangerous(dst, stateView)) {
            return 1000d; // avoid unless there's no other way
        }
        return 1d;
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
        if (this.getCurrentPlan() == null) {
            return false;
        }
        // replan if any step in the current plan is now near an enemy
        for (Coordinate c : this.getCurrentPlan()) {
            if (isDangerous(c, stateView)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Path<Coordinate> search(final Coordinate src, final Coordinate goal, final StateView stateView) {
        // TODO: complete me!

        // heap that always gives back the cheapest path so far
        PriorityQueue<Path<Coordinate>> heap = new PriorityQueue<>(new Comparator<Path<Coordinate>>() {
            @Override
            public int compare(Path<Coordinate> a, Path<Coordinate> b) {
                return Double.compare(a.trueCost(), b.trueCost());
            }
        });
        Set<Coordinate> visited = new HashSet<>();

        heap.add(new Path<Coordinate>(src));

        while (!heap.isEmpty()) {
            Path<Coordinate> currentPath = heap.poll();
            Coordinate current = currentPath.current();

            // already found a cheaper way here
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

                double weight = getEdgeWeight(current, neighbor, stateView);
                heap.add(new Path<Coordinate>(currentPath, neighbor, weight));
            }
        }

        return null;
    }

}
