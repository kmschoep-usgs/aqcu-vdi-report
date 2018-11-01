package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.parameter.VDiagramRequestParameters;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FieldVisitSetDateRangeTest {

	private VDiagramRequestParameters parameters = new VDiagramRequestParameters();
	private FieldVisitSetDateRange fieldVisitParamCalc = new FieldVisitSetDateRange();
	public static final LocalDate REPORT_END_DATE = LocalDate.of(2018, 03, 17);
	public static final LocalDate REPORT_START_DATE = LocalDate.of(2018, 03, 16);

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		parameters = new VDiagramRequestParameters();
		fieldVisitParamCalc = new FieldVisitSetDateRange();
	}
	
	@Test
	public void getNewStartDate() {
		
		parameters.setStartDate(REPORT_START_DATE);
		parameters.setEndDate(REPORT_END_DATE);
		parameters.setPriorYearsHistoric("1");
		DateRangeRequestParameters testParams = fieldVisitParamCalc.setNewStartDate(parameters, ZoneOffset.UTC);
		assertEquals(REPORT_START_DATE.minusYears(1), testParams.getStartDate());
	}

}
