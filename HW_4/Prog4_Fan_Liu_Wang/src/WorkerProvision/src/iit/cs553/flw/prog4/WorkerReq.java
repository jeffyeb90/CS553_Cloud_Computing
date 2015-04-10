package iit.cs553.flw.prog4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;

public class WorkerReq {
	private boolean isActive(String id) {
		SpotInstanceRequest
			r = ec2.describeSpotInstanceRequests(
					(new DescribeSpotInstanceRequestsRequest()
					).withSpotInstanceRequestIds(id)
				).getSpotInstanceRequests().get(0);
		
		System.out.println(r.getSpotInstanceRequestId()
				+ " is " + r.getState());
		
		return r.getState().equals("active");
	}
	
	public void run() {
		List<SpotInstanceRequest> reqs =
				ec2.requestSpotInstances(reqreq).getSpotInstanceRequests();
		System.out.println("req list: " + reqs);
        String id = reqs.get(0).getSpotInstanceRequestId();
        System.out.println("Created Spot request: " + id);
        
        do {
        	try { Thread.sleep(60000); } catch (InterruptedException e) {
				System.out.println("Request of spot instance is interrupted.");
				return;
			}
        } while (!isActive(id));
        // isActive(id) == true
	}
	
	public static final AmazonEC2Client
		ec2 = (new AmazonEC2Client(AWSCreds.WANG))
			.withRegion(Regions.US_WEST_2);
	private static final LaunchSpecification
		launchSpec = (new LaunchSpecification())
			.withImageId("ami-5d52056d").withInstanceType("m3.medium")
			.withSecurityGroups("launch-wizard-5");

	private RequestSpotInstancesRequest
		reqreq = (new RequestSpotInstancesRequest())
			.withSpotPrice("0.4").withInstanceCount(1)
			.withLaunchSpecification(launchSpec);
}
