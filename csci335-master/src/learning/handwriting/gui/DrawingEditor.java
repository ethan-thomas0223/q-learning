package learning.handwriting.gui;

import core.AIReflector;
import core.Duple;
import learning.core.Assessment;
import learning.core.Classifier;
import learning.handwriting.core.Drawing;
import learning.handwriting.core.SampleData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("serial")
public class DrawingEditor extends JFrame {

	public static void main(String[] args) {
		DrawingEditor gui = new DrawingEditor();
		gui.setVisible(true);
	}

	public final static int DRAWING_WIDTH = 40, DRAWING_HEIGHT = 40;

	private MousePencil c;
	private DrawingPanel view;
	private SampleData data;
	private Classifier<Drawing,String> ai;
	
	private JMenuItem open, save, visualize, assess;
	private JButton clear, recordDrawing, drawErase;
	private JFileChooser chooser;
	private JComboBox labeler, indexer, learner;
	private AIReflector<Classifier<Drawing, String>> finder;
	private JButton createLearner, applyLearner;
	private JTextField netLabel, progress;
	
	private JFrame visualFrame = new JFrame();
	private JFrame assessmentFrame = new JFrame();
	
	private Drawing d() {return view.getDrawing();}
	
	private Drawing makeNewDrawing() {return new Drawing(DRAWING_WIDTH, DRAWING_WIDTH);}
	
