package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.FlowElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 3/27/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "flow")
public class Flow extends FlowElement {

    @XmlAttribute(name = "next")
    private String nextElementId;

    @XmlElements({
            @XmlElement(name = "step", type = Step.class),
            @XmlElement(name = "flow", type = Flow.class),
            @XmlElement(name = "decision", type = Decision.class),
            @XmlElement(name = "split", type = Split.class)
    })
    private List<Element> elements = null;

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

    public Flow() {
    }

    ;

    public Flow(String id) {
        this.setId(id);
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public List<Element> getElements() {
        return this.elements;
    }

    public void addElement(Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(element);
    }

    public void addElement(int i, Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(i, element);
    }

    public Element getElement(String id) {
        if (this.elements != null)
            for (Element element : this.elements) {
                if (element.getId().equals(id)) return element;
            }
        return null;
    }

    public void removeElement(Element element) {
        this.elements.remove(element);
    }

    public String getNextElementId() {
        return this.nextElementId;
    }

    ;

    public void setNextElementId(String next) {
        this.nextElementId = next;
    }

    public void addNextElementId(String newNextElementId) {
        if (getNextTransition(newNextElementId) != null) return;
        if (this.nextElementId == null && this.nexts == null) {
            this.nextElementId = newNextElementId;
            return;
        }
        if (this.nextElementId != null && !this.nextElementId.equals("")) {
            this.nexts = new ArrayList<Next>();
            this.nexts.add(new Next("", nextElementId)); // makes next element from next attribute
            this.nextElementId = null;
        }
        this.nexts.add(new Next("", newNextElementId));
    }

    public Next getNextTransition(String id) {
        if (nextElementId != null && nextElementId.equals(id)) return new Next(null, nextElementId);
        if (nexts != null) {
            for (Next next : this.nexts) {
                if (next.getTo().equals(id)) return next;
            }
        }
        return null;
    }

    public void removeNextElementId(String nextElementIdToRemove) {
        if (this.nextElementId != null && this.nexts == null) {
            if (this.nextElementId.equals(nextElementIdToRemove)) {
                this.nextElementId = null;
                return;
            }
        }
        if (this.nextElementId == null && this.nexts != null) {
            for (Next next : this.nexts) {
                if (next.getTo().equals(nextElementIdToRemove)) {
                    this.nexts.remove(next);
                    break;
                }
            }
            if (this.nexts.size() == 0) {
                this.nexts = null;
                return;
            }
            if (this.nexts.size() == 1 && (this.nexts.get(0).getOn() == null || this.nexts.get(0).getOn().equals(""))) {
                this.nextElementId = this.nexts.get(0).getTo();
                this.nexts = null;
            }
        }
    }

    public List<Next> getAllNextTransitions() {
        List<Next> nextsToReturn = new ArrayList<Next>();
        if (this.nextElementId != null && this.nexts == null) {
            nextsToReturn.add(new Next(null, nextElementId));
        } else if (this.nextElementId == null && this.nexts != null) {
            for (Next next : this.nexts) {
                nextsToReturn.add(next);
            }
        }
        if (nextsToReturn.size() == 0) return null;
        return nextsToReturn;
    }

    public void editNextTransitionTo(String nextTransitionTo, String newOnValue) {
        if (this.nextElementId != null && this.nexts == null) {
            if (newOnValue == null || newOnValue.equals("")) return;
            else {
                this.nexts = new ArrayList<Next>();
                this.nexts.add(new Next(newOnValue, nextTransitionTo));
                this.nextElementId = null;
            }
        } else if (this.nextElementId == null && this.nexts != null) {
            if (this.nexts.size() == 1) {
                if (newOnValue == null || newOnValue.equals("")) {
                    this.nextElementId = nextTransitionTo;
                    this.nexts = null;
                } else {
                    this.nexts.get(0).setOn(newOnValue);
                }
            } else getNextTransition(nextTransitionTo).setOn(newOnValue);
        }
    }

    public void renameDestinationOfNextTransition(String nextTransitionTo, String newNextTransitionDestination) {
        if (this.nextElementId != null && this.nexts == null) {
            this.nextElementId = newNextTransitionDestination;
        } else if (this.nextElementId == null && this.nexts != null) {
            getNextTransition(nextTransitionTo).setTo(newNextTransitionDestination);
        }
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

}
