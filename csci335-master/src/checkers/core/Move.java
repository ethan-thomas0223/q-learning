package checkers.core;

public class Move {

	private int startRow, startCol, endRow, endCol;
	private int srPos, scPos, erPos;

	public Move(Checkerboard c, int sRow, int sCol, int eRow, int eCol) {
		startRow = sRow;
		startCol = sCol;
		endRow = eRow;
		endCol = eCol;

		int base = Math.max(c.numRows(), c.numCols());
		erPos = base;
		scPos = erPos * base;
		srPos = scPos * base;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getStartCol() {
		return startCol;
	}

	public int getEndRow() {
		return endRow;
	}

	public int getEndCol() {
		return endCol;
	}

	// Pre: None
	// Post: Returns true if this SingleMove has the same start and end
	//       as m does
	public boolean equals(Object o) {
		if (o instanceof Move m) {
			return (startRow == m.startRow && startCol == m.startCol &&
					endRow == m.endRow && endCol == m.endCol);
		} else {
			return false;
		}
	}

	public String toString() {
		return "(" + startRow + ", " + startCol + ") to ("
				+ endRow + ", " + endCol + ")";
	}

	public int hashCode() {
		return startRow * srPos + startCol * scPos + endRow * erPos + endCol;
	}

	// Pre: None
	// Post: Returns true if this is a capture move
	public boolean isCapture() {
		return (Math.abs(endRow - startRow) == 2) &&
				(Math.abs(endCol - startCol) == 2);
	}
}
