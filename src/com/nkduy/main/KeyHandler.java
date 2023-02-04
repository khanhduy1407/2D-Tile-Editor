package com.nkduy.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	Main main;

	public KeyHandler(Main main) {
		this.main = main;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		if(e.isShiftDown() &&  e.getKeyCode() == KeyEvent.VK_W) {
			main.moveAll("up");
		} else if(e.isShiftDown() &&  e.getKeyCode() == KeyEvent.VK_S) {
			main.moveAll("down");
		} else if(e.isShiftDown() &&  e.getKeyCode() == KeyEvent.VK_A) {
			main.moveAll("left");
		} else if(e.isShiftDown() &&  e.getKeyCode() == KeyEvent.VK_D) {
			main.moveAll("right");
		}

		if(e.isControlDown() &&  e.getKeyCode() == KeyEvent.VK_S) {
			main.saveMap();
		} else if(code == KeyEvent.VK_W) {
			main.moveUp();
		} else if(code == KeyEvent.VK_S) {
			main.moveDown();
		} else if(code == KeyEvent.VK_A) {
			main.moveLeft();
		} else if(code == KeyEvent.VK_D) {
			main.moveRight();
		} else if(code == KeyEvent.VK_G) {
			main.gridOnOff();
		} else if(e.getKeyCode() == KeyEvent.VK_Q) {
			main.zoomIn();
		} else if(e.getKeyCode() == KeyEvent.VK_E) {
			main.zoomOut();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
