package com.nkduy.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Main {

	// Simple 2D Tile Editor
	// Developed by NKDuy (2023)

	double version = 1.01;
	GUI gui = new GUI(this);
	ArrayList<Tile> tiles = new ArrayList<>();

	int maxWorldCol;
	int maxWorldRow;
	int mapTileNum[][];

	// Tile Selection Panel
	int selectedTileNum;
	boolean settingSolid = false;

	// Editor Panel
	int selectedEditorPanel = 1;
	int selectedCol, selectedRow;
	int topLeftCol = 0;
	int topLeftRow = 0;
	boolean gridOn = true;

	// SAVE & LOAD
	String tileImageDirectory;
	String tileSheetDirectory;
	String tileSheetName;
	String mapFileDirectory;
	String mapName;
	String tileDataFileDirectory;
	String tileDataFileName;

	// WorldMap
	BufferedImage worldImage;
	Graphics2D wg2;

	// MEMO
	// MouseWheelListener calls the zoom in/out twice, so it's not working properly

	// TODO

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		gui.setupGUI();
		setMapSize(50); // Default Map Size
		gui.scale = gui.medium;
		loadConfig();
		gui.setEditorSize(gui.scale);
	}

	// CONFIG
	public void saveConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("system.txt"));
			bw.write("" + gui.scale);

			bw.close();
		} catch (Exception e) {
			System.out.println("Save Config Exception!");
		}
	}

	public void loadConfig() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("system.txt"));
			gui.scale = Double.parseDouble(br.readLine());

			br.close();
		} catch (Exception e) {
			System.out.println("Load Config Exception!");
		}
	}

	// FILE MENU
	public void createNewProject() {
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "Oops, no");

		int answer = JOptionPane.showConfirmDialog
				(null, "Start a new project? \nThe unsaved map will be lost.\n ","New Project",JOptionPane.YES_NO_OPTION);

		if(answer == JOptionPane.YES_OPTION) {
			resetMapTileNum();
			resetTileSelectionImages();
			resetWorldImage();
			mapFileDirectory = null;
			mapName = null;
		}
	}

	public void selectDirectory(String type) {
		if (type.equals("tile")) {
			FileDialog fd = new FileDialog(gui.window, "Select a tile", FileDialog.LOAD);
			fd.setVisible(true);

			if (fd.getFile() != null) {
				tileImageDirectory = fd.getDirectory();
				importTile();
			}
		}
		if (type.equals("sheet")) {
			FileDialog fd = new FileDialog(gui.window, "Select a tile sheet", FileDialog.LOAD);
			fd.setVisible(true);

			if (fd.getFile() != null) {
				tileSheetName = fd.getFile();
				tileSheetDirectory = fd.getDirectory();
				importTileSheet();
			}
		} else if (type.equals("saveData")) {
			FileDialog fd = new FileDialog(gui.window, "Save Tile Data", FileDialog.SAVE);
			fd.setVisible(true);

			if (fd.getFile()!=null) {
				tileDataFileName = fd.getFile();
				tileDataFileDirectory = fd.getDirectory();
				saveTileData();
			}
		} else if (type.equals("loadData")) {
			FileDialog fd = new FileDialog(gui.window, "Load Tile Data", FileDialog.LOAD);
			fd.setVisible(true);

			if(fd.getFile()!=null) {
				tileDataFileName = fd.getFile();
				tileDataFileDirectory = fd.getDirectory();
				loadTileData();
			}
		}
	}

	// IMPORT TILE
	private void importTile() {
		// Reset current tiles
		resetTileSelectionImages();

		File path = new File(tileImageDirectory);
		File files[] = path.listFiles();

		ArrayList<File> filess = new ArrayList<>();

		// Only get PNG or JPG
		for(int i = 0; i < files.length; i++) {
			if (files[i].getName().contains("png")) {
				filess.add(files[i]);
			}
			if (files[i].getName().contains("jpg")) {
				filess.add(files[i]);
			}
		}

		tiles.clear();

		for (int i = 0; i < filess.size(); i++) {
			try {
				// Get file name and image
				tiles.add(new Tile());
				tiles.get(i).name = filess.get(i).getName();
				tiles.get(i).image = ImageIO.read(filess.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		scaleImage();
		setIcons();
		refreshEditorImage();
	    refreshWorldImage();
	}

	private void importTileSheet() {
		File file = new File(tileSheetDirectory + tileSheetName);

		try {
			BufferedImage tileSheet = ImageIO.read(file);
			int sheetCol = tileSheet.getWidth()/16;
			int sheetRow = tileSheet.getHeight()/16;

			int i = 0;
			int col = 0;
			int row = 0;

			while(col < sheetCol && row < sheetRow) {
				tiles.add(new Tile());
				tiles.get(i).image = tileSheet.getSubimage(col*16, row*16, 16, 16);
				col++;
				i++;

				if(col == sheetCol) {
					col = 0;
					row++;
				}
			}
		} catch(IOException e) {
			System.out.println("tile sheet exception");
		}

		scaleImage();
		setIcons();
		refreshEditorImage();
	    refreshWorldImage();
	}

	public void scaleImage() {
		for (int i = 0; i < tiles.size(); i++) {
			Image image48 = tiles.get(i).image
					.getScaledInstance((int) (48*gui.scale), (int)(48*gui.scale), Image.SCALE_DEFAULT);
			if (i < tiles.size()) {
				tiles.get(i).icon = new ImageIcon(image48);
			}
		}
	}

	public void setIcons() {
		for(int i = 0; i < tiles.size(); i++) {
			gui.tileButton[i].setIcon(tiles.get(i).icon);
		}
	}

	// TILE DATA
	private void saveTileData() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tileDataFileDirectory + tileDataFileName));

			for(int i = 0; i < tiles.size(); i++) {
				bw.write(tiles.get(i).name);
				bw.newLine();
				bw.write(""+tiles.get(i).collision);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadTileData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(tileDataFileDirectory + tileDataFileName));

			String line;

			for (int i = 0; i < tiles.size(); i++) {
				if ((line = br.readLine()) != null) {
					tiles.get(i).name = line;

					if (br.readLine().equals("true")) {
						tiles.get(i).collision = true;
					} else {
						tiles.get(i).collision = false;
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setCollisionRedFrame();
	}

	private void setCollisionRedFrame() {
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i).collision == true) {
				gui.tileButton[i].setBorder(BorderFactory.createLineBorder(Color.red, 1));
			}
		}
	}

	// MAP
	public void saveMap() {
		if(mapName == null) {
			saveMapAs();
		} else {
			savingMap();
		}
	}

	public void saveMapAs() {
		FileDialog fd = new FileDialog(gui.window, "Save", FileDialog.SAVE);
		fd.setVisible(true);

		if (fd.getFile() != null) {
			mapName = fd.getFile();
			mapFileDirectory = fd.getDirectory();
			setTitle(mapName);
			savingMap();
		}
	}

	private void savingMap() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(mapFileDirectory + mapName));

			int col=0;
			int row=0;

			while(col < maxWorldCol && row < maxWorldRow){
				bw.write(mapTileNum[col][row] + " "); // add space for splitting the line when loading
				col++;

				if(col == maxWorldCol) {
					col = 0;
					row++;
					bw.newLine();
				}
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Exception!");
		}
	}

	public void loadMap() {
		// Open map file
		FileDialog fd = new FileDialog(gui.window, "Open", FileDialog.LOAD);
		fd.setVisible(true);

		if(fd.getFile()!=null) {
			mapName = fd.getFile();
			mapFileDirectory = fd.getDirectory();
		}

		// Load map file
		try {
			BufferedReader br0 = new BufferedReader(new FileReader(mapFileDirectory + mapName));

			// Check the map size
			String line0 = br0.readLine();
			String splitLine0[] = line0.split(" ");
			int mapSize = splitLine0.length;
			if (mapSize > maxWorldCol) {
				setMapSize(mapSize);
			}
			br0.close();

			// Load the map
			BufferedReader br = new BufferedReader(new FileReader(mapFileDirectory + mapName));
			int col=0;
			int row=0;

			while (col < mapSize && row < mapSize) {
				String line = br.readLine();

				while (col < mapSize) {
					String splitLine[] = line.split(" ");
					int tileNum = Integer.parseInt(splitLine[col]);
					mapTileNum[col][row] = tileNum;
					col++;
				}

				if (col == mapSize) {
					col = 0;
					row++;
				}
			}
			br.close();
			setTitle(mapName);
		} catch(Exception e) {
			System.out.println("Load Map Exception!");
		}

		resetWorldImage();
	}

	public void setTitle(String s) {
		gui.window.setTitle("Simple 2D Tile Editor v" + version + " - " + s);
	}

	// MAP SIZE
	public void showMapSizeNotification(int size) {
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "Oops, no");

		int result = JOptionPane.showConfirmDialog
				(null,"Changing the map size will reset the current map. \nAre you sure to change it?","Set Map Size\n ",
				JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			setMapSize(size);
		}
	}

	public void setMapSize(int size) {
		maxWorldCol = size;
		maxWorldRow = size;

		mapTileNum = new int[size][size];
		selectedEditorPanel = 1;
		showSelectEditorPanel();

		topLeftCol = 0;
		topLeftRow = 0;

		gui.label.get(gui.label_worldMapSize).setText("Map Size: " + size + " x " + size);
		gui.label.get(gui.label_colRow).setText("Col:" + 0 + ", Row:" + 0);

		resetWorldImage();
	}

	// RESET
	private void resetMapTileNum() {
		int col = 0;
		int row = 0;

		while (col < maxWorldCol && row < maxWorldRow) {
			mapTileNum[col][row] = 0;
			col++;
			if (col == maxWorldCol) {
				col = 0;
				row++;
			}
		}
	}

	private void resetTileSelectionImages() {
		for(int i = 0; i < tiles.size(); i++) {
			gui.tileButton[i].setIcon(null);
			gui.tileButton[i].setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			tiles.get(i).icon = null;
		}
		tiles.clear();
		// Reset the selected tile image
		gui.label.get(gui.label_selectedImage).setIcon(null);
		gui.label.get(gui.label_imageNum).setText(null);
	}

	private void resetWorldImage() {
		// Create World Image
		worldImage = new BufferedImage(16*maxWorldCol, 16*maxWorldRow, BufferedImage.TYPE_INT_ARGB);
		wg2 = worldImage.createGraphics();

		refreshEditorImage();
		refreshWorldImage();
	}

	// REFRESH
	public void refreshEditorImage() {
		if (topLeftCol < 0) {
			topLeftCol = 0;
		}
		if (topLeftCol > maxWorldCol - gui.maxScreenCol) {
			topLeftCol = maxWorldCol - gui.maxScreenCol;
		}
		if (topLeftRow < 0) {
			topLeftRow = 0;
		}
		if (topLeftRow > maxWorldRow - gui.maxScreenRow) {
			topLeftRow = maxWorldRow - gui.maxScreenRow;
		}

		BufferedImage editorImage = new BufferedImage(
				gui.defaultScreenCol*gui.tileSize, gui.defaultScreenRow*gui.tileSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = editorImage.createGraphics();

		int blockSize = 0;

		switch (selectedEditorPanel) {
			case 1: blockSize = (int) (48*gui.scale); break;
			case 2: blockSize = (int) (24*gui.scale); break;
			case 3: blockSize = (int) (12*gui.scale); break;
			case 4: blockSize = (int) (6*gui.scale); break;
		}

		int x = 0;
		int y = 0;
		int colStart = topLeftCol;
		int rowStart = topLeftRow;
		int colEnd = colStart + gui.maxScreenCol;
		int rowEnd = rowStart + gui.maxScreenRow;
		int col = colStart;
		int row = rowStart;

		while(col < colEnd && row < rowEnd) {
			int tileNum = mapTileNum[col][row];

			if (tiles.size() != 0 && tileNum >= 0) {
				g2.drawImage(tiles.get(tileNum).image, x, y, blockSize, blockSize, null);
			}

			col++;
			x += blockSize;

			if(col == colEnd) {
				col = colStart;
				x = 0;
				row++;
				y += blockSize;
			}
		}

		gui.editorImagePanel.drawGra(editorImage, 0, 0, editorImage.getWidth(), editorImage.getHeight());

		// Draw green frame
		drawGreenFrame();

		gui.window.requestFocus();
	}

	public void drawGreenFrame() {
		// Draw green frame
		double wmpTileSize = (double) gui.worldMPSize/maxWorldCol;
		int x2 = (int) (topLeftCol * wmpTileSize);
		int y2 = (int) (topLeftRow * wmpTileSize);
		int width = (int) (gui.maxScreenCol * wmpTileSize);
		int height = (int) (gui.maxScreenRow * wmpTileSize);
		gui.greenFrame.setBounds(x2, y2, width, height);
	}

	public void refreshWorldImage() {
		int tileSize = 16;
		int x = 0;
		int y = 0;
		int col = 0;
		int row = 0;

		while(col < maxWorldCol && row < maxWorldRow) {
			int tileNum = mapTileNum[col][row];

			if (tiles.size() != 0 && tileNum >= 0) {
				wg2.drawImage(tiles.get(tileNum).image, x, y, null);
			}

			col++;
			x += tileSize;
			if (col == maxWorldCol) {
				col = 0;
				x = 0;
				row++;
				y+= tileSize;
			}
		}

		gui.worldMapPanel.drawGra(worldImage, 0, 0, gui.worldMPSize, gui.worldMPSize);
		gui.window.requestFocus();
	}

	// EDITOR ACTION
	public void drawOnEditor() {
		int worldCol = topLeftCol + selectedCol;
		int worldRow = topLeftRow + selectedRow;

		if (selectedTileNum <= tiles.size()) {
			mapTileNum[worldCol][worldRow] = selectedTileNum; // TODO
		}
		refreshEditorImage();
		drawOnWorldMap(worldCol,worldRow);
	}

	public void showSelectEditorPanel() {
		// Clear all panels
		for (int i = 1; i < gui.editorPanel.length; i++) {
			gui.editorPanel[i].setVisible(false);
		}
		// Set the selected panel visible
		gui.editorPanel[selectedEditorPanel].setVisible(true);

		gui.maxScreenCol = getMaxScreenCol();
		gui.maxScreenRow = getMaxScreenRow();

		refreshEditorImage();
	}

	public int getMaxScreenCol() {
		switch(selectedEditorPanel) {
			case 1: gui.maxScreenCol = gui.defaultScreenCol;break;
			case 2: gui.maxScreenCol = gui.defaultScreenCol*2;break;
			case 3: gui.maxScreenCol = gui.defaultScreenCol*4;break;
			case 4: gui.maxScreenCol = gui.defaultScreenCol*8;break;
		}
		return gui.maxScreenCol;
	}

	public int getMaxScreenRow() {
		switch(selectedEditorPanel) {
			case 1: gui.maxScreenRow = gui.defaultScreenRow;break;
			case 2: gui.maxScreenRow = gui.defaultScreenRow*2;break;
			case 3: gui.maxScreenRow = gui.defaultScreenRow*4;break;
			case 4: gui.maxScreenRow = gui.defaultScreenRow*8;break;
		}
		return gui.maxScreenRow;
	}

	// WORLD MAP ACTION
	public void drawOnWorldMap(int worldCol, int worldRow) {
		int tileNum = mapTileNum[worldCol][worldRow];

		if (tiles.size() > 0) {
			wg2.drawImage(tiles.get(tileNum).image, worldCol*16, worldRow*16, null);
			gui.worldMapPanel.drawGra(worldImage, 0, 0, gui.worldMPSize, gui.worldMPSize);
			gui.window.requestFocus();
		}
	}

	// TOOL
	public void zoomIn() {
		if(selectedEditorPanel > 1) {
			selectedEditorPanel--;
			showSelectEditorPanel();
		}
	}

	public void zoomOut() {
		if (selectedEditorPanel < gui.editorPanel.length-1) {
			selectedEditorPanel++;
			if (getMaxScreenCol() > maxWorldCol) {
				selectedEditorPanel--;
			}
		}
		showSelectEditorPanel();
	}

	public void moveUp() {
		topLeftRow -= gui.maxScreenRow/8;

		if (topLeftRow < 0) {
			topLeftRow = 0;
		}
		refreshEditorImage();
	}

	public void moveDown() {
		topLeftRow += gui.maxScreenRow/8;

		if (topLeftRow > maxWorldRow - gui.maxScreenRow) {
			topLeftRow = maxWorldRow - gui.maxScreenRow;
		}
		refreshEditorImage();
	}

	public void moveLeft() {
		topLeftCol -= gui.maxScreenCol/8;

		if (topLeftCol < 0) {
			topLeftCol = 0;
		}
		refreshEditorImage();
	}

	public void moveRight() {
		topLeftCol += gui.maxScreenCol/8;

		if (topLeftCol >  (maxWorldCol - gui.maxScreenCol)) {
			topLeftCol = maxWorldCol - gui.maxScreenCol;
		}
		refreshEditorImage();
	}

	public void moveAll(String direction) {
		int newMapTileNum[][] = new int[maxWorldCol][maxWorldRow];

		int col = 0;
		int row = 0;

		while (col < maxWorldCol && row < maxWorldRow) {
			switch(direction) {
				case "up":
					if (row == maxWorldRow-1) {
						newMapTileNum[col][row] = 0;
					} else {
						newMapTileNum[col][row] = mapTileNum[col][row+1];
					}
					break;
				case "down":
					if (row == 0) {
						newMapTileNum[col][row] = 0;
					} else {
						newMapTileNum[col][row] = mapTileNum[col][row-1];
					}
					break;
				case "left":
					if (col == maxWorldCol-1) {
						newMapTileNum[col][row] = 0;
					} else {
						newMapTileNum[col][row] = mapTileNum[col+1][row];
					}
					break;
				case "right":
					if (col == 0) {
						newMapTileNum[col][row] = 0;
					} else {
						newMapTileNum[col][row] = mapTileNum[col-1][row];
					}
					break;
			}

			col++;
			if (col == maxWorldCol) {
				col = 0;
				row++;
			}
		}

		// REPLACE mapTileNum with newMapTileNum
		col = 0;
		row = 0;
		while (col < maxWorldCol && row < maxWorldRow) {
			mapTileNum[col][row] = newMapTileNum[col][row];
			col++;
			if (col == maxWorldCol) {
				col =0;
				row++;
			}
		}

		refreshEditorImage();
		refreshWorldImage();
	}

	public void splash() {
		int col = topLeftCol;
		int row = topLeftRow;
		int endCol = col + gui.maxScreenCol;
		int endRow = row + gui.maxScreenRow;

		while (col < endCol && row < endRow) {
			mapTileNum[col][row] = selectedTileNum;
			col++;
			if (col == endCol) {
				col = topLeftCol;
				row++;
			}
		}
		refreshEditorImage();
		refreshWorldImage();
	}

	public void splashAll() {
		int col = 0;
		int row = 0;
		while (col < maxWorldCol && row < maxWorldRow) {
			mapTileNum[col][row] = selectedTileNum;
			col++;
			if (col == maxWorldCol) {
				col = 0;
				row++;
			}
		}
		refreshEditorImage();
		refreshWorldImage();
	}
	public void erase() {
		int col = topLeftCol;
		int row = topLeftRow;
		int endCol = col + gui.maxScreenCol;
		int endRow = row + gui.maxScreenRow;

		while (col < endCol && row < endRow) {
			mapTileNum[col][row] = 0;
			col++;
			if (col == endCol) {
				col = topLeftCol;
				row++;
			}
		}
		refreshEditorImage();
		refreshWorldImage();
	}

	public void eraseAll() {
		int col = 0;
		int row = 0;
		while (col < maxWorldCol && row < maxWorldRow) {
			mapTileNum[col][row] = 0;
			col++;
			if (col == maxWorldCol) {
				col = 0;
				row++;
			}
		}
		refreshEditorImage();
		refreshWorldImage();
	}

	public void gridOnOff() {
		int col = 0;
		int row = 0;
		while (col < 16 && row < 9) {
			if (gridOn == true) {
				gui.editBlocks16[col][row].setBorderPainted(false);
			}
			if (gridOn == false) {
				gui.editBlocks16[col][row].setBorderPainted(true);
			}
			col++;
			if (col == 16) {
				col = 0;
				row++;
			}
		}

		col = 0;
		row = 0;
		while(col < 32 && row < 18) {
			if (gridOn == true) {
				gui.editBlocks32[col][row].setBorderPainted(false);
			}
			if (gridOn == false) {
				gui.editBlocks32[col][row].setBorderPainted(true);
			}
			col++;
			if (col == 32) {
				col = 0;
				row++;
			}
		}

		col = 0;
		row = 0;
		while (col < 64 && row < 36) {
			if (gridOn == true) {
				gui.editBlocks64[col][row].setBorderPainted(false);
			}
			if (gridOn == false) {
				gui.editBlocks64[col][row].setBorderPainted(true);
			}
			col++;
			if(col == 64) {
				col = 0;
				row++;
			}
		}

//		col = 0;
//		row = 0;
//		while (col < 128 && row < 72) {
//
//			if (gridOn == true) {
//				gui.editBlocks128[col][row].setBorderPainted(false);
//			}
//			if (gridOn == false) {
//				gui.editBlocks128[col][row].setBorderPainted(true);
//			}
//			col++;
//			if (col == 128) {
//				col = 0;
//				row++;
//			}
//		}

		if (gridOn) {
			gridOn = false;
		} else {
			gridOn = true;
		}
	}

}
