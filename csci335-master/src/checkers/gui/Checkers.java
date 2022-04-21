package checkers.gui;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.AIReflector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.function.ToIntFunction;

public class Checkers extends JFrame {
    
    private JButton startGame, evaluate, evalSearch, computerMove;
    private JComboBox<String> evalFuncs, searchers;
    private CheckersPanel board;
    private JLabel message, value;
    private ToIntFunction<Checkerboard> evaluation;
    private CheckersSearcher searchAlg;
    private JTextField depthField, nodeField, timeField;
    private AIReflector<ToIntFunction<Checkerboard>> evalFuncClasses;
    private AIReflector<CheckersSearcher> searcherClasses;
    
    private static boolean debug = false;
    
    public Checkers() {
        setTitle("Checkers");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        
        board = new CheckersPanel();
        board.addMouseListener(new CheckerListener());
        pane.add(board, BorderLayout.CENTER);
        
        JPanel top = new JPanel(new GridLayout(4,1));
        pane.add(top, BorderLayout.NORTH);
        
        JPanel buttons = new JPanel(new FlowLayout());
        top.add(buttons);
        
        startGame = new JButton("Start new game");
        startGame.addActionListener(new GameStarter());
        buttons.add(startGame);
        
        computerMove = new JButton("Computer move");
        computerMove.addActionListener(new ComputerMover());
        buttons.add(computerMove);
        
        message = new JLabel();
        buttons.add(message);
        
        JPanel heuristics = new JPanel(new FlowLayout());
        top.add(heuristics);
        
        heuristics.add(new JLabel("Evaluation function"));
    	evalFuncClasses = new AIReflector<>(ToIntFunction.class, "checkers.evaluators");
        evalFuncs = new JComboBox<>();
        for (String evalFuncStr: evalFuncClasses.getTypeNames()) {
        	evalFuncs.addItem(evalFuncStr);
        }
        evalFuncs.setSelectedIndex(0);
        evalFuncs.addActionListener(new Loader());
        heuristics.add(evalFuncs);
        
        evaluate = new JButton("Evaluate board");
        evaluate.addActionListener(new Evaluator());
        heuristics.add(evaluate);
        
        value = new JLabel();
        heuristics.add(value);
        
        JPanel heuristics2 = new JPanel(new FlowLayout());
        top.add(heuristics2);
        
        heuristics2.add(new JLabel("Search Algorithm"));
    	searcherClasses = new AIReflector<>(CheckersSearcher.class, "checkers.searchers", ToIntFunction.class);
        searchers = new JComboBox<>();
        for (String searcherStr: searcherClasses.getTypeNames()) {
        	searchers.addItem(searcherStr);
        }
        searchers.setSelectedIndex(0);
        searchers.addActionListener(new Loader());
        heuristics2.add(searchers);
        
        heuristics2.add(new JLabel("Depth"));
        depthField = new JTextField(3);
        depthField.addActionListener(new Loader());
        heuristics2.add(depthField);
        
        evalSearch = new JButton("Evaluate with search");
        evalSearch.addActionListener(new Evaluator());
        heuristics2.add(evalSearch);
        
        JPanel nodeInfo = new JPanel(new FlowLayout());
        nodeInfo.add(new JLabel("Nodes expanded"));
        nodeField = new JTextField(10);
        nodeInfo.add(nodeField);
        nodeInfo.add(new JLabel("Time"));
        timeField = new JTextField(10);
        nodeInfo.add(timeField);
        top.add(nodeInfo);
        
        restart();
    }
    
    private void makeMove(Move m) {
        if (m != null && !board.getBoard().gameOver()) {
            System.out.println(m);
            board.getBoard().move(m);
            board.repaint();
            changeTurn();
        }
    }
    
    private void loadAlgorithms() {
        searchAlg = CheckersSearcher.makeSearcher((String)searchers.getSelectedItem(), (String)evalFuncs.getSelectedItem(), evalFuncClasses, searcherClasses);
        if (searchAlg == null) {
            JOptionPane.showMessageDialog(null, "Could not load heuristics");
        } else {
            evaluation = searchAlg.getEvaluator();
            resetDepth();
        }
    }
    
