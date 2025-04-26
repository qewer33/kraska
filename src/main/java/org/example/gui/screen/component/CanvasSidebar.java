package org.example.gui.screen.component;

import org.example.app.color.ColorManager;
import org.example.app.tool.AbstractTool;
import org.example.app.tool.ToolOptionsProvider;
import org.example.gui.screen.component.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CanvasSidebar extends JPanel {
    private final JPanel toolOptionsContainer;
    private final JLabel titleLabel;
    private final ColorManager colorManager = ColorManager.getInstance();

    public CanvasSidebar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Color Section ===
        JLabel colorLabel = new JLabel("Colors");
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(colorLabel);

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        JPanel colorButtons = createColorButtonsPanel();
        colorPanel.add(colorButtons);
        add(colorPanel);

        add(Box.createVerticalStrut(20)); // spacing

        // === Tool Options Section ===
        titleLabel = new JLabel("Tool Options");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);

        toolOptionsContainer = new JPanel();
        toolOptionsContainer.setLayout(new BoxLayout(toolOptionsContainer, BoxLayout.Y_AXIS));
        toolOptionsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolOptionsContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(toolOptionsContainer);

        add(Box.createVerticalGlue()); // push everything up
    }

    private JPanel createColorButtonsPanel() {
        JPanel colorButtonsPanel = new JPanel();
        colorButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        colorButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton primaryColorBtn = new JButton();
        primaryColorBtn.setPreferredSize(new Dimension(35, 35));
        primaryColorBtn.setBackground(colorManager.getPrimary());
        primaryColorBtn.setOpaque(true);
        primaryColorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        primaryColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Primary Color", colorManager.getPrimary());
            if (chosen != null) {
                primaryColorBtn.setBackground(chosen);
                colorManager.setPrimary(chosen);
            }
        });

        JButton secondaryColorBtn = new JButton();
        secondaryColorBtn.setPreferredSize(new Dimension(35, 35));
        secondaryColorBtn.setBackground(colorManager.getSecondary());
        secondaryColorBtn.setOpaque(true);
        secondaryColorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        secondaryColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Secondary Color", colorManager.getSecondary());
            if (chosen != null) {
                secondaryColorBtn.setBackground(chosen);
                colorManager.setSecondary(chosen);
            }
        });

        colorManager.addColorChangeListener((primary, secondary) -> {
            primaryColorBtn.setBackground(primary);
            secondaryColorBtn.setBackground(secondary);
        });

        ImageIcon swapIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/swap.png")));

        JButton swapBtn = new JButton(swapIcon);
        swapBtn.setPreferredSize(new Dimension(40, 20));
        swapBtn.addActionListener(e -> {
            colorManager.swap();
        });

        colorButtonsPanel.add(Box.createHorizontalStrut(20));
        colorButtonsPanel.add(primaryColorBtn);
        colorButtonsPanel.add(swapBtn);
        colorButtonsPanel.add(secondaryColorBtn);

        colorButtonsPanel.add(Box.createVerticalStrut(10));

        JPanel paletteWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        paletteWrapper.setOpaque(false);

        paletteWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        paletteWrapper.add(new ColorPalette());   // new sexy palette oye

        colorButtonsPanel.add(paletteWrapper);

        return colorButtonsPanel;
    }

    public void updateToolOptions(AbstractTool tool) {
        titleLabel.setText(tool.getName() + " Options");

        toolOptionsContainer.removeAll();

        if (tool instanceof ToolOptionsProvider provider) {
            JPanel optionsPanel = provider.getToolOptionsPanel();
            toolOptionsContainer.add(optionsPanel);
        } else {
            toolOptionsContainer.add(new JLabel("No options available."));
        }

        toolOptionsContainer.revalidate();
        toolOptionsContainer.repaint();
    }
}
