import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class SokobanBoard {
	
	public static char[][] cells;
	
	public final SokobanState startingState;
	
	
	
	public SokobanBoard(List<String> rows){
		Set<Coordinate> boxPositions = new HashSet<Coordinate>();
		Coordinate startingPosition = null;
		cells = new char[rows.size()][rows.get(0).length()];
		int rowNum = 0;
		for (String row : rows) {
			int colNum = 0;
			for (char c : row.toCharArray()) {
				if (c == '@') { // Player
					startingPosition = new Coordinate(rowNum, colNum);
					cells[rowNum][colNum] = ' ';
				}
				else if (c == '+') { // Player on goal
					startingPosition = new Coordinate(rowNum, colNum);
					cells[rowNum][colNum] = '.';
				}
				else if (c == '$') { // Box
					boxPositions.add(new Coordinate(rowNum, colNum));
					cells[rowNum][colNum] = ' ';
				}
				else if (c == '*') { // Box on goal
					boxPositions.add(new Coordinate(rowNum, colNum));
					cells[rowNum][colNum] = '.';
				}
				else if (c == '.') { // Goal
					cells[rowNum][colNum] = c;
				}
				else if (c == ' ') { // Empty space
					cells[rowNum][colNum] = c;
				}
				else { // Spurious 
					throw new RuntimeException("Unknown character: " + c);
				}
				colNum++;
			}
			rowNum++;
		}
		Set<Coordinate> reachablePositions = getReachablePositions(cells, startingPosition, boxPositions);
		startingState = new SokobanState(boxPositions, reachablePositions, startingPosition, null);

	}
	
	private Set<Coordinate> getReachablePositions(char[][] cells, Coordinate currentPosition, Set<Coordinate> boxPos) {
		Set<Coordinate> result = new HashSet<Coordinate>();
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		q.add(currentPosition);
		result.add(currentPosition);
		while (!q.isEmpty()){
			Coordinate cur = q.remove();
			Coordinate tmp = null;
			for (int i = 0; i < 4; i++){
				switch (i) {
				case 0:
					tmp = new Coordinate(cur.row - 1, cur.col);
					break;
				case 1:						
					tmp = new Coordinate(cur.row +1, cur.col);
					break;				
				case 2:
					tmp = new Coordinate(cur.row, cur.col - 1);
					break;
				case 3:						
					tmp = new Coordinate(cur.row, cur.col + 1);
					break;

				default:
					break;
				}
				if (cells[tmp.row][tmp.col] != '#' && !boxPos.contains(tmp)&& !result.contains(tmp)){
					result.add(tmp);
				}
			}
		}
		return result;
	}

	public char at(int row, int column){
		return cells[row][column];
	}

}
