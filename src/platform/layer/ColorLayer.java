package platform.layer;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.RandomAccessFile;

import editor.LevelEditor;

import platform.Block;
import platform.Zone;

import ui.Game;

public class ColorLayer extends Layer {
	private Color c;
	
	public ColorLayer(Block parentBlock, RandomAccessFile data) throws IOException {
		super(parentBlock, data.readByte());
		int r = data.readUnsignedByte();
		int g = data.readUnsignedByte();
		int b = data.readUnsignedByte();
		int a = data.readUnsignedByte();
		c = new Color(r, g, b, a);
	}
	private ColorLayer(ColorLayer l) {
		super(l.getParentBlock(), l.getDepth());
		this.c = l.c;
	}
	public String toString() {
		return String.format("[%d] Color: %s", this.getDepth(), this.getColorString());
	}
	private String getColorString() {
		String hex = Integer.toHexString(c.getRGB() & 0xffffff);
		if (hex.length() < 6) {
		    hex = "0" + hex;
		}
		hex = "#" + hex + " (" + Integer.toHexString(c.getAlpha()) + ")";
		return hex.toUpperCase();
	}

	@Override
	public void paint(Graphics g, int i, int j) {
		g.setColor(c);
		Zone z = LevelEditor.editor.getZone();
		g.fillRect(i*Game.TILE_X, j*Game.TILE_Y,
				 z.getBlockSizeX() * Game.TILE_X, z.getBlockSizeY() * Game.TILE_Y);
	}

	@Override
	public void saveData(RandomAccessFile file) throws IOException {
		file.writeByte(LayerID.COLOR_DECORATION.getIndex());
		file.writeByte(this.getDepth());
		file.writeByte(c.getRed());
		file.writeByte(c.getGreen());
		file.writeByte(c.getBlue());
		file.writeByte(c.getAlpha());
	}

	@Override
	public boolean hasEditableProperties() {
		return true;
	}
	@Override
	public boolean hasVariableDepth() {
		return true;
	}
	
	public void setColor(Color c) {
		this.c = c;
	}
	public Color getColor() {
		return c;
	}
	@Override
	public Object clone() {
		return new ColorLayer(this);
	}

}
