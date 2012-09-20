import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class SokobanBoard {
	
	
	public static boolean[][] staticDead;
	public static char[][] cells;
	
	public final SokobanState startingState;
	
	
	
	public SokobanBoard(List<String> rows){
		Set<Coordinate> boxPositions = new HashSet<Coordinate>();
		Coordinate startingPosition = null;
		Set<Coordinate> goalPositions = new HashSet<Coordinate>();
		cells = new char[rows.size()][rows.get(0).length()];
		staticDead = new boolean[cells.length][cells[0].length];
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
					goalPositions.add(startingPosition);
					cells[rowNum][colNum] = '.';
				}
				else if (c == '$') { // Box
					boxPositions.add(new Coordinate(rowNum, colNum));
					cells[rowNum][colNum] = ' ';
				}
				else if (c == '*') { // Box on goal
					Coordinate cor = new Coordinate(rowNum, colNum);
					boxPositions.add(cor);
					cells[rowNum][colNum] = '.';
					goalPositions.add(cor);
				}
				else if (c == '.') { // Goal
					cells[rowNum][colNum] = c;
					
					goalPositions.add(new Coordinate(rowNum, colNum));
				}
				else if (c == ' ') { // Empty space
					cells[rowNum][colNum] = c;
				}
				else if (c == '#') {
					cells[rowNum][colNum] = c;
				}
				else { // Spurious 
					throw new RuntimeException("Unknown character: " + c);
				}
				colNum++;
			}
			rowNum++;
		}
		
		
		for (int i = 0; i < staticDead.length;i++){
			for(int j = 0; j < staticDead[0].length; j++){
				staticDead[i][j] = true;
			}
		}
		for (Coordinate goal : goalPositions){
			Set<Coordinate> visited = new HashSet<Coordinate>();
			Queue<Coordinate> q = new LinkedList<Coordinate>();
			q.add(goal);
			visited.add(goal);
			staticDead[goal.row][goal.col] = false;
			while (!q.isEmpty()){
				Coordinate cur = q.remove();
				for(Coordinate neighbour : cur.getNeighbours()){
					if (cells[neighbour.row][neighbour.col] != '#') {
						if (cur.canBePulledFrom(neighbour)){
							staticDead[neighbour.row][neighbour.row] = false;
						}
						if (!visited.contains(neighbour)) {
							visited.add(neighbour);
							q.add(neighbour);
						}
					}
				}
			}
		}
		
		
//		Coordinate left, right, up, down;
//		
//		for (int row = 1; row < cells.length - 1; row++){
//			for (int col = 1; col < cells[0].length - 1; col++){
//				if (cells[row][col] == ' '){
//					left = new Coordinate(row, col - 1);
//					right = new Coordinate(row, col + 1);
//					up = new Coordinate(row-1, col);
//					down = new Coordinate(row + 1, col);
//					
//					if (cells[up.row][up.col] == '#' && cells[right.row][right.col] == '#'){
//						staticDead[row][col] = true;
//						continue;
//					} else if (cells[right.row][right.col] == '#' && cells[down.row][down.col] == '#') {
//						staticDead[row][col] = true;
//						continue;
//					}else if (cells[down.row][down.col] == '#' && cells[left.row][left.col] == '#') {
//						staticDead[row][col] = true;
//						continue;
//					}else if (cells[left.row][left.col] == '#' && cells[up.row][up.col] == '#') {
//						staticDead[row][col] = true;
//						continue;
//					} else {
//						staticDead[row][col] = false;
//					}
//				}
//			}
//		}
		startingState = new SokobanState(boxPositions, startingPosition, null);

	}

}
