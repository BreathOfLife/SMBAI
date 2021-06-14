package smbai;

import java.io.Serializable;
import java.util.ArrayList;

public class Agent implements Serializable{
	ArrayList<Strategy> strats;
	double score = 0;
	
	public Agent(int strategyQuantity) {
		strats = new ArrayList<Strategy>();
		createStrategies(strategyQuantity);
	}

	public Agent(ArrayList<Strategy> strats) {
		this.strats = strats;
	}
	
	private void createStrategies(int quantity) {
		for (int strategyIndex = 0; strategyIndex < quantity; strategyIndex++) {
			strats.add(new Strategy(RandomHandler.hypRandI(0.3)));
		}
		//Default Rule
		strats.add(new Strategy(0));
	}
	
	public Agent agentClone() {
		ArrayList<Strategy> stratsList = new ArrayList<Strategy>();
		for (Strategy str : strats) {
			stratsList.add(str.clone());
		}
		return new Agent(stratsList);
	}
	
	public int getStratSize() {
		return strats.size();
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double s) {
		score = s;
	}
	
	public void decideActions() {
		for (Strategy strat: strats) {
			if (strat.evaluateConditions()) {
				switch (strat.getActionType()) {
				case 'r':
					ButtonExecution.setRightRelease(strat.getActionDuration());
					break;
				case 'l':
					ButtonExecution.setLeftRelease(strat.getActionDuration());
					break;
				case 'j':
					ButtonExecution.setJumpRelease(strat.getActionDuration());
					break;
				case 'f':
					ButtonExecution.setFireRelease(strat.getActionDuration());
					break;
				}
			}
			
		}
	}

	public Strategy getStrat(int index) {
		return strats.get(index);
	}
}
