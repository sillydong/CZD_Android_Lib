package czd.lib.application;

import android.util.Log;

public class Timer {
	long startTime, stopTime;

	public Timer() {
		startTime = 0l;
		stopTime = 0l;
	}

	/**
	 * start Timer
	 */
	public void start() {
		startTime = System.nanoTime();
	}

	/**
	 * stop Timer and print how long the action takes and restart the timer
	 *
	 * @param what
	 */
	public void stop(String what, boolean restart) {
		stopTime = System.nanoTime();
		if (restart)
		{
			start();
		}
		long val = (stopTime - startTime) / 1000L;
		Log.e("Pull", what + ":" + val);
	}
}
