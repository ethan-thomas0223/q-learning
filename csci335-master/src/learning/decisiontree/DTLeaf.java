package learning.decisiontree;

import core.Duple;
import learning.core.Updateable;

import java.util.ArrayList;
import java.util.Set;

public class DTLeaf<V, L, F, FV> implements DecisionTree<V, L, F, FV> {
	private L label;
	
	public DTLeaf(L label) {this.label = label;}

	@Override
	public L classify(V v) {
		return label;
	}

	@Override
	public void addAllLabels(Set<L> labels) {
		labels.add(label);
	}

	@Override
	public <D extends Updateable<Duple<F,FV>>> void visualize(L label, ArrayList<Duple<F, FV>> features, D drawable) {
		if (label.equals(this.label)) {
			for (Duple<F,FV> feature: features) {
				drawable.update(feature);
			}
		}
	}

	public L getLabel() {return label;}

	@Override
	public String toStringHelp(int numTabs) {
		return tabs(numTabs) +  "DTLeaf: " + label + "\n";
	}

	public String toString() {
		return toStringHelp(0);
	}

	public boolean equals(Object other) {
		return this.toString().equals(other.toString());
	}
}
