import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SokobanState implements Comparable<SokobanState> {

	private static final int REACHED_GOALS_WEIGHT = 50;
	private static final int MOBILITY_WEIGHT = 20;
	public final Set<Coordinate> boxLocations;
	public final Set<Coordinate> pushOrPullFromPositions;
	public final Coordinate currentPosition, pushOrPullFromPosition;
	public final int priority;
	public final SokobanState parent;
	public final boolean isReverse;
	private final int hashCode;

	public SokobanState(Set<Coordinate> boxLocations,
			Coordinate currentPosition, Coordinate pushFromPosition,
			SokobanState parent, boolean isReverse) {
		this.boxLocations = boxLocations;
		this.currentPosition = currentPosition;
		this.pushOrPullFromPosition = pushFromPosition;
		this.isReverse = isReverse;
		this.pushOrPullFromPositions = calculatePushOrPullFromPositions();
		this.parent = parent;
		this.priority = evaluate();
		this.hashCode = computeHashCode();
	}

	private Set<Coordinate> calculatePushOrPullFromPositions() {
		Set<Coordinate> reachablePositions = getReachablePositions();
		Set<Coordinate> pushOrPullFromPositions = new HashSet<Coordinate>();
		
		for (Coordinate box : boxLocations) {
			for (Coordinate neighbour : box.getNeighbours()) {
				if (reachablePositions.contains(neighbour)) {
					pushOrPullFromPositions.add(neighbour);
				}
			}
		}
		return pushOrPullFromPositions;
	}

	public int computeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((boxLocations == null) ? 0 : boxLocations.hashCode());
		result = prime
				* result
				+ ((pushOrPullFromPositions == null) ? 0 : pushOrPullFromPositions
						.hashCode());
		return result;
	}

	@Override
	public int hashCode() {
		return hashCode;
	};

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SokobanState other = (SokobanState) obj;
		if (hashCode != other.hashCode)
			return false;
		if (!boxLocations.equals(other.boxLocations))
			return false;
		if (!pushOrPullFromPositions.equals(other.pushOrPullFromPositions))
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
				if ((!isReverse && pushOrPullFromPositions.contains(from) && box
						.isPushableFrom(from, boxLocations))
						|| (isReverse && pushOrPullFromPositions.contains(from) && box
								.canBePulledFrom(from, boxLocations))) {
					Coordinate playerPosition = (isReverse ? from.push(box)
							: box), newBoxPosition = (isReverse ? from : box
							.push(from));
					Set<Coordinate> newBoxPositions = new HashSet<Coordinate>();
					newBoxPositions.addAll(boxLocations);
					newBoxPositions.remove(box);
					newBoxPositions.add(newBoxPosition);
					SokobanState child = new SokobanState(newBoxPositions,
							playerPosition, from, this, isReverse);
					if (!child.isDead())
						children.add(child);
				}
			}
		}
		return children;
	}

	private int evaluate() {
		int val = 0;
		val += REACHED_GOALS_WEIGHT * goalsReached();


		if (isReverse) {
			return val;
		}

		val += MOBILITY_WEIGHT * mobilityValue();

		return val;
	}

	private int mobilityValue() {
		int val = 0;
		int highestPossible = boxLocations.size() * 4;
		for (Coordinate box : boxLocations) {
			for (Coordinate from : box.getNeighbours()) {
				if (pushOrPullFromPositions.contains(from)
						&& box.isPushableFrom(from, boxLocations)) {
					val++;
				}
			}
		}
		return (int) (100 * (float) val / (float) highestPossible);
	}

	private int goalsReached() {
		int doneBoxes = 0;
		for (Coordinate goal : (isReverse ? SokobanBoard.startBoxPositions
				: SokobanBoard.goalPositions)) {
			if (boxLocations.contains(goal))
				doneBoxes ++;
		}
		return (int) (100 * ((float) doneBoxes / (float) SokobanBoard.startBoxPositions.size()));
	}


	private boolean isDead() {
		return false;
	}

	private Set<Coordinate> getReachablePositions() {
		Set<Coordinate> result = new HashSet<Coordinate>();
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		q.add(currentPosition);
		result.add(currentPosition);
		while (!q.isEmpty()) {
			Coordinate cur = q.remove();
			for (Coordinate neighbour : cur.getNeighbours()) {
				if (SokobanBoard.cells[neighbour.row][neighbour.col] != '#'
						&& !boxLocations.contains(neighbour)
						&& !result.contains(neighbour)) {
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
				Coordinate currentPos = Coordinate.get(row, col);
				if (boxLocations.contains(currentPos)) {
					if (c == '.') {
						c = '*';
					} else {
						c = '$';
					}
				}
				if (currentPos.equals(this.currentPosition)) {
					if (c == '.') {
						c = '+';
					} else {
						c = '@';
					}
				}
				// if (c != '#' && SokobanBoard.staticDead[row][col]){
				// c = ',';
				// }
				sb.append(c);
			}
			sb.append('\n');
		}

		return sb.toString();
	}

}
