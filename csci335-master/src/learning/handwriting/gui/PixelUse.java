package learning.handwriting.gui;

import java.awt.*;

public enum PixelUse {
	ON {
		@Override
		public Color getColor() {return Color.BLUE;}
	}, 
	OFF {
		@Override
		public Color getColor() {return Color.WHITE;}
	}, 
	IGNORED {
		@Override
		public Color getColor() {return Color.LIGHT_GRAY;}
	}, 
	CONFLICT {
		@Override
		public Color getColor() {return Color.RED;}
	};
	
	abstract public Color getColor();
	
	public static PixelUse[][] ignoreAll(int width, int height) {
		PixelUse[][] use = new PixelUse[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				use[x][y] = PixelUse.IGNORED;
			}
		}
		return use;
	}
}
