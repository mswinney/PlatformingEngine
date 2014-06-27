package leveldata;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import javax.imageio.ImageIO;

import leveldata.layer.TileLayer.Tile;

import ui.Game;


public final class ZettaUtil {


	public static final String FONT_FILE = "resources/data/PressStart2P.ttf";

	public static Font systemFont;

	public static int randomInt(int min, int max) {
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}
	public static String formatNumber(double num) {
		if (num%1==0) {
			return "" + (int)num;
		}
		else {
			return "" + num;
		}
	}
	public static String formatPercent(double num) {
		return formatNumber(num);
	}
	public static boolean startsWithIgnoreCase(String command, String prefix) {
		boolean retval;
		try {
			retval = command.substring(0,prefix.length()).equalsIgnoreCase(prefix);
		}
		catch (StringIndexOutOfBoundsException e) {
			return false;
		}
		return retval;
	}

	public static void loadFont() {
		try {
			systemFont = Font.createFont(Font.TRUETYPE_FONT,
					ZettaUtil.class.getResourceAsStream(FONT_FILE)).deriveFont(16F);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param tileIndex Tile number to draw.
	 * @param tiles Tilesheet to pull from.
	 * @param g Graphics upon which to draw tiles.
	 * @param i X in TILES.
	 * @param j Y in TILES.
	 */
	public static void drawTile(int tileIndex, BufferedImage tiles, Graphics g, int i, int j, int xOffset, int yOffset) {
		int tileX = Game.TILE_X;
		int tileY = Game.TILE_Y;
		int tileSheetWidth = tiles.getWidth()/tileX;
		int sheetLocationX = tileX*(tileIndex%tileSheetWidth);
		int sheetLocationY = tileY*(tileIndex/tileSheetWidth);
		g.drawImage(tiles, i*tileX+xOffset, j*tileY+yOffset,
				i*tileX+tileX+xOffset, j*tileY+tileY+yOffset,
				sheetLocationX, sheetLocationY, sheetLocationX+tileX, sheetLocationY+tileY, null);
	}
	public static void drawTile(int tileIndex, BufferedImage tiles, Graphics g, int i, int j) {
		drawTile(tileIndex, tiles, g, i, j, 0, 0);
	}
	public static void drawTileAtLocation(int tileIndex, BufferedImage tiles, Graphics g,
			int x, int y) {
		drawTileAtLocation(tileIndex, tiles, g, x, y, Game.TILE_X, Game.TILE_Y);
	}
	public static void drawTileAtLocation(int tileIndex, BufferedImage tiles, Graphics g,
			int x, int y, int width, int height) {
		int tileX = Game.TILE_X;
		int tileY = Game.TILE_Y;
		int tileSheetWidth = tiles.getWidth()/tileX;
		int sheetLocationX = tileX*(tileIndex%tileSheetWidth);
		int sheetLocationY = tileY*(tileIndex/tileSheetWidth);
		g.drawImage(tiles, x, y, x+width, y+height,
				sheetLocationX, sheetLocationY, sheetLocationX+tileX, sheetLocationY+tileY, null);
	}
	public static int occurrencesOf(String source, String match) {
		int output = 0;
		int matchLoc = source.indexOf(match);
		while (matchLoc != -1) {
			source = source.substring(matchLoc+1);
			matchLoc = source.indexOf(match);
			output++;
		}
		return output;
	}
	public static <T> BufferedImage loadImage(String filepath) {
		try {
			File imageFile = new File(filepath);
			if (imageFile.exists()) {
				BufferedImage tileset = ImageIO.read(imageFile);
				return tileset;
			}
		} catch (IOException e) {
			ZettaUtil.error("Couldn't read from image " + filepath);
		}
		return null;
	}
	public static <T> RandomAccessFile loadResource(String filePath) {
		try {
			//java.net.URL resourceURL = filePath;
			File file = new File(filePath);
			return new RandomAccessFile(file, "r");
		} catch (IOException e) {
			ZettaUtil.error("Couldn't read from resource " + filePath);
		} /*catch (URISyntaxException e) {
			ZettaUtil.error("Path syntax error when reading from resource " + filePath);
		}*/
		return null;
	}
	public static void error(String err, Object... args) {
		System.out.printf("ERROR: " + err + "\n", args);
	}
	public static void warning(String warn, Object... args) {
		System.out.printf("WARNING: " + warn + "\n", args);
	}
	public static void log(Object log) {
		System.out.println(log.toString());
	}
	public static void init() {
		Block.loadTilesets();
		leveldata.layer.LayerID.initialize();
	}
	public static String pathComponent(String fp) {
		if (fp != null) {
			if (fp.contains("/")) {
				return fp.substring(0, fp.lastIndexOf("/"));
			}
			else if (fp.contains("\\")) {
				return fp.substring(0, fp.lastIndexOf("\\"));
			}
		}
		return null;
	}
	public static String fileComponent(String fp) {
		if (fp != null) {
			if (fp.contains("/")) {
				return fp.substring(fp.lastIndexOf("/")+1);
			}
			else if (fp.contains("\\")) {
				return fp.substring(fp.lastIndexOf("\\")+1);
			}
		}
		return null;
	}
	public static void drawTile(Tile t, Graphics g, int i, int j) {
		drawTile(t.getIndex(), Block.getTileset(t.getTileset()), g, i, j);
	}
	public static int clamp(int i, int low, int high) {
		return Math.max(low, Math.min(high, i));
	}
	public static String getExtension(File f) {
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}
}