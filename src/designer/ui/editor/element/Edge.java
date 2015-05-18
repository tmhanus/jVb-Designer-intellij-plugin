package designer.ui.editor.element;

import designer.ui.editor.DesignerContext;
import specification.Flow;
import specification.Split;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

/**
 *  Created by Tomas Hanusas on 03.02.15.
 */
public class Edge {
    private Point parentPoint;
    private Point childPoint;
    private Element successor;
    private Element predecessor;
    private boolean isHighlighted;
    private Line2D line;

    private DesignerContext designerContext;
    private boolean isSelected;

    public Edge(Element parent, Element child) {
        this.successor = child;
        this.predecessor = parent;
        this.line = new Line2D.Double();
        recalcEdge();
        this.isHighlighted = false;
    }

    public Edge(Element parent, Point tmpChildrenPoint) {
        this.predecessor = parent;
//        this.parentPoint = parent.getPosition();
        this.childPoint = tmpChildrenPoint;
        this.parentPoint = parent.getAttachPoint(tmpChildrenPoint);
        this.line = new Line2D.Double();
        this.line.setLine(this.parentPoint.getX(), this.parentPoint.getY(), this.childPoint.getX(), this.childPoint.getY());
    }

    public void setChildPoint(Point newChildPoint) {
        this.childPoint = newChildPoint;
        this.line.setLine(this.parentPoint.getX(), this.parentPoint.getY(), this.childPoint.getX(), this.childPoint.getY());
    }

    public void highlightEdge() {
        this.isHighlighted = true;
    }

    public void unhighlightEdge() {
        this.isHighlighted = false;
    }

    public void selectEdge() {
        this.isSelected = true;
    }

    public void deselectEdge() {
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Called every time canvas or elements move in order to make changes also in edge position.
     */
    public void recalcEdge() {
        if (((predecessor instanceof Split) && (successor instanceof Flow)) || (predecessor instanceof Flow && successor instanceof SplitElementEnd)) {
            this.parentPoint = this.predecessor.getChildPoint();
            this.childPoint = this.successor.getParentPoint();
        } else {
            this.parentPoint = this.predecessor.getPosition();
//            if (this.successor != null) System.out.println("ID_SUCCESSOR: " + this.successor.getId());
//            if (this.predecessor != null) System.out.println("ID_PREDECESSOR: " + this.predecessor.getId());
            this.childPoint = this.successor.getAttachPoint(this.predecessor);
        }

        this.line.setLine(this.parentPoint.getX(), this.parentPoint.getY(), this.childPoint.getX(), this.childPoint.getY());
    }

    public void setPredecessor(Element e) {
        if (e != null) this.predecessor = e;
    }

    public Element getSuccessor() {
        return this.successor;
    }

    public Element getPredecessor() {
        return this.predecessor;
    }

    public void draw(Graphics g) {
        if (this.isSelected == true) g.setColor(Color.decode("#1565C0"));
        else if (this.isHighlighted == true) g.setColor(Color.decode("#1E88E5"));
        else g.setColor(Color.black);
        /*
        g.drawLine((int)this.parentPoint.getX(),(int)this.parentPoint.getY(), (int)this.childPoint.getX(),(int)this.childPoint.getY());
        g.drawLine((int)this.parentPoint.getX()+1,(int)this.parentPoint.getY(), (int)this.childPoint.getX()+1,(int)this.childPoint.getY());*/

//        QuadCurve2D q = new QuadCurve2D.Float();
//// draw QuadCurve2D.Float with set coordinates
//        q.setCurve(this.parentPoint.x, this.parentPoint.y, -200, 200, this.childPoint.x, this.childPoint.y);
//        Graphics2D g2 = (Graphics2D) g.create();
//        g2.draw(q);


        drawArrow(g, (int) this.parentPoint.getX(), (int) this.parentPoint.getY(), (int) this.childPoint.getX(), (int) this.childPoint.getY());
    }


    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        /*g.drawLine(0, 0, len, 0);*/
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(g.getColor());

        g2.setStroke(new BasicStroke(2));
        /*g2.drawLine(0, 0, len, 0);*/

        g2.draw(new Line2D.Double(0, 0, len, 0));

        g.fillPolygon(new int[]{len, len - 8, len - 8, len},
                new int[]{0, -8, 8, 0}, 4);
    }

    /**
     * Returns true/false if edge contains/doesn't contain Point point.
     *
     * @param point
     * @return
     */
    public boolean containsPoint(Point point) {
        if (this.line != null) {
            double cislo = this.line.ptSegDist(this.line.getX1(), this.line.getY1(), this.line.getX2(), this.line.getY2(), point.getX(), point.getY());
            if (cislo < 3.0) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void setDesignerContext(DesignerContext designerContext) {
        this.designerContext = designerContext;
    }

    public DesignerContext getDesignerContext() {
        return this.designerContext;
    }
}
