package gov.usgs.aqcu.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingCurve;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingShift;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeRange;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.parameter.VDiagramRequestParameters;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.calc.FieldVisitSetDateRange;
import gov.usgs.aqcu.calc.MinMaxFinder;
import gov.usgs.aqcu.calc.ShiftNumberCalculator;
import gov.usgs.aqcu.model.*;
import gov.usgs.aqcu.retrieval.*;

@Service
public class ReportBuilderService {
	public static final String REPORT_TITLE = "V-Diagram";
	public static final String REPORT_TYPE = "vdiagram";
	
	private LocationDescriptionListService locationDescriptionListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private TimeSeriesDataService timeSeriesDataService;
	private FieldVisitDescriptionService fieldVisitDescriptionService;
	private FieldVisitDataService fieldVisitDataService;
	private FieldVisitMeasurementsBuilderService fieldVisitMeasurementsBuilderService;
	private RatingCurveListService ratingCurveListService;

	@Autowired
	public ReportBuilderService(
		LocationDescriptionListService locationDescriptionListService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		TimeSeriesDataService timeSeriesDataService,
		FieldVisitDescriptionService  fieldVisitDescriptionService,
		FieldVisitDataService fieldVisitDataService,
		FieldVisitMeasurementsBuilderService fieldVisitMeasurementsBuilderService,
		RatingCurveListService ratingCurveListService,
		QualifierLookupService qualifierLookupService) {
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.timeSeriesDataService = timeSeriesDataService;
		this.fieldVisitDescriptionService = fieldVisitDescriptionService;
		this.fieldVisitDataService = fieldVisitDataService;
		this.fieldVisitMeasurementsBuilderService = fieldVisitMeasurementsBuilderService;
		this.ratingCurveListService = ratingCurveListService;
	}

	public VDiagramReport buildReport(VDiagramRequestParameters requestParameters, String requestingUser) {
		VDiagramReport report = new VDiagramReport();
		MinMaxFinder minMaxFinder = new MinMaxFinder();
		FieldVisitSetDateRange fieldVisitDateRange = new FieldVisitSetDateRange();		
		// Time Series Metadata
		Map<String, TimeSeriesDescription> timeSeriesDescriptions = timeSeriesDescriptionListService.getTimeSeriesDescriptionList(requestParameters.getTsUidList())
		.stream().collect(Collectors.toMap(t -> t.getUniqueId(), t -> t));
		
		//Primary TS Metadata
		TimeSeriesDescription primaryDescription = timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		String primaryStationId = primaryDescription.getLocationIdentifier();
		TimeRange range = new TimeRange().setStartTime(requestParameters.getStartInstant(primaryZoneOffset)).setEndTime(requestParameters.getEndInstant(primaryZoneOffset));
		
		//Stage TS Metadata
		TimeSeriesDescription stageDescription = timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier());
		ZoneOffset stageZoneOffset = TimeSeriesUtils.getZoneOffset(stageDescription);
		
		//Time Series Corrected Data for Stage
		TimeSeriesDataServiceResponse stageTimeSeriesCorrectedData = timeSeriesDataService.get(
			requestParameters.getUpchainTimeseriesIdentifier(), 
			requestParameters,
			stageZoneOffset,
			false,
			false,
			false,
			"PointsOnly"
		);
		
		// Min/Max Stage Heights
		MinMaxData minMaxStageHeights = minMaxFinder.getMinMaxData(stageTimeSeriesCorrectedData.getPoints());
		
		// Rating Shifts
		List<RatingCurve> ratingCurves = getRatingCurves(requestParameters, primaryZoneOffset);
		List<RatingShift> ratingShiftList = getRatingShifts(requestParameters, primaryZoneOffset, ratingCurves);
		List<VDiagramRatingShift> ratingShifts = buildRatingShifts(ratingShiftList, ratingCurves, range);
		
		//Field Visits
		// if Years of Historic Measurements is specified in the request parameters, apply it here.
		DateRangeRequestParameters fieldVisitParams = fieldVisitDateRange.setNewStartDate(requestParameters, primaryZoneOffset);
		List<FieldVisitDescription> fieldVisits = fieldVisitDescriptionService.getDescriptions(primaryStationId, primaryZoneOffset, fieldVisitParams);
		
		//Measurements
		List<FieldVisitMeasurement> allFieldVisitMeasurements = new ArrayList<>();
		
