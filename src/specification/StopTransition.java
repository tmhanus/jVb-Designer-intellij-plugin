package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.StopElement;

import java.util.HashMap;
import java.util.Map;

/**
 *  Created by Tomas Hanus on 4/17/2015.
 */
public class StopTransition extends StopElement {

    private String exitStatus;
    private String restart;
    private HashMap<Element, Stop> stopOccurences;

    public StopTransition() {
        this.stopOccurences = new HashMap<Element, Stop>();
    }

    public StopTransition(String id) {
        this.setId(id);
        this.stopOccurences = new HashMap<Element, Stop>();
    }

    public StopTransition(String id, String exitStatus, String restart) {
        this.setId(id);
        this.exitStatus = exitStatus;
        this.restart = restart;
        this.stopOccurences = new HashMap<Element, Stop>();
    }

    public void addStopForElement(Element element) {
        Stop newStop = new Stop(getId(), exitStatus, restart);
        if (element.canAddTransitionElement(this)) {
            if (element instanceof Decision) {
                if (((Decision) element).canAddStop(getId())) {
                    ((Decision) element).addStop(newStop);
                    this.stopOccurences.put(element, newStop);
                }
            } else if (element instanceof Step) {
                if (((Step) element).canAddStop(getId())) {
                    ((Step) element).addStop(newStop);
                    this.stopOccurences.put(element, newStop);
                }
            } else if (element instanceof Flow) {
                if (((Flow) element).canAddStop(getId())) {
                    ((Flow) element).addStop(newStop);
                    this.stopOccurences.put(element, newStop);
                }
            }
        }
    }

    public void addStopForElement(Element element, Stop stop) {
        this.stopOccurences.put(element, stop);
    }

    public void removeStopForElement(Element element) {
        if (element instanceof Decision) {
            ((Decision) element).removeStop(getId());
        } else if (element instanceof Step) {
            ((Step) element).removeStop(getId());
        } else if (element instanceof Flow) {
            ((Flow) element).removeStop(getId());
        }
        this.stopOccurences.remove(element);
    }

//
//    public void setStepId(String newId) {
//        if (this.stopOccurences == null) return;
//        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
//            Element key = entry.getKey();
//            Stop value = entry.getValue();
//            if (key instanceof Decision) {
//                ((Decision) key).getStop(getId()).setId(newId);
//            }
//            else if (key instanceof Step) {
//                ((Step) key).getStop(getId()).setId(newId);
//            }
//            else if (key instanceof Flow) {
//                ((Flow) key).getStop(getId()).setId(newId);
//            }
//            value.setId(newId);
//        }
//        setId(newId);
//    }

    public void setExitStatus(String newExitStatus) {
        this.exitStatus = newExitStatus;
        if (this.stopOccurences == null) return;
        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
            Element key = entry.getKey();
            Stop value = entry.getValue();
            if (key instanceof Decision) {
                ((Decision) key).getStop(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Step) {
                ((Step) key).getStop(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Flow) {
                ((Flow) key).getStop(getId()).setExitStatus(newExitStatus);
            }
            value.setExitStatus(newExitStatus);
        }
    }

//    public void setStepRestart(String newRestart) {
//        this.restart = newRestart;
//        if (this.stopOccurences == null) return;
//        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
//            Element key = entry.getKey();
//            Stop value = entry.getValue();
//            if (key instanceof Decision) {
//                ((Decision) key).getStop(getId()).setRestart(newRestart);
//            }
//            else if (key instanceof Step) {
//                ((Step) key).getStop(getId()).setRestart(newRestart);
//            }
//            value.setRestart(newRestart);
//        }
//    }


    public void setRestart(String restart) {
        this.restart = restart;
        if (this.stopOccurences == null) return;
        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
            Element key = entry.getKey();
            Stop value = entry.getValue();
            if (key instanceof Decision) {
                ((Decision) key).getStop(getId()).setRestart(restart);
            } else if (key instanceof Step) {
                ((Step) key).getStop(getId()).setRestart(restart);
            } else if (key instanceof Flow) {
                ((Flow) key).getStop(getId()).setRestart(restart);
            }
            value.setRestart(restart);
        }
    }

    public String getOnForElement(Element element) {
        if (this.stopOccurences == null) return null;
        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
            Element key = entry.getKey();
            Stop value = entry.getValue();
            if (element.getId().equals(key.getId())) {
                return value.getOn();
            }
        }
        return null;
    }

    public void setOnForElement(Element element, String newOn) {
        if (this.stopOccurences == null) return;
        for (Map.Entry<Element, Stop> entry : this.stopOccurences.entrySet()) {
            Element key = entry.getKey();
            Stop value = entry.getValue();

            if (element.getId().equals(key.getId())) {
                if (key instanceof Decision) {
                    ((Decision) key).getStop(getId()).setOn(newOn);
                }
                if (key instanceof Step) {
                    ((Step) key).getStop(getId()).setOn(newOn);
                }
                if (key instanceof Flow) {
                    ((Flow) key).getStop(getId()).setOn(newOn);
                }
                value.setOn(newOn);
            }
        }
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public String getRestart() {
        return restart;
    }
}
