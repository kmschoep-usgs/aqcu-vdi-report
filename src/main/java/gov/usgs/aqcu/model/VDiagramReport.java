package gov.usgs.aqcu.model;

import java.util.List;

public class VDiagramReport {	
	private VDiagramReportMetadata reportMetadata;
	private List<VDiagramRatingShift> ratingShifts;
	private List<MinMaxPoint> maximumStageHeight;
	private List<MinMaxPoint> minimumStageHeight;
	
	public VDiagramReportMetadata getReportMetadata() {
		return reportMetadata;
	}
	
	public void setReportMetadata(VDiagramReportMetadata val) {
		reportMetadata = val;
	}

	public List<VDiagramRatingShift> getRatingShifts() {
		return ratingShifts;
	}

	public void setRatingShifts(List<VDiagramRatingShift> ratingShifts) {
		this.ratingShifts = ratingShifts;
	}

	public List<MinMaxPoint> getMaximumStageHeight() {
		return maximumStageHeight;
	}

	public void setMaximumStageHeight(List<MinMaxPoint> maximumStageHeight) {
		this.maximumStageHeight = maximumStageHeight;
	}

	public List<MinMaxPoint> getMinimumStageHeight() {
		return minimumStageHeight;
	}

	public void setMinimumStageHeight(List<MinMaxPoint> minimumStageHeight) {
		this.minimumStageHeight = minimumStageHeight;
	}

	
}
	
