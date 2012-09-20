import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import java.io.File;

public class ClientFile {

	public static void main(String[] pArgs) 
	{
		if(pArgs.length<1)
		{
			System.out.println("usage: java file");
			return;
		}
	
		try
		{
			File file = new File(pArgs[0]);
			BufferedReader lIn =new BufferedReader(new FileReader(file));

            String lLine=lIn.readLine();

            //read number of rows
            int lNumRows=Integer.parseInt(lLine);

            List<String> rows = new ArrayList<String>(lNumRows);
            
            //read each row
            
            Utils.DEBUG(1, "Recieved board: \n");
            for(int i=0;i<lNumRows;i++)
            {
                lLine=lIn.readLine();
                rows.add(lLine);
                Utils.DEBUG(1, "%s\n", lLine);
            }
            
            SokobanSolver solver = new SokobanSolver(new SokobanBoard(rows));
            
            String solution = solver.getSolution();
            
            Utils.DEBUG(1, "Our solution: %s\n", solution);
        }
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