		for (FieldVisitDescription visit: fieldVisits) {
			List<FieldVisitMeasurement> fieldVisitMeasurements = new ArrayList<>();
			FieldVisitDataServiceResponse fieldVisitData = fieldVisitDataService.get(visit.getIdentifier());
			String controlConditionName = fieldVisitData.getControlConditionActivity() != null ? fieldVisitData.getControlConditionActivity().getControlCondition() : null;
			
			if (requestParameters.getExcludeConditions() == null || controlConditionName == null || !requestParameters.getExcludeConditions().contains(controlConditionName)){
				fieldVisitMeasurements = fieldVisitMeasurementsBuilderService.extractFieldVisitMeasurements(fieldVisitData, requestParameters.getRatingModelIdentifier());
				allFieldVisitMeasurements.addAll(fieldVisitMeasurements);
			}
			
		}
		List<FieldVisitMeasurement> fieldVisitMeasurementsShiftSet = new ShiftNumberCalculator().calculateMeasurementsShiftNumber(range, ratingShifts, allFieldVisitMeasurements);
		report.setMeasurements(fieldVisitMeasurementsShiftSet);
		
		// Add rating shifts to report
		report.setRatingShifts(ratingShifts);
		report.setMaximumStageHeight(minMaxStageHeights.getMax());
		report.setMinimumStageHeight(minMaxStageHeights.getMin());
		
		//Report Metadata
		report.setReportMetadata(getReportMetadata(requestParameters,
			requestingUser,
			timeSeriesDescriptions
		));		
		return report;
	}

	protected VDiagramReportMetadata getReportMetadata(VDiagramRequestParameters requestParameters, String requestingUser, Map<String, TimeSeriesDescription> timeSeriesDescriptions) {
		VDiagramReportMetadata metadata = new VDiagramReportMetadata();
		metadata.setTitle(REPORT_TITLE);
		metadata.setRequestingUser(requestingUser);
		metadata.setRequestParameters(requestParameters);
		metadata.setStationId(timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier()).getLocationIdentifier());
		metadata.setStationName(locationDescriptionListService.getByLocationIdentifier(timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier()).getLocationIdentifier()).getName());
		metadata.setTimezone(AqcuTimeUtils.getTimezone(timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier()).getUtcOffset()));
		metadata.setPrimaryTimeseriesIdentifier(timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier()).getIdentifier());
		metadata.setUpchainTimeseriesIdentifier(timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier()).getIdentifier());
		
		return metadata;
	}
	
	protected List<RatingCurve> getRatingCurves(VDiagramRequestParameters requestParameters, ZoneOffset primaryZoneOffset) {
		List<RatingCurve> rawCurveList = ratingCurveListService.getRawResponse(requestParameters.getRatingModelIdentifier(), null, null, null).getRatingCurves();
		List<RatingCurve> ratingCurveList = ratingCurveListService.getAqcuFilteredRatingCurves(rawCurveList, 
				requestParameters.getStartInstant(primaryZoneOffset), requestParameters.getEndInstant(primaryZoneOffset));

		return ratingCurveList;
	}

	protected List<VDiagramRatingShift> buildRatingShifts(List<RatingShift> ratingShiftList, List<RatingCurve> ratingCurves, TimeRange range) {
		//Create Rating Shifts
		List<VDiagramRatingShift> ratingShifts = new ArrayList<>();
		ratingShiftList.parallelStream().forEachOrdered(shift -> {
			List<VDiagramRatingShift> ratingShiftsbyCurve = ratingCurves.parallelStream()
					.filter(x -> x.getShifts().contains(shift))
					.map(x -> {
						VDiagramRatingShift newShift = new VDiagramRatingShift(shift, x.getId());
						return newShift;
					})
					.collect(Collectors.toList());
			ratingShifts.addAll(ratingShiftsbyCurve);
		});
		List<VDiagramRatingShift> ratingShiftNumberSet = new ShiftNumberCalculator().calculateRatingShiftNumber(range, ratingShifts);
		return ratingShiftNumberSet;
	}
	
	protected List<RatingShift> getRatingShifts(VDiagramRequestParameters requestParameters, ZoneOffset primaryZoneOffset, List<RatingCurve> ratingCurves) {
		List<RatingShift> ratingShiftList =  ratingCurveListService.getAqcuFilteredRatingShifts(ratingCurves, 
				requestParameters.getStartInstant(primaryZoneOffset), requestParameters.getEndInstant(primaryZoneOffset));
		
		return ratingShiftList;
	}
}