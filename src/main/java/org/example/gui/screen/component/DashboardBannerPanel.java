package org.example.gui.screen.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class DashboardBannerPanel extends JPanel {
    private final int fixedHeight = 150;

    public final JButton newProjectButton;
    public final JButton loadProjectButton;

    public DashboardBannerPanel() {
        setLayout(new BorderLayout());

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false); // transparent background

        // Add padding between buttons
        newProjectButton = new JButton("Create New Project");
        ImageIcon newProjectIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/new.png")));
        newProjectButton.setIcon(newProjectIcon);

        loadProjectButton = new JButton("Load File As Project");
        ImageIcon loadProjectIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/open.png")));
        loadProjectButton.setIcon(loadProjectIcon);

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(newProjectButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(loadProjectButton);

        // Wrap in another panel for bottom-right anchoring with padding
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 25, 25)); // bottom-right padding
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = fixedHeight;

        ImageIcon leftImage =
                new ImageIcon(Objects.requireNonNull(getClass().getResource("/banner_logo.png")));
        ImageIcon rightImage =
                new ImageIcon(Objects.requireNonNull(getClass().getResource("/banner_art.png")));

        g.drawImage(leftImage.getImage(), 0, 0, leftImage.getIconWidth(), h, this);
        g.drawImage(rightImage.getImage(), w - rightImage.getIconWidth(), 0, rightImage.getIconWidth(), h, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, fixedHeight);
    }
}