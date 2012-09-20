
public class Utils {
	
	public static final int DEBUG_LEVEL = 3; 
	
	public static void DEBUG(int level, String format, Object... params) {
		if (level <= DEBUG_LEVEL) {
			System.out.printf(format, params);
		}
	}

}
