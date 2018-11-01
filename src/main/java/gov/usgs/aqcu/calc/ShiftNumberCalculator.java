/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.aqcu.calc;

import gov.usgs.aqcu.model.FieldVisitMeasurement;
import gov.usgs.aqcu.model.VDiagramRatingShift;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeRange;   
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author dpattermann
 */
public class ShiftNumberCalculator {
	
	/**
	 * Sets the Field visit measurements shift number by comparing it to the given list of 
	 * rating shifts. Checks the measurements for historic values and sets them
	 * to historic if so.
	 * 
	 * @param range Date range to work within
	 * @param comparisonRatingShiftsList Used to match shift numbers to
	 * @param measurements List of field visit measurements which need shift number set.
	 * @return Returns a list of measurements with shift numbers and historic set properly.
	 */
	public List<FieldVisitMeasurement> calculateMeasurementsShiftNumber(TimeRange range, List<VDiagramRatingShift> comparisonRatingShiftsList, List<FieldVisitMeasurement> measurements){
		checkMeasurementsForHistoric(range, measurements);
		
		for(FieldVisitMeasurement measurement: measurements){
			if(contains(range, measurement.getMeasurementStartDate())){
				for(VDiagramRatingShift comparisonShift: comparisonRatingShiftsList){
					TimeRange comparisonRange = new TimeRange().setStartTime(comparisonShift.getApplicableStartDateTime()).setEndTime(comparisonShift.getApplicableEndDateTime());
					if(contains(comparisonRange, measurement.getMeasurementStartDate())){
						measurement.setShiftNumber(comparisonShift.getShiftNumber());
					}
				}
			}
		}
		
		return measurements;
	}
	
	/**
	 * Calculates the shift numbers for a list of Rating shifts.
	 * 
	 * @param range The date range to work within, ignore if outside the range.
	 * @param ratingShiftsList Rating shifts which need shift numbers set.
	 * @return  A list of rating shifts that have been given shift numbers.
	 */
	public List<VDiagramRatingShift> calculateRatingShiftNumber(TimeRange range, List<VDiagramRatingShift> ratingShiftsList){
		Set<String> uniqueShiftNumbers = new HashSet<>();
		
		for(VDiagramRatingShift ratingShift: ratingShiftsList){
			TimeRange shiftRange = new TimeRange().setStartTime(ratingShift.getApplicableStartDateTime()).setEndTime(ratingShift.getApplicableEndDateTime());
			
			if(overlaps(range, shiftRange)){
				uniqueShiftNumbers.add(hashShiftPoints(ratingShift.getStagePoints(), ratingShift.getShiftPoints()));
				ratingShift.setShiftNumber(uniqueShiftNumbers.size());
			}
		}
		return ratingShiftsList;
	}
	
	/**
	 * Creates a combined string to see when different instances of stage and shift points
	 * have the same values. 
	 * 
	 * @param stagePoints Stage point values to combine
	 * @param shiftPoints shift point values to combine
	 * @return a string with values next to each other with a "|" between
	 */
	public String hashShiftPoints(List<BigDecimal> stagePoints, List<BigDecimal> shiftPoints) {		
		StringBuilder ret = new StringBuilder();		
		if (stagePoints != null) {		
			ret.append(stagePoints.toString());		
		}		
		ret.append("|");		
		if (shiftPoints != null) {		
			ret.append(shiftPoints.toString());		
		}		
				
		return ret.toString();		
	}
	
	/**
	 * Checks a list of field visit measurements and sets the measurements that 
	 * are outside of the date range to Historic.
	 * 
	 * @param range The DateRange which the points should be inside
	 * @param measurements The field visit measurements to check if they are inside the date range.
	 */
	public void checkMeasurementsForHistoric(TimeRange range, List<FieldVisitMeasurement> measurements){
		for(FieldVisitMeasurement measurement: measurements){
			measurement.setHistoric(!contains(range,measurement.getMeasurementStartDate()));
		}
	}
	
	/**
	 * Utility function that returns whether or not the provided time is contained
	 * within the time range. Note that containment is not considered true of the
	 * edges of the comparison range are equal to the provided time. The provided
	 * time must be AFTER the start date of the range and BEFORE the end date of
	 * the range for this to return true.
	 * 
	 * @param inTime The time that should be compared with the date range calling this function.
	 * @return TRUE - The provided time is contained in the date range. | FALSE - The provided time is not contained in the date range.
	 */
	public boolean contains(TimeRange inRange, Instant inTime) {
		return inRange.getStartTime().isBefore(inTime) && inRange.getEndTime().isAfter(inTime);
	}
	
	/**
	 * Utility function that checks whether or not the provided date range overlaps
	 * the date range that this function is being called from.
	 * 
	 * @param dateRange The date range that should be compared with the date range calling this function.
	 * @return TRUE - The date ranges overlap. | FALSE - The date ranges do not overlap.
	 */
	public static boolean overlaps(TimeRange dateRange, TimeRange inDateRange) {
		Instant inStart = inDateRange.getStartTime();
		Instant inEnd = inDateRange.getEndTime();
		
		return dateRange.getStartTime().isBefore(inEnd) && dateRange.getEndTime().isAfter(inStart) || 
				dateRange.getEndTime().isAfter(inStart) && dateRange.getStartTime().isBefore(inEnd);
	}
}
