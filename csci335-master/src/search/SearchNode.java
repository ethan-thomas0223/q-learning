package search;

import java.util.ArrayDeque;
import java.util.Optional;

public class SearchNode<T> {
    private T value;
    private Optional<SearchNode<T>> parent;
    private int depth;

    public SearchNode(T value, Optional<SearchNode<T>> parent) {
        this.value = value;
        this.parent = parent;
        this.depth = parent.map(node -> 1 + node.depth).orElse(0);
    }

    public ArrayDeque<T> searchPath() {
        ArrayDeque<T> path = new ArrayDeque<>();
        Optional<SearchNode<T>> current = Optional.of(this);
        while (current.isPresent()) {
            path.addFirst(current.get().getValue());
            current = current.get().parent;
        }
        return path;
    }

    public T getValue() {return value;}

    public int getDepth() {return depth;}
}
