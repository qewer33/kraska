package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TextTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {

    private final ColorManager colorManager;
    private Color color;
    private int fontSize = 24;
    private String fontName = "Arial";
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean activateSelection = false;

    public TextTool() {
        super("Text Tool");
        this.colorManager = ColorManager.getInstance();
        this.color = colorManager.getPrimary();
    }

    // Create and return the options panel for the text tool
    @Override
    public JPanel getToolOptionsPanel() {
        ToolOptionsPanel panel = new ToolOptionsPanel();

        // Font selection
        JLabel fontLabel = new JLabel("Font:");
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontComboBox = new JComboBox<>(fonts);
        fontComboBox.setSelectedItem(fontName);

        // Font size slider
        JLabel sizeLabel = new JLabel("Size: " + fontSize);
        JSlider sizeSlider = new JSlider(8, 72, fontSize);

        // Bold and Italic checkboxes
        JCheckBox boldCheck = new JCheckBox("Bold", isBold);
        JCheckBox italicCheck = new JCheckBox("Italic", isItalic);

        // Listeners to update settings on change
        fontComboBox.addActionListener(e -> fontName = (String) fontComboBox.getSelectedItem());
        sizeSlider.addChangeListener(e -> {
            fontSize = sizeSlider.getValue();
            sizeLabel.setText("Size: " + fontSize);
        });
        boldCheck.addActionListener(e -> isBold = boldCheck.isSelected());
        italicCheck.addActionListener(e -> isItalic = italicCheck.isSelected());

        // Activate selection checkbox
        JCheckBox activateSelectionBox = new JCheckBox("Select after creation");
        activateSelectionBox.setSelected(activateSelection);
        activateSelectionBox.addItemListener(e -> activateSelection = activateSelectionBox.isSelected());
        activateSelectionBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to the panel
        panel.addComponentGroup(new JComponent[]{fontLabel, fontComboBox});
        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{boldCheck, italicCheck});
        panel.addComponent(activateSelectionBox);

        return panel;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 && e.getButton() != MouseEvent.BUTTON3) return;

        // Set primary or secondary color based on mouse button
        this.color = e.getButton() == MouseEvent.BUTTON1 ? colorManager.getPrimary() : colorManager.getSecondary();
        Point point = canvas.getUnzoomedPoint(e.getPoint());

        // Show a text input dialog
        JTextField textField = new JTextField(20);
        int result = JOptionPane.showConfirmDialog(null, textField, "Enter Text", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String userText = textField.getText();
            if (!userText.isEmpty()) {
                drawText(canvas, point.x, point.y, userText);
            }
        }
    }

    // Draw the specified text at the given position
    private void drawText(Canvas canvas, int x, int y, String text) {
        int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
        Font font = new Font(fontName, style, fontSize);

        Graphics2D g2d = canvas.getCanvasImage().createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.drawString(text, x, y);
        g2d.dispose();
        canvas.repaint();
    }

    @Override public void onMouseDrag(Canvas canvas, MouseEvent e) {}
    @Override public void onMouseRelease(Canvas canvas, MouseEvent e) {}
}
