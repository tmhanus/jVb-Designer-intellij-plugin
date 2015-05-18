package designer.ui.editor;

import codeGeneration.xml.JobFileGenerator;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import designer.ui.editor.element.*;
import designer.ui.properties.core.notifier.SelectionChangedNotifier;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import specification.*;
import specification.definitions.AdditionalElement;
import specification.definitions.Definition;
import specification.definitions.ElementSpec;

import javax.swing.*;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanusas on 25.01.15.
 */
public class JSLCanvas extends JPanel {
    /**
     * Reference for instance of this class for subclases
     */
    private JSLCanvas thisCanvas = this;

    /**
     * Actual Project
     */
    private Project project;

    /**
     * .jsl file opened by this graphic editor
     */
    private VirtualFile virtualFile;

    private JSLFileGraphicEditor jslFileGraphicEditor;

    /**
     * Toolbar reference
     */
    private CanvasToolbar canvasToolbar;

    /**
     * Factory and store for all edges
     */
    private EdgeFactory edgeFactory;

    /**
     * Factory and store for all elements and root element
     */
    private ElementFactory elementFactory;

    /**
     * Stores additional info for elements/edges
     */
    private Definition diagramDefinition;

    /**
     * List of actually selected elements
     */
    private List<Element> selection;
    private Point point;

    /**
     * Displacement of canvas
     */
    private Point displacement; // Store displacement number in both axis
    private int zoom = 100;

    /**
     * Indicates canvas shifting
     */
    private Boolean dragging;

    /**
     * Temporary store for new edge
     */
    private Edge newEdge;

    /**
     * Stores information if jslFileIsValid
     */
    private boolean isJslFileValid;

    /**
     * Stores information about wheter next references are valid or no
     */
    private boolean areReferencesValid;

    /**
     * List of designer Contexts
     */
    private List<DesignerContext> designerContexts;

    /**
     * Actually active designer context
     */
    private DesignerContext activeContext;

    private DataFlavor dataFlavor;

    /**
     * Constructor
     *
     * @param project
     * @param jslFileGraphicEditor
     */
    public JSLCanvas(Project project, JSLFileGraphicEditor jslFileGraphicEditor) {
        super();
        this.jslFileGraphicEditor = jslFileGraphicEditor;
        this.virtualFile = jslFileGraphicEditor.getFile();

        this.selection = new ArrayList<Element>();
        this.isJslFileValid = false;
        this.dataFlavor = new DataFlavor(String.class, String.class.getSimpleName());
        this.edgeFactory = new EdgeFactory();
        this.elementFactory = new ElementFactory();
        this.project = project;
        this.designerContexts = new ArrayList<DesignerContext>();
        this.setLayout(null);
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
        this.displacement = new Point(0, 0);
        //this.addMouseWheelListener(new MouseWheelHandler());

        new CanvasDropTargetListener(this);

        ToolTipManager.sharedInstance().registerComponent(this);

        repaint();
    }

    /**
     * Notify JslFileGraphicEditor about diagram change.
     */
    public void fireJobDiagramChange() {
        this.jslFileGraphicEditor.jobDiagramChanged();
    }

    /**
     * @return if jslFile is valid to schema and all references are valid it returns true
     */
    public boolean isDiagramValid() {
        return this.isJslFileValid && this.areReferencesValid;
    }

    /**
     * This method deselects all selected elements previously selected for dragging.
     *
     * @param lElements
     */
    public void deselectAllForDragging(List<Element> lElements) {
        for (Element element : lElements) {
            if (element.isSelectedForDragging()) {
                element.deselectForDragging();
            }
        }
    }

    /**
     * Deselect all selected elements.
     *
     * @param elementsToDeselect
     */
    public void deselectAllElements(List<Element> elementsToDeselect) {
        for (Element element : elementsToDeselect) {
            if (element.isSelected())
                element.deselect();
        }
    }

    /**
     * Deselect all selected edges.
     *
     * @param edgesToDeselect - edges to deselect
     */
    public void deselectAllEdges(List<Edge> edgesToDeselect) {
        if (edgesToDeselect == null) return;
        for (Edge edge : edgesToDeselect) {
            edge.deselectEdge();
        }
    }

    public void setCanvasToolbar(CanvasToolbar canvasToolbar) {
        this.canvasToolbar = canvasToolbar;
    }

    // **************************************************************************************
    // ****************************** MOUSE & MOUSE MOTION **********************************
    // **************************************************************************************

