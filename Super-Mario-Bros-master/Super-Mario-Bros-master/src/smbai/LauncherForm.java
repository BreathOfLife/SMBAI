package smbai;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/*Do you want to generate a full new set of agents? CHECKBOX
 *If you want to pull them from a save, please enter the name of the txt file (Leave blank if checked previous box): TEXT FIELD
 *When the program closes do you want to save to the same txt file and overwrite it? CHECKBOX
 * TEXT FIELD
 *
 *Number of agents per generation: INTEGER
 *Selectivity Factor (Should be a decimal from 0-1): DOUBLE
 *Gene Flow Factor (Should be a decimal from 0-1): DOUBLE
 *GENE FLOW + SELECTIVITY SHOULD BE <= 1
 *Number of generations (Set 0 if desiring to proceed indefinitely): INTEGER
 *Remember, program can be paused and saved at any point by pressing ESC
 *
 *RUN SMBAI
 */

public class LauncherForm extends JFrame implements ActionListener{
	private Container c;
	private JLabel titleL = new JLabel("SMBAI Launcher");
	private JLabel randomAgentsL = new JLabel("Do you want to generate a full new set of agents?");
	private JCheckBox randomAgentsC = new JCheckBox();
	private boolean randomAgentsA;
	private JLabel saveFilePullL = new JLabel("If you want to pull them from a save please enter the name of the txt file (Leave blank if checked previous box):");
	private JComboBox saveFilePullT;
	private JLabel resetIndexL = new JLabel("If you are pulling from a save, do you want to start from the beginning of the set (This allows you to see the best agents first): ");
	private JCheckBox resetIndexC = new JCheckBox();
	private boolean resetIndexA;
	private String saveFilePullA;
	private JLabel overwriteL = new JLabel("When the program closes do you want to save to the same txt file and overwrite it?");
	private JCheckBox overwriteC = new JCheckBox();
	private boolean overwriteA;
	private JLabel saveFilePushL = new JLabel("If not what is the new txt file you want to save it to (Leave blank if checked previous box)?");
	private JTextField saveFilePushT = new JTextField();
	private String saveFilePushA;
	private JLabel numAgentsL = new JLabel("Number of agents per generation:");
	private JTextField numAgentsT = new JTextField();
	private int numAgentsA;
	private JLabel selectFactL = new JLabel("Selectivity Factor (Should be a decimal from 0-1):");
	private JTextField selectFactT = new JTextField();
	private double selectFactA;
	private JLabel geneFlowL = new JLabel("Gene Flow Factor (Should be a decimal from 0-1):");
	private JTextField geneFlowT = new JTextField();
	private double geneFlowA;
	private JLabel selectGeneFlowFactNoteL = new JLabel("*GENE FLOW + SELECTIVITY SHOULD BE LESS THAN OR EQUAL TO 1*");
	private JLabel numGenL = new JLabel("Number of generations (Set 0 if desiring to proceed indefinitely):");
	private JTextField numGenT = new JTextField();
	private int numGenA;
	private JLabel speedL = new JLabel("Tick speed (Usually between 60 and 1000):");
	private JTextField speedT = new JTextField();
	private double speedA;
	private JLabel escNoteL = new JLabel("*Remember program can be paused and saved at any point by pressing ESC*");
	private JButton runB = new JButton("Run SMBAI");
	private JLabel errorText = new JLabel();
	
