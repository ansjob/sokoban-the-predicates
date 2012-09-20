import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class SokobanState implements Comparable<SokobanState> {
	
	public final Set<Coordinate> boxLocations;
	public final Set<Coordinate> reachableLocations;
	public final Coordinate currentPosition, pushFromPosition;
	public final int priority = 1;
	public final SokobanState parent;
	
	public SokobanState(Set<Coordinate> boxLocations,
			 Coordinate currentPosition, Coordinate pushFromPosition, SokobanState parent) {
		this.boxLocations = boxLocations;
		this.currentPosition = currentPosition;
		this.pushFromPosition = pushFromPosition;
		this.parent = parent;
		this.reachableLocations = getReachablePositions();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((boxLocations == null) ? 0 : boxLocations.hashCode());
		result = prime
				* result
				+ ((reachableLocations == null) ? 0 : reachableLocations
						.hashCode());
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
		SokobanState other = (SokobanState) obj;
		if (boxLocations == null) {
			if (other.boxLocations != null)
				return false;
		} else if (!boxLocations.equals(other.boxLocations))
			return false;
		if (reachableLocations == null) {
			if (other.reachableLocations != null)
				return false;
		} else if (!reachableLocations.equals(other.reachableLocations))
			return false;
		return true;
	}

	@Override
	public int compareTo(SokobanState o) {
		return o.priority - priority; // Higher priority first
	}

	public Set<SokobanState> getChildren() {
		Set<SokobanState> children = new HashSet<SokobanState>();
		for (Coordinate box : boxLocations) {
			for (Coordinate from : box.getNeighbours()) {
				if (reachableLocations.contains(from) && box.isPushableFrom(from, boxLocations)) {
					Coordinate playerPosition = box, newBoxPosition = box.push(from);
					Set<Coordinate> newBoxPositions = new HashSet<Coordinate>();
					newBoxPositions.addAll(boxLocations);
					newBoxPositions.remove(box);
					newBoxPositions.add(newBoxPosition);
					SokobanState child = new SokobanState(newBoxPositions, playerPosition, from, this);
					if (!child.isDead())
						children.add(child);
				}
			}
		}
		return children;
	}	
	
	private boolean isDead() {
		return false;
	}

	private Set<Coordinate> getReachablePositions() {
		Set<Coordinate> result = new HashSet<Coordinate>();
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		q.add(currentPosition);
		result.add(currentPosition);
		while (!q.isEmpty()){
			Coordinate cur = q.remove();
			for (Coordinate neighbour : cur.getNeighbours()) {
				if (SokobanBoard.cells[neighbour.row][neighbour.col] != '#' 
						&& !boxLocations.contains(neighbour)
						&& !result.contains(neighbour))
				{
					result.add(neighbour);
					q.add(neighbour);
				}
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < SokobanBoard.cells.length; row++) {
			for (int col = 0; col < SokobanBoard.cells[0].length; ++col) {
				char c = SokobanBoard.cells[row][col];
				Coordinate currentPos = new Coordinate(row, col);
				if (boxLocations.contains(currentPos)) {
					if (c == '.') {
						c = '*';
					}
					else {
						c = '$';
					}
				}
				if (currentPos.equals(this.currentPosition)) {
					if (c == '.') {
						c = '+';
					}
					else {
						c = '@';
					}
				}
				if (c != '#' && SokobanBoard.staticDead[row][col]){
					c = ',';
				}
				sb.append(c);
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
	
}
