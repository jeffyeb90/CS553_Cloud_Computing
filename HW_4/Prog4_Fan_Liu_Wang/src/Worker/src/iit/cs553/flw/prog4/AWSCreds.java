package iit.cs553.flw.prog4;

import com.amazonaws.auth.AWSCredentials;

public enum AWSCreds implements AWSCredentials {
	LIU("AKIAIVYUUZTRDTFH6KCQ", "x6/mAB84aGSsejsP0+HXrILx0w0aYGxrNaxnzw3N"),
	WANG("AKIAJ2PNQPJZKOB33GSA", "HZdVzzNpXcn/UkUNywEl3vxxP/bwYS9kQe2rX9aI");

	private AWSCreds(String access, String secret) {
		accessKey = access;
		secretKey = secret;
	}
	
	@Override
	public String getAWSAccessKeyId() { return accessKey; }

	@Override
	public String getAWSSecretKey() { return secretKey; }

	private String accessKey;
	private String secretKey;
}
