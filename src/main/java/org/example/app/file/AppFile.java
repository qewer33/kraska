package org.example.app.file;

import org.example.gui.canvas.Canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AppFile {

    private static AppFile instance; //Bunu koymasam application class'ını değiştirmek zorunda kalacaktım

    private final Canvas canvas;
    private File file;
    private Date creationDate;
    private Date lastModifiedDate;

    public AppFile(Canvas canvas) {
        this.canvas = canvas;
        AppFile.instance = this; // set global access
    }

    public static void setInstance(AppFile instance){
        AppFile.instance = instance;
    }

    public static AppFile getInstance() {
        return instance;
    }

    public void open(File imageFile){
        try{
            BufferedImage image = ImageIO.read(imageFile);
            canvas.setImage(image);
            this.file = imageFile;
            this.creationDate = new Date(imageFile.lastModified());
            this.lastModifiedDate = new Date();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void save() {
        if (this.file != null) {
            saveToFile(this.file);
            JOptionPane.showMessageDialog(null, "File is successfully saved!");
        } else {
            saveAs();
        }
    }

    public void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
            }
            saveToFile(selectedFile);
            this.file = selectedFile;
            this.creationDate = new Date();
        }
    }
    private void saveToFile(File targetFile) {
        try {
            BufferedImage image = canvas.getImage();
            ImageIO.write(image, "png", targetFile);
            this.lastModifiedDate = new Date();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save file.");
        }
    }
}