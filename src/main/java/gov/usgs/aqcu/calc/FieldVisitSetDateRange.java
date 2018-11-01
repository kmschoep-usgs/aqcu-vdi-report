package gov.usgs.aqcu.calc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.parameter.VDiagramRequestParameters;
import gov.usgs.aqcu.validation.RequiredIdentifiersPresent;

@RequiredIdentifiersPresent
public class FieldVisitSetDateRange {
	private static final int YEARS_PRIOR_FOR_HISTORIC = 0;
	
	
	public DateRangeRequestParameters setNewStartDate(VDiagramRequestParameters inRequestParameters, ZoneOffset zoneOffset) {
		DateRangeRequestParameters fieldVisitParams = new DateRangeRequestParameters();
		Instant instStartDate = inRequestParameters.getStartInstant(zoneOffset);
		LocalDate startDate = instStartDate.atZone(zoneOffset).toLocalDate().minusYears(determineYearsPrior(inRequestParameters.getPriorYearsHistoric()));
		LocalDate endDate = inRequestParameters.getEndInstant(zoneOffset).atZone(zoneOffset).toLocalDate();
		fieldVisitParams.setStartDate(startDate);
		fieldVisitParams.setEndDate(endDate);
		return fieldVisitParams;
	}
	
	private int determineYearsPrior(String yearsPriorForHistoric) {
		int yrsPrior = YEARS_PRIOR_FOR_HISTORIC;
		if (yearsPriorForHistoric != null) {
			try {
				yrsPrior = Integer.parseInt(yearsPriorForHistoric);
			} catch (NumberFormatException nfe) {
				//use default
			}
		}
		return yrsPrior;
	}
	
}
