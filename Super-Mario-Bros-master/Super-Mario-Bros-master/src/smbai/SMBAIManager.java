package smbai;

import java.util.ArrayList;

import manager.GameEngine;

public class SMBAIManager {
	public static int agentQuantityPerGen = 100;
	public static double selectivityFactor = 0.2;
	public static double geneFlowFactor = 0.1; //What percent of each new population (after generation 1) should be randomly generated from scratch
	public static final int numNewRandomStrats = 1; //Number of random random strats that should be added to each hybrid agent
	public static final char[] trigTypeList = new char[] {'e','r','f','c'}; //Enemy, Right Wall, Floor, Ceiling
	public static final char[] actionTypeList = new char[] {'r','l','j','f'}; //Walk Right, Walk Left, Jump, Fire
	private static ArrayList<Agent> agentList = new ArrayList<Agent>();
	private static ArrayList<Agent> survivingList = new ArrayList<Agent>();
	private static int activeAgentIndex = 0;
	private static int activeGenIndex = 0;
	private static int[] pureHybBeast = {-1,-1,-1}; //These are the indexes for the first agent types of each category, purebreds, hybrids, and beasts
	
	/*In each newly created agent population there are 3 types of agents, 
	 * purebred that are direct descendants of the successful parents with no manipulation, 
	 * hybrids that take a variety of different strategies from the gene pool of the successful parents with some mutation as well as some new random strategies, 
	 * lastly there is a portion of the agents that are completely new and random
	 */
	
	public static String pullFile;
	public static String pushFile;
	private static boolean readFromSave;
	private static int totalGens;
	public static boolean autoStart = true;
	private static double oldTickSpeed = -1;
	
	public SMBAIManager() {
		getLauncherInput();
		if (readFromSave) {
			ReadData.read(pullFile);
			if (SMBAILauncher.resetIndex) {
				activeAgentIndex = 0;
			}
		} else {
			createRandomAgents();
		}
	}
	
	private void getLauncherInput() {
		pullFile = SMBAILauncher.pullFile;
		pushFile = SMBAILauncher.pushFile;
		readFromSave = SMBAILauncher.pullSave;
		agentQuantityPerGen = SMBAILauncher.agentPerGen;
		selectivityFactor = SMBAILauncher.selectFact;
		geneFlowFactor = SMBAILauncher.geneFlow;
		totalGens = SMBAILauncher.gens;
		
		
	}

	private void createRandomAgents() {
		for (int agentCreationIndex = 0; agentCreationIndex < agentQuantityPerGen; agentCreationIndex++) {
			agentList.add(new Agent(RandomHandler.hypRandI(5)));
		}
	}
	
	private void createRandomAgents(int limitedValue) {
		for (int agentCreationIndex = 0; agentCreationIndex < limitedValue; agentCreationIndex++) {
			agentList.add(new Agent(RandomHandler.hypRandI(5)));
		}
	}

	public void startAIRun() {
		GameEngine.gameEngine.selectMapViaKeyboard();
	}
	
	public void aiLoop() {
		ButtonExecution.updateTimings();
		agentList.get(activeAgentIndex).decideActions();
	}

	public void scoreAI() {
		if (GameEngine.gameEngine.getMapManager().endLevel()) {
			agentList.get(activeAgentIndex).setScore(GameEngine.gameEngine.getRemainingTime());
		} else {
			agentList.get(activeAgentIndex).setScore(GameEngine.gameEngine.getMapManager().getMario().getX() - 10000);
		}
		System.out.println("Agent " + (activeAgentIndex + 1) + " Score: " + agentList.get(activeAgentIndex).getScore());
	}

	@SuppressWarnings("unused")
	private void reGeneration() {
		System.out.println("==========================");
		System.out.println("Spawning new generation...");
		naturalSelection();
		agentList.clear();
		pureHybBeast[0] = agentList.size();
		copyParents(); //Duplicate the parents to ensure the successful agents survive
		pureHybBeast[1] = agentList.size();
		System.out.println("Generating agents based on parent traits");
		for (int i = agentList.size(); i < (int) ((1 - geneFlowFactor) * agentQuantityPerGen); i++) {
			agentList.add(selectParentTraits());
		}
		pureHybBeast[2] = agentList.size();
		geneFlow();
		survivingList.clear();
		System.out.println("==========================");
	}

	private void geneFlow() {
		int numIntroduced = agentQuantityPerGen - agentList.size();
		System.out.println("Introducing " + numIntroduced + " new agents to the environment");
		createRandomAgents(numIntroduced);
	}

	private void copyParents() {
		System.out.println("Carrying duplicates of the survivors of the parent generation to the new one");
		for (int i = 0; i < survivingList.size(); i++) {
			agentList.add(survivingList.get(i).agentClone());
		}
	}

