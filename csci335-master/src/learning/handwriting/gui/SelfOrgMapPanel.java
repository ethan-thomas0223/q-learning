package learning.handwriting.gui;

import learning.handwriting.core.FloatDrawing;
import learning.som.SelfOrgMap;

import javax.swing.*;
import java.awt.*;

public class SelfOrgMapPanel extends JPanel {
	private SelfOrgMap<FloatDrawing> som;
	
	public SelfOrgMapPanel(SelfOrgMap som) {
		super();
		this.som = som;
		setBackground(Color.white);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double xScale = (double)getWidth() / som.getMapWidth();
		double yScale = (double)getHeight() / som.getMapHeight();
		
		for (int x = 0; x < som.getMapWidth(); ++x) {
			for (int y = 0; y < som.getMapHeight(); ++y) {
				FloatDrawing fd = som.getNode(x, y);
				int xStart = (int)(x * xScale);
				int yStart = (int)(y * yScale);
				int xEnd = (int)((x + 1) * xScale);
				int yEnd = (int)((y + 1) * yScale);
				double xDrawScale = (double)(xEnd - xStart) / fd.getWidth();
				double yDrawScale = (double)(yEnd - yStart) / fd.getHeight();
				for (int xSub = xStart; xSub < xEnd; ++xSub) {
					for (int ySub = yStart; ySub < yEnd; ++ySub) {
						int xRef = (int)((xSub - xStart) / xDrawScale);
						int yRef = (int)((ySub - yStart) / yDrawScale);
						int gray = (int)((1 - fd.get(xRef, yRef)) * 255);
						g.setColor(new Color(gray,gray,gray));
						g.fillRect(xSub, ySub, 1, 1);
					}
				}
			}
		}
	}
}
