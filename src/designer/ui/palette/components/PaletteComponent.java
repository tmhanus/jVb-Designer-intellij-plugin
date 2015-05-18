package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public abstract class PaletteComponent extends JLabel implements DragGestureListener, DragSourceListener {
    /**
     * Indicates if this palette component is active or not
     */
    private boolean isActive;

    private Color oldBgColor;

    /**
     * Icon of Palette Component
     */
    private Icon icon;

    /**
     * Name of ElementType that this component is representing
     */
    public String componentType;

    /**
     * Palette Panel
     */
    private PalettePanel palettePanel;
    private MouseListener mouseListener;


    // Drag and Drop Source
    private DragSource dragSource;


    ///////// FLAVORS
    DataFlavor dataFlavor = new DataFlavor(String.class,
            String.class.getSimpleName());

    public PaletteComponent(String type, PalettePanel panel) {
        this.mouseListener = addMouseListener();// Set mouse Listener on Component
        this.setText(type);         // Set label text
        this.componentType = type;  // Store information about type e.g. "Batchlet"
        this.isActive = false;
        this.palettePanel = panel;
        this.oldBgColor = this.getBackground();

        this.addMouseListener(this.mouseListener);

        this.setOpaque(true);
        // Padding for JLabel
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.setBorder(BorderFactory.createCompoundBorder(null, paddingBorder));
        this.setPreferredSize(new Dimension(130, 35));
        this.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Flow32.png")));

        // Create Source for Drag And Drop
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    /**
     * Make this component active and change it's background color
     */
    public void activate() {
        PaletteComponent oldActiveComponent = this.palettePanel.getActivePComponent();
        if (oldActiveComponent != null) {
            oldActiveComponent.deactivate();
        }
        palettePanel.setActivePComponent(this);
        this.oldBgColor = this.getBackground();
        this.setBackground(new Color(47, 101, 202));
        this.isActive = true;
    }

    /**
     * Make this component inactive and change it's background color back
     */
    public void deactivate() {
        if (this.oldBgColor != null) this.setBackground(oldBgColor);
        else this.setBackground(Color.DARK_GRAY);

        this.isActive = false;
    }


    private MouseListener addMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                activate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        };
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent event) {
        Cursor cursor = null;
        PaletteComponent tmpComponent = (PaletteComponent) event.getComponent();

        if (event.getDragAction() == DnDConstants.ACTION_COPY) {
            cursor = DragSource.DefaultCopyDrop;
        }
        String tmpText = tmpComponent.getText();

        dragSource.startDrag(event, DragSource.DefaultCopyDrop, new TransferableText(tmpText), this);
    }

    class TransferableText implements Transferable {

        private String tString;

        public TransferableText(String txt) {
            this.tString = txt;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{dataFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(dataFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {

            if (flavor.equals(dataFlavor))
                return tString;
            else
                throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }
}
