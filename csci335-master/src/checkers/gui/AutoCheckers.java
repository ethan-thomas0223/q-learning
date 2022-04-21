package checkers.gui;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.PlayerColor;
import core.AIReflector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.ToIntFunction;

public class AutoCheckers extends JFrame {
    
    private JButton evaluate, save;
    private JComboBox<String> evalBox1, evalBox2, searcherBox1, searcherBox2;
    private JTextField depthField1, depthField2, numGamesField, maxMovesField;
    private JTextArea results;
    private JFileChooser chooser = new JFileChooser();
    private AIReflector<ToIntFunction<Checkerboard>> evalFuncClasses;
    private AIReflector<CheckersSearcher> searcherClasses;
    
    public AutoCheckers() {
        setTitle("AutoCheckers");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        
        results = new JTextArea(5, 50);
        results.setFont(new Font("Courier", Font.PLAIN, 15));
        pane.add(new JScrollPane(results), BorderLayout.CENTER);
        
        JPanel top = new JPanel(new GridLayout(3,1));
        pane.add(top, BorderLayout.NORTH);
        
        JPanel buttons = new JPanel(new FlowLayout());
        top.add(buttons);
        
        evaluate = new JButton("Run tournament");
        evaluate.addActionListener(new Runner());
        buttons.add(evaluate);
        
        buttons.add(new JLabel("Number of games"));
        numGamesField = new JTextField(4);
        numGamesField.setText("32");
        buttons.add(numGamesField);
        
        buttons.add(new JLabel("Maximum moves/game"));
        maxMovesField = new JTextField(4);
        maxMovesField.setText("100");
        buttons.add(maxMovesField);
        
        save = new JButton("Save results");
        save.addActionListener(new Saver());
        buttons.add(save);
        
        JPanel player1 = new JPanel(new FlowLayout());
        top.add(player1);

        player1.add(new JLabel("Player1:"));
        
        player1.add(new JLabel("Evaluation function"));
        evalBox1 = new JComboBox<>();
        player1.add(evalBox1);
        
        player1.add(new JLabel("Search Algorithm"));
        searcherBox1 = new JComboBox<>();
        player1.add(searcherBox1);

        player1.add(new JLabel("Depth Limit"));
        depthField1 = new JTextField(3);
        depthField1.setText("4");
        player1.add(depthField1);
        
        JPanel player2 = new JPanel(new FlowLayout());
        top.add(player2);
        
        player2.add(new JLabel("Player2:"));        

        player2.add(new JLabel("Evaluation function"));
    	evalFuncClasses = new AIReflector<>(ToIntFunction.class, "checkers.evaluators");
        evalBox2 = new JComboBox<>();
        for (String evalFuncStr: evalFuncClasses.getTypeNames()) {
        	evalBox1.addItem(evalFuncStr);
        	evalBox2.addItem(evalFuncStr);
        }
        player2.add(evalBox2);
        evalBox1.setSelectedIndex(0);
        evalBox2.setSelectedIndex(0);
        
        player2.add(new JLabel("Search Algorithm"));
    	searcherClasses = new AIReflector<>(CheckersSearcher.class, "checkers.searchers", ToIntFunction.class);
        searcherBox2 = new JComboBox<>();
        for (String searcherStr: searcherClasses.getTypeNames()) {
        	searcherBox1.addItem(searcherStr);
        	searcherBox2.addItem(searcherStr);
        }
        player2.add(searcherBox2);
        searcherBox1.setSelectedIndex(0);
        searcherBox2.setSelectedIndex(0);
        
        player2.add(new JLabel("Depth Limit"));
        depthField2 = new JTextField(3);
        depthField2.setText("4");
        player2.add(depthField2);
    }
    
    private class RunThread extends Thread {
        
    	private long player1Nodes, player2Nodes;
        private int player1Wins, player2Wins, totalGames, 
                    totalTurns, maxTurns, numGames;
        private CheckersSearcher player1, player2;
        
