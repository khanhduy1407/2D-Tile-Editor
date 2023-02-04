package com.nkduy.main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JLabel {

	Image image;
	int x, y, width, height;

	public ImagePanel() {
		//
	}

	public void drawGra(ImageIcon icon, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if(icon != null) {
			image = icon.getImage();
		}
		setOpaque(false);
		repaint();
	}

	public void drawGra(BufferedImage image, int x, int y, int width, int height) {

		this.image = image;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		setOpaque(false);
		repaint();
	}

	public void drawGra(Image image, int x, int y, int width, int height) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		setOpaque(false);
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(image != null) {
			g.drawImage(image, x, y, width, height, null);
		}
		if(image == null) {
			g.drawImage(null, 0, 0, 0, 0, null);
		}
	}

    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

}
