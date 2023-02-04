package com.nkduy.main;

import javax.swing.*;
import java.awt.*;

public class BWButton extends JButton {

	Font font1 = new Font("Arial", Font.BOLD, 15);

	public BWButton() {
		setBackground(Color.black);
		setForeground(Color.white);
		setFocusPainted(false);
		setFont(font1);
	}
}
