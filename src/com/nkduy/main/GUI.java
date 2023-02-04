package com.nkduy.main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GUI {

    Main main;
    JFrame window;
    Font font1 = new Font("Arial", Font.PLAIN, 15);
    Font font2 = new Font("Arial", Font.PLAIN, 10);
    String fontName = "Arial";
    final double small = 0.75; // 1050x712
    final double medium = 1.0; // 1400x950
    final double large = 1.5; // 2100x1425
    double scale = medium;
    String about;

    // Editor Panel
    int tileSize = 48;
    int defaultScreenCol = 16;
    int defaultScreenRow = 9;
    int maxScreenCol = defaultScreenCol;
    int maxScreenRow = defaultScreenRow;
    int editorPanelWidth;
    int editorPanelHeight;
    JPanel editorPanel[] = new JPanel[4];
    ImagePanel editorImagePanel;
    JButton editBlocks16[][] = new JButton[defaultScreenCol][defaultScreenRow];
    JButton editBlocks32[][] = new JButton[defaultScreenCol*2][defaultScreenRow*2];
    JButton editBlocks64[][] = new JButton[defaultScreenCol*4][defaultScreenRow*4];

    // Tile Selection Panel
    int maxTilePanelNum = 10;
    JPanel tilePanel[] = new JPanel[maxTilePanelNum];
    JButton tileButton[] = new JButton[100*maxTilePanelNum];
    int currentTilePanelNum = 0;

    BWButton midB, upB, downB, solidB, zoomInB, zoomOutB,
            bucketB, eraserB, moveUpB, moveDownB, moveLeftB, moveRightB;

    // Label
    ArrayList<JLabel> label = new ArrayList<>();
    final int label_selected = 0;
    final int label_selectedImage = 1;
    final int label_imageNum = 2;
    final int label_colRow = 3;
    final int label_worldMapSize = 4;

    // World Map
    ImagePanel worldMapPanel;
    int worldMPSize;
//    JLabel worldMapSizeLabel;
    JLabel greenFrame;

    boolean mousePressed = false;
    Timer timer;

    public GUI(Main main) {
        this.main = main;
    }

    public void setupGUI() {
        about = "Simple 2D Tile Editor v" + main.version +
                "\n\nDeveloped by NKDuy (2023)\n\nFeel free to use!\n\n\n"
                + "Update (Feb 4, 2023)\n- Added the Editor Size option.\n"
                + "- Fixed a tile data loading issue.\n"
                + "- Fixed a minor focus issue.\n"
                + "- Changed the zoom in/out icons";

        createWindow();
        createEditorPanel(1, editBlocks16, defaultScreenCol, defaultScreenRow);
        createEditorPanel(2, editBlocks32, defaultScreenCol*2, defaultScreenRow*2);
        createEditorPanel(3, editBlocks64, defaultScreenCol*4, defaultScreenRow*4);
        createEditorImagePanel(); // This needs to be placed after edit panels, so it is placed below
        createWorldMapPanel();
        createToolBox();
        createTileSelectionPanel();
        createLabel();

        window.setVisible(true);
        editorPanel[1].setVisible(true);
    }

    public void setEditorSize(double scale) {
        this.scale = scale;
        main.saveConfig();
        tileSize = (int) (48*scale);
        editorPanelWidth = tileSize * defaultScreenCol; // 576/768
        editorPanelHeight = tileSize * defaultScreenRow; // 432

        worldMPSize = (int)(300*scale);

        int x, y, w, h;

        // WINDOW
        x = (int) (1400*scale);
        y = (int) (950*scale);
        window.setSize(x,y);

        // EDITOR PANEL
        x = (int) (50*scale);
        y = (int) (60*scale);
        editorImagePanel.setBounds(x, y, editorPanelWidth, editorPanelHeight);
        editorPanel[1].setBounds(x, y, editorPanelWidth, editorPanelHeight);
        editorPanel[2].setBounds(x, y, editorPanelWidth, editorPanelHeight);
        editorPanel[3].setBounds(x, y, editorPanelWidth, editorPanelHeight);

        // TILE SELECTION PANEL
        x = (int) (50*scale);
        y = (int) (600*scale);
        for(int i = 0; i < maxTilePanelNum; i++) {
            tilePanel[i].setBounds(x, y, tileSize*20, tileSize*5);
        }

        x = (int) (1010*scale);
        y = (int) (648*scale);
        w = (int) (54*scale);
        h = (int) (48*scale);
        midB.setBounds(x, y, w, h);
        midB.setFont(new Font(fontName, Font.PLAIN, (int) (15*scale)));

        x = (int) (1010*scale);
        y = (int) (600*scale);
        upB.setBounds(x, y, w, h);
        upB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_up 50x50c.png")));

        x = (int) (1010*scale);
        y = (int) (696*scale);
        downB.setBounds(x, y, w, h);
        downB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_down 50x50c.png")));

        x = (int) (1010*scale);
        y = (int) (744*scale);
        solidB.setBounds(x, y, w, h);
        solidB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("brick-wall48x48.png")));

        // WORLD MAP
        x = (int) (1010*scale);
        y = (int) (130*scale);
        w = (int) (300*scale);
        h = (int) (300*scale);
        worldMapPanel.setBounds(x, y, w, h);

        // LABEL
        x = (int) (50*scale);
        y = (int) (545*scale);
        w = (int) (230*scale);
        h = (int) (50*scale);
        label.get(label_selected).setBounds(x, y, w, h);
        x = (int) (200*scale);
        y = (int) (545*scale);
        w = (int) (48*scale);
        h = (int) (48*scale);
        label.get(label_selectedImage).setBounds(x, y, w, h);
        x = (int) (280*scale);
        y = (int) (545*scale);
        w = (int) (250*scale);
        h = (int) (48*scale);
        label.get(label_imageNum).setBounds(x, y, w, h);
        x = (int) (550*scale);
        y = (int) (495*scale);
        w = (int) (260*scale);
        h = (int) (48*scale);
        label.get(label_colRow).setBounds(x, y, w, h);
        x = (int) (1010*scale);
        y = (int) (430*scale);
        w = (int) (300*scale);
        h = (int) (50*scale);
        label.get(label_worldMapSize).setBounds(x, y, w, h);

        x = (int) (856*scale);
        y = (int) (396*scale);
        w = (int) (48*scale);
        h = (int) (48*scale);
        zoomInB.setBounds(x, y, w, h);
        zoomInB.setFont(font2);
        zoomInB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zoomin48.png")));

        x = (int) (856*scale);
        y = (int) (444*scale);
        zoomOutB.setBounds(x, y, w, h);
        zoomOutB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zoomout48.png")));

        x = (int) (904*scale);
        y = (int) (396*scale);
        bucketB.setBounds(x, y, w, h);
        bucketB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("bucket50x50b.png")));

        x = (int) (904*scale);
        y = (int) (444*scale);
        eraserB.setBounds(x, y, w, h);
        eraserB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("eraser50x50b.png")));

        x = (int) (880*scale);
        y = (int) (207*scale);
        moveUpB.setBounds(x, y, tileSize, tileSize);
        moveUpB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_up 50x50.png")));

        x = (int) (880*scale);
        y = (int) (303*scale);
        moveDownB.setBounds(x, y, tileSize, tileSize);
        moveDownB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_down 50x50.png")));

        x = (int) (830*scale);
        y = (int) (255*scale);
        moveLeftB.setBounds(x, y, tileSize, tileSize);
        moveLeftB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_left 50x50.png")));

        x = (int) (928*scale);
        y = (int) (255*scale);
        moveRightB.setBounds(x, y, tileSize, tileSize);
        moveRightB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("arrow_right 50x50.png")));

        main.scaleImage();
        main.setIcons();
        main.refreshEditorImage();
        main.refreshWorldImage();
    }

    // CREATE
    private void createWindow() {
        window = new JFrame("Simple 2D Tile Editor v" + main.version);
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("boy_down_1.png"));
        window.setIconImage(icon.getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.getContentPane().setBackground(new Color(0, 0, 0));
        window.setResizable(false);
//		window.setLocationRelativeTo(null);
        window.addKeyListener(new KeyHandler(main));
        createWindowMenu();
    }

    private void createWindowMenu() {
        // Top Menu
        JMenuBar topMenuBar = new JMenuBar();
        window.setJMenuBar(topMenuBar);

        // Menu
        JMenu menu[] = new JMenu[6];
        JMenuItem menuItem[] = new JMenuItem[20];

        // FILE
        int m = 0;
        menu[m] = new JMenu("File");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        int i = 0;
        menuItem[i] = new JMenuItem("New Project");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.createNewProject();
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Import Tile");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.selectDirectory("tile");
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Import Tile Sheet");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.selectDirectory("sheet");
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Save Tile Data");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.selectDirectory("saveData");
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Load Tile Data");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.selectDirectory("loadData");
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Save Map");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.saveMap();
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Save Map As");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.saveMapAs();
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Load Map");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.loadMap();
            }
        });
        menu[m].add(menuItem[i]);

        // EDITOR SIZE
        m++;
        menu[m] = new JMenu("Editor Size");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        i++;
        menuItem[i] = new JMenuItem("Small");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditorSize(small);
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Medium");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditorSize(medium);
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Large");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditorSize(large);
            }
        });
        menu[m].add(menuItem[i]);

        // MAP SIZE
        m++;
        menu[m] = new JMenu("Map Size");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        i++;
        menuItem[i] = new JMenuItem(50 + " x " + 50);
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showMapSizeNotification(50);
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem(100 + " x " + 100);
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showMapSizeNotification(100);
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem(250 + " x " + 250);
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showMapSizeNotification(250);
            }
        });
        menu[m].add(menuItem[i]);

        // VIEW
        m++;
        menu[m] = new JMenu("View");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        i++;
        menuItem[i] = new JMenuItem("Grid On/Off");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.gridOnOff();
            }
        });
        menu[m].add(menuItem[i]);

        // TOOL
        m++;
        menu[m] = new JMenu("Tools");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        i++;
        menuItem[i] = new JMenuItem("Splash On Entire Map");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.splashAll();
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("Erase All Tiles On Map");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.eraseAll();
            }
        });
        menu[m].add(menuItem[i]);

        // HELP
        m++;
        menu[m] = new JMenu("Help");
        menu[m].setFont(font1);
        topMenuBar.add(menu[m]);

        i++;
        menuItem[i] = new JMenuItem("Shortcut List");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubWindow("Keyboard Shortcut List",
                        "[E]  Zoom In\n[Q]  Zoom Out\n"
                                + "[W/A/S/D]  Move the edit position\n"
                                + "[Shift + W/A/S/D]  Move all the tiles\n"
                                + "[G]  Grid On/Off\n"
                                + "[Ctrl + S]  Save Map");
            }
        });
        menu[m].add(menuItem[i]);

        i++;
        menuItem[i] = new JMenuItem("About");
        menuItem[i].setFont(font1);
        menuItem[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubWindow("About",about);
            }
        });
        menu[m].add(menuItem[i]);
    }

    private void createEditorImagePanel() {
        editorImagePanel = new ImagePanel();
        editorImagePanel.setBackground(Color.black);
        editorImagePanel.setOpaque(true);
        window.add(editorImagePanel);
    }

    private void createEditorPanel(int i, JButton editBlocks[][], int maxCol, int maxRow) {
        editorPanel[i] = new JPanel();
        editorPanel[i].setLayout(new GridLayout(maxRow, maxCol));
        editorPanel[i].setOpaque(false);
        editorPanel[i].setVisible(false);
        window.add(editorPanel[i]);

        int col = 0;
        int row = 0;

        while(col < maxCol && row < maxRow) {
            editBlocks[col][row] = new JButton();
            editBlocks[col][row].setOpaque(false);
            editBlocks[col][row].setBackground(Color.black);
            editBlocks[col][row].setActionCommand(col + " " + row);
            editBlocks[col][row].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String colAndRow = e.getActionCommand();
                    String s[] = colAndRow.split(" ");
                    main.selectedCol = Integer.parseInt(s[0]);
                    main.selectedRow = Integer.parseInt(s[1]);
                    main.drawOnEditor();
                }
            });
            int tempC = col;
            int tempR = row;
            editBlocks[col][row].addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    mousePressed = true;
                    main.selectedCol = tempC;
                    main.selectedRow = tempR;
                    main.drawOnEditor();
                }
                public void mouseReleased(MouseEvent e) {
                    mousePressed = false;
                }
                public void mouseEntered(MouseEvent e) {
                    if (mousePressed) {
                        main.selectedCol = tempC;
                        main.selectedRow = tempR;
                        main.drawOnEditor();
                    }
                    int worldCol = main.topLeftCol + tempC;
                    int worldRow = main.topLeftRow + tempR;
                    label.get(label_colRow).setText("Col:" + worldCol + ", Row:" + worldRow);
                }
                public void mouseExited(MouseEvent e) {}
            });
            editorPanel[i].add(editBlocks[col][row]);

            col++;
            if(col == maxCol) {
                col = 0;
                row++;
            }
        }
    }

    private void createTileSelectionPanel() {
        // Create tilePanel
        for(int i = 0; i < maxTilePanelNum; i++) {
            tilePanel[i] = new JPanel();
            tilePanel[i].setBackground(Color.blue);
            tilePanel[i].setLayout(new GridLayout(5, 20));
            tilePanel[i].setVisible(false);
            window.add(tilePanel[i]);
        }
        tilePanel[0].setVisible(true);

        // Create tileButton
        int tilePanelNum = 0;
        for(int i = 0; i < tileButton.length; i++) {
            int tempBN = i;
            tileButton[i] = new JButton();
            tileButton[i].setBackground(Color.black);
            tileButton[i].setForeground(Color.white);
            tileButton[i].setActionCommand("" + i);
            tileButton[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int command = Integer.parseInt(e.getActionCommand());
                    // Get tile number
                    main.selectedTileNum = command;
                    // Display tile image
                    if(main.selectedTileNum < main.tiles.size()) {
                        label.get(label_selectedImage).setIcon(main.tiles.get(main.selectedTileNum).icon);
                    }

                    if(main.selectedTileNum < main.tiles.size()) {
                        // SETTING SOLID
                        if(main.settingSolid == true) {
                            if(main.tiles.get(command).collision == false) {
                                main.tiles.get(command).collision = true;
                                tileButton[tempBN].setBorder(BorderFactory.createLineBorder(Color.red, 1));
                            } else if(main.tiles.get(command).collision == true) {
                                main.tiles.get(command).collision = false;
                                tileButton[tempBN].setBackground(Color.black);
                                tileButton[tempBN].setBorder(BorderFactory.createLineBorder(Color.gray, 1));
                            }
                        }

                        // Display tile number and solid info
                        if(main.tiles.get(command).collision == true) {
                            label.get(label_imageNum).setText("Num:" + command + " (Solid)");
                        } else {
                            label.get(label_imageNum).setText("Num:" + command);
                        }
                    } else {
                        label.get(label_imageNum).setText(null);
                    }
                    window.requestFocus();
                }
            });

            tilePanelNum = i/100;
            tilePanel[tilePanelNum].add(tileButton[i]);
        }

        // Create Panel Change Buttons
        midB = new BWButton();
        midB.setText("" + currentTilePanelNum);
        window.add(midB);

        upB = new BWButton();
        upB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentTilePanelNum > 0) {
                    currentTilePanelNum--;
                    tilePanel[currentTilePanelNum].setVisible(true);
                    tilePanel[currentTilePanelNum+1].setVisible(false);
                    midB.setText("" + currentTilePanelNum);
                }
                window.requestFocus();
            }
        });
        window.add(upB);

        downB = new BWButton();
        downB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentTilePanelNum < maxTilePanelNum-1) {
                    currentTilePanelNum++;
                    tilePanel[currentTilePanelNum].setVisible(true);
                    tilePanel[currentTilePanelNum-1].setVisible(false);
                    midB.setText("" + currentTilePanelNum);
                }
                window.requestFocus();
            }
        });
        window.add(downB);

        solidB = new BWButton();
        solidB.setFont(font1);
        solidB.setToolTipText("Clicking a tile while the wall icon is red to change the tile's solid status.");
        solidB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(main.settingSolid == false) {
                    main.settingSolid = true;
                    solidB.setBackground(Color.red);
                    ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("brick-wall48x48red.png"));
                    solidB.setIcon(icon);
                } else if(main.settingSolid == true) {
                    main.settingSolid = false;
                    solidB.setBackground(Color.black);
                    ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("brick-wall48x48.png"));
                    solidB.setIcon(icon);
                }
            }
        });
        window.add(solidB);
    }

    private void createWorldMapPanel() {
        worldMapPanel = new ImagePanel();

        Border border = BorderFactory.createLineBorder(Color.white, 1);
        worldMapPanel.setBorder(border);
        worldMapPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                double ratio = (double) worldMPSize/main.maxWorldCol;
                System.out.println("ratio:"+ratio);
                int uniCol = (int)(x/ratio);
                int uniRow = (int)(y/ratio);
                System.out.println("uniCol:"+uniCol);

                main.topLeftCol = uniCol-(maxScreenCol/2);
                main.topLeftRow = uniRow-(maxScreenRow/2);

                main.refreshEditorImage();
            }
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}

        });
        window.add(worldMapPanel);

        greenFrame = new JLabel();
        Border greenBorder = BorderFactory.createLineBorder(Color.green, 1);
        greenFrame.setBorder(greenBorder);
        worldMapPanel.add(greenFrame);

