import java.util.HashSet;
import java.util.Set;


public class SokobanState implements Comparable<SokobanState> {
	
	public final Set<Coordinate> boxLocations;
	public final Set<Coordinate> reachableLocations;
	public final Coordinate currentPosition;
	public final int priority = 1;
	public final SokobanState parent;
	
	public SokobanState(Set<Coordinate> boxLocations,
			Set<Coordinate> reachableLocations, Coordinate currentPosition, SokobanState parent) {
		this.boxLocations = boxLocations;
		this.reachableLocations = reachableLocations;
		this.currentPosition = currentPosition;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "State [boxLocations=" + boxLocations + ", reachableLocations="
				+ reachableLocations + "]";
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
		Set<Coordinate> pushFromLocations = new HashSet<Coordinate>();
		for (Coordinate box : boxLocations) {
			for (int i = 0; i < 4; ++i) {
				Coordinate tmp = null;
				switch(i) {
				case 0:
					tmp = new Coordinate(box.row + 1, box.col);
					break;
				case 1:
					tmp = new Coordinate(box.row -1 , box.col);
					break;
				case 2:
					tmp = new Coordinate(box.row, box.col +1);
					break;
				case 3:
					tmp = new Coordinate(box.row, box.col -1);
					break;
				}
				if (reachableLocations.contains(tmp) && isPushableFrom(box, tmp)) {
					pushFromLocations.add(tmp);
				}
			}
		}
		
		return children;
	}

	private boolean isPushableFrom(Coordinate box, Coordinate from) {
		return SokobanBoard.cells[box.row][row.col];
	}
	
	
}
