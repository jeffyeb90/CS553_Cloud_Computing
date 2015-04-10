package iit.cs553.flw.prog4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;

public class WorkerProvision {
	private static final int deltaT = 60000;
	private static final int workerStartupTime = 150000; // 2min30sec; TODO dynamic
	
	private static void startAWorker() {
		WorkerReq req = new WorkerReq();
		System.out.println("WorkerReq initialized");
		req.run();
		System.out.println("WorkerReq finished running");
	}
	
	private static void staticProvision(int nworkers) {
		for (int i = 0; i < nworkers; i++) {
			startAWorker();
		}
		System.out.println(nworkers + " workers are up and waiting.");
	}
	
	private static void dynamicProvision() throws InterruptedException {
		long time_prev = 0;
		long time = Calendar.getInstance().getTimeInMillis();
		int qlen_prev = 0;
		int qlen = sqs.approxTaskQLen();
		double mu;
		while (qlen > 0) {
			if (qlen > qlen_prev) {
				System.out.println("tasks: +" + (qlen - qlen_prev));
				startAWorker();
			} else { // qlen_prev >= qlen
				System.out.println("tasks: -" + (qlen_prev - qlen));
				mu = (double) (qlen_prev - qlen) / (time - time_prev);
				System.out.println("mu: " + mu + " tasks/sec");
				if (qlen / mu > workerStartupTime) {
					// in case where there is enough time to start up a new worker
					// before tasks are handled in current mu, start up a new worker.
					startAWorker();
				}
			}
			
			Thread.sleep(deltaT);
			
			qlen_prev = qlen;
			qlen = sqs.approxTaskQLen();
			time_prev = time;
			time = Calendar.getInstance().getTimeInMillis();
		} // qlen == 0
		
		// idle: keep checking task queue length
		while (qlen == 0) {
			Thread.sleep(500);
			qlen = sqs.approxTaskQLen();
		} // qlen > 0
		
		dynamicProvision();
	}
	
	/*
	 * provision static #workers
	 * provision dynamic
	 */
	public static void main(String[] args) throws InterruptedException {
		switch (args[0]) {
		case "static":
			staticProvision(Integer.valueOf(args[1]));
			break;
		case "dynamic":
			dynamicProvision();
			break;
		default:
			System.out.println("Use \"provision static #workers\" or \"provision dynamic\".");
			break;
		}
	}
	
	private static SQS_Handler sqs = new SQS_Handler();
}
