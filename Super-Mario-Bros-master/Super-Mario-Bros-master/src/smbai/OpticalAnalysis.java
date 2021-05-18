package smbai;

import java.util.ArrayList;

import manager.GameEngine;
import manager.MapManager;
import model.Map;
import model.brick.Brick;
import model.enemy.Enemy;

public class OpticalAnalysis {
	public static TrpGameData getGameData() {
		double[] PNEnemy, PNWallRight, PNFloor, PNCeiling;
		MapManager mapManager = GameEngine.gameEngine.getMapManager();
		PNEnemy = findNearestPosition(mapManager,'e');
		PNWallRight = findNearestPosition(mapManager,'r');
		PNFloor = findNearestPosition(mapManager,'f');
		PNCeiling = findNearestPosition(mapManager,'c');
		TrpGameData gameDataObj = new TrpGameData(PNEnemy, PNWallRight, PNFloor, PNCeiling, new double[] {mapManager.getMario().getX(),mapManager.getMario().getY()});
		return gameDataObj;
	}
	
	private static double[] findNearestPosition(MapManager mapManager, char trigType) {
		double lowestDistance = Double.MAX_VALUE; //Could work with squared values or with base values dependending on the algorithm associated with the trigger type
		double proxSquared;
		int closestTriggerIndex = -1;
		Map map = mapManager.getMap();
		double[] marioPos = new double[] {map.getMario().getX(),map.getMario().getY()};
		ArrayList<Enemy> enemyList = map.getEnemies();
		ArrayList<Brick> allBrickList = map.getAllBricks();
		
		double[] returnSet = null;
		
		switch (trigType) {
		case 'e':
			for (int i = 0; i < enemyList.size(); i++) {
				proxSquared = (Math.pow((enemyList.get(i).getX()-marioPos[0]),2) + Math.pow((enemyList.get(i).getY()-marioPos[1]), 2));
				if (proxSquared < lowestDistance) {
					closestTriggerIndex = i;
					lowestDistance = proxSquared;
				}
			}
			if (closestTriggerIndex == -1) {
				returnSet = null;
			} else {
				returnSet = new double[] {enemyList.get(closestTriggerIndex).getX(),enemyList.get(closestTriggerIndex).getY()};
			}
			break;
		case 'r':
			for (int i = 0; i < allBrickList.size(); i++) {
				if (marioPos[0] < allBrickList.get(i).getX()) {
					proxSquared = (Math.pow((allBrickList.get(i).getX()-marioPos[0]),2) + Math.pow((allBrickList.get(i).getY()-marioPos[1]), 2));
					if (proxSquared < lowestDistance) {
						closestTriggerIndex = i;
						lowestDistance = proxSquared;
					}
				}
			}
			if (closestTriggerIndex == -1) {
				returnSet = null;
			} else {
				returnSet = new double[] {allBrickList.get(closestTriggerIndex).getX(),allBrickList.get(closestTriggerIndex).getY()};
			}
			break;
		case 'f':
			for (int i = 0; i < allBrickList.size(); i++) {
				if (marioPos[1] > allBrickList.get(i).getY()) {
					proxSquared = (Math.pow((allBrickList.get(i).getX()-marioPos[0]),2) + Math.pow((allBrickList.get(i).getY()-marioPos[1]), 2));
					if (proxSquared < lowestDistance) {
						closestTriggerIndex = i;
						lowestDistance = proxSquared;
					}
				}
				proxSquared = (Math.pow((map.getBottomBorder()-marioPos[1]),2));
				if (proxSquared < lowestDistance) {
					closestTriggerIndex = -2;
					lowestDistance = proxSquared;
				}
			}
			if (closestTriggerIndex == -1) {
				returnSet = null;
			} else if (closestTriggerIndex == -2) {
				returnSet = new double[] {marioPos[0],map.getBottomBorder()};
			} else {
				returnSet = new double[] {allBrickList.get(closestTriggerIndex).getX(),allBrickList.get(closestTriggerIndex).getY()};
			}
			break;
		case 'c':
			for (int i = 0; i < allBrickList.size(); i++) {
				if (marioPos[1] < allBrickList.get(i).getY()) {
					proxSquared = (Math.pow((allBrickList.get(i).getX()-marioPos[0]),2) + Math.pow((allBrickList.get(i).getY()-marioPos[1]), 2));
					if (proxSquared < lowestDistance) {
						closestTriggerIndex = i;
						lowestDistance = proxSquared;
					}
				}
			}
			if (closestTriggerIndex == -1) {
				returnSet = null;
			} else {
				returnSet = new double[] {allBrickList.get(closestTriggerIndex).getX(),allBrickList.get(closestTriggerIndex).getY()};
			}
			break;
		default:
			System.out.println("Trig Type in Find Nearest Position not found");
		}
		return returnSet;
		
		
	}
}
