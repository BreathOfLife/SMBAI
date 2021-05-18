package smbai;

import java.util.ArrayList;

import manager.GameEngine;

public class SMBAIManager {
	public static final int agentQuantityPerGen = 100;
	public static final double selectivityFactor = 0.2;
	public static final double geneFlowFactor = 0.1; //What percent of each new population (after generation 1) should be randomly generated from scratch
	public static final int numNewRandomStrats = 1; //Number of random random strats that should be added to each hybrid agent
	public static final char[] trigTypeList = new char[] {'e','r','f','c'}; //Enemy, Right Wall, Floor, Ceiling
	public static final char[] actionTypeList = new char[] {'r','l','j','f'}; //Walk Right, Walk Left, Jump, Fire
	private static ArrayList<Agent> agentList = new ArrayList<Agent>();
	private static ArrayList<Agent> survivingList = new ArrayList<Agent>();
	private static int activeAgentIndex = 0;
	
	/*In each newly created agent population there are 3 types of agents, 
	 * purebred that are direct descendants of the successful parents with no manipulation, 
	 * hybrids that take a variety of different strategies from the gene pool of the successful parents with some mutation as well as some new random strategies, 
	 * lastly there is a portion of the agents that are completely new and random
	 */
	
	public static String pullFile;
	public static String pushFile;
	public static boolean readFromSave = true;
	
	public SMBAIManager() {
		ButtonExecution.init();
		getLauncherInput();
		if (readFromSave) {
			ReadData.read(fileName);
		} else {
			createRandomAgents();
		}
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
		} else if (GameEngine.gameEngine.isGameOver()) {
			agentList.get(activeAgentIndex).setScore(GameEngine.gameEngine.getMapManager().getMario().getX() - 10000);
		} else {
			System.out.println("Error: Why was scoreAI called if the game isn't over?");
		}
		System.out.println("Agent " + (activeAgentIndex + 1) + " Score: " + agentList.get(activeAgentIndex).getScore());
	}

	@SuppressWarnings("unused")
	private void reGeneration() {
		System.out.println("Spawning new generation...");
		if (geneFlowFactor + selectivityFactor >= 1) {
			System.out.println("Hold up, gene flow and selectivity can't be that high because then they'll conflict with each other");
		}
		naturalSelection();
		agentList.clear();
		copyParents(); //Duplicate the parents to ensure the successful agents survive
		geneFlow();
		System.out.println("Generating agents based on parent traits");
		for (int i = agentList.size(); i < agentQuantityPerGen; i++) {
			agentList.add(selectParentTraits());
		}
		activeAgentIndex = 0;
	}

	private void geneFlow() {
		int numIntroduced = (int) (geneFlowFactor * agentQuantityPerGen);
		System.out.println("Introducing " + numIntroduced + " new agents to the environment");
		createRandomAgents(numIntroduced);
	}

	private void copyParents() {
		System.out.println("Carrying duplicates of the survivors of the parent generation to the new one");
		for (int i = 0; i < survivingList.size(); i++) {
			ArrayList<Strategy> stratsList = new ArrayList<Strategy>();
			for (int j = 0; j < survivingList.get(i).getStratSize(); j++) {
				stratsList.add(survivingList.get(i).getStrat(j).clone());
			}
			agentList.add(new Agent(stratsList));
		}
	}

	private Agent selectParentTraits() {
		ArrayList<Strategy> stratsList = new ArrayList<Strategy>();
		int numStrats = survivingList.get(RandomHandler.linRandI(survivingList.size())).getStratSize();
		for (int i = 0; i < numStrats - numNewRandomStrats; i++) {
			Agent currentParent = survivingList.get(RandomHandler.linRandI(survivingList.size()));
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
			System.out.println("Score of surviving agent: " + highScore);
			agentList.remove(highIndex);
		}
	}

	public void nextAI() {
		if ((activeAgentIndex + 1) < agentList.size()) {
			activeAgentIndex++;
		} else {
			reGeneration();
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
}