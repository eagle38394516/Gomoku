package io.github.eagle38394516;

public class PiecePosition {

	private int x, y;
	private static PiecePosition[][] positions = new PiecePosition[15][15];

	public static PiecePosition get(int x, int y) {
		if (positions[x - 1][y - 1] == null) {
			positions[x - 1][y - 1] = new PiecePosition(x, y);
		}
		return positions[x - 1][y - 1];
	}

	private PiecePosition(int x, int y) {
		if (x < 1 || x > 15 || y < 1 || y > 15) {
			throw new IllegalArgumentException("x = " + x + "; y = " + y + ";");
		}
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PiecePosition)) {
			return false;
		}
		PiecePosition p = (PiecePosition) obj;
		return this.x == p.x && this.y == p.y;
	}
}
