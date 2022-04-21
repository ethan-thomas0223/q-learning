package checkers.core;

import java.util.Locale;

public class Piece {
    private PlayerColor color;
    private boolean king;

    public Piece(PlayerColor color) {
        this.color = color;
        this.king = false;
    }

    @Override
    public int hashCode() {
        return (color == PlayerColor.RED ? 1 : 0) + (king ? 2 : 0);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Piece p) {
            return p.color == this.color && p.king == this.king;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = color == PlayerColor.BLACK ? "b" : "r";
        if (king) {
            result = result.toUpperCase(Locale.ROOT);
        }
        return result;
    }

    public Piece kinged() {
        Piece kinged = new Piece(this.color);
        kinged.king = true;
        return kinged;
    }

    public boolean isKing() {return this.king;}

    public PlayerColor getColor() {return this.color;}
}
