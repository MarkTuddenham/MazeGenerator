package maze.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import maze.Maze;
import maze.Maze.MazeGenerateType;
import maze.MazeException;

public class MazePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Maze m;
	private int lineSize = 2;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setTitle("Maze");
				f.getContentPane().add(new MazePanel(20, 20, new int[] { 0, 0 }, new int[] { 1, 1 },
						MazeGenerateType.COMPLETE));

				f.pack();
				f.setVisible(true);

			}
		});
	}

	public MazePanel(int width, int height, int mazeWidth, int mazeHeight, int[] start, int[] end,
			MazeGenerateType type) {
		setPreferredSize(new Dimension(width, height));
		m = new Maze(mazeWidth, mazeHeight);

		try {
			if (start != null && end != null) {
				m.setStartAndEnd(start, end);
			}

			m.generate(type);
		} catch (MazeException e) {
			e.printStackTrace();
		}

	}

	public MazePanel(int mazeWidth, int mazeHeight, int[] start, int[] end, MazeGenerateType type) {
		this(500, 500, mazeWidth, mazeHeight, start, end, type);
	}

	public MazePanel(int width, int height, int mazeWidth, int mazeHeight, MazeGenerateType type) {
		this(width, height, mazeWidth, mazeHeight, null, null, type);
	}

	public MazePanel(int mazeWidth, int mazeHeight, MazeGenerateType type) {
		this(500, 500, mazeWidth, mazeHeight, null, null, type);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(lineSize));
		g2d.setColor(Color.BLUE);

		int xMax = m.getSize().getMazeWidth();
		int yMax = m.getSize().getMazeHeight();
		int roomSize = Math.min(getWidth(), getHeight()) / Math.max(xMax, yMax) - lineSize;

		// draw borders
		int width = (roomSize + lineSize) * xMax;
		int height = (roomSize + lineSize) * yMax;
		int lineAdjust = lineSize / 2;

		g2d.drawLine(0, lineAdjust, width, lineAdjust);
		g2d.drawLine(lineAdjust, 0, lineAdjust, height);
		g2d.drawLine(width, 0, width, height);
		g2d.drawLine(0, height, width, height);

		// draw inside lines
		int yPos, xPos;
		for (int y = 0; y < yMax; ++y) {
			for (int x = 0; x < xMax; ++x) {
				int roomNumber = y * xMax + x;

				if (x + 1 < xMax && !m.isRoomConnected(roomNumber, roomNumber + 1)) {
					yPos = (lineSize + roomSize) * y;
					xPos = (lineSize + roomSize) * (x + 1);
					g2d.drawLine(xPos, yPos, xPos, yPos + roomSize + lineSize / 2);
				}

				if (y + 1 < yMax && !m.isRoomConnected(roomNumber, roomNumber + xMax)) {
					yPos = (lineSize + roomSize) * (y + 1);
					xPos = (lineSize + roomSize) * x;
					g2d.drawLine(xPos, yPos, xPos + roomSize + lineSize / 2, yPos);
				}
			}
		}

	}
}
