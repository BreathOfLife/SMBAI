package smbai;

import java.util.Random;

public class RandomHandler {
	private static Random rand = new Random();
	public static double hypRandD(double k) { //Hyperbolically generated random double, starts at k
		return k / rand.nextDouble() - k + 1;
	}
	public static int hypRandI(double k) { //Hyperbolically generated random integer, starts at 1
		return (int)(k / rand.nextDouble() - k + 1);
	}
	public static double linRandD(int length) { //Linearly generated random double, starts at 0
		return rand.nextDouble() * length;
	}
	public static int linRandI(int length) { //Linearly generated random integer, starts at 0, exclusive to input
		return (int)(rand.nextDouble() * (length));
	}
}
