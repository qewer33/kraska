package org.example.app.file;

import org.example.gui.canvas.Canvas;

import javax.imageio.ImageIO;
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

    public void save(File targetFile) {
        try {
            BufferedImage image = canvas.getImage();
            
            ImageIO.write(image, "png", targetFile);
            this.file = targetFile;
            this.lastModifiedDate = new Date();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
