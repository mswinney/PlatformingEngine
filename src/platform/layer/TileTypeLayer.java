package platform.layer;

import platform.layer.TileLayer.Tile;

public interface TileTypeLayer {
	public Tile getTile(int x, int y);
	public void setTile(Tile t, int x, int y);
	public TileTypeLayer duplicate();
}
