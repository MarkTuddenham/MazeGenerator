package maze;

import java.util.ArrayList;
import java.util.Random;

import maze.maths.DisjointSets;
import maze.maths.DisjointSetsException;

public class Maze {
	private DisjointSets ds;
	private MazeSize size;
	private Room start, end;
	
	private ArrayList<int[]> directlyConnectedRooms;

	public enum MazeGenerateType {
		SOLVABLE, COMPLETE
	}

	public Maze(int width, int height) {
		size = new MazeSize();
		size.setMazeSize(width, height);
		ds = new DisjointSets(size.getRoomCount());
		directlyConnectedRooms = new ArrayList<int[]>();
	}

	public void setStartAndEnd(int[] start, int[] end) throws MazeException {
		if(start == null || end == null){
			throw new MazeException("Either start or end was null.");
		}
		if (start.length != 2 || end.length != 2) {
			throw new MazeException("Start and End must be of length 2");
		}

		this.start = new Room(start[0], start[1]);
		this.end = new Room(end[0], end[1]);

	}

	public void generate(MazeGenerateType type) throws MazeException {
		directlyConnectedRooms.clear();
		
		int room1, room2, root1, root2;
		int roomCount = size.getRoomCount();
		Random r = new Random();

		while (!quitGeneration(type)) {
			/* 
			 * TODO improve so not completely random near the end
			 * in DisjointSet count the number of negatives in the array --> how many separate sections
			 * choose 2 sections
			 * ?use recursion? to build a list of their rooms and then find two neighbouring ones (1 in each)
			 * if not then pick 2 other sections
			 */
			
			
			do {
				room1 = r.nextInt(roomCount);
				room2 = getRandomNeighbour(room1);

				root1 = ds.find(room1);
				root2 = ds.find(room2);
			} while (root1 == root2);
//			System.out.println(room1+"("+root1+")-"+room2+"("+root2+")");
			
			try {
				ds.union(root1, root2);
				directlyConnectedRooms.add(new int[]{room1,room2});
			} catch (DisjointSetsException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean quitGeneration(MazeGenerateType type) throws MazeException {
		switch (type) {
		case SOLVABLE:
			if (start == null || end == null) {
				throw new MazeException("Start and End rooms are undefined.");
			}

			if (ds.find(start.getRoomNumber()) == ds.find(end.getRoomNumber())) {
				return true;
			} else {
				return false;
			}
		case COMPLETE:
			if (ds.getSize() <= 1) {
				return true;
			}

			// all the rooms connected?
			for (int i = 1; i < ds.getSize(); ++i) {
				if (ds.find(0) != ds.find(i)) {
					return false;
				}
			}
			return true;
		default:
			System.err.println("Unknown Maze Generation Type " + type);
			return true;
		}

	}

	public int getRandomNeighbour(int room1) throws MazeException {

		/*
		 * 0 = up 1 = down 2 = left 3 = right
		 */

		boolean[] directions = new boolean[4];
		int c = 0;

		if (room1 >= size.getMazeWidth()) {
			directions[0] = true;
			c++;
		} else {
			directions[0] = false;
		}

		if (room1 < size.getRoomCount() - size.getMazeWidth()) {
			directions[1] = true;
			c++;
		} else {
			directions[1] = false;
		}

		if (room1 % size.getMazeWidth() > 0) {
			directions[2] = true;
			c++;
		} else {
			directions[2] = false;
		}

		if (room1 % size.getMazeWidth() < size.getMazeWidth() - 1) {
			directions[3] = true;
			c++;
		} else {
			directions[3] = false;
		}

		Random r = new Random();
		int index = r.nextInt(c) + 1;

		int direction = -1;
		int count = 0;
		for (int i = 0; i < directions.length; i++) {
			if (directions[i]) {
				count++;
				if (count == index) {
					direction = i;
				}
			}
		}

		int room2;

		switch (direction) {
		// direction var not set
		case -1:
			throw new MazeException("Unable to find a neighbour");
			// up
		case 0:
			room2 = room1 - size.getMazeWidth();
			break;
		// down
		case 1:
			room2 = room1 + size.getMazeWidth();
			break;
		// left
		case 2:
			room2 = room1 - 1;
			break;
		// right
		case 3:
			room2 = room1 + 1;
			break;
		default:
			throw new MazeException("Something went wrong with the direction generator!");
		}

		return room2;
	}

	public class Room {
		private int[] pos;

		public Room(int x, int y) throws MazeException {
			if ((x >= size.getMazeWidth() || x < 0) || (y >= size.getMazeHeight() || y < 0)) {
				throw new MazeException("Room is out of bounds");
			} else {
				this.pos = new int[] { x, y };
			}
		}

		public int getRoomNumber() {
			return getY() * size.getMazeWidth() + getX();
		}

		public int getX() {
			return pos[0];
		}

		public int getY() {
			return pos[1];
		}

	}

	public class MazeSize {
		private int x, y;

		public void setMazeSize(int width, int height) {
			this.x = width;
			this.y = height;
		}

		public int getRoomCount() {
			return x * y;
		}

		public int getMazeWidth() {
			return x;
		}

		public int getMazeHeight() {
			return y;
		}
	}

	public MazeSize getSize() {
		return size;
	}

	public boolean isRoomConnected(int room1, int room2) {
		for(int[] r: directlyConnectedRooms){
			if((r[0]==room1 && r[1] == room2) || (r[0]==room2 && r[1] == room1)){
				return true;
			}
		}
		return false;
	}
}
