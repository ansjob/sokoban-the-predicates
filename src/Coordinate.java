import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Coordinate {
	
	public static Coordinate[][] coordinates; 
	
	public final int row,col;
	public Coordinate[] neighbours;
	

	public Coordinate(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public int hashCode() {
		return (row << 16) + col;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Coordinate other = (Coordinate) obj;
		if (row != other.row)
			return false;
		if (col != other.col)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Coordinate [row=" + row + ", col=" + col + "]";
	}
	
	private void calculateNeighbours() {
		ArrayList<Coordinate> neighbours = new ArrayList<Coordinate>(4);
		
		if (row != 0)
			neighbours.add(up());
		if (row != SokobanBoard.cells.length -1)
			neighbours.add(down());
		if (col != 0)
			neighbours.add(left());
		if (col != SokobanBoard.cells[0].length -1)
			neighbours.add(right());
		
		this.neighbours = neighbours.toArray(new Coordinate[neighbours.size()]);
	}

	public char getMoveChar(Coordinate from) {
		return getMoveChar(from, false);
	}
	
	public char getMoveChar(Coordinate from, boolean push) {
		int colDelta = col - from.col;
		int rowDelta = row - from.row;
		char c;
		if (colDelta == 1) {
			c = 'R';
		}
		else if (colDelta == -1) {
			c = 'L';
		}
		else if (rowDelta == 1) {
			c = 'D';
		}
		else if (rowDelta == -1) {
			c = 'U';
		}
		else {
			throw new RuntimeException("Illegal delta vector: (" + rowDelta + "," + colDelta + ")");
		}
		return (push ? c : Character.toLowerCase(c));
	}
	
	public Coordinate push(Coordinate from) {
		return get(2 * row  - from.row, 2*col - from.col);
	}
	
	public Coordinate pull(Coordinate from){
		return from.push(this);
	}

	public boolean canBePulledFromNoBoxes(Coordinate neighbour) {
		Coordinate target = neighbour.push(this);
		return SokobanBoard.cells[target.row][target.col] != '#'; 
	}
	
	public boolean canBePulledFrom(Coordinate neighbour, Set<Coordinate> boxPositions) {
		Coordinate target = neighbour.push(this);
		if (boxPositions.contains(target)) return false;
		return SokobanBoard.cells[target.row][target.col] != '#'; 
	}
	
	public boolean isPushableFrom(Coordinate from, Set<Coordinate> curBoxPositions) {
		Coordinate target = this.push(from);
		if (curBoxPositions.contains(target)) return false;
		return SokobanBoard.cells[target.row][target.col] != '#' && !SokobanBoard.staticDead[target.row][target.col];
	}
	
	public static Coordinate get(int row, int col){
		return coordinates[row][col];
	}
	
	public Coordinate left(){
		return coordinates[this.row][this.col-1];
	}
	public Coordinate down(){
		return coordinates[this.row+1][this.col];
	}
	public Coordinate right(){
		return coordinates[this.row][this.col+1];
	}
	public Coordinate up(){
		return coordinates[this.row-1][this.col];
	}

	public static void initNeighbours() {
		for (Coordinate[] row : coordinates) {
			for (Coordinate coord : row) {
				coord.calculateNeighbours();
			}
		}
	}
}
