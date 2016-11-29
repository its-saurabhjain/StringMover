package com.xerox.StringMover;

import java.util.Timer;
import java.util.TimerTask;

public class StringMover {

	public static void main(String[] args) {				
		
		StringMover stringMover = new StringMover();
		// set up scheduler
		Timer primaryTimer = new Timer();
		primaryTimer.scheduleAtFixedRate(new ScanForWork(), 100L, 1000L * Configurator.getInstance().getPOLLING_PERIOD());
	}

}
