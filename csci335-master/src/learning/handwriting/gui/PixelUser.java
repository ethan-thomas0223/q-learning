package learning.handwriting.gui;

import learning.handwriting.core.PixelUsePattern;

import java.util.Set;

public interface PixelUser {
	Set<String> getLabels();
	PixelUsePattern getPixelUse(String label);
}
