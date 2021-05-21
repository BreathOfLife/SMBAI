package smbai;

import java.io.Serializable;
import java.util.ArrayList;

public class Strategy implements Serializable{
	private ArrayList<Condition> conds;
	private char actionType;
	private double actionDuration;
	
	public char getActionType() {
		return actionType;
	}
	
	public double getActionDuration() {
		return actionDuration;
	}
	
	public Strategy(int conditionQuantity) {
		conds = new ArrayList<Condition>();
		createConditions(conditionQuantity);
		actionType = SMBAIManager.actionTypeList[RandomHandler.linRandI(SMBAIManager.actionTypeList.length)];
		actionDuration = RandomHandler.hypRandD(1);
	}
	
	public Strategy(ArrayList<Condition> conds, char actionType, double actionDuration) {
		this.conds = conds;
		this.actionDuration = actionDuration;
		this.actionType = actionType;
	}
	
	
	public Strategy clone() {
		ArrayList<Condition> condsClone = new ArrayList<Condition>();
		for (Condition cond : conds) {
			condsClone.add(cond.clone());
		}
		return new Strategy(condsClone,actionType,actionDuration);
	}
	
	private ArrayList<Condition> getConds() {
		return conds;
	}

	private void createConditions(int quantity) {
		for (int conditionIndex = 0; conditionIndex < quantity; conditionIndex++) {
			conds.add(new Condition());
		}
	}
	
	public boolean evaluateConditions() {
		TrpGameData compVision = OpticalAnalysis.getGameData();
		for (Condition cond: conds) {
			if (!cond.evaluate(compVision)) {
				return false;
			}
		}
		return true;
	}
	
	private double minorMutate(double initValue, double k) { //k should be a decimal as it is the exponent that the linearly created multiplier is raised to
		double polarity = 2 * (RandomHandler.linRandI(2) - 0.5); //Randomly decides on either -1 or +1
		double multiplier = Math.pow(RandomHandler.linRandD(1),1/k);
		if (polarity * initValue * multiplier + initValue <= 0) {
			return 0;
		}
		return polarity * initValue * multiplier + initValue;
	}
	
	public void massMinorMutate(double trigDifK, double trigMinK, double actionDurationK) {
		actionDuration = minorMutate(actionDuration,actionDurationK);
		for (Condition condition: conds) {
			double trigDif = minorMutate(condition.getMaxTrigRad() - condition.getMinTrigRad(),trigDifK);  //Manipulate Values
			double trigMin = minorMutate(condition.getMinTrigRad(),trigMinK); //Manipulate Values
			condition.setMinTrigRad(trigMin); //Set New Values
			condition.setMaxTrigRad(trigMin + trigDif); //Set New Values
		}
	}
}
