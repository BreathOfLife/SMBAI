package smbai;

//This class is simply to transport data about the proximity of the nearest of each sprite type
public class TrpGameData {
	public double[] PNEnemy,PNWallRight,PNFloor,PNCeiling,PNVoid,marioPos; //Proximity Nearest of all these things
	
	public TrpGameData(double[] enemy,double[] wallRight,double[] floor,double[] ceiling,double[] mario) {
		PNEnemy = enemy;
		PNWallRight = wallRight;
		PNFloor = floor;
		PNCeiling = ceiling;
		marioPos = mario;
	}
}
