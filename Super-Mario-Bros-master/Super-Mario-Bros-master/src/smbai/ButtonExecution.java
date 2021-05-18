package smbai;

import manager.ButtonAction;
import manager.GameEngine;

public class ButtonExecution {
	private static long[] timeLeftInAction = {-1,-1,-1,-1};
	private static boolean[] actionHeld = {false,false,false,false};
	private static long lastTime;
	private static long currentTime;
	
	public static void init() {
		lastTime = System.nanoTime();
	}
	
	public static void setRightRelease(double duration) {
		duration *= 1e+9;
		if (timeLeftInAction[0] < duration) {
			timeLeftInAction[0] = (long) duration;
		}
		actionHeld[0] = true;
		Right();
	}
	
	public static void setLeftRelease(double duration) {
		duration *= 1e+9;
		if (timeLeftInAction[1] < duration) {
			timeLeftInAction[1] = (long) duration;
		}
		actionHeld[1] = true;
		Left();
	}
	
	public static void setJumpRelease(double duration) {
		duration *= 1e+9;
		if (timeLeftInAction[2] < duration) {
			timeLeftInAction[2] = (long) duration;
		}
		actionHeld[2] = true;
		Jump();
	}
	
	public static void setFireRelease(double duration) {
		duration *= 1e+9;
		if (timeLeftInAction[3] < duration) {
			timeLeftInAction[3] = (long) duration;
		}
		actionHeld[3] = true;
		Fire();
	}
	
	public static void updateTimings() {
		currentTime = System.nanoTime();
		long diffTime = currentTime - lastTime;
		for (int timeLeftIndex = 0; timeLeftIndex < timeLeftInAction.length; timeLeftIndex++) {
			if (timeLeftInAction[timeLeftIndex] > 0) {
				timeLeftInAction[timeLeftIndex] -= diffTime;
				if (timeLeftInAction[timeLeftIndex] <= 0) {
					actionHeld[timeLeftIndex] = false;
					if (timeLeftIndex < 2) {
						GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.ACTION_COMPLETED_X);
					}
				}
			}
		}
		callFireAndJump();
		lastTime = currentTime;
	}
	
	private static void Right() {
		GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.M_RIGHT);
	}
	
	private static void Left() {
		GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.M_LEFT);
	}
	
	private static void Jump() {
		GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.JUMP);
	}
	
	private static void Fire() {
		GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.FIRE);
	}
	
	public static void callFireAndJump() {
		if (actionHeld[2]) {
			Jump();
		}
		if (actionHeld[3]) {
			Fire();
		}
	}
}
