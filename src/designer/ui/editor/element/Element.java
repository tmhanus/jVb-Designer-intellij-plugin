package designer.ui.editor.element;

import designer.ui.editor.DesignerContext;
import specification.*;

import javax.xml.bind.annotation.XmlTransient;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanusas on 03.02.15.
 */

public abstract class Element extends BaseElement {
    /**
     * Position of the element
     */
    @XmlTransient
    private Point position;

    /**
     * Zoom and displacement of element which helps to determine it's position on canvas
     */
    private Point displacement = new Point(0, 0);
    private int zoom = 100;

    /**
     * Border that represents position of an element
     */
    public Rectangle border = new Rectangle();

    /**
     * Border that represents drop area of an element
     */
    public Rectangle dropBorder = new Rectangle();

    /**
     * Indicates that dragged element, or the element just created can be add to this element. It's used to highlight element if drop is possible
     */
    private boolean dropEnabled = false;

    /**
     * Element width
     */
    private static int width = 80;

    /**
     * Element height
     */
    private static int height = 80;

    /**
     * List of all children (direct successors)
     */
    private List<Element> children = null;

    /**
     * List of all parents (direct predecessors)
     */
    private List<Element> parents;

    /**
     * Designer context of this element
     */
    private DesignerContext designerContext;

    /**
     * Indicates if element is selected, for example to edit it's properties on properties panel
     */
    private boolean isSelected;

    /**
     * Indicator whether element is dragging on canvas or not
     */
    private boolean isSelectedForDragging;

    public Element(Point position) {
        super();
        this.position = position;
        setBorder(this.border);
        setBorder(this.dropBorder);
    }

    public Element() {
        this.position = new Point(300, 100);
        setBorder(this.border);
        setBorder(this.dropBorder);
    }

    /**
     * Abstract class that returns element width
     */
    public abstract int getWidth();

    /**
     * Abstract class that returns element height
     */
    public abstract int getHeight();

    /**
     * Set element position right under the element specified in parameter element
     *
     * @param element
     */
    public void placeUnderElement(Element element) {
        setPosition(new Point(element.getPosition().x, element.getPosition().y + 100));
    }

    /**
     * Calls function getAttachPoint(Point attachFrom) to return attach point
     *
     * @param attachFrom
     * @return
     */
    public Point getAttachPoint(Element attachFrom) {
        return getAttachPoint(attachFrom.getPosition());
    }

    /**
     * Returns attach point according to location of attachFrom point regarding to this element position
     */
    public Point getAttachPoint(Point attachFrom) {
        float angle = (float) Math.toDegrees(Math.atan2(attachFrom.y - getPosition().y, attachFrom.x - getPosition().x));
        if (angle >= -135 && angle < -45) return new Point(getPosition().x, getPosition().y - getHeight() / 2);
        if ((angle >= 135 && angle <= 180) || (angle >= -180 && angle < -135))
            return new Point(getPosition().x - getWidth() / 2, getPosition().y);
        if (angle > 45 && angle < 135) return new Point(getPosition().x, getPosition().y + getHeight() / 2);
        if (angle >= -45 && angle <= 45) return new Point(getPosition().x + getWidth() / 2, getPosition().y);
        return getPosition();
    }


    public Point getPosition() {
        return this.position;
    }

    /**
     * Sets position of this element and also re-sets it's borders
     *
     * @param p
     */
    public void setPosition(Point p) {
        this.position = p;
        // re-set borders according to the new Point
        setBorder(this.border);
        setBorder(this.dropBorder);
    }

    /**
     * Returns true if Point is inside borders
     */
    public boolean containsCursor(Point p) {
        return this.border.contains(p);
    }

    public abstract void draw(Graphics g);

    /**
     * Draw ellipse around element.
     *
     * @param g
     */
    public void highlightHelp(Graphics g) {
//        g.setColor(Color.lightGray);
        g.setColor(new Color(117, 117, 117));
        int radius = 110 * zoom / 100;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawArc(getPosition().x - radius / 2, getPosition().y - radius / 2, radius, radius, 0, 360);

    }

    /**
     * Draw coloured square behind element in order to highlight it.
     *
     * @param g
     */
    public void highlightSelected(Graphics g) {
//        g.setColor(Color.decode("#A5B6F3"));
        g.setColor(Color.decode("#1E88E5"));
        g.fillRect(getPosition().x - getWidth() / 2 - 1, getPosition().y - getHeight() / 2 - 1, getWidth() + 2, getHeight() + 2);
    }


    private void setBorder(Rectangle border) {
        border.setBounds(getPosition().x - (((int) (getWidth() * zoom / 100)) / 2), getPosition().y - (((int) (getHeight() * zoom / 100)) / 2), ((int) (getWidth() * zoom / 100)), ((int) (getHeight() * zoom / 100)));
    }

    /**
     * Makes element selected
     */
    public void selectForDragging() {
        this.isSelectedForDragging = true;
    }

    /**
     * Update displacement of an element
     *
     * @param disp
     */
    public void updateDisplacement(Point disp) {
        this.displacement.setLocation(disp);
        this.position.x += displacement.x;
        this.position.y += displacement.y;
        setBorder(this.border);
        setBorder(this.dropBorder);
    }

