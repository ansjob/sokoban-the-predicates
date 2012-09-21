import java.util.HashSet;
import java.util.Set;


public class Coordinate {
	
	public static Coordinate[][] coordinates; 
	
	public final int row,col;
	

	public Coordinate(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + row;
		result = prime * result + col;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
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
	
	public Set<Coordinate> getNeighbours() {
		Set<Coordinate> neighbours = new HashSet<Coordinate>(10);
		neighbours.add(left());
		neighbours.add(right());
		neighbours.add(down());
		neighbours.add(up());
		
		return neighbours;
		
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

	public boolean canBePulledFrom(Coordinate neighbour) {
		Coordinate target = neighbour.push(this);
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
}
