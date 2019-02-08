package gov.usgs.aqcu.parameter;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class VDiagramRequestParametersTest {

	Instant reportEndInstant = Instant.parse("2018-03-16T23:59:59.999999999Z");
	Instant reportStartInstant = Instant.parse("2018-03-16T00:00:00.00Z");
	LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	LocalDate reportStartDate = LocalDate.of(2018, 03, 16);
    String primaryIdentifier = "test-identifier";
    List<String> excludeConditions = Arrays.asList("IceAnchor", "IceCover", "IceShore");

    @Test
	public void getAsQueryStringTest() {
    	VDiagramRequestParameters params = new VDiagramRequestParameters();
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
        params.setExcludeConditions(excludeConditions);
        String expected = "startDate=2018-03-16&endDate=2018-03-16&primaryTimeseriesIdentifier=test-identifier&excludeConditions=IceAnchor,IceCover,IceShore";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}
}