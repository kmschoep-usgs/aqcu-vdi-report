package gov.usgs.aqcu.parameter;

import java.util.ArrayList;
import java.util.List;

import gov.usgs.aqcu.validation.RequiredIdentifiersPresent;

@RequiredIdentifiersPresent
public class VDiagramRequestParameters extends ReportRequestParameters {

	private String upchainTimeseriesIdentifier;
	private String ratingModelIdentifier;
	private String priorYearsHistoric;
	private List<String> excludeConditions;
	
	public VDiagramRequestParameters() {
		excludeConditions = new ArrayList<>();
	}

	public String getUpchainTimeseriesIdentifier() {
		return upchainTimeseriesIdentifier;
	}
	public void setUpchainTimeseriesIdentifier(String stageId) {
		this.upchainTimeseriesIdentifier = stageId;
	}
	public String getRatingModelIdentifier() {
		return ratingModelIdentifier;
	}
	public void setRatingModelIdentifier(String ratingModelIdentifier) {
		this.ratingModelIdentifier = ratingModelIdentifier;
	}
	public String getPriorYearsHistoric() {
		return priorYearsHistoric;
	}
	public void setPriorYearsHistoric(String priorYearsHistoric) {
		this.priorYearsHistoric = priorYearsHistoric;
	}
	public List<String> getExcludedControlConditions() {
		return excludeConditions;
	}
	
	public void setExcludedControlConditions(List<String> val) {
		this.excludeConditions = val != null ? val : new ArrayList<>();
	}

	public List<String> getTsUidList() {
		List<String> tsUidList = new ArrayList<>();
		tsUidList.add(getPrimaryTimeseriesIdentifier());
		tsUidList.add(getUpchainTimeseriesIdentifier());
		return tsUidList;
	}
}
