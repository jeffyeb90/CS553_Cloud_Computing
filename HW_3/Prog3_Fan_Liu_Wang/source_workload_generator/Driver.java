import java.io.*;

public class Driver {

	public static void main(String[] args) throws IOException
	{
		WorkloadGenerator wg = new WorkloadGenerator();
		
		wg.setDataSet(100, 100, 100, 100, 10, 1);
		
		//wg.showTotalSize();
		
		wg.generateWorkload();
	}
}
