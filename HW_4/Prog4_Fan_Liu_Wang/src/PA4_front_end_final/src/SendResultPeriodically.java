import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
		final int nAtATime = 4;
		List<Task> ts = new ArrayList<Task>(nAtATime);
		for (int i = 0; i < nAtATime; i++) {
			ts.addAll(queueHandler.readTasksFromQueue());
			//System.out.println("get tasks from sqs");
		}
		
		try {
			if(ts.isEmpty()) {
				output.writeObject(new Task("fake", "", ""));
			} else {
				for (Task t : ts){
					System.out.println("Task from SQS: " + t.getTaskID());
					output.writeObject(t);
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}	
	}
}
