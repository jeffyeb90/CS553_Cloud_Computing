import java.io.*;
import java.util.*;

public class WorkloadGenerator {
	
	public static class FileInfo
	{
		private String fileType;
		private int numbers;
		
		public FileInfo(String type, int num)
		{
			fileType = type;
			numbers = num;
		}
		
	}
	
	private static List<FileInfo> fileArray = new ArrayList<FileInfo>();

	private static String nameSample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static Random rng = new Random();
	
	static FileGenerator fg = new FileGenerator();

	
	public static String generateFileName()
	{
		char str[] = new char[10];

		for(int i = 0; i <= 9; i++)
		{
			str[i] = nameSample.charAt(rng.nextInt(nameSample.length()));
		}
		
		return new String(str);
	}
	
	

	public void setDataSet(int n1, int n2, int n3, int n4, int n5, int n6)
	{
		fileArray.add(new FileInfo("1kb", n1));
		fileArray.add(new FileInfo("10kb", n2));
		fileArray.add(new FileInfo("100kb", n3));
		fileArray.add(new FileInfo("1mb", n4));
		fileArray.add(new FileInfo("10mb", n5));
		fileArray.add(new FileInfo("100mb", n6));
	}
	
	public void generateWorkload() throws IOException
	{
		int numOfBytes;
		
		Iterator<FileInfo> iter = fileArray.iterator();
		Iterator<FileInfo> iter2 = fileArray.iterator();
		Iterator<FileInfo> iter3 = fileArray.iterator();
		Iterator<FileInfo> iter4 = fileArray.iterator();
				
		while(iter.hasNext())
		{
			switch(iter.next().fileType)
			{
			case "1kb": numOfBytes = 1024;
						break;
			case "10kb": numOfBytes = 10240;
						break;
			case "100kb": numOfBytes = 102400;
						break;
			case "1mb":	numOfBytes = 1024*1024;
						break;
			case "10mb": numOfBytes = 1024*1024*10;
						break;
			default: numOfBytes = 1024*1024*100;
						break;
			}
			
			//System.out.println(numOfBytes);
			System.out.println("This iteration is of type" + iter2.next().fileType);
			System.out.println("This iteration has " + iter3.next().numbers + " files to generate");
			System.out.println("Each file is of size " + numOfBytes);
			
			
			int j = iter4.next().numbers;
						
			for (int i = 0; i < j; i++)
			{
				fg.generateFile(generateFileName(), numOfBytes);
			}
			
			
		}
	}
	
}
