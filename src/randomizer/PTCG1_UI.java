package randomizer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;



public class PTCG1_UI extends RandomizerUI {

	
	//prepatch
	JCheckBox deleteInvisibleWall,sanquiTutorialPatch;
	ButtonGroup moveGroup;
	JRadioButton movesCostNothing, movesCostColorless,movesCostSame;

	//random
	JCheckBox randomizeHealth,randomizeSets, randomizeDecks;
	//This will probably change to having an entire pane to itself with randomize move being required to set the other properties
	ButtonGroup randomizeMovesGroup;
	JRadioButton dontRandomizeMoves,randomizeMovesFully,randomizeMovesInStages;




	public PTCG1_UI(){

		this.setLayout(new GridLayout(2,0));
		JTabbedPane tabbedPane = new JTabbedPane();

		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		//tabbedPane.setPreferredSize(new Dimension(500, 300));


		/////////PREPATCH PANEL
		JPanel prepatchPanel = new JPanel();
		prepatchPanel.setLayout(new BoxLayout(prepatchPanel,BoxLayout.Y_AXIS));
		tabbedPane.addTab("Prepatch", null, prepatchPanel,"settings patched in before anything's randomized");

		deleteInvisibleWall = new JCheckBox("Delete Mr. Mime's Invisible Wall ability");
		deleteInvisibleWall.setToolTipText("Note: only Mr. Mime can use this ability anyway");
		prepatchPanel.add(deleteInvisibleWall);


		prepatchPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		movesCostNothing = new JRadioButton("Make moves cost nothing");
		movesCostNothing.setToolTipText("All energy costs will be turned to poison");
		movesCostColorless = new JRadioButton("Make moves cost colorless energy");
		movesCostColorless.setToolTipText("All energy costs are replaced with an equal amount of colorless energy");
		movesCostSame = new JRadioButton("Don't change move costs");
		movesCostSame.setToolTipText("Do you really need a tool tip for this one?");
		movesCostSame.setSelected(true);

		moveGroup = new ButtonGroup();
		moveGroup.add(movesCostSame);
		moveGroup.add(movesCostColorless);
		moveGroup.add(movesCostNothing);
		prepatchPanel.add(movesCostSame);
		prepatchPanel.add(movesCostColorless);
		prepatchPanel.add(movesCostNothing);

		prepatchPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		sanquiTutorialPatch = new JCheckBox("Remove the tutorial");
		sanquiTutorialPatch.setToolTipText("Credit:Sanqui");
		prepatchPanel.add(sanquiTutorialPatch);


		/////////RANDOM PANEL
		JPanel randomPanel = new JPanel();
		randomPanel.setLayout(new BoxLayout(randomPanel,BoxLayout.Y_AXIS));
		tabbedPane.addTab("Random", null, randomPanel,"General randomizer settings");

		randomizeHealth = new JCheckBox("Randomize Pokemon health");
		randomizeHealth.setToolTipText("On the range of 10 to 120. This will be a user selected range later");
		randomPanel.add(randomizeHealth);


		randomizeSets = new JCheckBox("Randomize card packs");
		randomizeSets.setToolTipText("Randomizes which sets each pokemon belongs to");
		randomPanel.add(randomizeSets);


		randomizeDecks= new JCheckBox("Randomize decks");
		randomizeDecks.setToolTipText("Randomizes preconstructed decks");
		randomPanel.add(randomizeDecks);


		randomPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		dontRandomizeMoves = new JRadioButton("Don't randomize moves");
		dontRandomizeMoves.setToolTipText("Keep all moves the same");
		dontRandomizeMoves.setSelected(true);
		randomizeMovesInStages = new JRadioButton("Randomize moves within stage");
		randomizeMovesInStages.setToolTipText("Randomize moves but make sure pokemon only get moves from others in the same stage");
		randomizeMovesFully = new JRadioButton("Randomize moves fully");
		randomizeMovesFully.setToolTipText("Ignore Stage");


		randomizeMovesGroup = new ButtonGroup();
		randomizeMovesGroup.add(dontRandomizeMoves);
		randomizeMovesGroup.add(randomizeMovesInStages);
		randomizeMovesGroup.add(randomizeMovesFully);
		randomPanel.add(dontRandomizeMoves);
		randomPanel.add(randomizeMovesInStages);
		randomPanel.add(randomizeMovesFully);

		//randomPanel.add(new JSeparator(SwingConstants.HORIZONTAL));





		//NON TABBED
		JButton saveRom = new JButton("Save Rom");
		saveRom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				saveRom();
			}

		});

		//saveRom.setPreferredSize(new Dimension(200, 200));
		this.add(tabbedPane);
		JPanel buttonPanel = new JPanel();
		this.add(buttonPanel);
		buttonPanel.add(saveRom);

		//fileLocation = new JTextField(location);
		//this.add(fileLocation);
		//fileLocation.setEnabled(false);
		//fileLocation.


		fc = new JFileChooser();
		fc.setDialogTitle("Choose Rom");
		fc.setApproveButtonText("Choose");
		fc.setFileFilter(new FileFilter() {

			public String getDescription() {
				return "Gameboy rom (*.gbc)";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".gbc") || filename.endsWith(".gb") ;
				}
			}
		});




	}


	public void saveRom(){

		PTCG1_Randomizer rando = new PTCG1_Randomizer(gameFile);

		//prepatch stuff here
		if(deleteInvisibleWall.isSelected()){
			rando.deleteInvisibleWallMove();		
		}
		if(sanquiTutorialPatch.isSelected()){
			rando.SanquiRemoveTutorialFromRom();
		}


		if(movesCostNothing.isSelected()){
			rando.setAllEnergyToColorless(true);
		}
		else if(movesCostColorless.isSelected()){
			rando.setAllEnergyToColorless(false);
		}



		//Random stuff here
		if(randomizeHealth.isSelected()){
			rando.randomizeHP(1, 12);
		}
		if(randomizeDecks.isSelected()){
			rando.randomizeDeckPointersInRom();
		}
		if(randomizeSets.isSelected()){
			rando.randomizeAllSets();			
		}

		if(randomizeMovesInStages.isSelected()){
			rando.randomizeMoves(true, false);
		}
		else if(randomizeMovesFully.isSelected()){
			rando.randomizeMoves(false, false);
		}


		fc.setSelectedFile(new File("TCG Randomized.gbc"));
		fc.showOpenDialog(PTCG1_UI.this);

		String romLoc = fc.getSelectedFile().getAbsolutePath();
		if(romLoc.endsWith(".gbc"))
			romLoc += ".gbc";

		rando.saveRom(fc.getSelectedFile().getAbsolutePath());
		
		//note: add check to see if it saved properly
		JOptionPane.showMessageDialog(null, "Finished Saving Rom.");

	}
}