	private Agent selectParentTraits() {
		ArrayList<Strategy> stratsList = new ArrayList<Strategy>();
		int numStrats = survivingList.get(RandomHandler.linRandI(survivingList.size())).getStratSize();
		for (int i = 0; i < numStrats - numNewRandomStrats; i++) {
			int currentParentIndex = Integer.MAX_VALUE;
			while (currentParentIndex > survivingList.size()) {
				currentParentIndex = 1 / RandomHandler.hypRandI(survivingList.size());
			}
			Agent currentParent = survivingList.get(currentParentIndex);
			stratsList.add(currentParent.getStrat(RandomHandler.linRandI(currentParent.getStratSize())).clone());
			stratsList.get(stratsList.size()-1).massMinorMutate(0.1, 0.1, 0.1);
		}
		for (int i = 0; i < numNewRandomStrats; i++) {
			stratsList.add(new Strategy(RandomHandler.hypRandI(0.3)));
		}
		return new Agent(stratsList);
	}

	private void naturalSelection() {
		int numSurvive = (int)(agentList.size() * selectivityFactor);
		System.out.println("Selecting " + numSurvive + " of the parent generation to survive and reproduce");
		for (int i = 0; i < numSurvive; i++) {
			if (i >= agentQuantityPerGen) {
				break;
			}
			double highScore = Integer.MIN_VALUE;
			int highIndex = 0;
			for (int j = 0; j < agentList.size(); j++) {
				//System.out.printf("Comparing Agent %d with Agent %d and their scores are %f and %f%n",highIndex,j,highScore,agentList.get(j).getScore());
				if (agentList.get(j).getScore() > highScore) {
					highScore = agentList.get(j).getScore();
					highIndex = j;
				}
			}
			survivingList.add(agentList.get(highIndex));
			agentList.remove(highIndex);
		}
		//Sort list
		ArrayList<Agent> tempList = new ArrayList<Agent>();
		Agent highestScoreAgent;
		int highestScoreIndex;
		while (survivingList.size() > 0) {
			highestScoreAgent = survivingList.get(0);
			highestScoreIndex = 0;
			for (int i = 0; i < survivingList.size(); i++) {
				if (highestScoreAgent.getScore() < survivingList.get(i).getScore()) {
					highestScoreAgent = survivingList.get(i);
					highestScoreIndex = i;
				}
			}
			tempList.add(highestScoreAgent);
			survivingList.remove(highestScoreIndex);	
		}
		survivingList = tempList;
		for (Agent agent : survivingList) {
			System.out.println("Score of surviving agent: " + agent.getScore());
		}
	}

	public void nextAI() {
		ButtonExecution.reset();
		if (agentList.get(activeAgentIndex).getScore() >= 95.0) {
			System.out.println("Agent has recieved inachievable score, retrying at lower frame rate...");
			if (oldTickSpeed == -1) {
				oldTickSpeed = GameEngine.getTickSpeed();
			}
			GameEngine.setTickSpeed(60);
		} else {
			if (oldTickSpeed != -1) {
				System.out.println("Reverting tick speed");
				GameEngine.setTickSpeed(oldTickSpeed);
				oldTickSpeed = -1;
			}
			if ((activeAgentIndex + 1) < agentList.size()) {
				activeAgentIndex++;
				if (activeAgentIndex == pureHybBeast[0]) {
					System.out.println("Intializing Testing of Purebreeds");
				} else if (activeAgentIndex == pureHybBeast[1]) {
					System.out.println("Intializing Testing of Hybrids");
				} else if (activeAgentIndex == pureHybBeast[2]) {
					System.out.println("Intializing Testing of Beasts");
				}
			} else {
				reGeneration();
				activeAgentIndex = 0;
				activeGenIndex++;
				SaveData.save(pushFile);
				if ((activeGenIndex >= totalGens) && totalGens != 0) {
	                System.exit(0);
				}
			}
			/*if (agentList.get(activeAgentIndex).getScore() != 0) {
				System.out.println("Current agent has been previously tested with a score of: " + agentList.get(activeAgentIndex).getScore());
			}*/
		}
		
	}
	
	public static ArrayList<Agent> getAgentList() {
		return agentList;
	}
	
	public static void setAgentList(ArrayList<Agent> agentListP) {
		agentList = agentListP;
	}

	public static int getAgentIndex() {
		return activeAgentIndex;
	}

	public static void setAgentIndex(int val) {
		activeAgentIndex = val;
		
	}

	public static int[] getCatIndexes() {
		return pureHybBeast;
	}

	public static void setCatIndexes(int[] catIndexes) {
		pureHybBeast = catIndexes;
	}
}
