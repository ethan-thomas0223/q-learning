package maze.core;

import core.Direction;
import core.Pos;

import java.util.*;
import java.util.stream.Collectors;

public class Maze {
    private int xSize, ySize;
    private Pos start, end;
    
    private EnumSet<Direction>[][] barriers;
    private Pos[][] cells;
    private Set<Pos> treasures;
    
    @SuppressWarnings("unchecked")
    public Maze(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        start = end = null;
        cells = new Pos[xSize][ySize];
        barriers = new EnumSet[xSize][ySize];
        for (int x = 0; x < xSize; ++x) {
            for (int y = 0; y < ySize; ++y) {
                cells[x][y] = new Pos(x, y);
                barriers[x][y] = EnumSet.allOf(Direction.class);
            }
        }
        
        treasures = new LinkedHashSet<>();
    }

    public MazeExplorer getGoal() {
        MazeExplorer endNode = new MazeExplorer(this, this.getEnd());
        endNode.addTreasures(this.getTreasures());
        return endNode;
    }
    
    // Pre: 0 <= perfection <= 1.0
    // Post: Randomly generates a maze of the given size, starting at start
    //       and ending at end; if perfection = 1, the maze is perfect; if
    //       perfection = 0, the maze has very few walls
    public void makeMaze(Pos start, Pos end, int numTreasures, double perfection) {
        this.start = start;
        this.end = end;
        
        ArrayList<Pos> openList = new ArrayList<>();
        Map<Pos,Pos> predecessors = new HashMap<>();
        Set<Pos> visited = new LinkedHashSet<>();
        openList.add(end);
        while (openList.size() > 0) {
            Pos current = openList.remove(openList.size() - 1);
            if (!visited.contains(current)) {
                visited.add(current);
                if (predecessors.containsKey(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
                ArrayList<Pos> neighbors = getNeighbors(current);
                Collections.shuffle(neighbors);
                for (Pos neighbor: neighbors) {
                    openList.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            } else if (Math.random() > perfection) {
                if (predecessors.keySet().contains(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
            }
        }
        
        if (visited.size() != xSize * ySize) {
        	throw new IllegalStateException("Some cells weren't visited");
        }
        
        addTreasure(numTreasures);
    }

    private void addTreasure(int numTreasures) {
        treasures = new LinkedHashSet<>();
        int numUntried = xSize * ySize - 2;
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                Pos candidate = new Pos(i, j);
                if (!candidate.equals(getStart()) && !candidate.equals(getEnd())) {
                    double prob = (double)numTreasures / (double)numUntried;
                    if (Math.random() < prob) {
                        treasures.add(candidate);
                        numTreasures--;
                    }
                    numUntried--;
                }
            }
        }
    }
    
    public Pos getStart() {return start;}
    public Pos getEnd() {return end;}
    
    public boolean within(Pos mc) {
    	return mc.getX() >= getXMin() && mc.getX() <= getXMax() && mc.getY() >= getYMin() && mc.getY() <= getYMax();
    }
    public boolean isStart(Pos mc) {return start.equals(mc);}
    public boolean isEnd(Pos mc) {return end.equals(mc);}
    public boolean isTreasure(Pos mc) {return treasures.contains(mc);}
    public boolean isTreasure(int x, int y) {return isTreasure(new Pos(x, y));}
    
    public Set<Pos> getTreasures() {return Collections.unmodifiableSet(treasures);}
    
    public int getXMin() {return 0;}
    public int getYMin() {return 0;}
    public int getXMax() {return xSize - 1;}
    public int getYMax() {return ySize - 1;}
    public int getXSize() {return xSize;}
    public int getYSize() {return ySize;}
    
    // Pre: c.isNeighbor(n)
    // Post: Returns true if it is not possible to travel from c to n in one
    //       step; returns false otherwise
    public boolean blocked(Pos c, Pos n) {
        if (!c.isNeighbor(n)) {
            throw new IllegalArgumentException(c + " is not a neighbor to " + n);
        }
        
        return barriers[c.getX()][c.getY()].contains(Direction.between(c, n));
    }
    
    public boolean blocked(Pos c, Direction d) {
    	return blocked(c.getX(), c.getY(), d);
    }
    
    public boolean blocked(int x, int y, Direction d) {
    	return barriers[x][y].contains(d);
    }
    
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	for (int row = 0; row < ySize; ++row) {
    		for (int col = 0; col < xSize; ++col) {
    			result.append('#');
    			appendSpot(result, blocked(col, row, Direction.N));
    		}
    		result.append("#\n");
    		
    		for (int col = 0; col < xSize; ++col) {
    			appendSpot(result, blocked(col, row, Direction.W));
    			result.append(' ');
    		}
    		appendSpot(result, blocked(xSize - 1, row, Direction.E));
    		result.append('\n');
    	}
    	
    	for (int col = 0; col < xSize; ++col) {
    		result.append('#');
    		appendSpot(result, blocked(col, ySize - 1, Direction.S));
    	}
    	result.append("#\n");
    	return result.toString();
    }
    
    private void appendSpot(StringBuilder result, boolean wall) {
    	result.append(wall ? '#' : ' ');
    }
    
    // Pre: first and second are Manhattan neighbors
    // Post: Knocks down a wall between them, if it exists
    private void knockDownBetween(Pos first, Pos second) {
    	barriers[first.getX()][first.getY()].remove(Direction.between(first, second));
    	barriers[second.getX()][second.getY()].remove(Direction.between(second, first));
    	
    	if (blocked(first, second) || blocked(second, first)) {
    		throw new IllegalStateException("knock down did not work");
    	}
    }
    
    // Pre: none
    // Post: Returns all legal neighbors of current in an arbitrary
    //       ordering, disregarding walls completely.
    public ArrayList<Pos> getNeighbors(Pos current) {
        return current.getNeighbors().stream().filter(this::within).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Maze xSize ySize");
            System.exit(1);
        }
        
        int xSize = Integer.parseInt(args[0]);
        int ySize = Integer.parseInt(args[1]);
        Maze m = new Maze(xSize, ySize);
        System.out.println("Before knockdown");
        System.out.println(m);
        
        m.makeMaze(new Pos(m.getXMin(), m.getYMin()),
                   new Pos(m.getXMax(), m.getYMax()), 0, 1);
        System.out.println("Maze-ified");
        System.out.println(m);
    }
}
