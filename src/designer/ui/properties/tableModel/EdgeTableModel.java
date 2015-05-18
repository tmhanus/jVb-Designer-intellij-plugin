package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import designer.ui.editor.JSLCanvas;
import designer.ui.editor.element.Edge;
import designer.ui.editor.element.EdgeOnValue;
import designer.ui.editor.element.Element;
import designer.ui.properties.PropertyTable;
import specification.*;
import specification.definitions.Definition;

import javax.swing.table.AbstractTableModel;

/**
 *  Created by Tomas Hanus on 4/17/2015.
 */
public class EdgeTableModel extends AbstractTableModel {
    private Definition definition;
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Edge edge;
    private PropertyTable propertyTable;
    private JSLCanvas jslCanvas;

    public EdgeTableModel(Edge edge, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.edge = edge;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;
        this.definition = jslCanvas.getDiagramDefinition();
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        if (this.edge instanceof EdgeOnValue) return 1;
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (this.edge instanceof EdgeOnValue) {
            if (row == 0) {
                if (col == 0) return new String("On");
                if (col == 1) {
                    if (this.edge.getSuccessor() instanceof StopTransition)
                        return ((StopTransition) this.edge.getSuccessor()).getOnForElement(this.edge.getPredecessor());
                    if (this.edge.getSuccessor() instanceof EndTransition)
                        return ((EndTransition) this.edge.getSuccessor()).getOnForElement(this.edge.getPredecessor());
                    if (this.edge.getSuccessor() instanceof FailTransition)
                        return ((FailTransition) this.edge.getSuccessor()).getOnForElement(this.edge.getPredecessor());
                    if (this.edge.getPredecessor() instanceof Decision) {
                        Next nextTransitionFromDecision = ((Decision) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor());
                        if (nextTransitionFromDecision != null) {
                            return nextTransitionFromDecision.getOn();
                        }
                    }
                    if (this.edge.getPredecessor() instanceof Step) {
                        Next nextTransitionFromStep = ((Step) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor().getId());
                        if (nextTransitionFromStep != null) {
                            return nextTransitionFromStep.getOn();
                        }
                    }
                    if (this.edge.getPredecessor() instanceof Flow) {
                        Next nextTransitionFromStep = ((Flow) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor().getId());
                        if (nextTransitionFromStep != null) {
                            return nextTransitionFromStep.getOn();
                        }
                    }
                    return null;// ((EdgeOnValue) this.edge).getOn();
                }
            }
        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (this.edge instanceof EdgeOnValue) {
            if (row == 0) {
                if (col == 1) {
                    Element predecessor = this.edge.getPredecessor();
                    Element successor = this.edge.getSuccessor();
                    ((EdgeOnValue) this.edge).setOn((String) value);
                    if (successor instanceof StopTransition)
                        ((StopTransition) successor).setOnForElement(predecessor, (String) value);
                    if (successor instanceof EndTransition)
                        ((EndTransition) successor).setOnForElement(predecessor, (String) value);
                    if (successor instanceof FailTransition)
                        ((FailTransition) successor).setOnForElement(predecessor, (String) value);

                    if (this.edge.getPredecessor() instanceof Decision) {
                        Next nextTransitionFromDecision = ((Decision) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor());
                        if (nextTransitionFromDecision != null) {
                            nextTransitionFromDecision.setOn((String) value);
                        }
                    }
                    if (this.edge.getPredecessor() instanceof Step) {
                        Next nextTransitionFromStep = ((Step) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor().getId());
                        if (nextTransitionFromStep != null) {
                            ((Step) this.edge.getPredecessor()).editNextTransitionTo(this.edge.getSuccessor().getId(), (String) value);
                        }
                    }
                    if (this.edge.getPredecessor() instanceof Flow) {
                        Next nextTransitionFromStep = ((Flow) this.edge.getPredecessor()).getNextTransition(this.edge.getSuccessor().getId());
                        if (nextTransitionFromStep != null) {
                            ((Flow) this.edge.getPredecessor()).editNextTransitionTo(this.edge.getSuccessor().getId(), (String) value);
                        }
                    }
                }
            }
            this.jslCanvas.fireJobDiagramChange();
        }
    }

    public boolean isCellEditable(int row, int col) {
        if ((this.edge instanceof EdgeOnValue) && (col == 1)) return true;
        return false;
    }
}