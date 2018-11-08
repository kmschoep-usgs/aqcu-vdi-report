package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterWithUnit;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingCurve;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.OffsetPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingShift;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingShiftPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeRange;

import gov.usgs.aqcu.model.FieldVisitMeasurement;
import gov.usgs.aqcu.model.VDiagramRatingShift;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingCurveType;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ShiftNumberCalculatorTest {
	private static final Logger log = LoggerFactory.getLogger(ShiftNumberCalculatorTest.class);
	
	private TimeRange range = new TimeRange().setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
			.setEndTime(Instant.parse("2017-01-10T00:00:00Z"));

	Instant startTime = Instant.parse("2017-01-01T00:00:00Z");
	Instant endTime = Instant.parse("2017-01-10T00:00:00Z");
	
	private static FieldVisitMeasurement fieldVisitMeasurementA = new FieldVisitMeasurement();
	private static FieldVisitMeasurement fieldVisitMeasurementB = new FieldVisitMeasurement();
	private static FieldVisitMeasurement fieldVisitMeasurementG = new FieldVisitMeasurement();
	private static FieldVisitMeasurement fieldVisitMeasurementH = new FieldVisitMeasurement();
	
	private static List<FieldVisitMeasurement> MEASUREMENT_LIST = new ArrayList<>();
	
	private static final PeriodOfApplicability periodA = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setRemarks("premarks-a");
	private static final PeriodOfApplicability periodB = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-02T00:00:00Z"))
		.setEndTime(Instant.parse("9999-12-31T23:59:59.9999999Z"))
		.setRemarks("premarks-b");
	private static final PeriodOfApplicability periodC = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-04T00:00:00Z"))
		.setEndTime(Instant.parse("9999-12-31T23:59:59.9999999Z"))
		.setRemarks("premarks-a");
	private static final PeriodOfApplicability periodD = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-05T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-07T00:00:00Z"))
		.setRemarks("premarks-b");
	private static final PeriodOfApplicability periodE = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-07T00:00:00Z"))
		.setEndTime(Instant.parse("9999-12-31T23:59:59.9999999Z"))
		.setRemarks("premarks-a");
	private static final PeriodOfApplicability periodF = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-10T00:00:00Z"))
		.setEndTime(Instant.parse("9999-12-31T23:59:59.9999999Z"))
		.setRemarks("premarks-b");
	private static final PeriodOfApplicability periodG = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-11T00:00:00Z"))
		.setEndTime(Instant.parse("2017-01-12T00:00:00Z"))
		.setRemarks("premarks-a");
	private static final PeriodOfApplicability periodH = new PeriodOfApplicability()
		.setStartTime(Instant.parse("2017-01-12T00:00:00Z"))
		.setEndTime(Instant.parse("9999-12-31T23:59:59.9999999Z"))
		.setRemarks("premarks-b");

	private static final RatingShift shiftA1 = new RatingShift()
		.setPeriodOfApplicability(periodA)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));
	private static final RatingShift shiftA2 = new RatingShift()
		.setPeriodOfApplicability(periodB)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(2.0).setShift(1.0))));
	private static final RatingShift shiftA3 = new RatingShift()
		.setPeriodOfApplicability(periodE)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));
	private static final RatingShift shiftA4 = new RatingShift()
		.setPeriodOfApplicability(periodF)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));
	private static final RatingShift shiftB1 = new RatingShift()
		.setPeriodOfApplicability(periodC)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));
	private static final RatingShift shiftB2 = new RatingShift()
		.setPeriodOfApplicability(periodD)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));
	private static final RatingShift shiftC1 = new RatingShift()
		.setPeriodOfApplicability(periodG)
		.setShiftPoints(new ArrayList<>(Arrays.asList(new RatingShiftPoint().setInputValue(1.0).setShift(1.0))));

	public static final RatingCurve CURVE_A = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-a")
		.setId("id-a")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodA, periodB, periodE)))
		.setRemarks("remarks-a")
		.setShifts(new ArrayList<>(Arrays.asList(shiftA1, shiftA2, shiftA3, shiftA4)))
		.setType(RatingCurveType.LinearTable);
	public static final RatingCurve CURVE_B = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-b")
		.setId("id-b")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodC, periodD)))
		.setRemarks("remarks-b")
		.setShifts(new ArrayList<>(Arrays.asList(shiftB1, shiftB2)))
		.setType(RatingCurveType.LinearTable);
	public static final RatingCurve CURVE_C = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-c")
		.setId("id-c")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodG)))
		.setRemarks("remarks-c")
		.setShifts(new ArrayList<>(Arrays.asList(shiftC1)))
		.setType(RatingCurveType.LinearTable);
	public static final RatingCurve CURVE_D = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-d")
		.setId("id-d")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodH)))
		.setRemarks("remarks-d")
		.setShifts(new ArrayList<>())
		.setType(RatingCurveType.LinearTable);
	
	public static final VDiagramRatingShift vDShiftA1 = new VDiagramRatingShift(shiftA1,"id-a");
	public static final VDiagramRatingShift vDShiftA2 = new VDiagramRatingShift(shiftA2,"id-a");
	public static final VDiagramRatingShift vDShiftA3 = new VDiagramRatingShift(shiftA3,"id-a");
	public static final VDiagramRatingShift vDShiftA4 = new VDiagramRatingShift(shiftA4,"id-a");
	public static final VDiagramRatingShift vDShiftC1 = new VDiagramRatingShift(shiftC1,"id-c");

	public static final List<RatingCurve> CURVE_LIST = new ArrayList<RatingCurve>(Arrays.asList(CURVE_A, CURVE_B, CURVE_C, CURVE_D));	
	public static final List<VDiagramRatingShift> SHIFT_LIST = new ArrayList<VDiagramRatingShift>(Arrays.asList(vDShiftA1, vDShiftA2, vDShiftA3, vDShiftA4, vDShiftC1));

    @Before
	public void setup() {
    	fieldVisitMeasurementA.setMeasurementStartDate(Instant.parse("2017-01-01T05:00:00Z"));
    	fieldVisitMeasurementB.setMeasurementStartDate(Instant.parse("2017-01-06T00:00:00Z"));
    	fieldVisitMeasurementG.setMeasurementStartDate(Instant.parse("2017-01-11T10:00:00Z"));
    	fieldVisitMeasurementH.setMeasurementStartDate(Instant.parse("2015-01-01T00:00:00Z"));
    	
    	MEASUREMENT_LIST = new ArrayList<FieldVisitMeasurement>(Arrays.asList(fieldVisitMeasurementA, fieldVisitMeasurementB, fieldVisitMeasurementG, fieldVisitMeasurementH));
    	
	}
    
    @Test
	public void testCalculateRatingShiftNumber(){
		log.debug("test calculateRatingShiftNumber");
		List<VDiagramRatingShift> shiftList = new ShiftNumberCalculator().calculateRatingShiftNumber(range, SHIFT_LIST);
		assertEquals(5, shiftList.size());
		assertEquals(1, shiftList.get(0).getShiftNumber());
		assertEquals(2, shiftList.get(1).getShiftNumber());
		assertEquals(0, shiftList.get(4).getShiftNumber());
	}
    
    /**
	 * Test of calculateMeasurementsShiftNumber method
	 */
	@Test
	public void testCalculateMeasurementsShiftNumber() {
		log.debug("test calculateMeasurementsShiftNumber");
		List<VDiagramRatingShift> shiftList = new ShiftNumberCalculator().calculateRatingShiftNumber(range, SHIFT_LIST);
		List<FieldVisitMeasurement>  measurements = new ShiftNumberCalculator().calculateMeasurementsShiftNumber(range, shiftList, MEASUREMENT_LIST);
		assertEquals("1", measurements.get(0).getShiftNumber().toString());
		assertEquals(false, measurements.get(0).isHistoric());
		assertEquals("2", measurements.get(1).getShiftNumber().toString());
		assertNull(measurements.get(2).getShiftNumber());
		assertEquals(true, measurements.get(3).isHistoric());
	}
}