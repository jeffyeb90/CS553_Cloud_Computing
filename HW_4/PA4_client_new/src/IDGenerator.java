import java.util.Random;

public class IDGenerator {
	
	private static String nameSample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static Random rng = new Random();

	public String generateID()
	{
		char str[] = new char[10];

		for(int i = 0; i <= 9; i++)
		{
			str[i] = nameSample.charAt(rng.nextInt(nameSample.length()));
		}
		
		return new String(str);
	}
}