    /**
     * Update position of an element
     *
     * @param diff
     */
    public void updatePosition(Point diff) {
        this.position.x += diff.x;
        this.position.y += diff.y;
        setBorder(this.border);
        setBorder(this.dropBorder);
    }

    /**
     * Returns true if Point p is somewhere inside of dropBorder
     *
     * @param p
     * @return
     */
    public boolean canDrop(Point p) {
        return this.dropBorder.contains(p);
    }

    /**
     * @return Returns True if element is selected
     */
    public boolean isSelectedForDragging() {
        return this.isSelectedForDragging;
    }

    /**
     * Deselect Element
     */
    public void deselectForDragging() {
        this.isSelectedForDragging = false;
    }

    /**
     * Add successor to element
     *
     * @param child
     */
    public void addChild(Element child) {
        if (this.children == null) this.children = new ArrayList<Element>();

        if (this instanceof Decision) {
        } else if (this instanceof Step) {
            if (!(child instanceof StopTransition) && !(child instanceof EndTransition) && !(child instanceof FailTransition))
                ((Step) this).addNextElementId(child.getId());
//                ((Step) this).setNextElementId(child.getId());
        } else if (this instanceof Flow) {
            if (!(child instanceof StopTransition) && !(child instanceof EndTransition) && !(child instanceof FailTransition))
                ((Flow) this).addNextElementId(child.getId());
//            ((Flow) this).setNextElementId(child.getId());
        } else if (this instanceof Split) {
            ((Split) this).setNextElementId(child.getId());
        } else if (this instanceof Start) {
            ((Start) this).setNextElementId(child.getId());
        }
        // These Transition end elements are not added as Next Transitions
        if (!(child instanceof StopTransition) && !(child instanceof EndTransition) && !(child instanceof FailTransition)) {
            this.children.add(child);
            child.addParent(this);
        }
    }

    /**
     * Remove element from children if it exists
     *
     * @param element
     */
    public void removeChild(Element element) {
        if (children != null) {
            children.remove(element);
            element.removeParent(this);
            if (children.size() == 0) children = null;
        }
    }

    /**
     * Set parent of an Element
     *
     * @param parent
     */
    public void addParent(Element parent) {
        if (this.parents == null) this.parents = new ArrayList<Element>();
        if (!this.isBetweenParents(parent))
            this.parents.add(parent);
    }

    public List<Element> getParents() {
        return this.parents;
    }

    /**
     * Returns true if element possibleParent is between parents of this element
     *
     * @param possibleParent
     * @return
     */
    public boolean isBetweenParents(Element possibleParent) {
        if (this.parents == null) return false;
        for (Element element : this.parents)
            if (element == possibleParent) return true;
        return false;
    }

    /**
     * Removes parent parentToRemove from this element's parents
     *
     * @param parentToRemove
     */
    public void removeParent(Element parentToRemove) {
        if (this.parents == null) return;
        this.parents.remove(parentToRemove);
        if (this.parents.size() == 0) this.parents = null;
    }

    /**
     * Abstract method which returns true/false if this element can/can't be child of current father element
     *
     * @param possibleChild
     * @return
     */
    public abstract boolean canAdd(String possibleChild);

    /**
     * Method returns false. It was meant to be overriden by those elements which can contain transition end elements(Stop, End, Fail)
     *
     * @param transitionElement
     * @return
     */
    public boolean canAddTransitionElement(Element transitionElement) {
        return false;
    }

    public boolean canAdd(Element child) {
        return canAdd(child.getType());
    }

    /**
     * Abstract method returns String Type of an element
     *
     * @return
     */
    public abstract String getType();

    /**
     * Method returns list of elements or null, if they don't exist.
     *
     * @return
     */
    public List<Element> getChildren() {
        return this.children;
    }

    /**
     * Returns Point for parent's Edge connection
     *
     * @return
     */
    public Point getParentPoint() {
        return new Point(getPosition().x, getPosition().y - ((int) (getHeight() * zoom / 100)) / 2);
    }

    /**
     * Returns Point for children's Edge connection
     *
     * @return
     */
    public Point getChildPoint() {
        return new Point(getPosition().x, getPosition().y + ((int) (getHeight() * zoom / 100)) / 2);
    }

    /**
     * Enables drop (For highlighting possible drop areas)
     */
    public void enableDrop() {
        this.dropEnabled = true;
    }

    /**
     * Disables drop (For not - highlighting areas which aren't suitable for drop)
     */
    public void disableDrop() {
        this.dropEnabled = false;
    }

    /**
     * Returns true/false if drop is/is not enabled
     *
     * @return
     */
    public boolean isDropEnabled() {
        return this.dropEnabled;
    }

    public void updateZoom(int zoom) {
        this.zoom = zoom;
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setDesignerContext(DesignerContext designerContext) {
        this.designerContext = designerContext;
    }

    public DesignerContext getDesignerContext() {
        return this.designerContext;
    }

    // it is meant to override theese methods from elements, that can contain Stops, Ends, Fails (Decision & Step)
    public List<Stop> getStops() {
        return null;
    }

    public List<End> getEnds() {
        return null;
    }

    public List<Fail> getFails() {
        return null;
    }


    public void select() {
        this.isSelected = true;
    }

    public void deselect() {
        this.isSelected = false;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
}
