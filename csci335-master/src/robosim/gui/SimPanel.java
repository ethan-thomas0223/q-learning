package robosim.gui;

import robosim.core.SimObject;
import robosim.core.Simulator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.stream.Stream;

public class SimPanel extends JPanel {
    Simulator sim;

    public SimPanel() {
        super();
        sim = new Simulator(getWidth(), getHeight());
    }

    Simulator getSim() {return sim;}

    protected void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        sim.resize(getWidth(), getHeight());
        gc.setColor(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        sim.drawOn(gc);
        requestFocus();
    }

    public void add(SimObject obj) {
        sim.add(obj);
        repaint();
    }

    public void reset() {
        sim.reset();
    }

    public void openFrom(File openee) throws IOException {
        Stream<String> stream = Files.lines(openee.toPath());
        StringBuilder sb = new StringBuilder();
        stream.forEach(s -> sb.append(s).append("\n"));
        sim.resetObjectsFrom(sb.toString());
        repaint();
    }

    public void saveTo(File savee) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(savee);
        out.println(sim.getMapString());
        out.close();
    }
}
