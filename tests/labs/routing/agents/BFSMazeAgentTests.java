package tests.labs.routing.agents;


// SYSTEM IMPORTS
import edu.bu.labs.routing.State;
import edu.bu.labs.routing.State.StateView;
import edu.bu.labs.routing.agents.MazeAgent;
import edu.bu.labs.routing.graph.Path;
import edu.bu.labs.routing.maze.Maze;
import edu.bu.labs.routing.maze.Tile;
import edu.bu.labs.routing.utils.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


// JAVA PROJECT IMPORTS
import tests.labs.routing.states.StateRepository;
import src.labs.routing.agents.BFSMazeAgent;


public class BFSMazeAgentTests extends Object {

    @Test
    public void testConstructor() {
        BFSMazeAgent agent = new BFSMazeAgent(0);

        // TODO: check fields?

        assertNotNull(agent);
    }

    @Test
    public void testInitializeFromState5x3VerticalLinearMaze() {
        State state = StateRepository.make5x3VerticalLinearMaze();
        StateView view = state.getView();

        // the state has the agent id inside it so make sure it matches!
        MazeAgent agent = new BFSMazeAgent(state.getStudentAgentId());

        agent.initializeFromState(view);

        // map has 1 friendly unit and 0 enemy units
        assertEquals(state.getStudentAgentId(), agent.getAgentId());
        assertEquals(Tile.State.FINISH, state.getMaze().getTile(agent.getFinishCoordinate()).state());
        assertEquals(0, agent.getEnemyUnitIds().size());
    }

    @Test
    public void testSearch5x3VerticalLinearMaze() {
        State state = StateRepository.make5x3VerticalLinearMaze();
        StateView view = state.getView();

        // if you want to see the maze you can print it!
        // System.out.println(state.getMaze());
        // the START coordinate is (1,1), the FINISH is (3,1) and there is an intermediary coordinate (2,1)

        // the state has the agent id inside it so make sure it matches!
        MazeAgent agent = new BFSMazeAgent(state.getStudentAgentId());

        agent.initializeFromState(view);

        // search for a path from the START to the FINISH coordinate
        Coordinate finishCoord = agent.getFinishCoordinate();
        Coordinate myUnitCoord = view.getUnitView(agent.getAgentId(), agent.getMyUnitId()).currentPosition();

        Path<Coordinate> expectedPath = new Path<Coordinate>(
            new Path<Coordinate>( // the parent path (where the destination came from)
                new Path<Coordinate>(myUnitCoord), // the root (source) of the path
                new Coordinate(2, 1),   // the intermediary coordinate (between the source and the destination)
                1.0
            ),
            finishCoord, // the destination of the path
            1.0
        );

        // check that the paths are the same. Note that we can only do this because there
        // is a *single* shortest-path from the source to the destination. If there are multiple
        // shortest-paths, we cannot guarantee that the vertices are the same but we can guarantee
        // they have the same cost. We can also guarantee that the returned path is valid.
        Path<Coordinate> actualPath = agent.search(myUnitCoord, finishCoord, view);
        assertEquals(expectedPath.numVertices(), actualPath.numVertices());
        assertEquals(expectedPath.trueCost(), actualPath.trueCost());
        while(expectedPath != null) {
            assertEquals(expectedPath.current(), actualPath.current());
            expectedPath = expectedPath.parent();
            actualPath = actualPath.parent();
        }
    }

}

