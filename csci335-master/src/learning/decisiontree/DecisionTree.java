package learning.decisiontree;

import core.Duple;
import learning.core.Updateable;

import java.util.ArrayList;
import java.util.Set;

public interface DecisionTree<V, L, F, FV> {
	L classify(V d);
	void addAllLabels(Set<L> labels);

	<D extends Updateable<Duple<F,FV>>> void visualize(L label, ArrayList<Duple<F,FV>> features, D drawable);

	String toStringHelp(int numTabs);

	default String tabs(int n) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t".repeat(Math.max(0, n)));
		return sb.toString();
	}
}
