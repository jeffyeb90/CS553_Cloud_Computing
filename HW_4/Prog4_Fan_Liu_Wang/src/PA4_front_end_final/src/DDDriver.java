import java.util.concurrent.ExecutionException;

public class DDDriver {
	
	public static void main(String[] args)
		throws ClassNotFoundException, ExecutionException, InterruptedException
	{
		
		if (args.length == 0){
			System.out.println("No argument input!");
		}
		else if (args[1].equals("-lw")){
			
			Frontend fnd = new Frontend();
			
			fnd.setPortNumber(Integer.parseInt(args[0]));
			fnd.createThreadPool(Integer.parseInt(args[2]));
			
			fnd.startServing();
			
			fnd.shutdownThreadPool();
		}
		else if(args[1].equals("-rw")){
			
			Frontend_remote fnd = new Frontend_remote();
			fnd.setPortNumber(Integer.parseInt(args[0]));
			fnd.startServing();
		}
		else{
			System.out.println("wrong arguments!");
		}
	}

}