//		worldMapSizeLabel = new JLabel();
//		worldMapSizeLabel.setForeground(Color.white);
//		worldMapSizeLabel.setFont(font1.deriveFont(26f));
//		worldMapSizeLabel.setHorizontalAlignment(JLabel.CENTER);
//
//		window.add(worldMapSizeLabel);
    }

    private void createLabel() {
        label.add(new JLabel());
        label.get(label_selected).setForeground(Color.white);
        label.get(label_selected).setFont(font1.deriveFont(20f));
        label.get(label_selected).setText("Selected:");
        window.add(label.get(label_selected));

        label.add(new JLabel());
        window.add(label.get(label_selectedImage));

        label.add(new JLabel());
        label.get(label_imageNum).setForeground(Color.white);
        label.get(label_imageNum).setFont(font1.deriveFont(20f));
        window.add(label.get(label_imageNum));

        label.add(new JLabel());
        label.get(label_colRow).setForeground(Color.white);
        label.get(label_colRow).setFont(font1.deriveFont(20f));
        label.get(label_colRow).setHorizontalAlignment(JLabel.RIGHT);
        window.add(label.get(label_colRow));

        label.add(new JLabel());
        label.get(label_worldMapSize).setForeground(Color.white);
        label.get(label_worldMapSize).setFont(font1.deriveFont(20f));
        label.get(label_worldMapSize).setHorizontalAlignment(JLabel.CENTER);
        window.add(label.get(label_worldMapSize));
    }

    private void createToolBox() {
        zoomInB = new BWButton();
        zoomInB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.zoomIn();
            }
        });
        window.add(zoomInB);

        zoomOutB = new BWButton();
        zoomOutB.setFont(font1);
        zoomOutB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.zoomOut();
            }
        });
        window.add(zoomOutB);

        bucketB = new BWButton();
        bucketB.setFont(font1);
        bucketB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.splash();
            }
        });
        window.add(bucketB);

        eraserB = new BWButton();
        eraserB.setFont(font1);
        eraserB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.erase();
            }
        });
        window.add(eraserB);

        // Controller
        moveUpB = new BWButton();
        moveUpB.setBorderPainted(false);
        moveUpB.setFont(font1);
        moveUpB.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                timer = new Timer(60, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        main.moveUp();
                    }
                });
                timer.restart();
            }
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                timer.stop();
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        window.add(moveUpB);

        moveDownB = new BWButton();
        moveDownB.setBorderPainted(false);
        moveDownB.setFont(font1);
        moveDownB.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                timer = new Timer(60, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        main.moveDown();
                    }
                });
                timer.restart();
            }
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                timer.stop();
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        window.add(moveDownB);

        moveLeftB = new BWButton();
        moveLeftB.setBorderPainted(false);
        moveLeftB.setFont(font1);
        moveLeftB.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                timer = new Timer(60, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        main.moveLeft();
                    }
                });
                timer.restart();
            }
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                timer.stop();
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        window.add(moveLeftB);

        moveRightB = new BWButton();
        moveRightB.setBorderPainted(false);
        moveRightB.setFont(font1);
        moveRightB.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                timer = new Timer(60, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        main.moveRight();
                    }
                });
                timer.restart();
            }
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                timer.stop();
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        window.add(moveRightB);
    }

    public void showSubWindow(String title, String description) {
        JFrame subWindow = new JFrame(title);
        subWindow.setSize(400, 400);
        subWindow.setResizable(false);
        subWindow.setLocationRelativeTo(null);
        subWindow.setLayout(null);
        subWindow.getContentPane().setBackground(Color.black);

        JTextArea jta = new JTextArea();
        jta.setBounds(20, 50, 360, 300);
        jta.setBackground(Color.black);
        jta.setForeground(Color.white);
        jta.setFont(font1);
        jta.setText(description);
        jta.setEditable(false);
        subWindow.add(jta);

        subWindow.setVisible(true);
    }
}
