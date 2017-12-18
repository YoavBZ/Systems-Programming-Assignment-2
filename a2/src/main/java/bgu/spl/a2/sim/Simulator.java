/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	public static ActorThreadPool actorThreadPool;
	private static File jsonFile;
	private static JsonObject jsonObject;

	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start() {
		// TODO: Wait between phases
		// Phase 1
		submitActionsList(jsonObject.get("Phase1").getAsJsonArray());
		// Phase 2
		submitActionsList(jsonObject.get("Phase2").getAsJsonArray());
		// Phase 3
		submitActionsList(jsonObject.get("Phase3").getAsJsonArray());
	}

	private static void submitActionsList(JsonArray array) {
		for (JsonElement element : array){
			JsonObject jsonObject = element.getAsJsonObject();
			// TODO: Create new action and submit
		}
	}

	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 *
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
		actorThreadPool = myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String, PrivateState> end() {
		try {
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
		}
		return (HashMap<String, PrivateState>) actorThreadPool.getActors();
	}

	public static int main(String[] args) {
		jsonFile = new File(args[0]);
		JsonParser parser = new JsonParser();
		try {
			JsonElement element = parser.parse(new FileReader(jsonFile));
			jsonObject = element.getAsJsonObject();
			attachActorThreadPool(new ActorThreadPool(jsonObject.get("threads").getAsInt()));
			start();
			HashMap<String, PrivateState> SimulationResult;
			SimulationResult = end();
			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}
}
