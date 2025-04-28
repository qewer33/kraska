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
    private final JLabel toolOptionsLabel;
    private final ColorManager colorManager = ColorManager.getInstance();

    public CanvasSidebar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        ToolOptionsPanel panel = new ToolOptionsPanel();

        // === Color Section ===
        JLabel colorLabel = new JLabel("Colors");
        colorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        JPanel colorButtons = createColorButtonsPanel();

        JPanel palette = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        palette.setOpaque(false);
        palette.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 0));
        palette.add(new ColorPalette());

        // === Tool Options Section ===
        toolOptionsLabel = new JLabel("Tool Options");
        toolOptionsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        toolOptionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolOptionsContainer = new ToolOptionsPanel();

        panel.addComponentGroup(new JComponent[]{colorLabel, colorButtons, palette}, 0);
        panel.addComponentGroup(new JComponent[]{toolOptionsLabel, toolOptionsContainer}, 30);

        add(panel, BorderLayout.NORTH);
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

        colorButtonsPanel.add(Box.createHorizontalStrut(35));
        colorButtonsPanel.add(primaryColorBtn);
        colorButtonsPanel.add(swapBtn);
        colorButtonsPanel.add(secondaryColorBtn);

        return colorButtonsPanel;
    }

    public void updateToolOptions(AbstractTool tool) {
        toolOptionsLabel.setText(tool.getName() + " Options");

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
