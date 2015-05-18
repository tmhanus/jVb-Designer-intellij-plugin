package designer.ui.editor;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.SplitElement;
import designer.ui.editor.element.SplitElementEnd;
import specification.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/22/2015.
 */
public class ElementFactory {
    //    private static ElementFactory instance = null;
    // CONSTANTS
    private static final String CHUNK_ELEMENT = "Chunk";
    private static final String BATCHLET_ELEMENT = "Batchlet";
    private static final String DECISION_ELEMENT = "Decision";
    private static final String FLOW_ELEMENT = "Flow";
    private static final String SPLIT_ELEMENT = "Split";
    private static final String START_ELEMENT = "Start";
    private static final String STOP_ELEMENT = "Stop";
    private static final String END_ELEMENT = "End";
    private static final String FAIL_ELEMENT = "Fail";

    // ATTRIBUTES
    /**
     * List of all elements
     */
    private List<Element> elements;

    /**
     * Root job element
     */
    private Job rootElement;

    public ElementFactory() {
        this.elements = new ArrayList<Element>();
    }

//    public static ElementFactory getInstance(){
//        if (instance == null){
//            instance = new ElementFactory();
//        }
//        return instance;
//    }

    public Element createElement(String elementType, Element parent, DesignerContext activeContext) {

        String newElementId = generateId(elementType);
        Element child = null;

        if (elementType.equals(CHUNK_ELEMENT)) {
            child = new Step(newElementId, CHUNK_ELEMENT);
        } else if (elementType.equals(BATCHLET_ELEMENT)) {
            child = new Step(newElementId, BATCHLET_ELEMENT);
        } else if (elementType.equals(DECISION_ELEMENT)) {
            child = new Decision(newElementId);
        } else if (elementType.equals(FLOW_ELEMENT)) {
            child = new Flow(newElementId);
        } else if (elementType.equals(START_ELEMENT)) {
            if (!startAlreadyExists(activeContext))
                child = new Start(newElementId);
        } else if (elementType.equals(SPLIT_ELEMENT)) {
            // Split Element Cannot be created anywere else then in root Job Element
            //if (activeContext.getContextId().equals(rootElement.getId()))
            child = new Split(newElementId);
        } else if (elementType.equals(STOP_ELEMENT)) {
            child = new StopTransition(newElementId);
        } else if (elementType.equals(END_ELEMENT)) {
            child = new EndTransition(newElementId);
        } else if (elementType.equals(FAIL_ELEMENT)) {
            child = new FailTransition(newElementId);
        }

        if (child != null && parent != null && (child instanceof EndTransition || child instanceof StopTransition || child instanceof FailTransition)) {
            if (!parent.canAddTransitionElement(child)) child = null;
        }

        if (child != null) {
            elements.add(child);
            child.setDesignerContext(activeContext);
            if (parent != null) {
                if (parent instanceof Decision || parent instanceof Step || parent instanceof Flow) {
                    if (child instanceof StopTransition) ((StopTransition) child).addStopForElement(parent);
                    else if (child instanceof FailTransition) ((FailTransition) child).addFailForElement(parent);
                    else if (child instanceof EndTransition) ((EndTransition) child).addEndForElement(parent);
                    else {
                        if (parent instanceof Decision)
                            ((Decision) parent).createNextTransition(null, child.getId());
                    }
                }
                if (parent instanceof SplitElementEnd) {
                    getSplitPairElement(parent).addChild(child);
                    child.placeUnderElement(parent);
                } else if (parent instanceof Split) {
                    child.placeUnderElement(parent);
                } else {
                    parent.addChild(child);
                    child.placeUnderElement(parent);
                }
            }
        }
        return child;
    }


    private boolean startAlreadyExists(DesignerContext designerContext) {
        for (Element element : this.elements)
            if (element instanceof Start && element.getDesignerContext().getContextId().equals(designerContext.getContextId()))
                return true;
        return false;
    }

    public Element createSplitPairElement(Element split) {
        Element newSplitEndElement = new SplitElementEnd(new Point(split.getPosition().x, split.getPosition().y + 200));
        String splitEndId = split.getId() + "_End";
        newSplitEndElement.setId(splitEndId);
        newSplitEndElement.setDesignerContext(split.getDesignerContext());
        elements.add(newSplitEndElement);
        return newSplitEndElement;
    }

