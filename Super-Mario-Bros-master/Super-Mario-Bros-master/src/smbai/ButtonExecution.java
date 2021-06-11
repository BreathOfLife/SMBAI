package smbai;

import manager.ButtonAction;
import manager.GameEngine;

public class ButtonExecution {
	private static long[] framesLeftInAction = {-1,-1,-1,-1};
	private static boolean[] actionHeld = {false,false,false,false};
	
	public static void setRightRelease(double duration) {
		duration *= 60;
		if (framesLeftInAction[0] < duration) {
			framesLeftInAction[0] = (long) duration;
		}
		actionHeld[0] = true;
		Right();
	}
	
	public static void setLeftRelease(double duration) {
		duration *= 60;
		if (framesLeftInAction[1] < duration) {
			framesLeftInAction[1] = (long) duration;
		}
		actionHeld[1] = true;
		Left();
	}
	
	public static void setJumpRelease(double duration) {
		duration *= 60;
		if (framesLeftInAction[2] < duration) {
			framesLeftInAction[2] = (long) duration;
		}
		actionHeld[2] = true;
		Jump();
	}
	
	public static void setFireRelease(double duration) {
		duration *= 60;
		if (framesLeftInAction[3] < duration) {
			framesLeftInAction[3] = (long) duration;
		}
		actionHeld[3] = true;
		Fire();
	}
	
	public static void updateTimings() {
		for (int timeLeftIndex = 0; timeLeftIndex < framesLeftInAction.length; timeLeftIndex++) {
			if (framesLeftInAction[timeLeftIndex] > 0) {
				framesLeftInAction[timeLeftIndex]--;
				if (framesLeftInAction[timeLeftIndex] <= 0) {
					actionHeld[timeLeftIndex] = false;
					framesLeftInAction[timeLeftIndex] = -1;
					if (timeLeftIndex < 2) {
						GameEngine.gameEngine.inputManager.virtualNotifyInput(ButtonAction.ACTION_COMPLETED_X);
					}
				}
			}
		}
		callFireAndJump();
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
	
	public static void reset() {
		framesLeftInAction[0] = -1;
		framesLeftInAction[1] = -1;
		framesLeftInAction[2] = -1;
		framesLeftInAction[3] = -1;
	}
}
