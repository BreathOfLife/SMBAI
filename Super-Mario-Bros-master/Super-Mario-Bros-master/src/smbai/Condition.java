package smbai;

import java.io.Serializable;

public class Condition implements Serializable{

	private double minTrigRad, maxTrigRad;
	private char plane, trigType;
	private int planeIndex;
	
	public double getMinTrigRad() {return minTrigRad;}
	public double getMaxTrigRad() {return maxTrigRad;}
	public char getPlane() {return plane;}
	public char getTrigType() {return trigType;}
	public void setMinTrigRad(double newValue) {minTrigRad = newValue;}
	public void setMaxTrigRad(double newValue) {maxTrigRad = newValue;}
	public void setPlane(char newValue) {plane = newValue;}
	public void setTrigType(char newValue) {trigType = newValue;}
	
	public Condition() {
		minTrigRad = RandomHandler.hypRandD(0.5);
		maxTrigRad = RandomHandler.hypRandD(1) + minTrigRad;
		if (RandomHandler.linRandI(2) == 1) {
			plane = 'x';
			planeIndex = 0;
		} else {
			plane = 'y';
			planeIndex = 1;
		}
		trigType = SMBAIManager.trigTypeList[RandomHandler.linRandI(SMBAIManager.trigTypeList.length)];
	}
	
	public Condition(double minTR, double maxTR, char pln, char tT, int plnInd) {
		minTrigRad = minTR;
		maxTrigRad = maxTR;
		plane = pln;
		trigType = tT;
		planeIndex = plnInd;
	}
	
	public boolean evaluate(TrpGameData compVision) {
		double[] relevantCoords;
		switch (trigType) {
		case 'e':
			relevantCoords = compVision.PNEnemy;
			break;
		case 'r':
			relevantCoords = compVision.PNWallRight;
			break;
		case 'f':
			relevantCoords = compVision.PNFloor;
			break;
		case 'c':
			relevantCoords = compVision.PNCeiling;
			break;
		default:
			relevantCoords = null;
			System.out.println("Error, condition evaluation trig type not found");
		}
		if (relevantCoords ==  null) {
			return false;
		}
		if ((relevantCoords[planeIndex] - compVision.marioPos[planeIndex] >= minTrigRad) && (relevantCoords[planeIndex] - compVision.marioPos[planeIndex] <= maxTrigRad)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Condition clone() {
		return new Condition(minTrigRad, maxTrigRad, plane, trigType, planeIndex);
	}
}
