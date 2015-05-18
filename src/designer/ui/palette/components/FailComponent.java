package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class FailComponent extends PaletteComponent {

    private static final String FAIL_COMPONENT_TYPE = "Fail";

    public FailComponent(PalettePanel panel) {
        super(FAIL_COMPONENT_TYPE, panel);
    }


    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Fail32.png")));
    }
}