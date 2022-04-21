package robosim.gui;

import core.AIReflector;
import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.SimObjMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Sim extends JFrame {
    public static final double RADIUS = 10.0;
    public static final int PERIOD = 60;

    JComboBox<SimObjMaker> objectToPlace;
    JComboBox<String> ai;
    JButton start, stop, singleStep, resume, reset;
    JTextField total, forward, collisions, dirt;
    JTextArea messages;
    SimPanel sim;

    Timer timer;

    Controller controller;

    AIReflector<Controller> ais;

    JMenuItem open, save;

    public Sim() {
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        sim = new SimPanel();
        getContentPane().add(sim, BorderLayout.CENTER);
        sim.addMouseListener(new SimClickListener());
        sim.addKeyListener(new KeyHandler());

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        addFileMenu(bar);

        objectToPlace = new JComboBox<>();
        for (SimObjMaker maker: SimObjMaker.values()) {objectToPlace.addItem(maker);}

        timer = new Timer(PERIOD, e -> advanceSimulation());

        start = new JButton("Start");
        start.addActionListener(e -> {
            try {
                setupController();
                halt();
                sim.reset();
                timer.start();
            } catch (Exception ex) {
                oops(ex);
            }
        });

        stop = new JButton("Stop");
        stop.addActionListener(e -> halt());

        singleStep = new JButton("Single Step");
        singleStep.addActionListener(e -> {
            try {
                if (controller == null) {
                    setupController();
                }
                advanceSimulation();
            } catch (Exception ex) {
                oops(ex);
            }
        });

        resume = new JButton("Resume");
        resume.addActionListener(e -> timer.start());

        reset = new JButton("Reset");
        reset.addActionListener(e -> {
            try {
                setupController();
                halt();
                sim.reset();
            } catch (Exception ex) {
                oops(ex);
            }
        });

        ai = new JComboBox<>();
        ais = new AIReflector<>(Controller.class, "robosim.ai");
        for (String typeName: ais.getTypeNames()) {
            ai.addItem(typeName);
        }
        if (ai.getItemCount() > 0) {
            ai.setSelectedIndex(0);
        }

        JPanel topButtons = new JPanel();
        getContentPane().add(topButtons, BorderLayout.NORTH);
        topButtons.add(start);
        topButtons.add(stop);
        topButtons.add(singleStep);
        topButtons.add(resume);
        topButtons.add(reset);

        JPanel selections = new JPanel();
        selections.add(ai);
        selections.add(objectToPlace);
        getContentPane().add(selections, BorderLayout.SOUTH);

        JPanel info = new JPanel();
        info.setLayout(new GridLayout(4, 1));
        total = new JTextField(5);
        forward = new JTextField(5);
        collisions = new JTextField(5);
        dirt = new JTextField(5);
        addLabeledField(info, total, "Total");
        addLabeledField(info, forward, "Forward");
        addLabeledField(info, collisions, "Collisions");
        addLabeledField(info, dirt, "Dirt");
        getContentPane().add(info, BorderLayout.EAST);

        messages = new JTextArea(10, 10);
        getContentPane().add(messages, BorderLayout.WEST);
    }

    private void advanceSimulation() {
        controller.control(sim.getSim());
        sim.getSim().move();
        refreshStats();
        messages.setText(controller.getStatus());
        sim.repaint();
    }

    private void refreshStats() {
        total.setText(Integer.toString(sim.getSim().getTotalMoves()));
        forward.setText(Integer.toString(sim.getSim().getForwardMoves()));
        collisions.setText(Integer.toString(sim.getSim().getCollisions()));
        dirt.setText(Integer.toString(sim.getSim().getDirt()));
    }

    void addLabeledField(JComponent container, JTextField field, String label) {
        field.setEditable(false);
        JPanel pair = new JPanel();
        pair.add(new JLabel(label));
        pair.add(field);
        container.add(pair);
    }

    private class SimClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            sim.add(objectToPlace.getItemAt(objectToPlace.getSelectedIndex()).makeAt(e.getX(), e.getY()));
        }
    }

    private void addFileMenu(JMenuBar bar) {
        JMenu fileMenu = new JMenu("File");
        bar.add(fileMenu);

        open = new JMenuItem("Open");
        open.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    sim.openFrom(chooser.getSelectedFile());
                } catch (IOException ex) {
                    oops(ex);
                }
            }});
        fileMenu.add(open);

        save = new JMenuItem("Save");
        save.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    sim.saveTo(chooser.getSelectedFile());
                } catch (FileNotFoundException ex) {
                    oops(ex);
                }
            }
        });
        fileMenu.add(save);
    }

    void setupController() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        controller = ais.newInstanceOf(ai.getItemAt(ai.getSelectedIndex()));
    }

    void halt() {
        timer.stop();
    }

    private void oops(Exception e) {
        JOptionPane.showMessageDialog(this, "Exception: " + e.getMessage());
        e.printStackTrace();
    }

    static Optional<Action> key2action(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            return Optional.of(Action.LEFT);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            return Optional.of(Action.RIGHT);
        } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            return Optional.of(Action.FORWARD);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            return Optional.of(Action.BACKWARD);
        } else {
            return Optional.empty();
        }
    }

    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            Sim.key2action(e).ifPresent(action -> {
                action.applyTo(sim.getSim());
                sim.getSim().move();
                refreshStats();
            });
            repaint();
        }
    }

    public static void main(String[] args) {
        Sim s = new Sim();
        s.setVisible(true);
    }
}
