package leveldata.layer;

import java.awt.Graphics;
import java.io.IOException;
import java.io.RandomAccessFile;

import leveldata.Block;

public class ImageLayer extends Layer {
	private short imageIndex;
	private short Xparallax;
	private short Yparallax;
	private short Xscroll;
	private short Yscroll;
	public ImageLayer(Block parentBlock, RandomAccessFile data) throws IOException {
		super(parentBlock, data.readByte());
		imageIndex = data.readShort();
		Xparallax = data.readShort();
		Yparallax = data.readShort();
		Xscroll = data.readShort();
		Yscroll = data.readShort();
	}
	private ImageLayer(ImageLayer l) {
		super(l.getParentBlock(), l.getDepth());
		this.imageIndex = l.imageIndex;
		this.Xparallax = l.Xparallax;
		this.Yparallax = l.Yparallax;
		this.Xscroll = l.Xscroll;
		this.Yscroll = l.Yscroll;
	}
	@Override
	public void paint(Graphics g, int i, int j, int xOffset, int yOffset) {
		// TODO Auto-generated method stub

	}
	@Override
	public void saveData(RandomAccessFile file) throws IOException {
		file.writeByte(LayerID.IMAGE_DECORATION.getIndex());
		file.writeByte(this.getDepth());
		file.writeShort(imageIndex);
		file.writeShort(Xparallax);
		file.writeShort(Yparallax);
		file.writeShort(Xscroll);
		file.writeShort(Yscroll);
	}
	@Override
	public boolean hasEditableProperties() {
		return true;
	}
	@Override
	public boolean hasVariableDepth() {
		return true;
	}
	@Override
	public ImageLayer clone() {
		return new ImageLayer(this);
	}
	
	
}
