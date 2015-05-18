package designer.ui.editor;

import designer.ui.editor.element.Edge;
import designer.ui.editor.element.EdgeOnValue;
import designer.ui.editor.element.Element;
import designer.ui.editor.element.SplitElementEnd;
import specification.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/22/2015.
 */
public class EdgeFactory {
    //    private static EdgeFactory instance = null;
    private List<Edge> edges;

    public EdgeFactory() {
        this.edges = new ArrayList<Edge>();
    }

//    public static EdgeFactory getInstance(){
//        if (instance == null){
//            instance = new EdgeFactory();
//        }
//        return instance;
//    }

    public void createEdge(Element predecessor, Element successor, DesignerContext activeContext) {
        if (this.edges == null) this.edges = new ArrayList<Edge>();
        Edge newEdge = null;
        if (successor instanceof StopTransition || successor instanceof EndTransition || successor instanceof FailTransition || predecessor instanceof Decision || predecessor instanceof Step || predecessor instanceof Flow) {
            if (predecessor instanceof Flow && successor instanceof SplitElementEnd)
                newEdge = new Edge(predecessor, successor); // Normal edge between flow and splitElementEnd
            else newEdge = new EdgeOnValue(predecessor, successor);
        } else newEdge = new Edge(predecessor, successor);
        newEdge.setDesignerContext(activeContext);
        this.edges.add(newEdge);
    }

    public void removeEdge(Element predecessor, Element successor) {
        if ((this.edges == null) || (this.edges.size() == 0)) return;

        Edge edgeToRemove = null;
        for (Edge e : this.edges) {
            if ((e.getSuccessor() == successor) && (e.getPredecessor() == predecessor)) {
                edgeToRemove = e;
            }
        }
        if (edgeToRemove != null) {
            predecessor.removeChild(successor);
            this.edges.remove(edgeToRemove);
        }
    }

    public List<Edge> getEdges() {
        if (this.edges == null) this.edges = new ArrayList<Edge>();
        return this.edges;
    }

    public void removeEdge(Edge edgeToRemove) {
        if ((this.edges == null) || (this.edges.size() == 0)) return;
        Element predecessor = edgeToRemove.getPredecessor();
        if (predecessor instanceof Decision) {
            ((Decision) edgeToRemove.getPredecessor()).removeNextTransition(edgeToRemove.getSuccessor().getId());
        } else if (predecessor instanceof Step) {
            ((Step) edgeToRemove.getPredecessor()).removeNextElementId(edgeToRemove.getSuccessor().getId());
//            ((Step)edgeToRemove.getPredecessor()).setNextElementId(null);
        } else if (predecessor instanceof Flow) {
            ((Flow) edgeToRemove.getPredecessor()).removeNextElementId(edgeToRemove.getSuccessor().getId());
//            ((Flow)edgeToRemove.getPredecessor()).setNextElementId(null);
        } else if (predecessor instanceof Start) {
            ((Start) edgeToRemove.getPredecessor()).setNextElementId(null);
        }
        /*else if (predecessor instanceof SplitElementEnd){

            ((Split)edgeToRemove.getPredecessor()).setNextElementId(null);
        }*/

        predecessor.removeChild(edgeToRemove.getSuccessor());
//        edgeToRemove.getSuccessor().setP
        this.edges.remove(edgeToRemove);
    }

    public void removeEdgesConnectedTo(Element element) {
        List<Edge> edgesToRemove = new ArrayList<Edge>();
        for (Edge edge : this.edges) {
            if ((edge.getPredecessor() == element) || (edge.getSuccessor() == element)) {
                edgesToRemove.add(edge);
            }
        }
        this.edges.removeAll(edgesToRemove);
    }

    public Edge getEdgeUnderCursor(Point p, DesignerContext designerContext) {
        for (Edge edge : this.edges) {
            if (edge.containsPoint(p) && designerContext.belongsToContext(edge))
                return edge;
        }
        return null;
    }

    public void removeAllEdges() {
        this.edges = null;
    }
}
