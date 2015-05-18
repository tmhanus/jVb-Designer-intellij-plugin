package specification;

import designer.ui.editor.element.DecisionElement;
import designer.ui.editor.element.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "decision")
public class Decision extends DecisionElement {
    ////////////////////////////////////
    // Attributes
    @XmlAttribute
    private String ref;

    @XmlElement
    private Properties properties;

    ////////////////////////////////////
    // Elements

    // Transitions
    @XmlElements({@XmlElement(name = "stop", type = Stop.class)})
    private List<Stop> stops;
    @XmlElements({@XmlElement(name = "end", type = End.class)})
    private List<End> ends;
    @XmlElements({@XmlElement(name = "fail", type = Fail.class)})
    private List<Fail> fails;
    @XmlElements({@XmlElement(name = "next", type = Next.class)})
    private List<Next> nexts;


    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************
    public Decision() {

    }

    public Decision(String id) {
        this.setId(id);
        this.ref = this.getId() + "_ref";
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public void createNextTransition(String on, String to) {
        if (this.nexts == null) this.nexts = new ArrayList<Next>();
        this.nexts.add(new Next(on, to));
    }

    public void removeNextTransition(String to) {
        if (this.nexts == null) return;
        List<Next> nextTransitionsToRemove = new ArrayList<Next>();
        for (Next next : this.nexts) {
            if (next.getTo().equals(to))
                nextTransitionsToRemove.add(next);
        }
        if (nextTransitionsToRemove != null) {
            this.nexts.removeAll(nextTransitionsToRemove);
        }
    }

    public void renameNextTransition(String oldNextTransition, String newNextTransition) {
        if (this.nexts == null || this.nexts.size() == 0) return;
        for (Next next : this.nexts) {
            if (next.getTo().equals(oldNextTransition))
                next.setTo(newNextTransition);
        }
    }


    public List<Next> getAllNextTransitions() {
        if (this.nexts == null) this.nexts = new ArrayList<Next>();
        return this.nexts;
    }

    public Next getNextTransition(Element transitionToElement) {
        if (this.nexts == null) return null;
        for (Next next : this.nexts) {
            if (next.getTo().equals(transitionToElement.getId())) {
                return next;
            }
        }
        return null;
    }

    public boolean containsNextTransitionTo(Element element) {
        boolean contains = false;
        for (Next next : this.nexts) {
            if (next.getTo().equals(element.getId())) {
                contains = true;
            }
        }
        return contains;
    }

    public void addStop(Stop stop) {
        if (this.stops == null) this.stops = new ArrayList<Stop>();
        if (!this.stops.contains(stop))
            this.stops.add(stop);
    }

    public Stop getStop(String stopId) {
        if (this.stops == null) return null;
        Stop stopToReturn = null;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(stopId))
                stopToReturn = stop;
        }
        return stopToReturn;
    }

    public End getEnd(String endId) {
        if (this.ends == null) return null;
        End endToReturn = null;
        for (End end : this.ends) {
            if (end.getId().equals(endId))
                endToReturn = end;
        }
        return endToReturn;
    }

    public void removeStop(String stopId) {
        if (this.stops == null) return;
        Stop stopToRemove = null;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(stopId))
                stopToRemove = stop;
        }
        if (stopToRemove == null) return;
        this.stops.remove(stopToRemove);
        if (this.stops.size() == 0) this.stops = null;
    }

    @Override
    public List<Stop> getStops() {
        return stops;
    }

    public void addEnd(End end) {
        if (this.ends == null) this.ends = new ArrayList<End>();
        if (!this.ends.contains(end))
            this.ends.add(end);
    }

    public void removeEnd(String endId) {
        if (this.ends == null) return;
        End endToRemove = null;
        for (End end : this.ends) {
            if (end.getId().equals(endId))
                endToRemove = end;
        }
        if (endToRemove == null) return;
        this.ends.remove(endToRemove);
        if (this.ends.size() == 0) this.ends = null;
    }

    @Override
    public List<End> getEnds() {
        return ends;
    }

    public void addFail(Fail fail) {
        if (this.fails == null) this.fails = new ArrayList<Fail>();
        if (!this.fails.contains(fail))
            this.fails.add(fail);
    }

    public void removeFail(String failId) {
        if (this.fails == null) return;
        Fail failToRemove = null;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(failId))
                failToRemove = fail;
        }
        if (failToRemove == null) return;
        this.fails.remove(failToRemove);
        if (this.fails.size() == 0) this.fails = null;
    }

    @Override
    public List<Fail> getFails() {
        return fails;
    }

    public Fail getFail(String failId) {
        if (this.fails == null) return null;
        Fail failToReturn = null;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(failId))
                failToReturn = fail;
        }
        return failToReturn;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean canAddStop(String id) {
        if (this.stops == null) return true;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(id)) return false;
        }
        return true;
    }

    public boolean canAddFail(String id) {
        if (this.fails == null) return true;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(id)) return false;
        }
        return true;
    }

    public boolean canAddEnd(String id) {
        if (this.ends == null) return true;
        for (End end : this.ends) {
            if (end.getId().equals(id)) return false;
        }
        return true;
    }

    public boolean canAddTransitionElement(Element transitionElement) {
        if (transitionElement instanceof EndTransition) {
            if (this.ends == null) return true;
            for (End end : this.ends) {
                if (end.getId().equals(transitionElement.getId())) return false;
            }
        } else if (transitionElement instanceof StopTransition) {
            if (this.stops == null) return true;
            for (Stop stop : this.stops) {
                if (stop.getId().equals(transitionElement.getId())) return false;
            }
        } else if (transitionElement instanceof FailTransition) {
            if (this.fails == null) return true;
            for (Fail fail : this.fails) {
                if (fail.getId().equals(transitionElement.getId())) return false;
            }
        }
        return true;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
