package io.github.rypofalem.bigbads;


import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

public class Util {
	public static Random random = new Random();

	/*
	 * This method returns a weighted random object.
	 *
	 * weightedChoices is a Map of T keys linked to Integer weights
	 * This method will ignore T with weights less than or equal to 0
	 *
	 * Will return null if weightedChoices is null or devoid of any greater than zero weights
	 * The total of all weights should be less than Integer.MAX_VALUE (2^31 - 1) or this method will return null
	 */
	public static <T> T getWeightedRandomChoice(Map<T, Integer> weightedChoices){
		if(weightedChoices == null) return null;
		int total = 0;
		for(int weight : weightedChoices.values()){
			if(weight <= 0) continue;
			try {total = Math.addExact(total, weight);} catch(ArithmeticException e) {
				System.out.println(String.format("Could not add %d and %d", total, weight));
				e.printStackTrace();
				return null;}
		}
		if(total <= 0) return null;

		total = random.nextInt(total);
		for(T key : weightedChoices.keySet()){
			if(weightedChoices.get(key) <= 0) continue;
			total -= weightedChoices.get(key);
			if(total < 0) return key;
		}

		return null;
	}

	public static Vector getDirectionTo(Location from, Location to){
		return getDirectionTo(from.toVector(), to.toVector());
	}

	public static Vector getDirectionTo(Vector from, Vector to){
		return to.subtract(from);
	}

}
