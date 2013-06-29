package platform;

import java.io.IOException;
import java.net.URISyntaxException;

public class Playground {
	public static void main(String[] args) throws IOException, URISyntaxException {
		/*MapIcon m = new MapIcon();
		m.setDoor(DoorPosition.UP, 15);
		System.out.println(m.getDoor(DoorPosition.UP));
		m.setDoor(DoorPosition.LEFT, 13);
		System.out.println(m.getDoor(DoorPosition.LEFT));
		m.setDoor(DoorPosition.DOWN, 14);
		System.out.println(m.getDoor(DoorPosition.DOWN));
		m.setDoor(DoorPosition.RIGHT, 12);
		System.out.println(m.getDoor(DoorPosition.RIGHT));*/
		System.out.println(ZettaUtil.clamp(0, 1, 10));
		System.out.println(ZettaUtil.clamp(3, 1, 10));
		System.out.println(ZettaUtil.clamp(11, 1, 10));
	}
}
