package gov.usgs.aqcu.model;

import java.time.ZoneOffset;

import gov.usgs.aqcu.parameter.VDiagramRequestParameters;

public class VDiagramReportMetadata extends ReportMetadata {
	private VDiagramRequestParameters requestParameters;
	private String primaryTimeseriesIdentifier;
	private String upchainTimeseriesIdentifier;
	private String ratingModelIdentifier;
	private String requestingUser;

	public String getRequestingUser() {
		return requestingUser;
	}
	
	public VDiagramRequestParameters getRequestParameters() {
		return requestParameters;
	}

	public void setRequestingUser(String val) {
		this.requestingUser = val;
	}
	
	public void setPrimaryTimeseriesIdentifier(String dischargeId) {
		primaryTimeseriesIdentifier = dischargeId;
	}
	
	public void setUpchainTimeseriesIdentifier(String stageId) {
		this.upchainTimeseriesIdentifier = stageId;
	}
	
	public void setRequestParameters(VDiagramRequestParameters val) {
		requestParameters = val;
		//Report Period displayed should be exactly as received, so get as UTC
		setStartDate(val.getStartInstant(ZoneOffset.UTC));
		setEndDate(val.getEndInstant(ZoneOffset.UTC));
	}

}