    /**
     * Place element connected with Start element into first place in his context (Job, Flow)
     */
    public void makeElementFirst(Element element) {
        if (element.getDesignerContext().getContextId().equals(rootElement.getId())) {
            this.rootElement.removeElement(element);
            this.rootElement.addElement(0, element);
        } else {
            Element rootContextElement = getRootContextElement(element.getDesignerContext().getContextId());
            if (rootContextElement != null) {
                ((Flow) rootContextElement).removeElement(element);
                ((Flow) rootContextElement).addElement(0, element);
            }
        }
    }

    /**
     * Return split pair element to SplitElement, or SplitElementEnd
     *
     * @param split
     * @return
     */
    public Element getSplitPairElement(Element split) {
        if (split instanceof SplitElement) {
            return getElement(split.getId() + "_End");
        } else if (split instanceof SplitElementEnd) {
            // return Split Element .... but at first cut suffix from the end
            return getElement(split.getId().substring(0, split.getId().length() - "_End".length()));
        }

        return null;
    }

    public Job createRootJobElement() {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.rootElement = new Job("Job01");
        return this.rootElement;
    }

    public void setRootElement(Job job) {
        this.rootElement = job;
    }

    /**
     * Generate Simple Unique Id to element.
     *
     * @param elementType
     * @return
     */
    public String generateId(String elementType) {
        int counter = 0;
        boolean success = false;
        boolean idMatch = false;

        while (!success) {
            idMatch = false;
            for (Element e : this.elements) {
                if (e.getId().equals(elementType + "_" + counter)) {
                    idMatch = true;
                    break;
                }
            }
            success = !idMatch; // IF ID is Already used, then continue, otherwise finish while cycle
            if (!success) counter++;
        }
        return elementType + "_" + Integer.toString(counter);
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public Element getElement(String id) {
        for (Element element : elements) {
            if (element.getId().equals(id)) return element;
        }
        return null;
    }

    // When loading elements from XML, we don't create them, we just load them to element factory
    public void addElement(Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(element);
    }

    /**
     * Return element that is root element for designer context represented by parameter contextId
     */
    public Element getRootContextElement(String contextId) {
        for (Element element : this.elements) {
            if (element.getId().equals(contextId)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Return element from context designerContext which is actually under the cursor.
     *
     * @param p
     * @param designerContext
     * @return
     */
    public Element getElementUnderCursor(Point p, DesignerContext designerContext) {
        if (this.elements == null) return null;
        for (Element element : this.elements) {
            if (element.containsCursor(p) && designerContext.belongsToContext(element))
                return element;
        }
        return null;
    }

    public void removeElement(Element clickedElement) {
        // Remove NextReferences to clickedElement
        for (Element element : this.elements) {
            if (element instanceof Decision) {
                ((Decision) element).removeNextTransition(clickedElement.getId());
            } else if (element instanceof Step) {
                ((Step) element).removeNextElementId(clickedElement.getId());
            } else if (element instanceof Flow) {
                ((Flow) element).removeNextElementId(clickedElement.getId());
            } else if (element instanceof Split) {
                if (clickedElement.getId().equals(((Split) element).getNextElementId())) {
                    ((Split) element).setNextElementId(null);
                }
            } else if (element instanceof Start) {
                if (clickedElement.getId().equals(((Start) element).getNextElementId())) {
                    ((Start) element).setNextElementId(null);
                }
            }
        }
        if ((clickedElement instanceof StopTransition) || (clickedElement instanceof EndTransition) || (clickedElement instanceof FailTransition)) {
            for (Element element : this.elements) {
                if (element instanceof Decision) {
                    if ((clickedElement instanceof StopTransition) && (((Decision) element).getStops() != null))
                        ((StopTransition) clickedElement).removeStopForElement(element);
                    if ((clickedElement instanceof EndTransition) && (((Decision) element).getEnds() != null))
                        ((EndTransition) clickedElement).removeEndForElement(element);
                    if ((clickedElement instanceof FailTransition) && (((Decision) element).getFails() != null))
                        ((FailTransition) clickedElement).removeFailForElement(element);
                } else if (element instanceof Step) {
                    if ((clickedElement instanceof StopTransition) && (((Step) element).getStops() != null))
                        ((StopTransition) clickedElement).removeStopForElement(element);
                    if ((clickedElement instanceof EndTransition) && (((Step) element).getEnds() != null))
                        ((EndTransition) clickedElement).removeEndForElement(element);
                    if ((clickedElement instanceof FailTransition) && (((Step) element).getFails() != null))
                        ((FailTransition) clickedElement).removeFailForElement(element);
                } else if (element instanceof Flow) {
                    if ((clickedElement instanceof StopTransition) && (((Flow) element).getStops() != null))
                        ((StopTransition) clickedElement).removeStopForElement(element);
                    if ((clickedElement instanceof EndTransition) && (((Flow) element).getEnds() != null))
                        ((EndTransition) clickedElement).removeEndForElement(element);
                    if ((clickedElement instanceof FailTransition) && (((Flow) element).getFails() != null))
                        ((FailTransition) clickedElement).removeFailForElement(element);
                }
            }
        }

        // Remove This Parent from all children element
        if (clickedElement.getChildren() != null) {
            for (Element child : clickedElement.getChildren())
                child.removeParent(clickedElement);
        }
        // Remove from Parent's children
        for (Element element : this.elements) {
            if ((element.getChildren() != null) && (element.getChildren().contains(clickedElement))) {
                element.removeChild(clickedElement);
            }
        }

        // Remove InsideFlow Elements
        if (clickedElement instanceof Flow) {
            List<Element> flowSubElements = getAllFlowSubElements((Flow) clickedElement);
            if (flowSubElements != null)
                this.elements.removeAll(flowSubElements);
        }

        //Remove from Job/Flow context
        // Clicked Element is in RootElement (Job) or in particular Split Element
        if (clickedElement.getDesignerContext().getContextId().equals(rootElement.getId())) {
            if (clickedElement instanceof SplitElement) {
                removeAllSplitSubElements((Split) clickedElement);
            }
            // if root element containsPoint clicked element
            if (rootElement.getElements().contains(clickedElement)) {
                rootElement.removeElement(clickedElement);
            } else { // if clicked element is inside of any split element
                for (Element e : this.elements) {
                    if (e instanceof Split) {
                        ((Split) e).removeElement(clickedElement);
                    }
                }
            }
        } else {
            //Clicked Element is not in Root Element, so program finds his root context element
            if (clickedElement instanceof SplitElement) {
                removeAllSplitSubElements((Split) clickedElement);
//                List<Element> elementsToRemove = ((Split)clickedElement).getAllElements();
//                if (elementsToRemove != null)
//                    this.elements.removeAll(elementsToRemove);
//                ((Split)clickedElement).removeAllElements();
            }
            Element contextElement = null;
            for (Element element : this.elements) {
                if (clickedElement.getDesignerContext().getContextId().equals(element.getId())) {
                    contextElement = element;
                }
            }
            if (contextElement != null) {
                if (contextElement instanceof Flow) {
                    ((Flow) contextElement).removeElement(clickedElement);
                }
            }
        }
        // remove from Elements List
        this.elements.remove(clickedElement);
    }

    public boolean existsSplitEndPairElement(Element splitElement) {
        if (getElement(splitElement.getId() + "_End") != null)
            return true;
        return false;
    }

    public void removeAllElements() {
        this.elements.clear();
        this.rootElement = null;
    }

    public void removeAllElements(List<Element> elementsToRemove) {
        if (elementsToRemove != null && elementsToRemove.size() > 0)
            this.elements.removeAll(elementsToRemove);
    }

    public StopTransition getStopTransition(String id) {
        StopTransition stopTransitionToReturn = null;
        for (Element element : this.elements) {
            if (element.getId() != null && element.getId().equals(id))
                stopTransitionToReturn = (StopTransition) element;
        }
        return stopTransitionToReturn;
    }

    public EndTransition getEndTransition(String id) {
        EndTransition endTransitionToReturn = null;
        for (Element element : this.elements) {
            if (element.getId() != null && element.getId().equals(id))
                endTransitionToReturn = (EndTransition) element;
        }
        return endTransitionToReturn;
    }

    public FailTransition getFailTransition(String id) {
        FailTransition failTransitionToReturn = null;
        for (Element element : this.elements) {
            if (element.getId() != null && element.getId().equals(id))
                failTransitionToReturn = (FailTransition) element;
        }
        return failTransitionToReturn;
    }

    /**
     * Check if identifier ID is already used in any of elements. If it is, than return id of this collision element in parameter collisionElement.
     *
     * @param id
     * @param collisionInElement
     * @return
     */
    public boolean isIdentifierAlreadyUsed(String id, String[] collisionInElement) {
        boolean isIdUsed = false;
        for (Element element : this.elements) {
            collisionInElement[0] = element.getId();
            if (element.getId().equals(id)) {
                isIdUsed = true;
                break;
            }
            if (element instanceof Decision) {
                if (((Decision) element).getRef().equals(id)) {
                    isIdUsed = true;
                    break;
                }
            } else if (element instanceof Step) {
                if (((Step) element).isChunkOriented()) {
                    if (((Step) element).getChunk().getReader().getRef().equals(id)) {
                        isIdUsed = true;
                        break;
                    }
                    if (((Step) element).getChunk().getProcessor().getRef().equals(id)) {
                        isIdUsed = true;
                        break;
                    }
                    if (((Step) element).getChunk().getWriter().getRef().equals(id)) {
                        isIdUsed = true;
                        break;
                    }
                } else {
                    if (((Step) element).getBatchlet().getRef().equals(id)) {
                        isIdUsed = true;
                        break;
                    }
                }
            }
        }
        if (isIdUsed == false) collisionInElement[0] = "";
        return isIdUsed;
    }

    /**
     * Update ID of element, together with changing all next references to this element
     */
    public void updateElementId(Element changedElement, String newId) {
        for (Element element : this.elements) {
            if (element instanceof Decision) {
                ((Decision) element).renameNextTransition(changedElement.getId(), newId);
            } else if (element instanceof Step) {
                if (((Step) element).getNextTransition(changedElement.getId()) != null) {
                    ((Step) element).renameDestinationOfNextTransition(changedElement.getId(), newId);
                }
//                if (changedElement.getId().equals(((Step) element).getNextElementId())){
//                    ((Step) element).setNextElementId(newId);
//                }
            } else if (element instanceof Flow) {
                if (((Flow) element).getNextTransition(changedElement.getId()) != null) {
                    ((Flow) element).renameDestinationOfNextTransition(changedElement.getId(), newId);
                }
//                if (changedElement.getId().equals(((Flow) element).getNextElementId())){
//                    ((Flow) element).setNextElementId(newId);
//                }
            } else if (element instanceof Split) {
                if (changedElement.getId().equals(((Split) element).getNextElementId())) {
                    ((Split) element).setNextElementId(newId);
                }
            } else if (element instanceof Start) {
                if (changedElement.getId().equals(((Start) element).getNextElementId())) {
                    ((Start) element).setNextElementId(newId);
                }
            }
        }

        if (changedElement instanceof Split) {
            getSplitPairElement(changedElement).setId(newId + "_End");
        }
    }

    /**
     * Set ot rules of creating structure of elements. Returns true or false if element (possibleChild) can be add to element(possibleParent).
     *
     * @param possibleParent
     * @param possibleChild
     * @return
     */
    public boolean canAddChildToParent(Element possibleParent, Element possibleChild) {
        if (possibleParent instanceof SplitElementEnd && getSplitPairElement(possibleParent) == possibleChild)
            return false;
        if (possibleParent instanceof SplitElementEnd) {
            if (getSplitPairElement(possibleParent).getChildren() != null && getSplitPairElement(possibleParent).getChildren().size() >= 1)
                return false;
        }
        if (possibleParent == possibleChild) return false;
        if ((possibleChild instanceof Flow) && ((Flow) possibleChild).isInsideOfSplit()) return false;
        if (possibleChild instanceof Flow && possibleParent instanceof Split && (possibleChild.getParents() != null || possibleChild.getChildren() != null))
            return false;
        if (possibleChild instanceof SplitElementEnd) return false;
        if (possibleChild.isBetweenParents(possibleParent)) return false;

        if (possibleChild instanceof EndTransition || possibleChild instanceof StopTransition || possibleChild instanceof FailTransition)
            return possibleParent.canAddTransitionElement(possibleChild);
        return possibleParent.canAdd(possibleChild);
    }


    public List<Element> getAllFlowSubElements(Flow flow) {
        List<Element> flowSubElements = new ArrayList<Element>();
        if (flow.getElements() != null && flow.getElements().size() != 0) {
            for (Element element : flow.getElements()) {
                flowSubElements.add(element);
                if (element instanceof Flow && ((Flow) element).getElements() != null && ((Flow) element).getElements().size() != 0)
                    flowSubElements.addAll(getAllFlowSubElements((Flow) element));
            }
        }
        if (flowSubElements.size() == 0) return null;
        return flowSubElements;
    }

    /**
     * If removing Split element it's also necessary to remove all his subelements, and if between this subelements is a flow with nested elements, theese elements has to be deleted likewise.
     *
     * @param split
     */
    public void removeAllSplitSubElements(Split split) {
        if (split.getElements() == null) return;
        List<Element> splitSubElementsToRemove = new ArrayList<Element>();
        for (Element element : split.getElements()) {
            splitSubElementsToRemove.add(element);
            List<Element> flowSubElements = getAllFlowSubElements((Flow) element);
            if (flowSubElements != null)
                splitSubElementsToRemove.addAll(flowSubElements);
        }
        this.elements.removeAll(splitSubElementsToRemove);
    }

    public Job getRootElement() {
        return rootElement;
    }
}
