package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public class ChunkComponent extends PaletteComponent {
    private static final String CHUNK_COMPONENT_TYPE = "Chunk";

    public ChunkComponent(PalettePanel panel) {
        super(CHUNK_COMPONENT_TYPE, panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Chunk32.png")));
    }
}
