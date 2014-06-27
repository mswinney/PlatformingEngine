package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import leveldata.Block;

public class MapIconPanel extends JPanel {
	private static final long serialVersionUID = 1777557279914469983L;
	private static final int[][] scrollXs = { {47, 40, 7, 0},
		{0, 7,  7,  0}, { 0,  7, 40, 47}, {40, 47, 47, 40} };
	private static final int[][] scrollYs = { { 0,  7, 7, 0},
		{0, 7, 40, 47}, {47, 40, 40, 47}, {40, 47,  0,  7} };
	private static final Color SCROLL_LOCKED = Color.RED;
	private static final Color SCROLL_UNLOCKED = Color.GREEN;
	

	private boolean updateOnPaint = false;
	private MapIcon mapIcon;

	public MapIconPanel() {
		super();
		mapIcon = new MapIcon();
	}
	public MapIconPanel(boolean updateOnPaint) {
		this();
		this.updateOnPaint = updateOnPaint;
	}
	/*@Override
	public void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
	}*/
	public void updateData() {
		Block b = LevelEditor.editor.getCurrentBlock();
		if (b != null) {
			mapIcon.updateData(b);
		}
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (updateOnPaint) { this.updateData(); }
		g.setColor(Color.BLACK);
		g.drawRect(7, 7, 33, 33);
		g.setColor(Color.PINK);
		g.fillRect(8, 8, 32, 32);
		mapIcon.paint(g);
	}

	@Override
	public Dimension getMinimumSize() { return new Dimension(48, 48); }
	public Dimension getMaximumSize() { return new Dimension(48, 48); }
	public Dimension getPreferredSize() { return new Dimension(48, 48); }
	public void setMapIcon(int selection) { mapIcon.setMapIcon(selection); }
	public MapIcon getMapIcon() { return mapIcon; }
	public static class MapIcon {
		private int mapIconIndex;
		private Color mapColor;
		private short mapMarkingBytes;
		private short doorBytes;
		private byte scrollByte;

		public MapIcon() { }
		public MapIcon(Block target) {
			this.mapIconIndex = target.getMapIconIndex();
			this.mapColor = target.getMapColor();
			this.mapMarkingBytes = target.getMapMarkingBytes();
			this.doorBytes = target.getDoorBytes();
			this.scrollByte = target.getScrollByte();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other.getClass().equals(MapIcon.class)) {
				MapIcon o = (MapIcon) other;
				if (this.mapIconIndex == o.mapIconIndex &&
						this.mapColor.equals(o.mapColor) &&
						this.mapMarkingBytes == o.mapMarkingBytes &&
						this.doorBytes == o.doorBytes &&
						this.scrollByte == o.scrollByte) {
					return true;
				}
			}
			return false;
		}

		public void paint(Graphics g) {
			this.drawScrollByte(g);
			Block.paintMapIcon(g, LevelEditor.editor.getZone().getDoorColors(),
					mapIconIndex, mapColor,
					mapMarkingBytes, doorBytes, 8, 8, 32, 32);
		}
		private void drawScrollByte(Graphics g) {
			boolean[] scrolling = Block.getScrollLocking(scrollByte);
			for (int i = 0; i < 4; i++) {
				if (scrolling[i]) {
					g.setColor(SCROLL_LOCKED);
				}
				else {
					g.setColor(SCROLL_UNLOCKED);
				}
				g.fillPolygon(scrollXs[i], scrollYs[i], 4);
				g.setColor(Color.BLACK);
				g.drawPolygon(scrollXs[i], scrollYs[i], 4);
			}
		}
		public void setMapIcon(int selection) {
			this.mapIconIndex = selection;
		}
		public void updateData(Block block) {
			mapIconIndex = block.getMapIconIndex();
			mapColor = block.getMapColor();
			mapMarkingBytes = block.getMapMarkingBytes();
			doorBytes = block.getDoorBytes();
			scrollByte = block.getScrollByte();
		}
		public int getMapIconIndex() { return mapIconIndex; }
		public short getMapMarkingBytes() { return mapMarkingBytes; }
		public short getDoorBytes() { return doorBytes; }
		public Color getMapColor() { return mapColor; }
		public byte getScrollByte() { return scrollByte; }
		public void setMapColor(Color mapColor) {
			this.mapColor = mapColor;
		}
		
		public void setScrollLock(ScrollLock dir, boolean lock) {
			if (lock) {
				// adding a lock-- OR 1
				scrollByte = (byte) (scrollByte | dir.getMask());
			}
			else {
				// removing a lock-- AND 0
				scrollByte = (byte) (scrollByte & ~dir.getMask());
			}
		}
		public boolean getScrollLock(ScrollLock dir) {
			return (scrollByte & dir.getMask()) > 0;
		}
		public static enum ScrollLock {
			UP(0x8), LEFT(0x4), DOWN(0x2), RIGHT(0x1);
			private int mask;
			ScrollLock(int mask) { this.mask = mask; }
			public int getMask() { return mask; }
		}
		public static enum DoorPosition {
			UP(0x0FFF, 12), LEFT(0xF0FF, 8), DOWN(0xFF0F, 4), RIGHT(0xFFF0, 0);
			private int mask;
			private int shifts;
			DoorPosition(int mask, int shifts) { this.mask = mask; this.shifts = shifts; }
			public int getMask() { return mask; }
			public int getShifts() { return shifts; }
		}
		public void setDoor(DoorPosition doorType, int newDoor) {
			doorBytes = (short) ((doorBytes & doorType.getMask()) +
					(newDoor << doorType.getShifts()) );
		}
		public int getDoor(DoorPosition doorType) {
			return ((doorBytes & ~doorType.getMask()) >> doorType.getShifts()) & 0xF;
		}
	}
}
