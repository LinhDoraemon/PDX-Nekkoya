package pdx.nekkoya48.multithread;

import java.util.HashMap;

import pdx.core101.string.StringUtils;
import pdx.nekkoya48.api.PRODUCE48;

public class MachineThread extends Thread {

	public static boolean isRunning = false;
	
	public static HashMap<String, PRODUCE48> MACHINES = new HashMap<>();
	
	public MachineThread() {
		isRunning = true;
	}
	
	@Override
	public void run() {
		while (isRunning) {
			try {
				if(MACHINES == null) return;
				MACHINES.keySet().forEach(m -> {
					MACHINES.get(m).work(StringUtils.toLocation(m));
				});
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
				isRunning = false;
			}
		}
	}
	
	
}
