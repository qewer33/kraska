package org.example.gui.screen.component;

import javax.swing.*;
import java.awt.*;

public class ToolOptionsPanel extends JPanel {
    private int row = 0;
    private final GridBagConstraints constraints = new GridBagConstraints();

    public ToolOptionsPanel() {
        setLayout(new GridBagLayout());

        this.constraints.gridx = 0;
        this.constraints.gridy = row;
        this.constraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public void addComponent(JComponent component) {
        this.addComponent(component, 10);
    }

    public void addComponent(JComponent component, int topMargin) {
        this.row++;

        this.constraints.insets = new Insets(topMargin,0,0,0);
        this.constraints.gridy = row;

        this.add(component, this.constraints);
    }

    public void addComponentGroup(JComponent[] components) {
        this.addComponentGroup(components, 10, 5);
    }

    public void addComponentGroup(JComponent[] components, int topMargin) {
        this.addComponentGroup(components, topMargin, 5);
    }

    public void addComponentGroup(JComponent[] components, int topMargin, int betweenMargin) {
        for (int i = 0; i < components.length; i++) {
            this.row++;
            JComponent component = components[i];

            if (i == 0) this.constraints.insets = new Insets(topMargin,0,0,0);
            else this.constraints.insets = new Insets(betweenMargin,0,0,0);
            this.constraints.gridy = this.row;

            this.add(component, this.constraints);
        }
    }
}
