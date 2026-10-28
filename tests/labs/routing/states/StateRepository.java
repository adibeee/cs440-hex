package tests.labs.routing.states;

// SYSTEM IMPORTS
import edu.bu.labs.routing.enums.Direction;
import edu.bu.labs.routing.maze.Maze;
import edu.bu.labs.routing.maze.Tile;
import edu.bu.labs.routing.utils.Coordinate;
import edu.bu.labs.routing.State;
import edu.bu.labs.routing.State.StateView;
import edu.bu.labs.routing.Unit;

import java.util.Random;


// JAVA PROJECT IMPORTS


public class StateRepository extends Object {

    public static State make5x3VerticalLinearMaze() {
        Tile.State[][] tiles = new Tile.State[][] {
                { Tile.State.WALL, Tile.State.WALL, Tile.State.WALL },
                { Tile.State.WALL, Tile.State.START, Tile.State.WALL },
                { Tile.State.WALL, Tile.State.EMPTY, Tile.State.WALL },
                { Tile.State.WALL, Tile.State.FINISH, Tile.State.WALL },
                { Tile.State.WALL, Tile.State.WALL, Tile.State.WALL },
        };
        Maze maze = new Maze(5, 3, tiles);
        Tile startTile = maze.findFirstTile(Tile.State.START);

        final int agentId = 0;

        State state = new State(maze, new Random(), agentId);
        state.addUnit(agentId, new Unit(0, 100, 0, 0, startTile));
        return state;
    }

}
