package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import processing.core.PVector;
import util.ImageLoader;

public class Instructions {
    private String instruction;
    private PVector pos;
    private BufferedImage img;

    public Instructions(int x, int y, String instruction){
        this.pos = new PVector(x, y);
        this.instruction = instruction;
        this.img = ImageLoader.loadImage("assets/instructionsBG.png");
    }

    public void displayInstruction(Graphics2D g2){
        AffineTransform transform = g2.getTransform();
        g2.translate(pos.x, pos.y);
        g2.scale(2, 1.25);

        // Draw the background image
        g2.drawImage(img, -img.getWidth() / 2, -img.getHeight() / 2, null);

        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(Color.BLACK);

        // Calculate text position
        int stringWidth = g2.getFontMetrics().stringWidth(instruction);
        int stringHeight = g2.getFontMetrics().getHeight();
        int textX = -stringWidth / 2;
        int textY = stringHeight / 2;

        // Draw the text centered relative to the image
        g2.drawString(instruction, textX, textY);

        g2.setTransform(transform);
    }
}
