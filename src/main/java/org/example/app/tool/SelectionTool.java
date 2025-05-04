package org.example.app.tool;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.canvas.OverlayPainter;
import org.example.gui.canvas.selection.SelectionManager;
import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SelectionTool extends AbstractTool implements CanvasPainter, OverlayPainter, ToolOptionsProvider {
    private final SelectionManager selectionManager = SelectionManager.getInstance();

    private JButton applyButton;
    private JLabel selectionCoordsLabel;
    private JLabel selectionSizeLabel;
    private JLabel selectionRotationLabel;

    public SelectionTool() {
        super("Selection");
    }

    @Override
    public void onActivate() {
        selectionManager.restrictToolInput = false;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        Point clicked = canvas.getUnzoomedPoint(e.getPoint());

        selectionManager.startCreating();

        /*
        applyButton.addActionListener(e1 -> {
            applySelection(canvas);
            canvas.repaint();
        });
         */

        canvas.repaint();
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        Point current = canvas.getUnzoomedPoint(e.getPoint());

        // updateInfoLabels();

        canvas.repaint();
    }

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {

        // updateInfoLabels();

        canvas.repaint();
    }

    @Override
    public void paintOverlay(Graphics2D g2d, double zoomFactor) {

    }

    public void applySelection(Canvas canvas) {

    }

    /*
    public void updateInfoLabels() {
        if (currentSelection == null) {
            selectionCoordsLabel.setText("    Coordinates: None");
            selectionSizeLabel.setText("    Size: None");
            selectionRotationLabel.setText("    Rotation: None");
        } else {
            selectionCoordsLabel.setText("    Coordinates: " + currentSelection.getBounds().x + ", " + currentSelection.getBounds().y);
            selectionSizeLabel.setText("    Size: " + currentSelection.getBounds().width + "x" + currentSelection.getBounds().height);
            selectionRotationLabel.setText("    Rotation: " + (int)(rotationAngle * 100) + "°");
        }
    }
     */

    @Override
    public JPanel getToolOptionsPanel() {
        ToolOptionsPanel panel = new ToolOptionsPanel();

        /*
        applyButton = new JButton("Apply Selection");

        JLabel selectionInfoLabel = new JLabel("Selection Info:");
        selectionCoordsLabel = new JLabel("    Coordinates: " + (currentSelection == null ? "None" : currentSelection.getBounds().toString()));
        selectionSizeLabel = new JLabel("    Size: " + (currentSelection == null ? "None" : currentSelection.getBounds().width + "x" + currentSelection.getBounds().height));
        selectionRotationLabel = new JLabel("    Rotation: " + (currentSelection == null ? "None" : (int)(rotationAngle * 100) + "%"));

        JCheckBox copyCheckBox = new JCheckBox("Copy Selection", this.copy);
        copyCheckBox.addActionListener(e -> {
            this.copy = copyCheckBox.isSelected();
        });

        panel.addComponent(applyButton);
        panel.addComponentGroup(
                new JComponent[]{
                        selectionInfoLabel,
                        selectionCoordsLabel,
                        selectionSizeLabel,
                        selectionRotationLabel
                }
        );
        panel.addComponent(copyCheckBox);
        panel.addComponent(copyCheckBox);
        panel.addEmptySpace();
        */

        return panel;
    }
}