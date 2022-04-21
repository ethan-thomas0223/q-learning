package learning.handwriting.gui;

import learning.handwriting.core.Drawing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MousePencil implements MouseMotionListener {
	private DrawingPanel v;
	private boolean isDrawing;
	private static final int PEN_SIZE = 2;
	
	public MousePencil(DrawingPanel v) {
		v.addMouseMotionListener(this);
		this.v = v;
		isDrawing = true;
	}
	
	private Drawing d() {return v.getDrawing();}
	
	public boolean isDrawing() {
		return isDrawing;
	}
	
	public void draw() {
		isDrawing = true;
	}
	
	public void erase() {
		isDrawing = false;
	}
	
	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / v.xCell();
		int y = e.getY() / v.yCell();
		for (int i = 0; i < PEN_SIZE; i++) {
			for (int j = 0; j < PEN_SIZE; j++) {
				d().set(x + i, y + j, isDrawing());
			}
		}
		v.repaint();
	}

	public void mouseMoved(MouseEvent e) {}
}
