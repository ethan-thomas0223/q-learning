package learning.handwriting.gui;

import learning.core.Assessment;

import javax.swing.table.AbstractTableModel;

public class AssessmentModel<L> extends AbstractTableModel {
    private Assessment<L> assessment;
    private static String[] headers = new String[]{"Label", "Examples", "Correct", "%"};

    public AssessmentModel(Assessment<L> assessment) {
        this.assessment = assessment;
    }

    @Override
    public int getRowCount() {
        return assessment.numLabels() + 1;
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == 0) {
            return headers[columnIndex];
        } else {
            L label = assessment.getLabel(rowIndex - 1);
            int total = assessment.getTotalFor(label);
            int correct = assessment.getCorrectFor(label);
            if (columnIndex == 0) {
                return label;
            } else if (columnIndex == 1) {
                return total;
            } else if (columnIndex == 2) {
                return correct;
            } else {
                return String.format("%5.2f", (double)correct / total * 100.0);
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
