package learning.handwriting.gui;

import learning.handwriting.core.PixelUsePattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class PixelUseVisualizer extends JPanel {
	private PixelUser target;
	private JComboBox labels;
	private VisPanel display;
	
	public PixelUseVisualizer(PixelUser target) {
		super();
		this.target = target;
		
		setLayout(new BorderLayout());
		JPanel top = new JPanel();
		labels = new JComboBox();
		for (String label: target.getLabels()) {
			labels.addItem(label);
		}
		labels.addActionListener(new Updater());
		top.add(labels);
		add(top, BorderLayout.NORTH);
		
		display = new VisPanel();
		add(display, BorderLayout.CENTER);
	}
	
	@SuppressWarnings("serial")
	private class VisPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			PixelUsePattern use = target.getPixelUse(labels.getSelectedItem().toString());
			use.draw(g, getWidth(), getHeight());
		}
	}
	
	private class Updater implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			display.repaint();
		}
	}
}
