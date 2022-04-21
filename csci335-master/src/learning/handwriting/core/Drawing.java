package learning.handwriting.core;

import core.Duple;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Scanner;

public class Drawing {
	private BitSet bits;
	private int width, height;
	
	private void init(int width, int height) {
		this.width = width;
		this.height = height;
		bits = new BitSet(width * height);
	}
	
	public Drawing(int width, int height) {
		init(width, height);
	}
	
	public Drawing(String encoded) {
		String[] tokens = encoded.split("\\|");
		int width = Integer.parseInt(tokens[0]);
		int height = Integer.parseInt(tokens[1]);
		init(width, height);

		for (int t = 2, x = 0; t < tokens.length; ++t, ++x) {
			String line = tokens[t];
			for (int y = 0; y < line.length(); ++y) {
				set(x, y, line.charAt(y) == 'X');
			}
		}
	}

	public Drawing resized(int targetWidth, int targetHeight) {
		Drawing r = new Drawing(targetWidth, targetHeight);
		for (int x = 0; x < targetWidth; x++) {
			int thisX = x * width / targetWidth;
			for (int y = 0; y < targetHeight; y++) {
				int thisY = y * height / targetHeight;
				r.set(x, y, isSet(thisX, thisY));
			}
		}
		return r;
	}

	public Drawing padded(int targetWidth, int targetHeight) {
	    assert targetWidth > width;
	    assert targetHeight > height;

	    Drawing p = new Drawing(targetWidth, targetHeight);
	    int xOffset = (targetWidth - width) / 2;
	    int yOffset = (targetHeight - height) / 2;
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            p.set(x + xOffset, y + yOffset, isSet(x, y));
            }
        }
	    return p;
    }
	
	public Drawing(Drawing other) {
		this(other.width, other.height);
		for (int i = 0; i < other.bits.size(); i++) {
			bits.set(i, other.bits.get(i));
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean inBounds(int x, int y) {
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}
	
	private int bitFor(int x, int y) {
		return y * width + x;
	}
	
	public void set(int x, int y, boolean on) {
		if (inBounds(x, y)) {
			bits.set(bitFor(x, y), on);
		}
	}
	
	public boolean isSet(int x, int y) {
		return bits.get(bitFor(x, y));
	}

	public PixelUse getFeatureValue(DrawingPoint dp) {
		return isSet(dp.getX(), dp.getY()) ? PixelUse.ON : PixelUse.OFF;
	}

	public int distance(Drawing other) {
		BitSet copy = (BitSet)bits.clone();
		copy.xor(other.bits);
		return copy.cardinality();
	}
	
	public void clear() {
		for (int x = 0; x < getWidth(); ++x) {
			for (int y = 0; y < getHeight(); ++y) {
				set(x, y, false);
			}
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getWidth());
		result.append("|");
		result.append(getHeight());
		result.append("|");
		for (int x = 0; x < getWidth(); ++x) {
			for (int y = 0; y < getHeight(); ++y) {
				result.append(bits.get(bitFor(x, y)) ? 'X' : 'O');
			}
			result.append("|");
		}
		return result.toString();
	}

	public int hashCode() {return bits.hashCode();}
	
	public boolean equals(Object other) {
		if (other instanceof Drawing) {
			return bits.equals(((Drawing)other).bits);
		} else {
			return false;
		}
	}

	public static ArrayList<Duple<Drawing,String>> parseDataFrom(Scanner s) {
		ArrayList<Duple<Drawing,String>> result = new ArrayList<>();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			String[] tokens = line.split(":");
			String label = tokens[0];
			for (int i = 1; i < tokens.length; ++i) {
				result.add(new Duple<>(new Drawing(tokens[i]), label));
			}
		}
		return result;
	}

	public static ArrayList<Duple<Drawing,String>> parseDataFrom(File f) throws FileNotFoundException {
		return parseDataFrom(new Scanner(f));
	}

	public static ArrayList<Duple<Drawing,String>> parseDataFrom(String s) throws FileNotFoundException {
		return parseDataFrom(new File(s));
	}

	public static ArrayList<Duple<DrawingPoint,PixelUse>> allFeatures(ArrayList<Duple<Drawing,String>> data) {
		HashSet<Duple<DrawingPoint,PixelUse>> values = new HashSet<>();
		for (Duple<Drawing,String> datum: data) {
			for (int x = 0; x < datum.getFirst().getWidth(); x++) {
				for (int y = 0; y < datum.getFirst().getHeight(); y++) {
					DrawingPoint dp = new DrawingPoint(x, y);
					values.add(new Duple<>(dp, datum.getFirst().getFeatureValue(dp)));
				}
			}
		}
		return new ArrayList<>(values);
	}
}