	public DrawingEditor() {
		setSize(800, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		finder = new AIReflector<>(Classifier.class, "learning.handwriting.learners");
		
		getContentPane().setLayout(new BorderLayout());

		view = new DrawingPanel(makeNewDrawing());
		c = new MousePencil(view);
		getContentPane().add(view, BorderLayout.CENTER);
		
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		addFileMenu(bar);
		addLearnerMenu(bar);
		addLearnerControls();
		addViewByLabel();
	}
	
	private void addFileMenu(JMenuBar bar) {
		JMenu fileMenu = new JMenu("File");
		bar.add(fileMenu);
		
		open = new JMenuItem("Open");
		open.addActionListener(new Opener());
		fileMenu.add(open);
		
		save = new JMenuItem("Save");
		save.addActionListener(new Saver());
		fileMenu.add(save);
		
		chooser = new JFileChooser();
	}

	private void addLearnerMenu(JMenuBar bar) {
		JMenu learnerMenu = new JMenu("Learner");
		bar.add(learnerMenu);

		visualize = new JMenuItem("Visualize");
		visualize.addActionListener(new VisualizeFramer());
		learnerMenu.add(visualize);

		assess = new JMenuItem("Assess");
		assess.addActionListener(new AssessmentFramer());
		learnerMenu.add(assess);
	}
	
	private void addLearnerControls() {
		JPanel learnerControls = new JPanel();
		createLearner = new JButton("Create learner");
		createLearner.addActionListener(new Creator());
		learnerControls.add(createLearner);
		
		learner = new JComboBox();
		for (String type: finder.getTypeNames()) {
			learner.addItem(type);
		}
		learnerControls.add(learner);
		
		applyLearner = new JButton("Classify drawing");
		applyLearner.addActionListener(new Applier());
		learnerControls.add(applyLearner);
		
		learnerControls.add(new JLabel("Drawing label:"));
		netLabel = new JTextField(10);
		netLabel.setEditable(false);
		learnerControls.add(netLabel);

		getContentPane().add(learnerControls, BorderLayout.SOUTH);
	}
	
	private void addViewByLabel() {
		data = new SampleData();
		
		JPanel dataPanel = new JPanel();
		labeler = new JComboBox();
		labeler.addActionListener(new Labeler());
		dataPanel.add(labeler);
		
		indexer = new JComboBox();
		indexer.addActionListener(new Swapper());
		dataPanel.add(indexer);
		
		clear = new JButton("Clear");
		clear.addActionListener(new Clearer());
		dataPanel.add(clear);
		
		recordDrawing = new JButton("Record drawing");
		recordDrawing.addActionListener(new Recorder());
		dataPanel.add(recordDrawing);
		
		drawErase = new JButton("Erase");
		drawErase.addActionListener(new DrawEraser());
		dataPanel.add(drawErase);

		progress = new JTextField(20);
		progress.setEditable(false);
		dataPanel.add(progress);
		
		getContentPane().add(dataPanel, BorderLayout.NORTH);
	}
	
	private class Opener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int choice = chooser.showOpenDialog(null);
			if (choice == JFileChooser.APPROVE_OPTION) {
				try {
					data = SampleData.parseDataFrom(new Scanner(chooser.getSelectedFile()));
					loadLabels();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void loadLabels() {
		labeler.removeAllItems();
		for (String label: data.allLabels()) {
			labeler.addItem(label);
		}
	}
	
	private class Saver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int choice = chooser.showSaveDialog(null);
			if (choice == JFileChooser.APPROVE_OPTION) {
				try {
					PrintStream ps = new PrintStream(chooser.getSelectedFile());
					ps.println(data.toString());
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private class Labeler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (hasCurrentLabel()) {
				String label = getCurrentLabel();
				if (data.hasLabel(label)) {
					indexer.removeAllItems();
					for (int i = 0; i < data.numDrawingsFor(label); ++i) {
						indexer.addItem(i);
					}
					changeIndexedDrawing();
				} else {
					data.addLabel(label);
				}
			}
		}
	}
	
	private boolean hasCurrentLabel() {
		return labeler.getItemCount() > 0;
	}
	
	private String getCurrentLabel() {
		return labeler.getSelectedItem().toString();
	}

	private void changeIndexedDrawing() {
		if (indexer.getItemCount() > 0) {
			String label = getCurrentLabel();
			int index = Integer.parseInt(indexer.getSelectedItem().toString());
			if (data.numDrawingsFor(label) > index) {
				view.resetDrawing(data.getDrawing(label, index));
			}
		}
	}
	
	private class Recorder implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String label = JOptionPane.showInputDialog("Enter drawing label");
			if (label != null) {
				data.addDrawing(label, view.getDrawing());
				loadLabels();
				indexer.addItem(data.numDrawingsFor(label));
			}
		}
	}
	
	private class Clearer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.resetDrawing(makeNewDrawing());
		}
	}
	
	private class Swapper implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			changeIndexedDrawing();
		}
	}
	
	private class DrawEraser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (c.isDrawing()) {
				drawErase.setText("Draw");
				c.erase();
			} else {
				drawErase.setText("Erase");
				c.draw();
			}
		}
	}
	
	private class Creator implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int choice = chooser.showOpenDialog(null);
			if (choice == JFileChooser.APPROVE_OPTION) {
				try {
					ArrayList<Duple<Drawing,String>> training = Drawing.parseDataFrom(new Scanner(chooser.getSelectedFile()));
					ai = finder.newInstanceOf(learner.getSelectedItem().toString());
					new Thread(() -> {
						Timer timer = createProgressTimer(progress);
						try {
							timer.start();
							ai.train(training);
							timer.stop();
							JOptionPane.showMessageDialog(DrawingEditor.this, "Training complete");
						} catch (Exception exc) {
							timer.stop();
							progress.setText("Error at: " + progress.getText());
							exc.printStackTrace();
							JOptionPane.showMessageDialog(null, "Exception: " + exc.getMessage());
						}
					}).start();
				} catch (FileNotFoundException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | SecurityException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(DrawingEditor.this, "Problem! " + e1.getMessage());
				}
			}
		}
	}
	
	private class Applier implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (ai != null) {
				netLabel.setText(ai.classify(d()));
			}
		}
	}
	
	private class VisualizeFramer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ai.getVisualization().ifPresentOrElse(visPanel -> {
				visualFrame.setContentPane(visPanel);
				visualFrame.setSize(300, 300);
				visualFrame.setVisible(true);
			}, () -> JOptionPane.showMessageDialog(DrawingEditor.this, "No Visualization available"));
		}
	}

	private class AssessmentFramer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int choice = chooser.showOpenDialog(null);
			if (choice == JFileChooser.APPROVE_OPTION) {
				try {
					assess(Drawing.parseDataFrom(new Scanner(chooser.getSelectedFile())), assessmentFrame, finder, learner.getSelectedItem().toString(), progress);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(DrawingEditor.this, "Problem! " + e1.getMessage());
				}
			}
		}
	}

	public static Timer createProgressTimer(JTextField progress) {
		progress.setText("");
		progress.setHorizontalAlignment(JTextField.RIGHT);
		return new Timer(1000, e -> {
			int prev = progress.getText().equals("") ? 0 : Integer.parseInt(progress.getText());
			progress.setText(Integer.toString(prev + 1));
		});
	}

	public static <V,L> void assess(ArrayList<Duple<V,L>> training, JFrame assessmentFrame,
									AIReflector<Classifier<V, L>> finder, String learner, JTextField progress) {
		try {
			int numPartitions = Integer.parseInt(JOptionPane.showInputDialog("Enter number of cross-validation sets:"));
			ArrayList<ArrayList<Duple<V,L>>> partitions = Assessment.partition(numPartitions, training);
			new Thread(() -> {
				Timer timer = createProgressTimer(progress);
				try {
					timer.start();
					JTabbedPane tabs = new JTabbedPane();
					ArrayList<Assessment<L>> assessments = Assessment.multiTrial(() -> finder.optionalInstanceOf(learner).get(), partitions);
					for (Assessment<L> assessment : assessments) {
						tabs.add(new JTable(new AssessmentModel<>(assessment)));
					}
					assessmentFrame.setContentPane(tabs);
					assessmentFrame.setSize(300, 300);
					assessmentFrame.setVisible(true);
					timer.stop();
					progress.setText("Finished in " + progress.getText() + " seconds");
				} catch (Exception | StackOverflowError exc) {
					timer.stop();
					progress.setText("Error at: " + progress.getText());
					exc.printStackTrace();
					JOptionPane.showMessageDialog(null, "Exception: " + exc.getMessage());
				}
			}).start();
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Assessment cancelled");
		}
	}
}
