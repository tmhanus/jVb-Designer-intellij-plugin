package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import designer.ui.editor.ElementFactory;
import designer.ui.editor.JSLCanvas;
import designer.ui.properties.PropertyTable;
import designer.ui.properties.editor.*;
import designer.ui.properties.renderer.*;
import specification.*;
import specification.definitions.Definition;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

/**
 *  Created by Tomas Hanus on 4/10/2015.
 */
public class StepTableModel extends AbstractTableModel {
    private final Definition definition;
    private ElementFactory elementFactory;
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Step step;
    private PropertyTable propertyTable;
    private boolean isPartitionEnabled;
    private String[] idCollisionIn = new String[1];
    private java.util.Properties standardValue;
    private ResourceBundle elementDefaultValues;
    private JSLCanvas jslCanvas;


    public StepTableModel(Step step, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.step = step;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;
        this.elementFactory = jslCanvas.getElementFactory();
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;
        this.definition = jslCanvas.getDiagramDefinition();
        this.elementDefaultValues = ResourceBundle.getBundle("designer.resources.bundleProperties.ElementDefaultValue");

        setRenderers();
        setEditors();
    }

    public void setRenderers() {
        this.propertyTable.getRowRendererModel().removeAllRenderers();

        this.propertyTable.getRowRendererModel().addRendererForRow(3, new ListenersCellRenderer());//Step Liseners
        this.propertyTable.getRowRendererModel().addRendererForRow(4, new PropertiesCellRenderer());//Step Properties


        if (step.isChunkOriented()) {
            int shift = 0;
            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom"))) {
                shift = 1;
                this.propertyTable.getRowRendererModel().addRendererForRow(6, new CheckpointAlgorithmCellRenderer());//CheckpointAlgorithm
            }
            this.propertyTable.getRowRendererModel().addRendererForRow(10 + shift, new ExceptionsCellRenderer());//Skippable Exception
            this.propertyTable.getRowRendererModel().addRendererForRow(11 + shift, new ExceptionsCellRenderer());//Retryable Exception
            this.propertyTable.getRowRendererModel().addRendererForRow(12 + shift, new ExceptionsCellRenderer());//NoRollback Exception

            if (step.isPartitionEnabled()) {
                this.propertyTable.getRowRendererModel().addRendererForRow(13 + shift, new CategoryRowRenderer());//Partition Category
                if (step.getPartition() != null && step.getPartition().isPartitionPlanDefinedAtRuntime() == false)
                    this.propertyTable.getRowRendererModel().addRendererForRow(17 + shift, new PartitionPropertiesCellRenderer());// PartitionProperties
                else
                    this.propertyTable.getRowRendererModel().addRendererForRow(17 + shift, new PropertiesCellRenderer());// Mapper Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(19 + shift, new PropertiesCellRenderer());//Reducer Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(21 + shift, new PropertiesCellRenderer());//Collector Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(23 + shift, new PropertiesCellRenderer());//Analyzer Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(24 + shift, new CategoryRowRenderer());//Reader Category
                this.propertyTable.getRowRendererModel().addRendererForRow(26 + shift, new PropertiesCellRenderer());//Reader Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(27 + shift, new CategoryRowRenderer());//Processor Category
                this.propertyTable.getRowRendererModel().addRendererForRow(29 + shift, new PropertiesCellRenderer());//Processor Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(30 + shift, new CategoryRowRenderer());//Writer Category
                this.propertyTable.getRowRendererModel().addRendererForRow(32 + shift, new PropertiesCellRenderer());//Writer Properties
            } else {
                this.propertyTable.getRowRendererModel().addRendererForRow(13 + shift, new CategoryRowRenderer());//Partition Category
                this.propertyTable.getRowRendererModel().addRendererForRow(15 + shift, new CategoryRowRenderer());//Reader Category
                this.propertyTable.getRowRendererModel().addRendererForRow(17 + shift, new PropertiesCellRenderer());//Reader Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(18 + shift, new CategoryRowRenderer());//Processor Category
                this.propertyTable.getRowRendererModel().addRendererForRow(20 + shift, new PropertiesCellRenderer());//Processor Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(21 + shift, new CategoryRowRenderer());//Writer Category
                this.propertyTable.getRowRendererModel().addRendererForRow(23 + shift, new PropertiesCellRenderer());//Writer Properties
            }
        } else {// TASK-ORIENTED


            if (step.isPartitionEnabled()) {
                this.propertyTable.getRowRendererModel().addRendererForRow(5, new CategoryRowRenderer());//Partition Category

                if ((step.getPartition() != null) && (step.getPartition().isPartitionPlanDefinedAtRuntime() == false))
                    this.propertyTable.getRowRendererModel().addRendererForRow(9, new PartitionPropertiesCellRenderer());// PartitionProperties
                else
                    this.propertyTable.getRowRendererModel().addRendererForRow(9, new PropertiesCellRenderer());// Mapper Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(11, new PropertiesCellRenderer());//Reducer Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(13, new PropertiesCellRenderer());//Collector Properties
                this.propertyTable.getRowRendererModel().addRendererForRow(17, new PropertiesCellRenderer());//Analyzer Properties

                this.propertyTable.getRowRendererModel().addRendererForRow(16, new CategoryRowRenderer());//Batchlet Category
                this.propertyTable.getRowRendererModel().addRendererForRow(18, new PropertiesCellRenderer());//Batchlet Properties
            } else {
                this.propertyTable.getRowRendererModel().addRendererForRow(5, new CategoryRowRenderer());//Partition Category
                this.propertyTable.getRowRendererModel().addRendererForRow(7, new CategoryRowRenderer());//Batchlet Category
                this.propertyTable.getRowRendererModel().addRendererForRow(9, new PropertiesCellRenderer());//Batchlet Properties
            }
        }
    }

    public void setEditors() {
        this.propertyTable.getRowEditorModel().removeAllEditors();

//        this.propertyTable.getRowEditorModel().addEditorForRow(2, new DefaultCellEditor(new JCheckBox()));
        this.propertyTable.getRowEditorModel().addEditorForRow(2, new DefaultCellEditor(new JCheckBox()));
        this.propertyTable.getRowEditorModel().addEditorForRow(3, new ListenersCellEditor(project));
        this.propertyTable.getRowEditorModel().addEditorForRow(4, new PropertiesCellEditor(project));

        if (step.isChunkOriented()) {
            ComboBox comboBox = new ComboBox();
            String defaultOption = new String("item");
            comboBox.addItem(defaultOption);
            comboBox.addItem(new String("custom"));
            comboBox.setSelectedItem(defaultOption);
            this.propertyTable.getRowEditorModel().addEditorForRow(5, new DefaultCellEditor(comboBox));
            int shift = 0;
            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom"))) {
                shift += 1;
                this.propertyTable.getRowEditorModel().addEditorForRow(6, new CheckpointAlgCellEditor(project));
            }

            //this.propertyTable.getRowEditorModel().addEditorForRow(8+shift, new DefaultCellEditor(new JCheckBox()));

            this.propertyTable.getRowEditorModel().addEditorForRow(10 + shift, new ExceptionsCellEditor(project));//13+shift SkippableExceptions
            this.propertyTable.getRowEditorModel().addEditorForRow(11 + shift, new ExceptionsCellEditor(project));//14+shift RetryableExceptions
            this.propertyTable.getRowEditorModel().addEditorForRow(12 + shift, new ExceptionsCellEditor(project));//15+shift NoRollbackExceptions


            this.propertyTable.getRowEditorModel().addEditorForRow(14 + shift, new DefaultCellEditor(new JCheckBox())); //Partition Enabled
            if (step.isPartitionEnabled()) {
                this.propertyTable.getRowEditorModel().addEditorForRow(15 + shift, new DefaultCellEditor(new JCheckBox()));//Runtime Planning
                ///////// RUNTIME PLANNING
                if (step.getPartition() != null && step.getPartition().isPartitionPlanDefinedAtRuntime())
                    this.propertyTable.getRowEditorModel().addEditorForRow(17 + shift, new PropertiesCellEditor(project));//Mapper Properties
                else
                    this.propertyTable.getRowEditorModel().addEditorForRow(17 + shift, new PartitionPropertiesCellEditor(project));//Partitions ListOF Properties

                this.propertyTable.getRowEditorModel().addEditorForRow(19 + shift, new PropertiesCellEditor(project));//Reducer Properties
                this.propertyTable.getRowEditorModel().addEditorForRow(21 + shift, new PropertiesCellEditor(project));//Collector Properties
                this.propertyTable.getRowEditorModel().addEditorForRow(23 + shift, new PropertiesCellEditor(project));//Analyzer Properties

                this.propertyTable.getRowEditorModel().addEditorForRow(26 + shift, new PropertiesCellEditor(project));//ReaderProperties
                this.propertyTable.getRowEditorModel().addEditorForRow(29 + shift, new PropertiesCellEditor(project));//ProcessorProperties
                this.propertyTable.getRowEditorModel().addEditorForRow(32 + shift, new PropertiesCellEditor(project));//WriterProperties
            } else {
                this.propertyTable.getRowEditorModel().addEditorForRow(17 + shift, new PropertiesCellEditor(project));//ReaderProperties
                this.propertyTable.getRowEditorModel().addEditorForRow(20 + shift, new PropertiesCellEditor(project));//ProcessorProperties
                this.propertyTable.getRowEditorModel().addEditorForRow(23 + shift, new PropertiesCellEditor(project));//WriterProperties
            }
        } else { //TASK ORIENTED
            this.propertyTable.getRowEditorModel().addEditorForRow(6, new DefaultCellEditor(new JCheckBox())); //Partition Enabled
            if (step.isPartitionEnabled()) {
                this.propertyTable.getRowEditorModel().addEditorForRow(7, new DefaultCellEditor(new JCheckBox()));//Runtime Planning
                ///////// RUNTIME PLANNING
                if ((step.getPartition() != null) && (step.getPartition().isPartitionPlanDefinedAtRuntime()))
                    this.propertyTable.getRowEditorModel().addEditorForRow(9, new PropertiesCellEditor(project));//Mapper Properties
                else
                    this.propertyTable.getRowEditorModel().addEditorForRow(9, new PartitionPropertiesCellEditor(project));//Partitions ListOF Properties

                this.propertyTable.getRowEditorModel().addEditorForRow(11, new PropertiesCellEditor(project));//Reducer Properties
                this.propertyTable.getRowEditorModel().addEditorForRow(13, new PropertiesCellEditor(project));//Collector Properties
                this.propertyTable.getRowEditorModel().addEditorForRow(15, new PropertiesCellEditor(project));//Analyzer Properties

                this.propertyTable.getRowEditorModel().addEditorForRow(18, new PropertiesCellEditor(project));//Batchlet Properties
            } else {
                this.propertyTable.getRowEditorModel().addEditorForRow(9, new PropertiesCellEditor(project));//Batchlet Properties
            }
        }
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        if (step.isChunkOriented()) {
            int isCheckpointAlgorithm = 0;
            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom")))
                isCheckpointAlgorithm = 1;
            if (step.isPartitionEnabled()) {
                return 33 + isCheckpointAlgorithm;
            } else {
                return 24 + isCheckpointAlgorithm;

            }
        } else { //TASK ORIENTED
            if (step.isPartitionEnabled()) {
                return 19;
            } else {
                return 10;
            }
        }
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int from = 0;
        if (rowIndex == from) {
            if (columnIndex == 0) return new String("Id");
            if (columnIndex == 1) return step.getId();
        } else if (rowIndex == from + 1) {
            if (columnIndex == 0) return new String("Start limit");
            if (columnIndex == 1) return step.getStartLimit();
        } else if (rowIndex == from + 2) {
            if (columnIndex == 0) return new String("Allow start if complete");
            if (columnIndex == 1) return step.isAllowStartIfComplete();
//        } else if (rowIndex == from+3) {
//            if (columnIndex == 0) return new String("Abstract");
//            if (columnIndex == 1) return step.isAbstract();
//        } else if (rowIndex == from+4) {
//            if (columnIndex == 0) return new String("Parent");
//            if (columnIndex == 1) return step.getParentElement();
        } else if (rowIndex == from + 3) {
            if (columnIndex == 0) return new String("Listeners");
            if (columnIndex == 1) return step.getListeners();
        } else if (rowIndex == from + 4) {
            if (columnIndex == 0) return new String("Properties");
            if (columnIndex == 1) return step.getProperties();
        }
        from = 5;
        if (step.isChunkOriented()) {
            if (rowIndex == from) {
                if (columnIndex == 0) return new String("Checkpoint Policy");
                if (columnIndex == 1) return step.getChunk().getCheckpoint_policy();
            }///  CHeckpoint Algorithm added/or not

            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom"))) {
                from += 1;
                if (rowIndex == from) {
                    if (columnIndex == 0) return new String("Checkpoint Algorithm");
                    if (columnIndex == 1) return step.getChunk().getCheckpointAlgorithm();
                }
            }
            if (rowIndex == from + 1) {
                if (columnIndex == 0) return new String("Item Count");
                if (columnIndex == 1) return step.getChunk().getItem_count();
            } else if (rowIndex == from + 2) {
                if (columnIndex == 0) return new String("Time Limit");
                if (columnIndex == 1) return step.getChunk().getTime_limit();
            } else if (rowIndex == from + 3) {
//                if (columnIndex == 0) return new String("Buffer Items");
//                if (columnIndex == 1) return step.getChunk().isBuffer_items();
//            } else if (rowIndex == from + 4) {
                if (columnIndex == 0) return new String("Skip Limit");
                if (columnIndex == 1) return step.getChunk().getSkip_limit();
            } else if (rowIndex == from + 4) {
                if (columnIndex == 0) return new String("Rertry Limit");
                if (columnIndex == 1) return step.getChunk().getRetry_limit();
            } else if (rowIndex == from + 5) {
                if (columnIndex == 0) return new String("Skippable exceptions");
                if (columnIndex == 1) return step.getChunk().getSkippableExceptions();
            } else if (rowIndex == from + 6) {
                if (columnIndex == 0) return new String("Retryable exceptions");
                if (columnIndex == 1) return step.getChunk().getRetryableExceptions();
            } else if (rowIndex == from + 7) {
                if (columnIndex == 0) return new String("No-rollback exceptions");
                if (columnIndex == 1) return step.getChunk().getNoRollbackExceptions();
            }
            from += 8;///?????
        }
        if (rowIndex == from) {
            if (columnIndex == 0) return new String("Partition");
            if (columnIndex == 1) return null;
        } else if (rowIndex == from + 1) {
            if (columnIndex == 0) return new String("Partition Enabled");
            if (columnIndex == 1) {
                if (definition.getElementSpec(step.getId()) == null) return false;
                return definition.getElementSpec(step.getId()).isPartitionEnabled();
            }
        }
        from += 2;
        if (step.isPartitionEnabled()) {
            if (rowIndex == from) {
                if (columnIndex == 0) return new String("Runtime planning");
                if (columnIndex == 1) return step.getPartition().isPartitionPlanDefinedAtRuntime();

            } else if (rowIndex == from + 1) {
                if (columnIndex == 0) {
                    if (step.getPartition().isPartitionPlanDefinedAtRuntime()) return new String("Mapper reference");
                    else return new String("Threads number");
                }
                if (columnIndex == 1) {
                    if (step.getPartition().isPartitionPlanDefinedAtRuntime()) {
                        if (step.getPartition().getPartitionMapper() == null) return null;
                        return step.getPartition().getPartitionMapper().getRef();
                    } else {
                        if (step.getPartition().getPartitionPlan() == null) return null;
                        return step.getPartition().getPartitionPlan().getThreadsNumber();
                    }
                }

            } else if (rowIndex == from + 2) {
                if (columnIndex == 0) {
                    if (step.getPartition().isPartitionPlanDefinedAtRuntime()) return new String("Mapper properties");
                    else return new String("Partition properties");
                }
                if (columnIndex == 1) {
                    if (step.getPartition().isPartitionPlanDefinedAtRuntime()) {
                        if (step.getPartition().getPartitionMapper() == null) return null;
                        return step.getPartition().getPartitionMapper().getProperties();
                    } else {
                        if (step.getPartition().getPartitionPlan() == null) return null;
                        return step.getPartition().getPartitionPlan().getProperties();
                    }
                }

            } else if (rowIndex == from + 3) {
                if (columnIndex == 0) return new String("Reducer Reference");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionReducer() == null) return null;
                    return step.getPartition().getPartitionReducer().getRef();
                }
            } else if (rowIndex == from + 4) {
                if (columnIndex == 0) return new String("Reducer Properties");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionReducer() == null) return null;
                    return step.getPartition().getPartitionReducer().getProperties();
                }
            } else if (rowIndex == from + 5) {
                if (columnIndex == 0) return new String("Collector Reference");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionCollector() == null) return null;
                    return step.getPartition().getPartitionCollector().getRef();
                }
            } else if (rowIndex == from + 6) {
                if (columnIndex == 0) return new String("Collector Properties");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionCollector() == null) return null;
                    return step.getPartition().getPartitionCollector().getProperties();
                }
            } else if (rowIndex == from + 7) {
                if (columnIndex == 0) return new String("Analyzer Reference");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionAnalyzer() == null) return null;
                    return step.getPartition().getPartitionAnalyzer().getRef();
                }
            } else if (rowIndex == from + 8) {
                if (columnIndex == 0) return new String("Analyzer Properties");
                if (columnIndex == 1) {
                    if (step.getPartition().getPartitionAnalyzer() == null) return null;
                    return step.getPartition().getPartitionAnalyzer().getProperties();
                }
            }
            from += 9;
        }
        if (step.isChunkOriented()) {
            if (rowIndex == from) {
                if (columnIndex == 0) return new String("Reader");
                if (columnIndex == 1) return null;
            } else if (rowIndex == from + 1) {
                if (columnIndex == 0) return new String("Reference");
                if (columnIndex == 1) return step.getChunk().getReader().getRef();
            } else if (rowIndex == from + 2) {
                if (columnIndex == 0) return new String("Properties");
                if (columnIndex == 1) return step.getChunk().getReader().getProperties();
            } else if (rowIndex == from + 3) {
                if (columnIndex == 0) return new String("Processor");
                if (columnIndex == 1) return null;
            } else if (rowIndex == from + 4) {
                if (columnIndex == 0) return new String("Reference");
                if (columnIndex == 1) {
                    if (step.getChunk().getProcessor() == null) return null;
                    return step.getChunk().getProcessor().getRef();
                }
            } else if (rowIndex == from + 5) {
                if (columnIndex == 0) return new String("Properties");
                if (columnIndex == 1) return step.getChunk().getProcessor().getProperties();
            } else if (rowIndex == from + 6) {
                if (columnIndex == 0) return new String("Writer");
                if (columnIndex == 1) return null;
            } else if (rowIndex == from + 7) {
                if (columnIndex == 0) return new String("Reference");
                if (columnIndex == 1) return step.getChunk().getWriter().getRef();
            } else if (rowIndex == from + 8) {
                if (columnIndex == 0) return new String("Properties");
                if (columnIndex == 1) return step.getChunk().getWriter().getProperties();
            }
        } else {
            if (rowIndex == from) {
                if (columnIndex == 0) return new String("Batchlet");
                if (columnIndex == 1) return null;

            } else if (rowIndex == from + 1) {
                if (columnIndex == 0) return new String("Reference");
                if (columnIndex == 1) return step.getBatchlet().getRef();

            } else if (rowIndex == from + 2) {
                if (columnIndex == 0) return new String("Properties");
                if (columnIndex == 1) return step.getBatchlet().getProperties();
            }
        }

        return null;
    }

    public void setValueAt(Object value, int row, int col) {
//        if (value == getValueAt(row, col)) return;
        int from = 0;
        if (row == from) {
            if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else {
                this.elementFactory.updateElementId(this.step, (String) value);
                step.setId((String) value);
            }
        } else if (row == from + 1) {
            try {
                step.setStartLimit(Integer.valueOf((String) value));
            } catch (NumberFormatException e) {
            }
        } else if (row == from + 2) {
            step.setAllowStartIfComplete((Boolean) value);
        } else if (row == from + 3) {
//             step.setIsAbstract((Boolean)value);
//        } else if (row == from+4) {
//            step.setParentElement((String)value);
//        } else if (row == from+5) {
            step.setListeners((Listeners) value);
        } else if (row == from + 4) {
            step.setProperties((Properties) value);
        }
        from = 5;
        if (step.isChunkOriented()) {
            if (row == from) {
                step.getChunk().setCheckpoint_policy((String) value);
                if (!((String) value).equals("custom"))
                    step.getChunk().setCheckpointAlgorithm(null); // if checkpoint policy is different then "custom" then set CheckpoinAlgorithm to null
                setEditors();
                setRenderers();
                fireTableDataChanged();
            }///  CHeckpoint Algorithm added/or not
            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom"))) {
                from += 1;
                if (row == from) {
                    step.getChunk().setCheckpointAlgorithm((CheckpointAlgorithm) value);
                }
            }
            if (row == from + 1) {
                try {
                    if (value == null || ((String) value).equals("")) step.getChunk().setItem_count(10);
                    else step.getChunk().setItem_count(Integer.valueOf((String) value));
                } catch (NumberFormatException e) {
                }
            } else if (row == from + 2) {
                try {
                    if (value == null || ((String) value).equals("")) step.getChunk().setTime_limit(0);
                    else step.getChunk().setTime_limit(Integer.valueOf((String) value));
                } catch (NumberFormatException e) {
                }
            } else if (row == from + 3) {
                try {
                    if (value == null || ((String) value).equals("")) step.getChunk().setSkip_limit(null);
                    else step.getChunk().setSkip_limit(Integer.valueOf((String) value));
                } catch (NumberFormatException e) {
                }
            } else if (row == from + 4) {
                try {
                    if (value == null || ((String) value).equals("")) step.getChunk().setRetry_limit(null);
                    else step.getChunk().setRetry_limit(Integer.valueOf((String) value));
                } catch (NumberFormatException e) {
                }
            } else if (row == from + 5) {
                step.getChunk().setSkippableExceptions((ExceptionClasses) value);
            } else if (row == from + 6) {
                step.getChunk().setRetryableExceptions((ExceptionClasses) value);
            } else if (row == from + 7) {
                step.getChunk().setNoRollbackExceptions((ExceptionClasses) value);
            }
            from += 8;
        }
        if (row == from) {
            return; // Partition Category
        } else if (row == from + 1) {
            step.setPartitionEnabled((Boolean) value);
            if (step.getPartition() == null) step.setPartition(new Partition());
            if (definition.getElementSpec(step.getId()) == null) {
                definition.createElementSpecification(step);
            }
            definition.getElementSpec(step.getId()).setPartitionEnabled((Boolean) value);
            if ((Boolean) value == false) step.setPartition(null); // Remove Partition From stepElement
            fireTableDataChanged();
            this.setEditors();
            this.setRenderers();
        }
        from += 2;
        if (step.isPartitionEnabled()) {
            if (row == from) {
                step.getPartition().setIsPartitionPlanDefinedAtRuntime((Boolean) value);
                if ((Boolean) value == false) {
                    step.removePartitionMapper();
                    step.getPartitionPlanFromBackup();
                } else {
                    step.removePartitionPlan();
                    step.getPartitionMapperFromBackup();
                }
                setEditors();
                setRenderers();
                fireTableDataChanged();
            } else if (row == from + 1) {
                if (step.getPartition().isPartitionPlanDefinedAtRuntime()) {
                    if (this.step.getPartition().getPartitionMapper() == null)
                        step.getPartition().setPartitionMapper(new PartitionMapper());
                    if (value == null || ((String) value).equals("")) this.step.getPartition().setPartitionMapper(null);
                    else this.step.getPartition().getPartitionMapper().setRef((String) value);
                } else {
                    if (this.step.getPartition().getPartitionPlan() == null)
                        step.getPartition().setPartitionPlan(new PartitionPlan());
                    try {
                        this.step.getPartition().getPartitionPlan().setThreadsNumber(Integer.valueOf((String) value));
                    } catch (NumberFormatException e) {
                    }
                }
            } else if (row == from + 2) {
                if (step.getPartition().isPartitionPlanDefinedAtRuntime()) {
                    if (this.step.getPartition().getPartitionMapper() == null)
                        step.getPartition().setPartitionMapper(new PartitionMapper());
                    this.step.getPartition().getPartitionMapper().setProperties((Properties) value);
                } else {
                    if (step.getPartition().getPartitionPlan() == null)
                        step.getPartition().setPartitionPlan(new PartitionPlan());
                    this.step.getPartition().getPartitionPlan().setProperties((List<Properties>) value);
                    int size = 0;
                    if (((List<Properties>) value) != null) size = ((List<Properties>) value).size();
                    this.step.getPartition().getPartitionPlan().setPartitionsNumber(size);
                }
            } else if (row == from + 3) {
                if (step.getPartition().getPartitionReducer() == null)
                    step.getPartition().setPartitionReducer(new PartitionReducer());
                if (value == null || ((String) value).equals("")) step.getPartition().setPartitionReducer(null);
                else step.getPartition().getPartitionReducer().setRef((String) value);
            } else if (row == from + 4) {
                if (step.getPartition().getPartitionReducer() == null)
                    step.getPartition().setPartitionReducer(new PartitionReducer());
                step.getPartition().getPartitionReducer().setProperties((Properties) value);
            } else if (row == from + 5) {
                if (step.getPartition().getPartitionCollector() == null)
                    step.getPartition().setPartitionCollector(new PartitionCollector());
                if (value == null || ((String) value).equals("")) step.getPartition().setPartitionCollector(null);
                else step.getPartition().getPartitionCollector().setRef((String) value);
            } else if (row == from + 6) {
                if (step.getPartition().getPartitionCollector() == null)
                    step.getPartition().setPartitionCollector(new PartitionCollector());
                step.getPartition().getPartitionCollector().setProperties((Properties) value);
            } else if (row == from + 7) {
                if (step.getPartition().getPartitionAnalyzer() == null)
                    step.getPartition().setPartitionAnalyzer(new PartitionAnalyzer());
                if (value == null || ((String) value).equals("")) step.getPartition().setPartitionAnalyzer(null);
                else step.getPartition().getPartitionAnalyzer().setRef((String) value);
            } else if (row == from + 8) {
                if (step.getPartition().getPartitionAnalyzer() == null)
                    step.getPartition().setPartitionAnalyzer(new PartitionAnalyzer());
                step.getPartition().getPartitionAnalyzer().setProperties((Properties) value);
            }
            from += 9;
        }
        if (step.isChunkOriented()) {
            if (row == from) {
                return; // READER CATEGORY
            } else if (row == from + 1) {
                if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                    showIdCollisionWarning();
                else step.getChunk().getReader().setRef((String) value);
            } else if (row == from + 2) {
                step.getChunk().getReader().setProperties((Properties) value);
            } else if (row == from + 3) {
                return; // PROCESSOR CATEGORY
            } else if (row == from + 4) {
                if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                    showIdCollisionWarning();
                else step.getChunk().setProcessorRef((String) value);
            } else if (row == from + 5) {
                step.getChunk().getProcessor().setProperties((Properties) value);
            } else if (row == from + 6) {
                return; // WRITER CATEGORY
            } else if (row == from + 7) {
                if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                    showIdCollisionWarning();
                else step.getChunk().getWriter().setRef((String) value);
            } else if (row == from + 8) {
                step.getChunk().getWriter().setProperties((Properties) value);
            }
        } else {
            if (row == from) {
                return; // BATCHLET CATEGORY
            } else if (row == from + 1) {
                if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                    showIdCollisionWarning();
                else step.getBatchlet().setRef((String) value);
            } else if (row == from + 2) {
                step.getBatchlet().setProperties((Properties) value);
            }
        }
        fireTableCellUpdated(row, col);
        this.jslCanvas.fireJobDiagramChange();
    }

    public boolean isCellEditable(int row, int col) {
        if (step.isChunkOriented()) {
            int shift = 0;
            if ((step.getChunk().getCheckpoint_policy() != null) && (step.getChunk().getCheckpoint_policy().equals("custom"))) {
                shift += 1;
            }
            if (step.isPartitionEnabled()) {
                if (row == 13 + shift) return false;//Partition Category
                if (row == 24 + shift) return false;//Reader Category
                if (row == 27 + shift) return false;//Processor Category
                if (row == 30 + shift) return false;//Writer Category
            } else {
                if (row == 13 + shift) return false;//Partition Category
                if (row == 15 + shift) return false;//Reader Category
                if (row == 18 + shift) return false;//Processor Category
                if (row == 21 + shift) return false;//Writer Category
            }
        } else {// TASK-ORIENTED
            if (step.isPartitionEnabled()) {
                if (row == 5) return false;//Partition Category
                if (row == 16) return false;//Batchlet Category
            } else {
                if (row == 5) return false;//Partition Category
                if (row == 7) return false;//Batchlet Category
            }
        }

        if (1 == col) {
            return true;
        } else {
            return false;
        }
    }

    private void showIdCollisionWarning() {
        JOptionPane.showMessageDialog(this.propertyTable,
                "This identifier is already used in element " + this.idCollisionIn[0] + "!",
                "Identifier conflict",
                JOptionPane.WARNING_MESSAGE);
    }
}
