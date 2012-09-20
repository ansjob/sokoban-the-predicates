import java.util.HashSet;
import java.util.Set;


public class Coordinate {
	
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
		neighbours.add(new Coordinate(row -1, col));
		neighbours.add(new Coordinate(row +1, col));
		neighbours.add(new Coordinate(row, col+1));
		neighbours.add(new Coordinate(row, col-1));
		
		return neighbours;
		
	}

	public char getMoveChar(Coordinate from) {
		return getMoveChar(from, false);
	}
	
	public char getMoveChar(Coordinate from, boolean push) {
		Coordinate delta = new Coordinate(row - from.row, col - from.col);
		char c;
		if (delta.col == 1) {
			c = 'R';
		}
		else if (delta.col == -1) {
			c = 'L';
		}
		else if (delta.row == 1) {
			c = 'D';
		}
		else if (delta.row == -1) {
			c = 'U';
		}
		else {
			throw new RuntimeException("Illegal delta vector: " + delta);
		}
		return (push ? c : Character.toLowerCase(c));
	}
	
	public Coordinate push(Coordinate from) {
		return new Coordinate(2 * row  - from.row, 2*col - from.col);
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
}
