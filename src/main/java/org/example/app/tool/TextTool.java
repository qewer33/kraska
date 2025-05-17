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
    private JTextField activeTextField = null;
    Point point;


    public TextTool() {
        super("Text");
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
        fontComboBox.setMaximumRowCount(10);

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

        // Add components to the panel
        panel.addComponentGroup(new JComponent[]{fontLabel, fontComboBox});
        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{boldCheck, italicCheck});

        return panel;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {

        if (activeTextField != null) return;
        if (e.getButton() != MouseEvent.BUTTON1 && e.getButton() != MouseEvent.BUTTON3) return;

        this.color = e.getButton() == MouseEvent.BUTTON1 ? colorManager.getPrimary() : colorManager.getSecondary();

        Point zoomedPoint = e.getPoint();
        point = canvas.getUnzoomedPoint(zoomedPoint);
        activeTextField = new JTextField();
        activeTextField.setFont(new Font(fontName, (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0), (int) (fontSize*canvas.getZoomFactor())));
        activeTextField.setForeground(color);
        activeTextField.setOpaque(false);
        activeTextField.setBackground(new Color(0, 0, 0, 0));
        activeTextField.setBorder(null);
        activeTextField.setBounds(zoomedPoint.x, zoomedPoint.y, 200, (int)((fontSize + 10)*canvas.getZoomFactor()));
        activeTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateWidth(canvas); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateWidth(canvas); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateWidth(canvas); }
        });
        activeTextField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {}

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String userText = activeTextField.getText();
                canvas.remove(activeTextField);
                canvas.repaint();
                if (!userText.isEmpty()) {
                    drawText(canvas, point.x, point.y + fontSize, userText);
                }
                activeTextField = null;
            }
        });

        canvas.addPropertyChangeListener("zoomFactor", evt -> {
            if (activeTextField == null) return;
            activeTextField.setFont(new Font(fontName, (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0), (int) (fontSize*canvas.getZoomFactor())));
            updateWidth(canvas);
        });

        canvas.getViewer().getOverlayPanel().setLayout(null);
        canvas.getViewer().getOverlayPanel().add(activeTextField);
        canvas.repaint();
        activeTextField.requestFocus();

        activeTextField.addActionListener(ev -> {
            String userText = activeTextField.getText();
            canvas.getViewer().getOverlayPanel().remove(activeTextField);
            canvas.repaint();
            if (!userText.isEmpty()) {
                drawText(canvas, point.x, point.y + fontSize, userText);
            }
        });
    }

    private void updateWidth(Canvas canvas) {
        FontMetrics fm = activeTextField.getFontMetrics(activeTextField.getFont());
        int textWidth = fm.stringWidth(activeTextField.getText());
        activeTextField.setBounds(
                (int)(point.getX()*canvas.getZoomFactor()),
                (int)(point.getY()*canvas.getZoomFactor()),
                Math.max(50, textWidth + 20),
                (int)((fontSize + 10)*canvas.getZoomFactor())
        );
        canvas.repaint();
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
