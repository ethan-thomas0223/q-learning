package search;

import java.util.Optional;

public interface SearchQueue<T> {
    void enqueue(SearchNode<T> node);
    Optional<SearchNode<T>> dequeue();
}
