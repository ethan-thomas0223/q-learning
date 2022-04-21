package learning.markov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LanguageGuesser extends JFrame {
    private JFileChooser chooser = new JFileChooser();
    private MarkovLanguage chain = new MarkovLanguage();
    private JTextField entry;
    private JTextArea output;
    private JTextField winner;

    public static void main(String[] args) {
        LanguageGuesser gui = new LanguageGuesser();
        gui.setVisible(true);
    }

    public LanguageGuesser() {
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton train = new JButton("Train");
        add(train, BorderLayout.WEST);

        output = new JTextArea(20, 8);
        output.setEditable(false);

        entry = new JTextField(20);
        entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                refreshOutput();
            }
        });
        add(entry, BorderLayout.NORTH);
        add(output, BorderLayout.CENTER);

        winner = new JTextField(10);
        winner.setEditable(false);
        add(winner, BorderLayout.SOUTH);

        train.addActionListener(action -> {
            int choice = chooser.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    String language = JOptionPane.showInputDialog("Enter language");
                    chain.countFrom(f, language);
                    refreshOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Exception: " + e.getMessage());
                }
            }
        });
    }

    private void refreshOutput() {
        ArrayList<Character> chars = MarkovLanguage.usableCharacters(entry.getText());
        LinkedHashMap<String, Double> distro = chain.labelDistribution(chars);
        StringBuilder table = new StringBuilder();
        for (Map.Entry<String,Double> entry: distro.entrySet()) {
            table.append(entry.getKey());
            table.append(':');
            table.append(entry.getValue());
            table.append('\n');
        }
        output.setText(table.toString());
        winner.setText(chain.bestMatchingChain(chars));
    }
}
