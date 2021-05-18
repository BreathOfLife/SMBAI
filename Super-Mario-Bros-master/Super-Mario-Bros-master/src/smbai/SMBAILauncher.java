package smbai;

import manager.GameEngine;

public class SMBAILauncher{

	public static boolean pullSave;
	public static String pullFile;
	public static String pushFile;
	public static int agentPerGen;
	public static int gens;
	public static double selectFact;
	public static double geneFlow;
	
	private static LauncherForm launchF;
	
	
	public static void main(String[] args) {
		launchF = new LauncherForm();

	}


	public static void submit() {
		pullSave =  launchF.getShouldPull();
		pullFile = launchF.getPull();
		pushFile = launchF.getPush();
		agentPerGen = launchF.getAPG();
		gens = launchF.getGens();
		selectFact = launchF.getSelect();
		geneFlow = launchF.getFlow();
		GameEngine.gameStart();
	}

}
