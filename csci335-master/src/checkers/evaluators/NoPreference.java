package checkers.evaluators;

import checkers.core.Checkerboard;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class NoPreference implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        return 0;
    }
}