    private void resetDepth() {
        try {
        	nodeField.setText("");
        	timeField.setText("");
            int limit = Integer.parseInt(depthField.getText());
            searchAlg.setDepthLimit(limit);
        } catch (NumberFormatException nfe) {
        } finally {
            depthField.setText(Integer.toString(searchAlg.getDepthLimit()));
        }    	
    }
    
    private void restart() {
        System.out.println("restarting...");
        board.getBoard().newGame();
        board.repaint();
       	changeTurn();
        loadAlgorithms();
    }
    
    private class Loader implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadAlgorithms();
        }
    }
    
    private class GameStarter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            restart();
        }
    }
    
    private class Evaluator implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == evaluate) {
                showBoardValue(evaluation.applyAsInt(board.getBoard()));
            } else if (e.getSource() == evalSearch) {
                searchAlg.selectMove(board.getBoard())
                        .ifPresentOrElse(chosen -> showBoardValue(chosen.getFirst()),
                                () -> value.setText("Game over"));
            }
        }
    }

    private void showBoardValue(double v) {
        value.setText("Value: " + ((double)(int)(v * 1000)/1000));
    }
    
    private class ComputerMover implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resetDepth();
            long start = System.currentTimeMillis();
            Checkerboard copy = board.getBoard().duplicate();
            Set<Move> searchMoves = board.getBoard().getCurrentPlayerMoves();
            searchAlg.selectMove(board.getBoard()).ifPresent(chosen -> {
                Move choice = chosen.getSecond();
                assert board.getBoard().equals(copy);
                long duration = System.currentTimeMillis() - start;
                double seconds = duration / 1000.0;
                Set<Move> currentMoves = board.getBoard().getCurrentPlayerMoves();
                assert currentMoves.equals(searchMoves);
                if (currentMoves.contains(choice)) {
                    makeMove(choice);
                    nodeField.setText(Integer.toString(searchAlg.numNodesExpanded()));
                    timeField.setText(String.format("%4.2f s", seconds));
                } else {
                    System.out.println("Moves: " + board.getBoard().getCurrentPlayerMoves());
                    System.out.println("Requested illegal move: " + choice);
                    JOptionPane.showMessageDialog(null, "AI requested an illegal move: " + choice);
                }
            });
        }
    }
    
    private class CheckerListener extends MouseAdapter {
        private int colStart, rowStart;
        private boolean awaitingRelease;
        
        public void mousePressed(MouseEvent e) {
            colStart = board.getCol(e.getX());
            rowStart = board.getRow(e.getY());
            awaitingRelease = board.getBoard().pieceAt(rowStart, colStart).isPresent();
            if (!awaitingRelease) {
                JOptionPane.showMessageDialog(null, "Illegal move");
                if (debug) {
                    System.out.println("No piece is at row " + rowStart + 
                    " and column " + colStart);
                }
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            if (awaitingRelease) {
                Set<Move> legal = board.getBoard().getCurrentPlayerMoves();
                Move mouseMove = new Move(board.getBoard(), rowStart, colStart,
                board.getRow(e.getY()),
                board.getCol(e.getX()));
                
                if (legal.contains(mouseMove)) {
                    makeMove(mouseMove);
                } else {
                    JOptionPane.showMessageDialog(null, "Illegal move");
                }
            } 
        }
    }

    private void changeTurn () {
        if (board.getBoard().playerWins(PlayerColor.RED)) {
            message.setText ("Red wins!");
        } else if (board.getBoard().playerWins(PlayerColor.BLACK)) {
            message.setText ("Black wins!");
        } else if (board.getBoard().isTurnFor(PlayerColor.BLACK)) {
            message.setText ("Black's turn");
        } else {
            message.setText ("Red's turn");
        }
    }
    
    public static void main(String[] args) {
        Checkers c = new Checkers();
        c.setVisible(true);
    }
}
