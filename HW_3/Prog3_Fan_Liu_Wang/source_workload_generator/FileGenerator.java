import java.util.*;
import java.io.*;

public class FileGenerator {
	
	private static String charSample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+=-[]{}|;:,./`~\'\"";
	private static Random rng = new Random();
	
	
	public String generateString()
	{
		char str[] = new char[100];

		for(int i = 0; i <= 98; i++)
		{
			str[i] = charSample.charAt(rng.nextInt(charSample.length()));
		}
		str[99] = '\n';
		return new String(str);
	}
	
	public String generateLastLine(int bytes)
	{
		char str[] = new char[bytes];
		
		for(int i = 0; i <= bytes-2; i++)
		{
			str[i] = charSample.charAt(rng.nextInt(charSample.length()));
		}
		str[bytes-1] = '\n';
		return new String(str);
	}

	public void generateFile(String fileName, int fileSize) throws IOException
	{
		PrintWriter pw = new PrintWriter(fileName);
		
		int counter = 0;
		
		while(counter < fileSize / 100)	
		{
			pw.print(generateString());
			counter++;
		}
		
		if(fileSize % 100 != 0)
			pw.print(generateLastLine(fileSize % 100));
		
		pw.close();
	}
	
}
