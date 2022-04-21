package search.breadthfirst;

import search.SearchNode;
import search.SearchQueue;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;

public class BreadthFirstQueue<T> implements SearchQueue<T> {
    private ArrayDeque<SearchNode<T>> queue = new ArrayDeque<>();
    private HashSet<T> visited = new HashSet<>();

    @Override
    public void enqueue(SearchNode<T> node) {
        if (!visited.contains(node.getValue())) {
            queue.addLast(node);
            visited.add(node.getValue());
        }
    }

    @Override
    public Optional<SearchNode<T>> dequeue() {
        if (queue.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(queue.removeFirst());
        }
    }
}
