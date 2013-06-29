package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import platform.ZettaUtil;

public final class Controls {
	private static final String CONTROL_FILE = "resources/defaultcontrols.ini";
	
	public String[] controlKeys;
	
	public Controls() {
		// TODO: attempt to read from external ini.
		// if not present, unpack default control file and read from that
		String file = CONTROL_FILE;
		Scanner data = null;
		try {
			data = new Scanner(new File(CONTROL_FILE));
		} catch (FileNotFoundException e) {
			ZettaUtil.error("Couldn't find control file " + CONTROL_FILE);
		}
		String next;
		controlKeys = new String[Input.values().length];
		while (data.hasNext()) {
			next = data.nextLine().trim();
			for (int i = 0; i < Input.values().length; i++) {
				String nextName = Input.values()[i].name();
				if (ZettaUtil.startsWithIgnoreCase(next, nextName)) {
					controlKeys[i] = next.substring(nextName.length() + 1).toUpperCase();
				}
			}
		}
	}
	public static enum Input {
		// valid input messages
		UP,
		DOWN,
		LEFT,
		RIGHT,
		JUMP,
		DEBUG;
	}
}