    /**
     * Sub class to handle mouse events such as MouseReleased, MouseClicked or MousePressed
     */
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragging) setCursor(Cursor.getDefaultCursor());
            if (dragging == false) {
                for (Element element : elementFactory.getElements()) {
                    if (element.isSelectedForDragging() && activeContext.belongsToContext(element)) { // Finds selected element and belongs to active context
                        for (Element stableElement : elementFactory.getElements()) { // Finds element which is under the cursor at time of Mouse Release
                            if ((stableElement.containsCursor(e.getPoint())) && (element != stableElement) && (activeContext.belongsToContext(stableElement))) { // Compare those two elements whether they are different and belongs to active context
                                if (element.isBetweenParents(stableElement)) continue;
                                if (elementFactory.canAddChildToParent(stableElement, element)) {
                                    if (stableElement instanceof Start) {
                                        elementFactory.makeElementFirst(element);
                                        fireJobDiagramChange();
                                    }
                                    if (!(stableElement instanceof SplitElement)) {
//                                         These Transition end elements are not added as Next Transitions
                                        stableElement.addChild(element);
                                        // If existing element is dropped on SplitEnd element, it has to add child also to SplitElement
                                        if (stableElement instanceof SplitElementEnd) {
                                            elementFactory.getSplitPairElement(stableElement).addChild(element);
                                        } else if (stableElement instanceof Decision || stableElement instanceof Step || stableElement instanceof Flow) {
                                            if (element instanceof StopTransition)
                                                ((StopTransition) element).addStopForElement(stableElement);
                                            else if (element instanceof EndTransition)
                                                ((EndTransition) element).addEndForElement(stableElement);
                                            else if (element instanceof FailTransition)
                                                ((FailTransition) element).addFailForElement(stableElement);
                                            else {
                                                if (stableElement instanceof Decision)
                                                    ((Decision) stableElement).createNextTransition(null, element.getId());
                                            }
                                        }
                                        element.addParent(stableElement);
                                        element.placeUnderElement(stableElement);
                                        if (element instanceof Split) {
                                            elementFactory.getSplitPairElement(element).setPosition(new Point(element.getPosition().x, element.getPosition().y + 200));
                                        }
                                        edgeFactory.createEdge(stableElement, element, activeContext);
                                        fireJobDiagramChange();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            dragging = false;
            for (Element tmpElement : elementFactory.getElements()) tmpElement.disableDrop();
            e.getComponent().repaint();
        }

        @Override
        public void mouseClicked(final MouseEvent e) {

            // ***************************************************************
            // *                     RIGHT MOUSE BUTTON                      *
            // ***************************************************************
            if (SwingUtilities.isRightMouseButton(e)) {

                final Element clickedElement = elementFactory.getElementUnderCursor(e.getPoint(), activeContext);
                final Edge clickedEdge = edgeFactory.getEdgeUnderCursor(e.getPoint(), activeContext);
                if (clickedElement != null) {
                    JPopupMenu menu = new JPopupMenu("ElementRMBPopup");

                    JMenuItem createEdge = new JMenuItem("Create Edge");
                    final Element finalTmpElement = clickedElement;
                    createEdge.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            newEdge = new Edge(finalTmpElement, e.getPoint());
                        }
                    });
                    JMenuItem delete = new JMenuItem("Delete Element");
                    delete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JLabel label = new JLabel("Are you sure you want to delete element?");
                            int result = JOptionPane.showConfirmDialog(null,
                                    "Are you sure?", "Element delete confirmation", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                edgeFactory.removeEdgesConnectedTo(clickedElement);
                                if (clickedElement instanceof Flow) {
                                    if (getDesignerContext(clickedElement.getId()) != null) {
                                        List<Element> flowSubElements = elementFactory.getAllFlowSubElements((Flow) clickedElement);
                                        if (flowSubElements != null) {
                                            for (Element element : flowSubElements) {
                                                if (element instanceof Flow && getDesignerContext(element.getId()) != null)
                                                    designerContexts.remove(getDesignerContext(element.getId()));
                                                edgeFactory.removeEdgesConnectedTo(element);
                                            }
                                        }
                                        designerContexts.remove(getDesignerContext(clickedElement.getId()));
                                        actualizeCanvasToolbar();
                                    }
                                }
                                elementFactory.removeElement(clickedElement);
                                if ((clickedElement instanceof SplitElement) || (clickedElement instanceof SplitElementEnd)) {
                                    Element splitPairElement = elementFactory.getSplitPairElement(clickedElement);

                                    elementFactory.removeElement(splitPairElement);
                                    edgeFactory.removeEdgesConnectedTo(splitPairElement);
                                    Split split;
                                    if (clickedElement instanceof Split) {
                                        split = (Split) clickedElement;
                                    } else split = (Split) splitPairElement;
                                    if (split.getElements() != null) {
                                        for (Element insideSplitFlow : split.getElements()) {
                                            List<Element> flowSubElements = elementFactory.getAllFlowSubElements((Flow) insideSplitFlow);
                                            if (flowSubElements != null) {
                                                for (Element element : flowSubElements) {
                                                    if (element instanceof Flow && getDesignerContext(element.getId()) != null)
                                                        designerContexts.remove(getDesignerContext(element.getId()));
                                                    edgeFactory.removeEdgesConnectedTo(element);
                                                }
                                            }
                                            edgeFactory.removeEdgesConnectedTo(insideSplitFlow);
                                            if (getDesignerContext(insideSplitFlow.getId()) != null)
                                                designerContexts.remove(getDesignerContext(insideSplitFlow.getId()));
                                            actualizeCanvasToolbar();
                                        }
                                    }

                                }
                                fireJobDiagramChange();
                                repaint();
                            }

                        }
                    });
                    menu.add(createEdge);
                    menu.addSeparator();
                    menu.add(delete);
                    menu.show(getOuter(), e.getX(), e.getY());
                }

                if (clickedEdge != null && clickedElement == null) {
                    JPopupMenu menu = new JPopupMenu("EdgeRMBPopup");
                    JMenuItem delete = new JMenuItem("Delete Edge");
                    final Edge finalTmpEdge = clickedEdge;
                    delete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            edgeFactory.removeEdge(finalTmpEdge);
                            if (finalTmpEdge.getSuccessor() instanceof StopTransition)
                                ((StopTransition) finalTmpEdge.getSuccessor()).removeStopForElement(finalTmpEdge.getPredecessor());
                            if (finalTmpEdge.getSuccessor() instanceof EndTransition)
                                ((EndTransition) finalTmpEdge.getSuccessor()).removeEndForElement(finalTmpEdge.getPredecessor());
                            if (finalTmpEdge.getSuccessor() instanceof FailTransition)
                                ((FailTransition) finalTmpEdge.getSuccessor()).removeFailForElement(finalTmpEdge.getPredecessor());

                            // if deleted edge connects Split and inner parallel elements
                            if (finalTmpEdge.getPredecessor() instanceof SplitElement) {
                                edgeFactory.removeEdgesConnectedTo(finalTmpEdge.getSuccessor());
                                ((Split) finalTmpEdge.getPredecessor()).removeElement(finalTmpEdge.getSuccessor());
                                if (finalTmpEdge.getPredecessor().getDesignerContext().getContextId().equals(elementFactory.getRootElement().getId()))
                                    elementFactory.getRootElement().addElement(finalTmpEdge.getSuccessor());
                                else {
                                    Element rootContextElement = elementFactory.getRootContextElement(activeContext.getContextId());
                                    if (rootContextElement != null && rootContextElement instanceof Flow)
                                        ((Flow) rootContextElement).addElement(finalTmpEdge.getSuccessor());
                                }
                                ((Flow) finalTmpEdge.getSuccessor()).setIsInsideOfSplit(false);
                            } //if deleted edge connects SplitEnd and innerSplit parallel elements
                            else if (finalTmpEdge.getSuccessor() instanceof SplitElementEnd) {
                                edgeFactory.removeEdgesConnectedTo(finalTmpEdge.getPredecessor());
                                ((Split) elementFactory.getSplitPairElement(finalTmpEdge.getSuccessor())).removeElement(finalTmpEdge.getPredecessor());
                                elementFactory.getRootElement().addElement(finalTmpEdge.getPredecessor());
                                ((Flow) finalTmpEdge.getPredecessor()).setIsInsideOfSplit(false);

                            } else if (finalTmpEdge.getPredecessor() instanceof SplitElementEnd) {
                                ((Split) elementFactory.getSplitPairElement(finalTmpEdge.getPredecessor())).setNextElementId(null);
                                elementFactory.getSplitPairElement(finalTmpEdge.getPredecessor()).removeChild(finalTmpEdge.getSuccessor());
                                finalTmpEdge.getSuccessor().removeParent(elementFactory.getSplitPairElement((finalTmpEdge.getPredecessor())));
                            }
                            fireJobDiagramChange();
                        }
                    });
                    menu.add(delete);
                    menu.show(getOuter(), e.getX(), e.getY());
                }
                if (clickedEdge == null && clickedElement == null) {
                    JPopupMenu menu = new JPopupMenu("JobRMBPopup");
                    JMenuItem generate = new JMenuItem("Generate Job File");
                    generate.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JobFileGenerator jobFileGenerator = new JobFileGenerator(project);

                            jobFileGenerator.generate(virtualFile, elementFactory.getRootElement().getId());
                        }
                    });
                    menu.add(generate);
                    menu.show(getOuter(), e.getX(), e.getY());
                }

            }
            // ***************************************************************
            // *                      LEFT MOUSE BUTTON                      *
            // ***************************************************************

            if (SwingUtilities.isLeftMouseButton(e)) {

                Element clickedElement = elementFactory.getElementUnderCursor(e.getPoint(), activeContext);
                /// UNHIGHLIGHT ALL EDGES and then Highlight clicked edge
                for (Edge edge : edgeFactory.getEdges()) edge.unhighlightEdge();

                Edge clickedEdge = edgeFactory.getEdgeUnderCursor(e.getPoint(), activeContext);

                deselectAllElements(elementFactory.getElements());
                deselectAllEdges(edgeFactory.getEdges());
                if (clickedEdge != null) clickedEdge.selectEdge();

                // ***************************************************************
                // *         Double Left Click --> designer ContextChange        *
                // ***************************************************************
                if (e.getClickCount() == 2) {
                    if ((clickedElement != null) && (clickedElement instanceof Flow)) {
                        changeContext(clickedElement.getId());
                        actualizeCanvasToolbar();
                    }
                }
                // ***************************************************************
                // *         Single Left Click --> Selection                     *
                // ***************************************************************

                SelectionChangedNotifier publisher = project.getMessageBus().syncPublisher(SelectionChangedNotifier.CHANGE_ACTION_TOPIC);

                if (clickedEdge == null && clickedElement == null)
                    publisher.selectionChanged(elementFactory.getRootElement(), thisCanvas);

                if (clickedEdge != null)
                    publisher.selectionChanged(clickedEdge, thisCanvas);

                if (clickedElement != null) {
                    selection.clear();

                    if (clickedElement instanceof SplitElementEnd)
                        selection.add(elementFactory.getSplitPairElement(clickedElement));
                    else selection.add(clickedElement);

                    publisher = project.getMessageBus().syncPublisher(SelectionChangedNotifier.CHANGE_ACTION_TOPIC);

                    if (clickedElement != null) {
                        if (clickedElement instanceof SplitElementEnd)
                            publisher.selectionChanged(elementFactory.getSplitPairElement(clickedElement), thisCanvas);
                        publisher.selectionChanged(clickedElement, thisCanvas);
                    }

                    // Creating edge from context popup menu of the edge
                    // Element can't create edge to himself
                    if ((newEdge != null) && elementFactory.canAddChildToParent(newEdge.getPredecessor(), clickedElement)) {
                        if (newEdge.getPredecessor() instanceof Start) {
                            elementFactory.makeElementFirst(clickedElement);
                        }
                        edgeFactory.createEdge(newEdge.getPredecessor(), clickedElement, activeContext);
                        if (newEdge.getPredecessor() instanceof SplitElementEnd) {
                            elementFactory.getSplitPairElement(newEdge.getPredecessor()).addChild(clickedElement);
                        }
                        //if predecessor is Split ... then do not add child to split,
                        if (!(newEdge.getPredecessor() instanceof Split) && !(newEdge.getPredecessor() instanceof SplitElementEnd)) {
                            newEdge.getPredecessor().addChild(clickedElement);
                        }
                        if (newEdge.getPredecessor() instanceof Split) {
                            if (clickedElement instanceof Flow)
                                ((Flow) clickedElement).setIsInsideOfSplit(true);
                            if (newEdge.getPredecessor().getDesignerContext().getContextId().equals(elementFactory.getRootElement().getId()))
                                elementFactory.getRootElement().removeElement(clickedElement);
                            else {
                                Element rootContextElement = elementFactory.getRootContextElement(newEdge.getPredecessor().getDesignerContext().getContextId());
                                if (rootContextElement != null && rootContextElement instanceof Flow)
                                    ((Flow) rootContextElement).removeElement(clickedElement);
                            }
                            ((Split) newEdge.getPredecessor()).addElement(clickedElement);
                            edgeFactory.createEdge(clickedElement, elementFactory.getSplitPairElement(newEdge.getPredecessor()), activeContext);
                        }
                        if (newEdge.getPredecessor() instanceof Decision || newEdge.getPredecessor() instanceof Step || newEdge.getPredecessor() instanceof Flow) {
                            if (clickedElement instanceof StopTransition && newEdge.getPredecessor().canAddTransitionElement(clickedElement))
                                ((StopTransition) clickedElement).addStopForElement(newEdge.getPredecessor());
                            else if (clickedElement instanceof EndTransition && newEdge.getPredecessor().canAddTransitionElement(clickedElement))
                                ((EndTransition) clickedElement).addEndForElement(newEdge.getPredecessor());
                            else if (clickedElement instanceof FailTransition && newEdge.getPredecessor().canAddTransitionElement(clickedElement))
                                ((FailTransition) clickedElement).addFailForElement(newEdge.getPredecessor());
                            else {
                                if (newEdge.getPredecessor() instanceof Decision) {
                                    ((Decision) newEdge.getPredecessor()).createNextTransition(null, clickedElement.getId());
                                }
                            }
                        }
                        fireJobDiagramChange();
                    } else {
                        deselectAllElements(elementFactory.getElements());
                        deselectAllEdges(edgeFactory.getEdges());
                        if (clickedElement != null) clickedElement.select();
                    }
                }
                newEdge = null;
                repaint();
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            deselectAllForDragging(elementFactory.getElements());
            deselectAllElements(elementFactory.getElements());
            deselectAllEdges(edgeFactory.getEdges());
            point = e.getPoint();
            // if there is element under cursor, find one
            Element pressedElement = elementFactory.getElementUnderCursor(e.getPoint(), activeContext);

            if (pressedElement != null)
                pressedElement.selectForDragging();

            if (pressedElement != null) {
                dragging = false;
            } else {
                deselectAllForDragging(elementFactory.getElements());
                dragging = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            e.getComponent().repaint();
        }

    }

    /**
     * Sub class to handle mouse events such as MouseDragged or MouseMoved
     */
    private class MouseMotionHandler extends MouseMotionAdapter {

        Point tmpPoint = new Point();

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!dragging) { //Moving Element(s)
                tmpPoint.setLocation(
                        e.getX() - point.x,
                        e.getY() - point.y);
                for (Element element : elementFactory.getElements()) {
                    if ((element.isSelectedForDragging()) && (activeContext.belongsToContext(element))) {
                        element.updatePosition(tmpPoint);
                        if (element instanceof Split) {
                            elementFactory.getSplitPairElement(element).updatePosition(tmpPoint);
                            if (((Split) element).getElements() != null) {
                                for (Element insideSplitElement : ((Split) element).getElements()) {
                                    insideSplitElement.updatePosition(tmpPoint);
                                }
                            }
                        } else if (element instanceof SplitElementEnd) {
                            elementFactory.getSplitPairElement(element).updatePosition(new Point(tmpPoint.getLocation().x, 0));
                        }
                        for (Edge edg : edgeFactory.getEdges()) edg.recalcEdge();
                        for (Element possibleParent : elementFactory.getElements()) {
                            if (elementFactory.canAddChildToParent(possibleParent, element))
                                possibleParent.enableDrop();
                            else possibleParent.disableDrop();
                        }
                        repaint();
                    }
                }
                point = e.getPoint();
            } else {// Moving canvas
                displacement.setLocation(
                        e.getX() - point.x,
                        e.getY() - point.y);
                for (Element element : elementFactory.getElements()) {
                    element.updatePosition(displacement);
                    for (Edge edg : edgeFactory.getEdges()) edg.recalcEdge();
                }

                point = e.getPoint();
            }
            e.getComponent().repaint();
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (newEdge != null) {
                newEdge.setChildPoint(mouseEvent.getPoint());
            }
            if (edgeFactory.getEdges() != null) {
                for (Edge edge : edgeFactory.getEdges()) {
                    edge.unhighlightEdge();
                }
                Edge edgeUnderCursor = edgeFactory.getEdgeUnderCursor(mouseEvent.getPoint(), activeContext);
                Element elementUnderCursor = elementFactory.getElementUnderCursor(mouseEvent.getPoint(), activeContext);
                if (edgeUnderCursor != null && elementUnderCursor == null) edgeUnderCursor.highlightEdge();
            }

            //Element elementUnderCursor = elementFactory.getElementUnderCursor(mouseEvent.getPoint(), activeContext);

            repaint();
        }
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        return new Point(e.getPoint());
    }

    /**
     * Set ToolTip text to name of element/edge actually under mouse cursor
     *
     * @param e
     * @return
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        if (elementFactory.getElementUnderCursor(e.getPoint(), activeContext) != null)
            return elementFactory.getElementUnderCursor(e.getPoint(), activeContext).getId();
        return null;
    }

    // **************************************************************************************
    // ****************************** MOUSE WHEEL - ZOOM IN/OUT *****************************
    // **************************************************************************************

    /**
     * Sub class to handle mouse events such as MouseWheelMoved
     */
    private class MouseWheelHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() < 0) {
                if (zoom < 100) {
                    zoom += 20;
                    for (Element element : elementFactory.getElements()) {
                        element.updateZoom(zoom);
                    }
                    for (Edge edge : edgeFactory.getEdges()) {
                        edge.recalcEdge();
                    }
                    repaint();
                }
            } else {
                if (zoom > 60) {
                    zoom -= 20;
                    for (Element element : elementFactory.getElements()) {
                        element.updateZoom(zoom);
                    }
                    for (Edge edge : edgeFactory.getEdges()) {
                        edge.recalcEdge();
                    }
                    repaint();
                }

            }
        }
    }

    // **************************************************************************************
    // ************************************* PAINT ******************************************
    // **************************************************************************************

    /**
     * Override method draw() of JPanel. At first draws all edges and then all elements together with highlighting marked elements
     *
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (newEdge != null) newEdge.draw(g);
        if (edgeFactory.getEdges() != null) {
            for (Edge edge : edgeFactory.getEdges()) {
                if (activeContext.belongsToContext(edge)) edge.draw(g);
            }
        }
        for (Element element : elementFactory.getElements()) {
            DesignerContext elementDesignerContext = element.getDesignerContext();
            if (elementDesignerContext != null) {
                String elementContextId = elementDesignerContext.getContextId();
                String activeContextId = this.activeContext.getContextId();
                if (elementContextId.equals(activeContextId)) {
                    if (element.isSelected()) element.highlightSelected(g);
                    element.draw(g);
                    if (element.isDropEnabled()) element.highlightHelp(g);
                }
            }
        }
    }

    // **************************************************************************************
    // ********************************* DRAG AND DROP **************************************
    // **************************************************************************************

    /**
     * Sub class that implements dropTargetListener and handles DnD gesture of creating new Elements from Palette
     */
    private class CanvasDropTargetListener extends DropTargetAdapter implements
            DropTargetListener {
        private DropTarget dropTarget;
        private JPanel panel;

        public CanvasDropTargetListener(JPanel panel) {
            this.panel = panel;

            dropTarget = new DropTarget(panel, DnDConstants.ACTION_COPY, this, true, null);
        }

        public void dragOver(DropTargetDragEvent event) {
            try {
                Transferable tr = event.getTransferable();
                String txt = (String) tr.getTransferData(dataFlavor);
                for (Element element : elementFactory.getElements()) {
                    if (activeContext.belongsToContext(element)) {
                        if (element.canAdd(txt)) element.enableDrop();
                        else element.disableDrop();
                    }
                }
            } catch (UnsupportedFlavorException e) {
                //e.printStackTrace();
            } catch (IOException e) {
            }
            repaint();
        }

        public Element getDropElement(Point p) {
            List<Element> elements = elementFactory.getElements();
            if (elements != null) {
                for (Element e : elements) {
                    if (e.canDrop(p) && e.getDesignerContext().getContextId().equals(activeContext.getContextId())) {
                        return e;
                    }
                }
            }
            return null;
        }

        public void drop(DropTargetDropEvent event) {
            Point point = event.getLocation();
            try {
                Transferable tr = event.getTransferable();
                String transferedElementType = (String) tr.getTransferData(dataFlavor);
                if (event.isDataFlavorSupported(dataFlavor)) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Element dropAreaElement = getDropElement(point);

                    Element newElement = null;

                    if (dropAreaElement != null) {
                        // Add element to specific context ... to specific Flow element or to Root Job Element
                        if (dropAreaElement.canAdd(transferedElementType) == true) {
                            newElement = elementFactory.createElement(transferedElementType, dropAreaElement, activeContext);
                            if (dropAreaElement instanceof SplitElement && transferedElementType.equals("Flow"))
                                ((Flow) newElement).setIsInsideOfSplit(true);
                            if (newElement != null) {
                                //edgeFactory.createEdge(dropAreaElement, newElement, activeContext);
                                edgeFactory.createEdge(dropAreaElement, newElement, activeContext);
                                if (dropAreaElement instanceof SplitElementEnd) {
                                    elementFactory.getSplitPairElement(dropAreaElement).addChild(newElement);
                                }
                            }
                        }
                    } else {
                        newElement = elementFactory.createElement(transferedElementType, dropAreaElement, activeContext);
                        if (newElement != null)
                            newElement.setPosition(event.getLocation());
                    }
                    //else If newElement was Created, add it to activeContext
                    if (newElement != null) {
                        if (elementFactory.getRootElement().getId().equals(activeContext.getContextId())) {
                            // If adding flow to split, then remove it from elementFactory.getRootElement() List of elements
                            if (dropAreaElement instanceof SplitElement) {
                                edgeFactory.createEdge(newElement, elementFactory.getSplitPairElement(dropAreaElement), activeContext);
                                elementFactory.getRootElement().removeElement(newElement);
                                ((Split) dropAreaElement).addElement(newElement);
                            } else if ((!(newElement instanceof Start)) && !(newElement instanceof StopTransition) && !(newElement instanceof EndTransition) && !(newElement instanceof FailTransition))
                                elementFactory.getRootElement().addElement(newElement);
                        } else {
                            Flow flow = (Flow) elementFactory.getRootContextElement(activeContext.getContextId());
                            if (dropAreaElement instanceof SplitElement) {
                                edgeFactory.createEdge(newElement, elementFactory.getSplitPairElement(dropAreaElement), activeContext);
                                flow.removeElement(newElement);
                                ((Split) dropAreaElement).addElement(newElement);
                            } else if ((!(newElement instanceof Start)) && !(newElement instanceof StopTransition) && !(newElement instanceof EndTransition) && !(newElement instanceof FailTransition))
                                flow.addElement(newElement);
                        }

                        if (dropAreaElement != null && dropAreaElement instanceof Start) {
                            elementFactory.makeElementFirst(newElement);
                        }

                        if (newElement instanceof Split) {
                            elementFactory.createSplitPairElement(newElement);
                        }
                        newElement.updateZoom(zoom);
                    }
                    if (edgeFactory.getEdges() != null)
                        for (Edge edg : edgeFactory.getEdges()) edg.recalcEdge();
                    for (Element element : elementFactory.getElements()) {
                        element.disableDrop();
                    }
                    repaint();

                    event.dropComplete(true);
                    this.panel.validate();
                    fireJobDiagramChange();
                    return;
                }
                for (Edge edg : edgeFactory.getEdges()) edg.recalcEdge();

                repaint();

                event.rejectDrop();

            } catch (Exception e) {
//                e.printStackTrace();
                event.rejectDrop();
            }
        }
    }

    public ElementFactory getElementFactory() {
        return this.elementFactory;
    }

    public EdgeFactory getEdgeFactory() {
        return this.edgeFactory;
    }

    public Job getRootElement() {
        return elementFactory.getRootElement();
    }

    /**
     * Returns instance of this class
     *
     * @return
     */
    public JSLCanvas getOuter() {
        return this;
    }

    /**
     * Changes active designer context in order to show elements of different designer context.
     * If designer context with asociatted with ID doesn't exist, it is created
     *
     * @param id
     */
    public void changeContext(String id) {
        DesignerContext wantedContext = null;
        for (DesignerContext dc : this.designerContexts) {
            if (dc.getContextId().equals(id))
                wantedContext = dc;
        }
        if (wantedContext == null) {
            wantedContext = new DesignerContext(id, this.activeContext);
            this.designerContexts.add(wantedContext);
        }
        this.activeContext = wantedContext;
        repaint();
    }

    /**
     * Method validate .jslFile to XSD Schema
     *
     * @param virtualFile file to validate
     * @param errMsg      returns error message to caller
     * @return
     */
    public boolean checkJslFileToSchema(VirtualFile virtualFile, String errMsg[]) {
        Source jslFile = new StreamSource(new StringReader(FileDocumentManager.getInstance().getDocument(this.virtualFile).getText()));
        InputStream is = getClass().getResourceAsStream("/designer/resources/jobXML_jVbEdit.xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema jslSchema = null;
        try {
            jslSchema = factory.newSchema(new StreamSource(is));
        } catch (SAXException e) {
            e.printStackTrace();
        }

        Validator validator = jslSchema.newValidator();
        boolean isValid = false;
        try {
            validator.validate(jslFile);
            isValid = true;
        } catch (SAXParseException e) {
            errMsg[0] = new String("Line: " + e.getLineNumber() + "; " + e.getMessage());
            isValid = false;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {

        }
        return isValid;
    }

    /**
     * Method control validity of an .jsl file and then load job diagram from .jsl file
     *
     * @param job
     */
    public void loadJobDiagram(Job job) {
        String[] errMsg = new String[1];
        this.isJslFileValid = checkJslFileToSchema(virtualFile, errMsg);
        this.areReferencesValid = jslFileGraphicEditor.areNextReferencesValid(job, errMsg);
        if (!this.isJslFileValid || !this.areReferencesValid) {
            JTextArea textArea = new JTextArea(errMsg[0]);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(this,
                    "<html><body><p style='width: 450px;'>" + errMsg[0] + "</p></body></html>",
                    "JSL File Error",
                    JOptionPane.ERROR_MESSAGE);

            this.setVisible(false);
            canvasToolbar.setVisible(false);
        } else {
            this.setVisible(true);
            canvasToolbar.setVisible(true);
            elementFactory.removeAllElements();
            this.designerContexts.clear();

            try {
                loadAdditionalElements();
            } catch (Exception e) {
                System.out.println("Error while parsing .jsd file!");
            }
            if (job == null) {
                elementFactory.setRootElement(new Job("Job_01"));
                this.activeContext = new DesignerContext(elementFactory.getRootElement().getId(), null);
                this.designerContexts.add(this.activeContext);
            } else if (job != null) {
                elementFactory.setRootElement(job);
                this.activeContext = new DesignerContext(elementFactory.getRootElement().getId(), null);
                this.designerContexts.add(this.activeContext);
                if (job.getElements() != null)
                    loadElementsFromJob(job);
            }
            loadDesignerContextForAdditionalElements();
            loadEdges();
            actualizeCanvasToolbar();
        }
    }

    /**
     * Loads designer contexts for additional elements from .jsd file
     */
    public void loadDesignerContextForAdditionalElements() {
        if (diagramDefinition != null && diagramDefinition.getAdditionalElements() != null) {
            for (AdditionalElement additionalElement : diagramDefinition.getAdditionalElements()) {
                if (getDesignerContext(additionalElement.getDesignerContextid()) != null)
                    additionalElement.getElement().setDesignerContext(getDesignerContext(additionalElement.getDesignerContextid()));
            }
        }
    }

    /**
     * Informs canvasToolbar to actualize it's content
     */
    public void actualizeCanvasToolbar() {
        this.canvasToolbar.actualizeContent();

    }

    /**
     * Check if designer context already exists
     *
     * @param designerContext
     * @return
     */
    public boolean designerContextAlreadyExists(DesignerContext designerContext) {
        if (this.designerContexts == null) return false;
        for (DesignerContext dc : this.designerContexts) {
            if (dc.getContextId().equals(designerContext.getContextId()))
                return true;
        }
        return false;
    }

    /**
     * Loads elements to job diagram. Called from method loadJobDiagram()
     *
     * @param job
     */
    private void loadElementsFromJob(Job job) {
        Point[] elementWithoutSpecPoint = new Point[1];
        elementWithoutSpecPoint[0] = new Point(200, 200);
        elementFactory.setRootElement(job);

        this.activeContext = createDesignerContext(elementFactory.getRootElement().getId(), null);

        for (Element element : elementFactory.getRootElement().getElements()) {
            ElementSpec elementSpec = null;
            if (this.diagramDefinition != null)
                elementSpec = this.diagramDefinition.getElementSpec(element.getId());
            if (elementSpec != null && elementSpec.getPosition() != null) {
                element.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                if (element instanceof Step) {
                    ((Step) element).setPartitionEnabled(elementSpec.isPartitionEnabled());
                    if (elementSpec.isPartitionEnabled() && ((Step) element).getPartition() == null) {
                        elementSpec.setPartitionEnabled(false);
                        ((Step) element).setPartitionEnabled(false);
                    }
                    if (((Step) element).getPartition() != null) {
                        ((Step) element).getPartition().setIsPartitionPlanDefinedAtRuntime(elementSpec.isRuntimePlanning());
                    }
                }
            } else placeElementWithoutSpecification(element, elementWithoutSpecPoint);

            if (element instanceof Step && elementSpec == null) {
                if (((Step) element).getPartition() != null) {
                    ((Step) element).setPartitionEnabled(true);
                    if (((Step) element).getPartition().getPartitionMapper() != null)
                        ((Step) element).getPartition().setIsPartitionPlanDefinedAtRuntime(true);
                }
            }

            element.setDesignerContext(this.activeContext);
            elementFactory.addElement(element);

            if (element instanceof Split) {
                if (!elementFactory.existsSplitEndPairElement(element)) {
                    elementFactory.createSplitPairElement(element);
                } else {
                    if (elementFactory.getSplitPairElement(element).getPosition().x != element.getPosition().x)
                        elementFactory.getSplitPairElement(element).getPosition().x = element.getPosition().x;
                }
                List<Element> splitElements = ((Split) element).getElements();
                if (splitElements != null) {
                    for (Element insideSplitElement : splitElements) {
                        if (insideSplitElement instanceof Flow) {
                            elementSpec = null;
                            if (this.diagramDefinition != null)
                                elementSpec = this.diagramDefinition.getElementSpec(insideSplitElement.getId());
                            if (elementSpec != null) {
                                insideSplitElement.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                            } else {
                                placeElementWithoutSpecification(insideSplitElement, elementWithoutSpecPoint);
                            }
                            insideSplitElement.setDesignerContext(this.activeContext);
                            ((Flow) insideSplitElement).setIsInsideOfSplit(true);

                            elementFactory.addElement(insideSplitElement);

                            loadEndTransitions(insideSplitElement, elementSpec, this.activeContext, elementWithoutSpecPoint);
                            if (insideSplitElement instanceof Flow)
                                loadInsideFlowElements(insideSplitElement);
                        }
                    }
                }
            }

            if (element instanceof Decision || element instanceof Step || element instanceof Flow) {
                loadEndTransitions(element, elementSpec, activeContext, elementWithoutSpecPoint);
            }

            if (element instanceof Flow) loadInsideFlowElements(element);
        }
    }

    /**
     * Create new designer Context if it doesn't already exist
     *
     * @param contextRootElementId
     * @param parentDesignerContext
     * @return
     */
    private DesignerContext createDesignerContext(String contextRootElementId, DesignerContext parentDesignerContext) {
        DesignerContext newDesignerContext = new DesignerContext(contextRootElementId, parentDesignerContext);
        if (!designerContextAlreadyExists(newDesignerContext)) {
            this.designerContexts.add(newDesignerContext);
            actualizeCanvasToolbar();
        }
        return newDesignerContext;
    }

    /**
     * Load EndTransitionElements(Stop, End, Fail) for steps, flows and decisions if there are any
     *
     * @param element
     * @param elementSpec
     * @param context
     * @param x
     */
    private void loadEndTransitions(Element element, ElementSpec elementSpec, DesignerContext context, Point[] x) {
        if (element.getStops() != null && element.getStops().size() != 0) {
            for (Stop stop : element.getStops()) {
                if (elementFactory.getStopTransition(stop.getId()) == null) {
                    if (stop.getId() == null)
                        stop.setId(elementFactory.generateId(StopElement.TYPE));
                    StopTransition newStopTransition = new StopTransition(stop.getId(), stop.getExitStatus(), stop.getRestart());
                    if (this.diagramDefinition != null)
                        elementSpec = this.diagramDefinition.getElementSpec(newStopTransition.getId());
                    if (elementSpec != null)
                        newStopTransition.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                    else {
                        placeElementWithoutSpecification(newStopTransition, x);
                    }
                    newStopTransition.setDesignerContext(context);
                    elementFactory.addElement(newStopTransition);
                }
                elementFactory.getStopTransition(stop.getId()).addStopForElement(element, stop);
            }
        }
        if (element.getEnds() != null && element.getEnds().size() != 0) {
            for (End end : element.getEnds()) {
                if (elementFactory.getEndTransition(end.getId()) == null) {
                    if (end.getId() == null) end.setId(elementFactory.generateId(EndElement.TYPE));
                    EndTransition newEndTransition = new EndTransition(end.getId(), end.getExitStatus());
                    if (this.diagramDefinition != null)
                        elementSpec = this.diagramDefinition.getElementSpec(newEndTransition.getId());
                    if (elementSpec != null)
                        newEndTransition.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                    else {
                        placeElementWithoutSpecification(newEndTransition, x);
                    }
                    newEndTransition.setDesignerContext(context);
                    elementFactory.addElement(newEndTransition);
                }
                elementFactory.getEndTransition(end.getId()).addEndForElement(element, end);
            }
        }
        if (element.getFails() != null && element.getFails().size() != 0) {
            for (Fail fail : element.getFails()) {
                if (elementFactory.getFailTransition(fail.getId()) == null) {
                    if (fail.getId() == null) fail.setId(elementFactory.generateId(FailElement.TYPE));
                    FailTransition newFailTransition = new FailTransition(fail.getId(), fail.getExitStatus());
                    if (this.diagramDefinition != null)
                        elementSpec = this.diagramDefinition.getElementSpec(newFailTransition.getId());
                    if (elementSpec != null)
                        newFailTransition.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                    else {
                        placeElementWithoutSpecification(newFailTransition, x);
                    }
                    newFailTransition.setDesignerContext(context);
                    elementFactory.addElement(newFailTransition);
                }
                elementFactory.getFailTransition(fail.getId()).addFailForElement(element, fail);
            }
        }
    }

    /**
     * Method that place element with no specification entry in .jsd file
     *
     * @param element
     * @param point
     */
    private void placeElementWithoutSpecification(Element element, Point[] point) {
        if (point[0].x >= 1000) {
            point[0].x = 200;
            point[0].y += 100;
        }
        element.setPosition(new Point(point[0].x, point[0].y));
        point[0].x += 100;
    }

    /**
     * Method is simmilar to method loadElementsFromJob() and is also called by this method in order to load nested flow elements.
     * Can be called recursively.
     *
     * @param rootFlowElement
     */
    public void loadInsideFlowElements(Element rootFlowElement) {
        Point[] insideFlowElementPointWithoutSpec = new Point[1];
        insideFlowElementPointWithoutSpec[0] = new Point(200, 200);
        if (((Flow) rootFlowElement).getElements() == null) return;

        DesignerContext newFlowDesignerContext = createDesignerContext(rootFlowElement.getId(), rootFlowElement.getDesignerContext());

        for (Element element : ((Flow) rootFlowElement).getElements()) {
            ElementSpec elementSpec = null;
            if (this.diagramDefinition != null)
                elementSpec = this.diagramDefinition.getElementSpec(element.getId());
            if (elementSpec != null) {
                element.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                if (element instanceof Step) {
                    ((Step) element).setPartitionEnabled(elementSpec.isPartitionEnabled());
                    if (((Step) element).getPartition() != null) {
                        ((Step) element).getPartition().setIsPartitionPlanDefinedAtRuntime(elementSpec.isRuntimePlanning());
                    }
                }
            } else placeElementWithoutSpecification(element, insideFlowElementPointWithoutSpec);

            element.setDesignerContext(newFlowDesignerContext);
            elementFactory.addElement(element);

            if (element instanceof Split) {
                if (!elementFactory.existsSplitEndPairElement(element)) {
                    elementFactory.createSplitPairElement(element);
                }
                List<Element> splitElements = ((Split) element).getElements();
                if (splitElements != null) {
                    for (Element insideSplitElement : splitElements) {
                        if (insideSplitElement instanceof Flow) {
                            elementSpec = null;
                            if (this.diagramDefinition != null)
                                elementSpec = this.diagramDefinition.getElementSpec(insideSplitElement.getId());
                            if (elementSpec != null)
                                insideSplitElement.setPosition(new Point(elementSpec.getPosition().getX(), elementSpec.getPosition().getY()));
                            else
                                placeElementWithoutSpecification(insideSplitElement, insideFlowElementPointWithoutSpec);
                            insideSplitElement.setDesignerContext(newFlowDesignerContext);
                            ((Flow) insideSplitElement).setIsInsideOfSplit(true);

                            loadEndTransitions(insideSplitElement, elementSpec, newFlowDesignerContext, insideFlowElementPointWithoutSpec);

                            elementFactory.addElement(insideSplitElement);
                            if (insideSplitElement instanceof Flow)
                                loadInsideFlowElements(insideSplitElement);
                        }
                    }
                }
            }
            if ((element instanceof Decision) || (element instanceof Step) || (element instanceof Flow)) {
                loadEndTransitions(element, elementSpec, newFlowDesignerContext, insideFlowElementPointWithoutSpec);
            }
            if (element instanceof Flow)
                loadInsideFlowElements(element);
        }
    }

    // load elements like SplitEnd, Start element

    /**
     * Loads additional elements like SplitEnd or StartElement from .jsd File
     */
    private void loadAdditionalElements() {
        if (diagramDefinition == null) return;
        List<AdditionalElement> additionalElements = diagramDefinition.getAdditionalElements();
        if (additionalElements == null) return;

        for (AdditionalElement additionalElement : additionalElements) {
            Element element = additionalElement.getElement();
            if (element != null) {
                element.setPosition(new Point(additionalElement.getPosition().getX(), additionalElement.getPosition().getY()));
                DesignerContext designerContext = getDesignerContext(additionalElement.getDesignerContextid());
                if (designerContext != null)
                    element.setDesignerContext(designerContext);
                elementFactory.addElement(element);
            }
        }
    }

    /**
     * Loads edges for already created elements
     */
    private void loadEdges() {
        edgeFactory.removeAllEdges();
        List<Element> elementsToRemoveAfteEdgeLoading = new ArrayList<Element>();
        for (Element element : elementFactory.getElements()) {
            String nextElementId = null;
            if (element instanceof Start) {
                nextElementId = ((Start) element).getNextElementId();
            }
            if (nextElementId != null) {
                edgeFactory.createEdge(element, elementFactory.getElement(nextElementId), element.getDesignerContext());
                element.addChild(elementFactory.getElement(nextElementId));
            }
            if (element instanceof Step) {
                List<Next> nextElementsIds = ((Step) element).getAllNextTransitions();
                if (nextElementsIds != null) {
                    for (Next next : nextElementsIds) {
                        edgeFactory.createEdge(element, elementFactory.getElement(next.getTo()), element.getDesignerContext());
                        element.addChild(elementFactory.getElement(next.getTo()));
                    }
                }
            }
            if (element instanceof Flow) {
                List<Next> nextElementsIds = ((Flow) element).getAllNextTransitions();
                if (nextElementsIds != null) {
                    for (Next next : nextElementsIds) {
                        edgeFactory.createEdge(element, elementFactory.getElement(next.getTo()), element.getDesignerContext());
                        element.addChild(elementFactory.getElement(next.getTo()));
                    }
                }
            }
            if (element instanceof Decision || element instanceof Step || element instanceof Flow) {

                if (element.getStops() != null) {
                    for (Stop stop : element.getStops()) {
                        edgeFactory.createEdge(element, elementFactory.getStopTransition(stop.getId()), element.getDesignerContext());
                    }
                }
                if (element.getEnds() != null) {
                    for (End end : element.getEnds()) {
                        edgeFactory.createEdge(element, elementFactory.getEndTransition(end.getId()), element.getDesignerContext());
                    }
                }
                if (element.getFails() != null) {
                    for (Fail fail : element.getFails()) {
                        edgeFactory.createEdge(element, elementFactory.getFailTransition(fail.getId()), element.getDesignerContext());
                    }
                }
                if (element instanceof Decision) {
                    for (Next next : ((Decision) element).getAllNextTransitions()) {
                        edgeFactory.createEdge(element, elementFactory.getElement(next.getTo()), element.getDesignerContext());
                        element.addChild(elementFactory.getElement(next.getTo()));
                    }
                }
            }
            if (element instanceof Split) {
                if (((Split) element).getElements() != null) {
                    for (Element e : ((Split) element).getElements()) {
                        Element splitPairElement = elementFactory.getSplitPairElement(element);
                        if (splitPairElement != null) {
                            edgeFactory.createEdge(element, e, element.getDesignerContext());
                            edgeFactory.createEdge(e, elementFactory.getSplitPairElement(element), element.getDesignerContext());
                        }
                    }
                }
            }
            if (element instanceof SplitElementEnd) {
                Element splitPairElement = elementFactory.getSplitPairElement(element);
                // If there is splitElementEnd by mistake from Additional Elements and pairt SplitStart element doesn't exist
                if (splitPairElement == null) {
                    elementsToRemoveAfteEdgeLoading.add(element);
                    continue;
                }
                nextElementId = ((Split) splitPairElement).getNextElementId();
                if (nextElementId != null) {
                    edgeFactory.createEdge(element, elementFactory.getElement(nextElementId), element.getDesignerContext());
                    splitPairElement.addChild(elementFactory.getElement(nextElementId));
                }
            }
        }
        if (elementsToRemoveAfteEdgeLoading != null && elementsToRemoveAfteEdgeLoading.size() > 0)
            elementFactory.removeAllElements(elementsToRemoveAfteEdgeLoading);
    }

    public Definition getDiagramDefinition() {
        actualizeDiagramDefinition();
        return this.diagramDefinition;
    }

    public void setDiagramDefinition(Definition definition) {
        this.diagramDefinition = definition;
    }

    /**
     * it's called before writing data into .jsd file in order to actualize this diagram definition
     */
    public void actualizeDiagramDefinition() {
        Definition newDefinition = new Definition();
        newDefinition.createElementsSpecification(elementFactory.getElements());
        this.diagramDefinition = newDefinition;
    }

    public DesignerContext getDesignerContext(String designerContextId) {
        for (DesignerContext designerContext : this.designerContexts) {
            if (designerContext.getContextId().equals(designerContextId))
                return designerContext;
        }
        return null;
    }

    public List<DesignerContext> getDesignerContexts() {
        return designerContexts;
    }

    public DesignerContext getActiveContext() {
        return activeContext;
    }

    public void setActiveContext(DesignerContext activeContext) {
        this.activeContext = activeContext;
    }
}
