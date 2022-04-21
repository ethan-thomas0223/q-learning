package maze.core;

import search.bestfirst.BestFirstSearcher;

import java.util.function.ToIntFunction;

public class MazeSearcher extends BestFirstSearcher<MazeExplorer> {
    public MazeSearcher(ToIntFunction<MazeExplorer> heuristic) {
        super(heuristic, MazeExplorer::getSuccessors, MazeExplorer::achievesGoal);
    }
}