	ArrayList<String> validPullFiles = new ArrayList<String>();
	private ArrayList<JComponent> components = new ArrayList<JComponent>();
	
	
	public LauncherForm() {
		components.add(titleL);
		components.add(randomAgentsL);
		components.add(randomAgentsC);
		components.add(saveFilePullL);
		for (String file : new File(System.getProperty("user.dir")).list()) {
			try {
				if (file.substring(file.length() - 6).equals(".smbai")) {
					validPullFiles.add(file);
				}
			} catch (StringIndexOutOfBoundsException e) {
				
			}
		}
		String[] validPullFilesArray = new String[validPullFiles.size()];
		for (int i = 0; i < validPullFiles.size(); i++) {
			validPullFilesArray[i] = validPullFiles.get(i);
		}
		saveFilePullT = new JComboBox(validPullFilesArray);
		
		components.add(saveFilePullT);
		components.add(resetIndexL);
		components.add(resetIndexC);
		components.add(overwriteL);
		components.add(overwriteC);
		components.add(saveFilePushL);
		components.add(saveFilePushT);
		components.add(numAgentsL);
		components.add(numAgentsT);
		numAgentsT.setText("100");
		components.add(selectFactL);
		components.add(selectFactT);
		selectFactT.setText(".2");
		components.add(geneFlowL);
		components.add(geneFlowT);
		geneFlowT.setText(".1");
		components.add(selectGeneFlowFactNoteL);
		components.add(numGenL);
		components.add(numGenT);
		numGenT.setText("0");
		components.add(speedL);
		components.add(speedT);
		speedT.setText("60");
		components.add(escNoteL);
		components.add(runB);
		components.add(errorText);
		
		setTitle("SMBAI Launcher");
		setBounds(0, 0, 1440, 900);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		c = getContentPane();
		c.setLayout(new FlowLayout(FlowLayout.CENTER));
		/*
		SpringLayout spring = new SpringLayout();
        c.setLayout(spring);
        spring.putConstraint(SpringLayout.WEST, randomAgentsC,
                5,
                SpringLayout.EAST, randomAgentsL);
        spring.putConstraint(SpringLayout.NORTH, randomAgentsC,
                5,
                SpringLayout.NORTH, getContentPane());
        spring.putConstraint(SpringLayout.EAST, getContentPane(),
                5,
                SpringLayout.EAST, randomAgentsC);
        ArrayList<JComponent> vertSep = new ArrayList<JComponent>();
        vertSep.add(titleL);
        vertSep.add(randomAgentsL);
        vertSep.add(saveFilePullL);
        vertSep.add(overwriteL);
        vertSep.add(saveFilePushL);
        vertSep.add();
        for (int i = 0; i < vertSep.size() - 1; i++) {
        	spring.putConstraint(SpringLayout.SOUTH, vertSep.get(i),
                    5,
                    SpringLayout.NORTH, vertSep.get(i+1));
        }
        spring.putConstraint(SpringLayout.SOUTH, getContentPane(),
                5,
                SpringLayout.SOUTH, errorText);
        */
        c.setBackground(new Color(4,107,122));
        
        for (JComponent comp : components) {
        	try {
				if (comp.getClass().equals(saveFilePushT.getClass())) {
	        		comp.setPreferredSize((new Dimension(100,25)));
	        		comp.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("Voyager.otf")).deriveFont(25f));
	        		comp.setForeground(new Color(255,109,249));
	        	} else if (comp.getClass().equals(saveFilePullT.getClass())){
	        		comp.setPreferredSize((new Dimension(150,25)));
	        		comp.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("Voyager.otf")).deriveFont(20f));
	        		comp.setForeground(new Color(255,109,249));
	        	} else {
	        		comp.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("Voyager.otf")).deriveFont(20f));
	        		comp.setForeground(new Color(99,48,122));
	        	}
				comp.setBackground(new Color(4,107,122));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
        	c.add(comp);
        }
        try {
			titleL.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("Voyager.otf")).deriveFont(40f));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        
        runB.addActionListener(this);
        //pack();
        setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == runB) {
			errorText.setText("");
			if (checkTypeValidity()) {
				formatAnswers();
				if (answerSensibility()) {
					setVisible(false);
					SMBAILauncher.submit();
				}
			}
            
        }
	}


	private void formatAnswers() {
		try {
			if (!saveFilePullA.substring(saveFilePullA.length()-6).equals(".smbai")) {
				saveFilePullA = validPullFiles.get(saveFilePullT.getSelectedIndex()) + ".smbai";
			}
		} catch (StringIndexOutOfBoundsException e) {
			saveFilePullA = saveFilePullA + ".smbai";
		}
		if (overwriteA && !saveFilePullA.equals(".smbai")) {
			saveFilePushA = saveFilePullA;
		} else {
			try {
				if (!saveFilePushA.substring(saveFilePushA.length()-6).equals(".smbai")) {
					saveFilePushA = saveFilePushA + ".smbai";
				}
			} catch (StringIndexOutOfBoundsException e) {
				saveFilePushA = saveFilePushA + ".smbai";
			}
		}
		
	}


	private boolean answerSensibility() {
		if (!randomAgentsA) {
			File f = new File(saveFilePullA);
			if (!(isPathValid(saveFilePullA) && f.exists())) {
				errorText.setText("File Error (The save file to pull from is invalid)");
				return false;
			}
		}
		if (!isPathValid(saveFilePushA)) {
			errorText.setText("File Error (The save file to push to is invalid)");
			return false;
		}
		if (geneFlowA + selectFactA > 1) {
			errorText.setText("Sum Error (The gene flow and selectivity factor together must add up to less than one");
			return false;
		}
		return true;
	}


	private boolean checkTypeValidity() {
		if (numAgentsT.getText().equals("")||selectFactT.getText().equals("")||geneFlowT.getText().equals("")||numGenT.getText().equals("")) {
			errorText.setText("Null Error (One or more sections required to be filled in were left blank)");
			return false;
		}
		if (!randomAgentsC.isSelected() && validPullFiles.get(saveFilePullT.getSelectedIndex()).equals("")) {
			errorText.setText("Null Error (Pull File is left blank without checking random agents)");
			return false;
		}
		if (!overwriteC.isSelected() && saveFilePushT.getText().equals("")) {
			errorText.setText("Null Error (Push File is left blank without checking overwrite)");
			return false;
		}
		if (randomAgentsC.isSelected() && saveFilePushT.getText().equals("")) {
			errorText.setText("Null Error (Push File is left blank)");
			return false;
		}
		try {
			randomAgentsA = randomAgentsC.isSelected();
			saveFilePullA = validPullFiles.get(saveFilePullT.getSelectedIndex()).trim();
			resetIndexA = (resetIndexC.isSelected() && !randomAgentsC.isSelected());
			overwriteA = overwriteC.isSelected();
			saveFilePushA = saveFilePushT.getText().trim();
			numAgentsA = Integer.parseInt(numAgentsT.getText().trim());
			selectFactA = Double.parseDouble(selectFactT.getText().trim());
			geneFlowA = Double.parseDouble(geneFlowT.getText().trim());
			numGenA = Integer.parseInt(numGenT.getText().trim());
			speedA = Double.parseDouble(speedT.getText().trim());
			if (selectFactA > 1 || selectFactA < 0) {
				errorText.setText("Validity Error (Selectivity Factor must be between 1 and 0)");
				return false;
			}
			if (geneFlowA > 1 || geneFlowA < 0) {
				errorText.setText("Validity Error (Gene Flow must be between 1 and 0)");
				return false;
			}
			if (speedA < 0) {
				errorText.setText("Validity Error (Tick Speed must be more than 0)");
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			errorText.setText("Number Format Exception (Please ensure there are decimals and integers are used correctly)");
			return false;
		}
		
	}
	
	public static boolean isPathValid(String path) {

        try {

            Paths.get(path);

        } catch (InvalidPathException ex) {
            return false;
        }

        return true;
    }


	public boolean getShouldPull() {
		return !randomAgentsA;
	}


	public String getPull() {
		return saveFilePullA;
	}


	public String getPush() {
		return saveFilePushA;
	}


	public int getAPG() {
		return numAgentsA;
	}


	public int getGens() {
		return numGenA;
	}


	public double getSelect() {
		return selectFactA;
	}


	public double getFlow() {
		return geneFlowA;
	}
	
	public boolean getResetIndex() {
		return resetIndexA;
	}
	
	public double getTicks() {
		return speedA;
	}
	
	
	
}
