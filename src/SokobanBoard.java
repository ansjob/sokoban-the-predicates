import java.util.HashSet;
import java.util.List;
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
		startingState = new SokobanState(boxPositions, startingPosition, null);

	}

}
