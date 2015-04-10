package iit.cs553.flw.prog4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class LocalWorker implements Callable<Task> {
	
	private Task myTask;
	
	public LocalWorker(Task t){
		myTask = t;
		//System.out.println("task id received by local worker " + t.getTaskID());
	}
	
	public void executeTask() throws IOException{
		//sleep is a bash command, so the command string needs to be parsed
		//if its a linux command, such as 'ls', do not need parse
		Process process = new ProcessBuilder("/bin/bash", "-c", myTask.getTaskDesc()).start();
		
		//Following is the method to get the process's output, but there is no output;
		//However, if not executing the following, the bash command will not be executed.
		BufferedReader br = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		
		String line;
		while((line = br.readLine())!= null){
			System.out.println(line);
		}
		br.close();
	}
	
	@Override
	public Task call(){
		//System.out.println(Thread.currentThread().getName()+ " execute task " + myTask.getTaskID());
		try {
			executeTask();
			//System.out.println("I slept well");
			return myTask.withResult("succeed");
		} catch (IOException e) {
			//System.out.println("I didn't sleep well");
			return myTask.withResult("failed");
		}
        //System.out.println(Thread.currentThread().getName()+" task " + myTask.TaskID + " finished\n");
	}
}
