package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public class BatchletComponent extends PaletteComponent {

    private static final String BATCHLET_COMPONENT_TYPE = "Batchlet";

    public BatchletComponent(PalettePanel panel) {
        super(BATCHLET_COMPONENT_TYPE, panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Batchlet32.png")));
    }
}
