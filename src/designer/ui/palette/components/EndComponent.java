package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class EndComponent extends PaletteComponent {

    private static final String END_COMPONENT_TYPE = "End";

    public EndComponent(PalettePanel panel) {
        super(END_COMPONENT_TYPE, panel);
    }


    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/End32.png")));
    }
}