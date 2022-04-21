package learning.handwriting.core;

import core.Duple;
import learning.core.Updateable;

import java.awt.*;

public class PixelUsePattern implements Updateable<Duple<DrawingPoint,PixelUse>> {
	private PixelUse[][] pattern;
	
	public PixelUsePattern(int width, int height) {
		pattern = new PixelUse[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				pattern[x][y] = PixelUse.IGNORED;
			}
		}
	}
	
	public int getWidth() {return pattern.length;}
	public int getHeight() {return pattern[0].length;}

	@Override
	public void update(Duple<DrawingPoint,PixelUse> update) {
		pattern[update.getFirst().getX()][update.getFirst().getY()] =
				pattern[update.getFirst().getX()][update.getFirst().getY()].updated(update.getSecond());
	}

	public void draw(Graphics g, int gWidth, int gHeight) {
		int width = pattern.length;
		for (int x = 0; x < width; ++x) {
			int height = pattern[x].length;
			for (int y = 0; y < height; ++y) {
				g.setColor(pattern[x][y].getColor());
				g.fillRect(x * gWidth / width, y * gHeight / height, (x+1) * gWidth / width, (y+1) * gHeight / height);
			}
		}
	}
}
