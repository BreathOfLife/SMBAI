package smbai;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
	private JLabel saveFilePullL = new JLabel("If you want to pull them from a save, please enter the name of the txt file (Leave blank if checked previous box):");
	private JTextField saveFilePullT = new JTextField();
	private String saveFilePullA;
	private JLabel overwriteL = new JLabel("When the program closes do you want to save to the same txt file and overwrite it?");
	private JCheckBox overwriteC = new JCheckBox();
	private boolean overwriteA;
	private JLabel saveFilePushL = new JLabel("If not, what is the new txt file you want to save it to (Leave blank if checked previous box)?");
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
	private JLabel selectGeneFlowFactNoteL = new JLabel("*GENE FLOW + SELECTIVITY SHOULD BE <= 1*");
	private JLabel numGenL = new JLabel("Number of generations (Set 0 if desiring to proceed indefinitely):");
	private JTextField numGenT = new JTextField();
	private int numGenA;
	private JLabel escNoteL = new JLabel("*Remember, program can be paused and saved at any point by pressing ESC*");
	private JButton runB = new JButton("Run SMBAI");
	private JLabel errorText = new JLabel();
	
	private ArrayList<JComponent> components = new ArrayList<JComponent>();
	
	
	public LauncherForm() {
		components.add(titleL);
		components.add(randomAgentsL);
		components.add(randomAgentsC);
		components.add(saveFilePullL);
		components.add(saveFilePullT);
		components.add(overwriteL);
		components.add(overwriteC);
		components.add(saveFilePushL);
		components.add(saveFilePushT);
		components.add(numAgentsL);
		components.add(numAgentsT);
		components.add(selectFactL);
		components.add(selectFactT);
		components.add(geneFlowL);
		components.add(geneFlowT);
		components.add(selectGeneFlowFactNoteL);
		components.add(numGenL);
		components.add(numGenT);
		components.add(escNoteL);
		components.add(runB);
		components.add(errorText);
		
		setTitle("SMBAI Launcher");
		setBounds(200, 90, 1100, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER,5,20));
        
        for (JComponent comp : components) {
        	comp.setFont(new Font("Arial", Font.PLAIN, 20));
        	c.add(comp);
        	if (comp.getClass().equals(saveFilePullT.getClass())) {
        		comp.setPreferredSize((new Dimension(50,25)));
        	}
        }
        
        runB.addActionListener(this);
        setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action Occured");
		if (e.getSource() == runB) {
			errorText.setText("");
			if (checkTypeValidity()) {
				formatAnswers();
				if (answerSensibility()) {
					SMBAILauncher.submit();
				}
			}
            
        }
	}


	private void formatAnswers() {
		try {
			if (!saveFilePullA.substring(saveFilePullA.length()-4).equals(".txt")) {
				saveFilePullA = saveFilePullT.getText() + ".txt";
			}
		} catch (StringIndexOutOfBoundsException e) {
			saveFilePullA = saveFilePullA + ".txt";
		}
		if (overwriteA) {
			saveFilePushA = saveFilePullA;
		} else {
			try {
				if (!saveFilePushA.substring(saveFilePushA.length()-4).equals(".txt")) {
					saveFilePushA = saveFilePushA + ".txt";
				}
			} catch (StringIndexOutOfBoundsException e) {
				saveFilePushA = saveFilePushA + ".txt";
			}
		}
		
	}


	private boolean answerSensibility() {
		File f = new File(saveFilePullA);
		if (!(isPathValid(saveFilePullA) && f.exists())) {
			errorText.setText("File Error (The save file to pull from is invalid)");
			return false;
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
		if (!randomAgentsC.isSelected() && saveFilePullT.getText().equals("")) {
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
			saveFilePullA = saveFilePullT.getText();
			overwriteA = overwriteC.isSelected();
			saveFilePushA = saveFilePushT.getText();
			numAgentsA = Integer.parseInt(numAgentsT.getText());
			selectFactA = Double.parseDouble(selectFactT.getText());
			geneFlowA = Double.parseDouble(geneFlowT.getText());
			numGenA = Integer.parseInt(numGenT.getText());
			if (selectFactA > 1 || selectFactA < 0) {
				errorText.setText("Validity Error (Selectivity Factor must be between 1 and 0)");
				return false;
			}
			if (geneFlowA > 1 || geneFlowA < 0) {
				errorText.setText("Validity Error (Gene Flow must be between 1 and 0)");
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
	
	
	
}
