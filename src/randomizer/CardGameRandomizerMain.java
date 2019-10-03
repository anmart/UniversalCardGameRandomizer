package randomizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import randomizer.DOTR.DOTR_UI;
import randomizer.Pokemon_TCG.PTCG1_UI;
import randomizer.YGO8.YGO8_Randomizer;
import randomizer.YGO8.YGO8_UI;

public class CardGameRandomizerMain extends JFrame {

    JPanel starterPanel;
    final JFileChooser fc;
    JCheckBox rememberBox;
    Path lastRomText = Paths.get("./lastRomSettings.txt");

    public static RandomizerUI main;

    public static void main(String[] args) {
	try {
	    // Set the Look and Feel of the application to the operating
	    // system's look and feel.
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException e) {
	} catch (InstantiationException e) {
	} catch (IllegalAccessException e) {
	} catch (UnsupportedLookAndFeelException e) {
	}

	CardGameRandomizerMain main = new CardGameRandomizerMain("CG randomizer");
	main.pack();
	main.setLocationRelativeTo(null);
	main.setVisible(true);

    }

    public CardGameRandomizerMain(String title) {
	super(title);

	fc = new JFileChooser();
	fc.setDialogTitle("Choose Rom");
	fc.setApproveButtonText("Choose");
	fc.setCurrentDirectory(new File(System.getProperty("user.home")));
	FileFilter gameboyFilter = new FileFilter() {

	    public String getDescription() {
		return "Gameboy rom (*.gbc)";
	    }

	    public boolean accept(File f) {
		if (f.isDirectory()) {
		    return true;
		} else {
		    String filename = f.getName().toLowerCase();
		    return filename.endsWith(".gbc") || filename.endsWith(".gb");
		}
	    }
	};
	fc.addChoosableFileFilter(gameboyFilter);
	fc.setFileFilter(gameboyFilter);
	fc.addChoosableFileFilter(new FileFilter() {

	    public String getDescription() {
		return "Playstation ISO (*.iso)";
	    }

	    public boolean accept(File f) {
		if (f.isDirectory()) {
		    return true;
		} else {
		    String filename = f.getName().toLowerCase();
		    return filename.endsWith(".iso");
		}
	    }
	});
	fc.addChoosableFileFilter(new FileFilter() {

	    public String getDescription() {
		return "Gameboy Advance rom (*.gba)";
	    }

	    public boolean accept(File f) {
		if (f.isDirectory()) {
		    return true;
		} else {
		    String filename = f.getName().toLowerCase();
		    return filename.endsWith(".gba");
		}
	    }
	});
	rememberBox = new JCheckBox("Use Last Rom (Hover)");
	rememberBox.setSelected(true);
	if (Files.exists(lastRomText)) {
	    try {
		rememberBox.setToolTipText(new String(Files.readAllBytes(lastRomText)));
	    } catch (IOException ex) {
		Logger.getLogger(CardGameRandomizerMain.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    rememberBox.setToolTipText("No Remembered Rom");
	}
	starterPanel = new JPanel();

	starterPanel.setPreferredSize(new Dimension(250, 50));
	starterPanel.add(new JLabel("What rom would you like to load?"), BorderLayout.NORTH);
	JButton loadRom = new JButton("Load Rom");
	loadRom.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		loadRom();
	    }
	});
	starterPanel.add(loadRom, BorderLayout.SOUTH);
	starterPanel.add(rememberBox, BorderLayout.SOUTH);
	this.add(starterPanel);
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public void loadRom() {

	File game;
	try {
	    if (rememberBox.isSelected() && Files.exists(lastRomText)) {
		game = new File(new String(Files.readAllBytes(lastRomText)));
	    } else {
		if(fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
		    return;
		}
		Files.write(lastRomText, fc.getSelectedFile().getAbsolutePath().getBytes());
		game = fc.getSelectedFile();
	    }

	    if (game.getPath().endsWith(".gbc") || game.getPath().endsWith(".gb")) {
		parseGB(game);
	    }
	    if (game.getPath().endsWith(".gba")) {
		parseGBA(game);
	    }
	    if (game.getPath().endsWith(".iso")) {
		parseISO(game);
	    }

	    // either way, reset location.
	    this.setLocationRelativeTo(null);

	} catch (NullPointerException e) {
	    JOptionPane.showMessageDialog(null, "Error: no rom found.");
	} catch (IOException ex) {
	    Logger.getLogger(CardGameRandomizerMain.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    // right now parses by title name, todo: search for better methods
    public void parseGB(File file) {
	int gbHeadStart = 0x0134;
	int gbHeadSize = 0x143 - gbHeadStart;
	FileInputStream gbgame;
	String headText = "";
	try {
	    byte[] headName = new byte[gbHeadSize];
	    gbgame = new FileInputStream(file);
	    gbgame.skip(gbHeadStart);
	    gbgame.read(headName);
	    headText = new String(headName, Charset.forName("US-ASCII")); // not sure this works, found on stack overflow
	    gbgame.close();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	RandomizerUI replace = null;
	if (headText.equalsIgnoreCase("POKECARD\0\0\0AXQE")) {
	    replace = new PTCG1_UI();
	}

	if (replace != null) {
	    replace.setFile(file);
	    replace.setFileChooser(fc);
	    this.remove(starterPanel);
	    this.add(replace);
	    this.pack();
	} else {
	    JOptionPane.showMessageDialog(null, "Error: not a valid rom.");
	}
    }

    public void parseISO(File file) {

	//for now, I don't know anything about PS Headers, so just create a DOTR randomizer.
	RandomizerUI replace = new DOTR_UI();
	replace.setFile(file);
	replace.setFileChooser(fc);
	this.remove(starterPanel);
	this.add(replace);
	this.pack();

    }

    public void parseGBA(File file) {
	int gbaHeadStart = 0xa0;
	int gbaHeadSize = 0xc;
	FileInputStream gbagame;
	String headText = "";
	try {
	    byte[] headName = new byte[gbaHeadSize];
	    gbagame = new FileInputStream(file);
	    gbagame.skip(gbaHeadStart);
	    gbagame.read(headName);
	    headText = new String(headName, Charset.forName("US-ASCII")); // not sure this works, found on stack overflow
	    gbagame.close();

	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	RandomizerUI replace = null;
	if (headText.equalsIgnoreCase("YUGIOH DM8\0\0")) {
	    replace = new YGO8_UI();
	    new YGO8_Randomizer(file);
	}

	if (replace != null) {
	    replace.setFile(file);
	    replace.setFileChooser(fc);
	    this.remove(starterPanel);
	    this.add(replace);
	    this.pack();
	} else {
	    JOptionPane.showMessageDialog(null, "Error: not a valid rom.");
	}
    }

}
