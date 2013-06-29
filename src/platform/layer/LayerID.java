package platform.layer;

import java.util.HashMap;

public enum LayerID {
	TILE_DECORATION   (0x7D),
	TILE_COLLISION    (0x7C),
	IMAGE_DECORATION  (0x1D),
	COLOR_DECORATION  (0xCD),
	END_BLOCK         (0xEB),
	ENTITY_DATA       (0xED),
	END_OF_FILE       (0xFF);
	private static HashMap<Integer, LayerID> layerids;
	private int index;
	private LayerID(int index) {
		this.index = index;
	}
	public static void initialize() {
		layerids = new HashMap<Integer, LayerID>();
		for (LayerID l : LayerID.values()) {
			layerids.put(l.index, l);
		}
	}
	public static LayerID getLayerID(int i) {
		return layerids.get(i);
	}
	public int getIndex() { return this.index; }
}
