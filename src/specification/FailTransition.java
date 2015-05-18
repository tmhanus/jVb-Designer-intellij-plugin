package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.FailElement;

import java.util.HashMap;
import java.util.Map;

/**
 *  Created by Tomas Hanus on 4/18/2015.
 */
public class FailTransition extends FailElement {
    private String exitStatus;

    private HashMap<Element, Fail> failOccurences;

    public FailTransition() {
        this.failOccurences = new HashMap<Element, Fail>();
    }

    public FailTransition(String id) {
        this.setId(id);
        this.failOccurences = new HashMap<Element, Fail>();
    }

    public FailTransition(String id, String exitStatus) {
        this.setId(id);
        this.exitStatus = exitStatus;
        this.failOccurences = new HashMap<Element, Fail>();
    }

    public void addFailForElement(Element element) {
        Fail newFail = new Fail(getId(), exitStatus);
        if (element instanceof Decision) {
            if (((Decision) element).canAddFail(getId())) {
                ((Decision) element).addFail(newFail);
                this.failOccurences.put(element, newFail);
            }
        } else if (element instanceof Step) {
            if (((Step) element).canAddFail(getId())) {
                ((Step) element).addFail(newFail);
                this.failOccurences.put(element, newFail);
            }
        } else if (element instanceof Flow) {
            if (((Flow) element).canAddFail(getId())) {
                ((Flow) element).addFail(newFail);
                this.failOccurences.put(element, newFail);
            }
        }
    }

    public void addFailForElement(Element element, Fail fail) {
        this.failOccurences.put(element, fail);
    }

    public void removeFailForElement(Element element) {
        if (element instanceof Decision) {
            ((Decision) element).removeFail(getId());
        } else if (element instanceof Step) {
            ((Step) element).removeFail(getId());
        } else if (element instanceof Flow) {
            ((Flow) element).removeFail(getId());
        }
        this.failOccurences.remove(element);
    }

    public String getOnForElement(Element element) {
        if (this.failOccurences == null) return null;
        for (Map.Entry<Element, Fail> entry : this.failOccurences.entrySet()) {
            Element key = entry.getKey();
            Fail value = entry.getValue();
            if (element.getId().equals(key.getId())) {
                return value.getOn();
            }
        }
        return null;
    }

    public void setOnForElement(Element element, String newOn) {
        if (this.failOccurences == null) return;
        for (Map.Entry<Element, Fail> entry : this.failOccurences.entrySet()) {
            Element key = entry.getKey();
            Fail value = entry.getValue();

            if (element.getId().equals(key.getId())) {
                if (key instanceof Decision) {
                    ((Decision) key).getFail(getId()).setOn(newOn);
                } else if (key instanceof Step) {
                    ((Step) key).getFail(getId()).setOn(newOn);
                } else if (key instanceof Flow) {
                    ((Flow) key).getFail(getId()).setOn(newOn);
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
        if (this.failOccurences == null) return;
        for (Map.Entry<Element, Fail> entry : this.failOccurences.entrySet()) {
            Element key = entry.getKey();
            Fail value = entry.getValue();
            if (key instanceof Decision) {
                ((Decision) key).getFail(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Step) {
                ((Step) key).getFail(getId()).setExitStatus(newExitStatus);
            } else if (key instanceof Flow) {
                ((Flow) key).getFail(getId()).setExitStatus(newExitStatus);
            }
            value.setExitStatus(newExitStatus);
        }
    }
}