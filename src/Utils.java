
public class Utils {
	
	public static final boolean DEBUGING = false; 
	
	public static void DEBUG(String format, Object... params) {
		if (DEBUGING) {
			System.out.printf(format, params);
		}
	}

}
