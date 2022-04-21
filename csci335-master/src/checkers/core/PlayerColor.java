package checkers.core;

import java.awt.*;

public enum PlayerColor {
    BLACK, RED;

    public PlayerColor opponent() {
        return this == BLACK ? RED : BLACK;
    }

    public Color color() {
        return this == BLACK ? new Color(0, 0, 0) : new Color(255, 0, 0);
    }
}
