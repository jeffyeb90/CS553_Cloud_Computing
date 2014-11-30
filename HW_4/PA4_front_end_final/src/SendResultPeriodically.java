import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;


public class SendResultPeriodically implements Runnable{
	
	private ObjectOutputStream output;
	
	private SQS_Handler queueHandler;
	
	public SendResultPeriodically(ObjectOutputStream o, SQS_Handler han){
		
		output = o;
		queueHandler = han;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		

		List<Task> new_list = queueHandler.readTasksFromQueue();
		
		if(new_list.isEmpty()){
			;
		}
		else{
			
			for (Task t : new_list){	
				System.out.println("Receive task id " + t.getTaskID());
				try {
					output.writeObject(t);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
}
