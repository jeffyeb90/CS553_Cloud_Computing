package iit.cs553.flw.prog4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Worker {
	private static class NoTaskException extends Throwable {
		@Override
		public Throwable fillInStackTrace() { return null; }
	}
	
	public Worker(int n) {
		queueHandler = new SQS_Handler(); // use default queues; see SQS_Handler
		dbHandler = new DB_Handler();
		executor = Executors.newFixedThreadPool(n);
	}
	
	public void createThreadPool(int n) {
		executor = Executors.newFixedThreadPool(n);
	}

	public void shutdownThreadPool() {
		executor.shutdown();
		while (!executor.isTerminated())
			;
		System.out.println("Finished all threads");
	}
	
	// retrieve tasks from SQS, and verify using DynamoDB;
	// return only new tasks.
	private List<Task> getTasks()
		throws InterruptedException, NoTaskException
	{
		// in best case, retrieve tasks nAtATime times
		final int nAtATime = 1;
		List<Task> ts = new ArrayList<Task>(nAtATime);
		for (int i = 0; i < nAtATime; i++) {
			ts.addAll(queueHandler.readTasksFromQueue());
			//System.out.println("get tasks from sqs");
		}
		
		// in case ts is empty,
		int ntries = 40000;
		while (ts.size() == 0) {
			if (ntries == 0) {
				System.out.println("no task to do, self-terminate.");
				throw new NoTaskException();
			}
			Thread.sleep(500); // TODO wait a minute 60,000,
			ntries--;
			ts = queueHandler.readTasksFromQueue(); // and only retrieve 1 time
		}
		
		// remove invalid tasks, by using DynamoDB
		Task tmp;
		for (Iterator<Task> it = ts.iterator(); it.hasNext();) {
			tmp = it.next();
			if (!dbHandler.SucceedPutItem(tmp)) {
				System.out.println("duplicated task, discard it");
				it.remove();
			}
		} // ts is all the new tasks
		return ts;
	}
	
	public void startWorkingSingleThread()
		throws InterruptedException
	{
		List<Task> tasks;
		Task ret;
		while (true) {
			try { tasks = getTasks(); } catch (NoTaskException e) {
				return;
			}// there are tasks
			for (final Task t : tasks) {
				ret = (new LocalWorker(t)).call(); System.out.println(ret);
				queueHandler.sendTaskInQueue(ret);
			}
		}
	}

	public void startWorking()
		throws InterruptedException, ExecutionException
	{
		CompletionService<Task>
			completion = new ExecutorCompletionService<Task>(executor);
		List<Task> tasks;
		
		while (true) {
			try { tasks = getTasks(); } catch (NoTaskException e) {
				// no task for a while, self-terminate
				executor.shutdown();
				return;
			}// there are tasks
			
			for (final Task t : tasks) {
				completion.submit(new LocalWorker(t));
			}
			Task t;
			for (int i = 0; i < tasks.size(); i++) {
				t = completion.take().get(); System.out.println("return " + t);
				queueHandler.sendTaskInQueue(t);
			}
		}
	}
	
	public static void main(String[] args)
		throws IOException, InterruptedException, ExecutionException
	{long begin = Calendar.getInstance().getTimeInMillis();
		Worker w = new Worker(Integer.valueOf(args[0]));
		w.startWorkingSingleThread();
		
		System.out.println("worker is shutting down;"
				+ " all work retrieved has been handled.");
		//(new ProcessBuilder("shutdown", "-h", "now")).start();
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("time: " + (end - begin));
	}
	
	private SQS_Handler queueHandler;
	private DB_Handler dbHandler;
	private ExecutorService executor;
}
