package learning.handwriting.core;

import java.awt.*;

public enum PixelUse {
	ON {
		@Override
		public PixelUse successor() {
			return OFF;
		}

		@Override
		public Color getColor() {return Color.BLACK;}
		
		@Override
		public PixelUse updated(PixelUse update) {
			return update == ON ? ON : CONFLICT;
		}
	}, 
	OFF {
		@Override
		public PixelUse successor() {
			return IGNORED;
		}

		@Override
		public Color getColor() {return Color.WHITE;}
		
		@Override
		public PixelUse updated(PixelUse update) {
			return update == OFF ? OFF : CONFLICT;
		}
	}, 
	IGNORED {
		@Override
		public PixelUse successor() {
			return CONFLICT;
		}

		@Override
		public Color getColor() {return Color.LIGHT_GRAY;}

		@Override
		public PixelUse updated(PixelUse update) {return update;}
	}, 
	CONFLICT {
		@Override
		public PixelUse successor() {
			return ON;
		}

		@Override
		public Color getColor() {return Color.RED;}
		
		@Override
		public PixelUse updated(PixelUse update) {return CONFLICT;}
	};

	abstract public Color getColor();
	abstract public PixelUse updated(PixelUse update);
	abstract public PixelUse successor();

	public int getARGB() {
		int r = 255 * getColor().getRed();
		int g = 255 * getColor().getGreen();
		int b = 255 * getColor().getBlue();
		int a = 255 * getColor().getAlpha();
		return a << 24 | r << 16 | g << 8 | b;
	}
}
