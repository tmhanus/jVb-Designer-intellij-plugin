package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.EndElement;

import java.util.HashMap;
import java.util.Map;

/**
 *  Created by Tomas Hanus on 4/17/2015.
 */
public class EndTransition extends EndElement {
    private String exitStatus;

    private HashMap<Element, End> endOccurences;

    public EndTransition() {
        this.endOccurences = new HashMap<Element, End>();
    }

    public EndTransition(String id) {
        this.setId(id);
        this.endOccurences = new HashMap<Element, End>();
    }

    public EndTransition(String id, String exitStatus) {
        this.setId(id);
        this.exitStatus = exitStatus;
        this.endOccurences = new HashMap<Element, End>();
    }

    public void addEndForElement(Element element) {
        End newEnd = new End(getId(), exitStatus);
        if (element instanceof Decision) {
            if (((Decision) element).canAddEnd(getId())) {
                ((Decision) element).addEnd(newEnd);
                this.endOccurences.put(element, newEnd);
            }
        } else if (element instanceof Step) {
            if (((Step) element).canAddEnd(getId())) {
                ((Step) element).addEnd(newEnd);
                this.endOccurences.put(element, newEnd);
            }
        } else if (element instanceof Flow) {
            if (((Flow) element).canAddEnd(getId())) {
                ((Flow) element).addEnd(newEnd);
                this.endOccurences.put(element, newEnd);
            }
        }
    }

    public void addEndForElement(Element element, End end) {
        this.endOccurences.put(element, end);
    }

    public void removeEndForElement(Element element) {
        if (element instanceof Decision) {
            ((Decision) element).removeEnd(getId());
        } else if (element instanceof Step) {
            ((Step) element).removeEnd(getId());
        } else if (element instanceof Flow) {
            ((Flow) element).removeEnd(getId());
        }
        this.endOccurences.remove(element);
    }

    public String getOnForElement(Element element) {
        if (this.endOccurences == null) return null;
        for (Map.Entry<Element, End> entry : this.endOccurences.entrySet()) {
            Element key = entry.getKey();
            End value = entry.getValue();
            if (element.getId().equals(key.getId())) {
                return value.getOn();
            }
        }
        return null;
    }

    public void setOnForElement(Element element, String newOn) {
        if (this.endOccurences == null) return;
        for (Map.Entry<Element, End> entry : this.endOccurences.entrySet()) {
            Element key = entry.getKey();
            End value = entry.getValue();

            if (element.getId().equals(key.getId())) {
                if (key instanceof Decision) {
                    ((Decision) key).getEnd(getId()).setOn(newOn);
                } else if (key instanceof Step) {
                    ((Step) key).getEnd(getId()).setOn(newOn);
                } else if (key instanceof Flow) {
                    ((Flow) key).getEnd(getId()).setOn(newOn);
                }
                value.setOn(newOn);
            }
        }
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String newExitStatus) {
        this.exitStatus = newExitStatus;
        if (this.endOccurences == null) return;
        for (Map.Entry<Element, End> entry : this.endOccurences.entrySet()) {
            Element key = entry.getKey();
            End value = entry.getValue();
            if (key instanceof Decision) {
                ((Decision) key).getEnd(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Step) {
                ((Step) key).getEnd(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Flow) {
                ((Flow) key).getEnd(getId()).setExitStatus(newExitStatus);
            }
            value.setExitStatus(newExitStatus);
        }
    }
}
