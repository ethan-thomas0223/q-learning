package maze.core;
import core.Pos;
import search.SearchNode;
import search.bestfirst.BestFirstSearcher;

import java.util.*;

public class MazePath {
    private ArrayList<Pos> path;
    private Set<Pos> included;
    
    public MazePath(int xStart, int yStart) {
        path = new ArrayList<>();
        included = new HashSet<>();
        append(new Pos(xStart, yStart));
    }
    
    public MazePath(SearchNode<MazeExplorer> searchResult, Maze m) {
    	this(m.getStart().getX(), m.getStart().getY());
        for (MazeExplorer me: searchResult.searchPath()) {
            append(me.getLocation());
        }
    }
    
    public Pos getStart() {return path.get(0);}
    public Pos getEnd() {return path.get(path.size() - 1);}
    public int getLength() {return path.size();}

    // Pre: 0 <= n < getLength()
    // Post: Returns nth point in the path
    public Pos getNth(int n) {return path.get(n);}
    
    public boolean hasVisited(Pos p) {
        return included.contains(p);
    }
    
    public void append(Pos next) {
        if (path.isEmpty() || next.isNeighbor(getEnd())) {
            path.add(next);
            included.add(next);
        }
    }
    
    public boolean solvesMaze(Maze target) {
        if (!getStart().equals(target.getStart()) || !getEnd().equals(target.getEnd())) {
            return false;
        }
        
        Set<Pos> treasureFound = new HashSet<>();
        for (int f = 0; f < path.size() - 1; ++f) {
            Pos c = path.get(f);
            Pos n = path.get(f+1);
            if (!c.isNeighbor(n) || target.blocked(c, n)) {
                return false;
            }
            if (target.isTreasure(c)) {treasureFound.add(c);}
        }
        
        for (Pos trs: target.getTreasures()) {
            if (!treasureFound.contains(trs)) {
                return false;
            }
        }
        
        return true;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Pos p: path) {
            s.append(p);
        }
        return s.toString();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof MazePath other) {
            if (getLength() != other.getLength()) {
                return false;
            }
            for (int i = 0; i < getLength(); ++i) {
                if (!other.path.get(i).equals(path.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public int hashCode() {return toString().hashCode();}
}
