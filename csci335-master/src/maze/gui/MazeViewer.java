package maze.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.function.ToIntFunction;

import javax.swing.*;

import core.Direction;
import core.Pos;
import maze.core.Maze;
import maze.core.MazeExplorer;
import maze.core.MazePath;
import core.AIReflector;
import maze.core.MazeSearcher;

@SuppressWarnings("serial")
public class MazeViewer extends JFrame {
    
    private Maze m;
    private MazePanel mp;
    private MazePath path;
    private JButton makeMaze, startPath, checkPath, up, down, left, right, solve;
    private JSlider perfector;
    private JTextField nField, dField, bField, sField;
    private JTextField mazeX, mazeY, treasure;
    private MoveListen mListen = new MoveListen();
    private AIReflector<ToIntFunction<MazeExplorer>> heuristics;
    private JComboBox<String> heuristicChooser;
    private HashMap<JButton, Direction> directionMap;
    
    public MazeViewer() {
        setTitle("Maze Viewer");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        heuristics = new AIReflector<>(ToIntFunction.class, "maze.heuristics");
        heuristicChooser = new JComboBox<>();
        for (String hStr: heuristics.getTypeNames()) {
        	heuristicChooser.addItem(hStr);
        }
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        
        JPanel leftSide = new JPanel(new FlowLayout());
        pane.add(leftSide, BorderLayout.WEST);
        leftSide.add(new JLabel("Perfection"));
        perfector = new JSlider(JSlider.VERTICAL, 0, 100, 100);
        leftSide.add(perfector);
        
        JPanel top = new JPanel(new GridLayout(2, 1));
        pane.add(top, BorderLayout.NORTH);
        
        JPanel controls = new JPanel(new FlowLayout());
        top.add(controls);
        controls.add(new JLabel("# Treasures"));
        treasure = new JTextField(5);
        treasure.setText("0");
        controls.add(treasure);
        makeMaze = new JButton("Make Maze");
        makeMaze.addActionListener(new MakeListen());
        controls.add(makeMaze);
        startPath = new JButton("Start Path");
        startPath.addActionListener(mListen);
        controls.add(startPath);
        
        controls.add(new JLabel("Width"));
        mazeX = new JTextField(4);
        mazeX.setText("10");
        controls.add(mazeX);
        controls.add(new JLabel("Height"));
        mazeY = new JTextField(4);
        mazeY.setText("10");
        controls.add(mazeY);
        
        checkPath = new JButton("Verify Path");
        checkPath.addActionListener(new Validator());
        controls.add(checkPath);
        
        JPanel data = new JPanel(new FlowLayout());
        top.add(data);
        
        data.add(heuristicChooser);
        solve = new JButton("Solve");
        solve.addActionListener(new SolveMaze());
        data.add(solve);
        
        nField = new JTextField(4);
        data.add(new JLabel("Nodes"));
        data.add(nField);
        
        dField = new JTextField(4);
        data.add(new JLabel("depth"));
        data.add(dField);
        
        bField = new JTextField(4);
        data.add(new JLabel("b*"));
        data.add(bField);
        
        sField = new JTextField(4);
        data.add(new JLabel("Solution length"));
        data.add(sField);
        
        JPanel superDir = new JPanel(new GridLayout(5, 1));
        pane.add(superDir, BorderLayout.EAST);
        superDir.add(new JPanel(new FlowLayout()));
        superDir.add(new JPanel(new FlowLayout()));
        
        JPanel directions = new JPanel(new BorderLayout());
        superDir.add(directions);
        up = new JButton("Up");
        up.addActionListener(mListen);
        directions.add(up, BorderLayout.NORTH);
        down = new JButton("Down");
        down.addActionListener(mListen);
        directions.add(down, BorderLayout.SOUTH);
        left = new JButton("Left");
        left.addActionListener(mListen);
        directions.add(left, BorderLayout.WEST);
        right = new JButton("Right");
        right.addActionListener(mListen);
        directions.add(right, BorderLayout.EAST);
        
        mp = new MazePanel();
        pane.add(mp, BorderLayout.CENTER);
        
        directionMap = new HashMap<JButton,Direction>();
        directionMap.put(up, Direction.N);
        directionMap.put(down, Direction.S);
        directionMap.put(left, Direction.W);
        directionMap.put(right, Direction.E);
    }
    
    private class MakeListen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int xSize = Integer.parseInt(mazeX.getText());
            int ySize = Integer.parseInt(mazeY.getText());
            m = new Maze(xSize, ySize);
            double perfection = (double)perfector.getValue()/(double)perfector.getMaximum();
            m.makeMaze(new Pos(m.getXMax(), m.getYMin()),
                       new Pos(m.getXMin(), m.getYMax()),
                       Integer.parseInt(treasure.getText()), perfection);
            mp.setMaze(m);
            mp.repaint();
        }
    }
    
    private class MoveListen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == startPath || path == null) {
            	path = new MazePath(m.getStart().getX(), m.getStart().getY());
            	mp.setPath(path);
            	mp.repaint();
            }

            if (directionMap.containsKey(e.getSource())) {
            	Direction d = directionMap.get(e.getSource());
            	if (!m.blocked(path.getEnd(), d)) {
            		Pos next = d.successor(path.getEnd());
            		if (m.within(next)) {
            			path.append(next);
            			mp.repaint();
            		}
            	}
            }
        }
    }

    private class Validator implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (path == null) {
                JOptionPane.showMessageDialog(null, "No path started");
            } else if (path.solvesMaze(m)) {
                JOptionPane.showMessageDialog(null, "Path solves maze");
            } else {
                JOptionPane.showMessageDialog(null, "Path does not solve maze");
            }
        }
    }
    
    private class SolveMaze implements ActionListener {
        public void actionPerformed(ActionEvent e) {        
            try {
				ToIntFunction<MazeExplorer> bfh = heuristics.newInstanceOf(heuristicChooser.getSelectedItem().toString());
				MazeSearcher searcher = new MazeSearcher(bfh);
				searcher.solve(new MazeExplorer(m, m.getStart()));
				if (searcher.success()) {
					path = new MazePath(searcher.getResult().get(), m);
					mp.setPath(path);
					mp.repaint();
					displayStats(searcher);
				} else {
					JOptionPane.showMessageDialog(MazeViewer.this, "Sorry, no solution found.");
				}
				
            } catch (Exception e1) {
                e1.printStackTrace();
				JOptionPane.showMessageDialog(MazeViewer.this, e1.getClass().getName() + ": " + e1.getMessage());
			}
        }
    }
    
    private void displayStats(MazeSearcher searcher) {
    	nField.setText(Integer.toString(searcher.getNumNodes()));
        dField.setText(Integer.toString(searcher.getMaxDepth()));
        double b = searcher.getBranchingFactor(0.01);
        b = (double)((int)(b * 100)) / 100;
        bField.setText(Double.toString(b));
        sField.setText(Integer.toString(searcher.getSolutionLength()));
    }
    
    public static void main(String[] args) {
        MazeViewer gui = new MazeViewer();
        gui.setVisible(true);
    }
}