        public RunThread() {
            player1Nodes = player2Nodes = 0;
            player1Wins = player2Wins = totalGames = totalTurns = 0;
            player1 = loadAlgorithms((String)searcherBox1.getSelectedItem(), (String)evalBox1.getSelectedItem(), depthField1);
            player2 = loadAlgorithms((String)searcherBox2.getSelectedItem(), (String)evalBox2.getSelectedItem(), depthField2);
            maxTurns = Integer.parseInt(maxMovesField.getText());
            numGames = Integer.parseInt(numGamesField.getText());
        }
        
        public void run() {
            ArrayList<Checkerboard> startingBoards = makeStartingBoards((numGames+1)/2);
            for (Checkerboard start: startingBoards) {
                Checkerboard dup = start.duplicate();
                runGame(dup, PlayerColor.BLACK);
                showFinalResults();
                runGame(start, PlayerColor.RED);
                showFinalResults();
            }
            results.append("Tournament complete\n");
        }

        private void runGame(Checkerboard board, PlayerColor player1Color) {
            int turn = 0;
            while (!board.gameOver() && turn < maxTurns) {
                if (board.isTurnFor(player1Color)) {
                    board.move(player1.selectMove(board).get().getSecond());
                    player1Nodes += player1.numNodesExpanded();
                } else {
                    board.move(player2.selectMove(board).get().getSecond());
                    player2Nodes += player2.numNodesExpanded();
                }
                ++turn;
                results.append(".");
            }
            
            totalTurns += turn;
            
            ++totalGames;
            if (board.playerWins(player1Color)) {
                ++player1Wins;
            } else if (board.playerWins(player1Color.opponent())) {
                ++player2Wins;
            }
        }
        
        // Pre: n >= 0
        // Post: Returns the first n boards generated by systematically producing
        //       every possible opening
        private ArrayList<Checkerboard> makeStartingBoards(int n) {
            ArrayList<Checkerboard> result = new ArrayList<Checkerboard>(n);
            result.add(new Checkerboard());
            int parent = 0;
            while (result.size() < n) {
                result.addAll(result.get(parent).getNextBoards());
                ++parent;
            }
            while (result.size() > n) {
                result.remove(result.size() - 1);
            }
            return result;
        }
        
        private void showFinalResults() {
            results.setText("");
            double player1NodesPerTurn = (double)(player1Nodes*100 / totalTurns)/100;
            double player2NodesPerTurn = (double)(player2Nodes*100 / totalTurns)/100;
            
            results.append("Total games played: " + totalGames + "\n");
            results.append("Wins: Player1: " + player1Wins + " Player2: " + player2Wins + "\n");
            results.append("Nodes/turn: Player1: " + player1NodesPerTurn + " Player2: " + player2NodesPerTurn + "\n\n");
        }
    }
    
    private class Runner implements ActionListener {
        private RunThread rt = null;
        public void actionPerformed(ActionEvent e) {
            if (rt != null && rt.isAlive()) {
                JOptionPane.showMessageDialog(null, "Previous tournament is still running");
            } else {
                rt = new RunThread();
                rt.start();
            }
        }
    }
    
    private CheckersSearcher loadAlgorithms(String searchStr, String evalStr, JTextField depthField) {
        CheckersSearcher searchAlg = CheckersSearcher.makeSearcher(searchStr, evalStr, evalFuncClasses, searcherClasses);
        if (searchAlg == null) {
            JOptionPane.showMessageDialog(null, "Could not load heuristics");
            return null;
        } else {
            try {
                int limit = Integer.parseInt(depthField.getText());
                searchAlg.setDepthLimit(limit);
            } catch (NumberFormatException nfe) {
            } finally {
                depthField.setText(Integer.toString(searchAlg.getDepthLimit()));
            }
        }
        return searchAlg;
    }
    
    private class Saver implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int option = chooser.showSaveDialog(null);
            if (option != JFileChooser.APPROVE_OPTION) {return;}
            File f = chooser.getSelectedFile();
            if (f.exists()) {
                int choice = JOptionPane.showConfirmDialog(null, "File exists; overwrite?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.NO_OPTION) {return;}
            }
            
            try {
                PrintWriter out = new PrintWriter(new FileWriter(f));
                out.print(results.getText());
                out.close();
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(null, "Trouble saving");
            }
        }
    }
    
    public static void main(String[] args) {
        AutoCheckers c = new AutoCheckers();
        c.setVisible(true);
    }
}
