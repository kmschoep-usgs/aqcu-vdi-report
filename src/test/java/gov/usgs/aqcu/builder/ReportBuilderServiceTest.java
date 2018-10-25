package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterWithUnit;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingCurve;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.OffsetPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingShift;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingShiftPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeRange;

import gov.usgs.aqcu.model.VDiagramRatingShift;
import gov.usgs.aqcu.retrieval.AquariusRetrievalService;
import gov.usgs.aqcu.retrieval.FieldVisitDataService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionService;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.QualifierLookupService;
import gov.usgs.aqcu.retrieval.RatingCurveListService;
import gov.usgs.aqcu.retrieval.TimeSeriesDataCorrectedService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingCurveType;

@RunWith(SpringRunner.class)
public class ReportBuilderServiceTest {
	private static final Logger log = LoggerFactory.getLogger(ReportBuilderServiceTest.class);
	
	@MockBean
	private AquariusRetrievalService aquariusService;
	@MockBean
	private LocationDescriptionListService locationDescriptionListService;
	@MockBean
	private TimeSeriesDescriptionListService timeSeriesDescriptionService;
	@MockBean
	private TimeSeriesDataCorrectedService timeSeriesDataCorrectedService;
	@MockBean
	private FieldVisitDescriptionService fieldVisitDescriptionService;
	@MockBean
	private FieldVisitDataService fieldVisitDataService;
	@MockBean
	private RatingCurveListService ratingCurveListService;
	@MockBean
	private QualifierLookupService qualifierLookupService;

	private ReportBuilderService service;	
	private TimeRange range = new TimeRange().setStartTime(Instant.parse("2017-01-01T00:00:00Z"))
			.setEndTime(Instant.parse("2017-01-10T00:00:00Z"));

	Instant startTime = Instant.parse("2017-01-01T00:00:00Z");
	Instant endTime = Instant.parse("2017-01-10T00:00:00Z");
		
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
		.setEquation("equation-a")
		.setId("id-a")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodC, periodD)))
		.setRemarks("remarks-a")
		.setShifts(new ArrayList<>(Arrays.asList(shiftB1, shiftB2)))
		.setType(RatingCurveType.LinearTable);
	public static final RatingCurve CURVE_C = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-a")
		.setId("id-a")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodG)))
		.setRemarks("remarks-a")
		.setShifts(new ArrayList<>(Arrays.asList(shiftC1)))
		.setType(RatingCurveType.LinearTable);
	public static final RatingCurve CURVE_D = new RatingCurve()
		.setBaseRatingTable(new ArrayList<>(Arrays.asList(new RatingPoint().setInputValue(1.0).setOutputValue(1.0))))
		.setEquation("equation-a")
		.setId("id-a")
		.setInputParameter(new ParameterWithUnit().setParameterName("param-in-a").setParameterUnit("param-unit-a"))
		.setOffsets(new ArrayList<>(Arrays.asList(new OffsetPoint().setInputValue(1.0).setOffset(1.0))))
		.setOutputParameter(new ParameterWithUnit().setParameterName("param-out-a").setParameterUnit("param-unit-a"))
		.setPeriodsOfApplicability(new ArrayList<>(Arrays.asList(periodH)))
		.setRemarks("remarks-a")
		.setShifts(new ArrayList<>())
		.setType(RatingCurveType.LinearTable);
	
	public static final VDiagramRatingShift vDShiftA1 = new VDiagramRatingShift(shiftA1,"id-a");
	public static final VDiagramRatingShift vDShiftA2 = new VDiagramRatingShift(shiftA2,"id-a");
	public static final VDiagramRatingShift vDShiftA3 = new VDiagramRatingShift(shiftA3,"id-a");
	public static final VDiagramRatingShift vDShiftA4 = new VDiagramRatingShift(shiftA4,"id-a");

	public static final List<RatingCurve> CURVE_LIST = new ArrayList<RatingCurve>(Arrays.asList(CURVE_A, CURVE_B));
	public static final List<RatingShift> SHIFT_LIST = new ArrayList<RatingShift>(Arrays.asList(shiftA1, shiftA2, shiftA3, shiftA4));
	public static final List<VDiagramRatingShift> VDIAGRAM_SHIFT_LIST = new ArrayList<VDiagramRatingShift>(Arrays.asList(vDShiftA1, vDShiftA2, vDShiftA3, vDShiftA4));

    @Before
	@SuppressWarnings("unchecked")
	public void setup() {
    	service = new ReportBuilderService(
    			locationDescriptionListService,
    			timeSeriesDescriptionService,
    			timeSeriesDataCorrectedService,
    			fieldVisitDescriptionService,
    			fieldVisitDataService,
    			ratingCurveListService,
    			qualifierLookupService);
	}
    
    @Test
	public void testBuildRatingShifts(){
		log.debug("test buildRatingShifts");
		List<VDiagramRatingShift> shiftList = service.buildRatingShifts(SHIFT_LIST, CURVE_LIST, range);
		assertEquals(4, shiftList.size());
		assertEquals(vDShiftA1.getRemarks(), shiftList.get(0).getRemarks());
		assertEquals(vDShiftA2.getRemarks(), shiftList.get(1).getRemarks());
		assertEquals(vDShiftA3.getRemarks(), shiftList.get(2).getRemarks());
		assertEquals(vDShiftA4.getRemarks(), shiftList.get(3).getRemarks());
		assertEquals(vDShiftA1.getCurveNumber(), shiftList.get(0).getCurveNumber());
		assertEquals(vDShiftA2.getCurveNumber(), shiftList.get(1).getCurveNumber());
		assertEquals(vDShiftA3.getCurveNumber(), shiftList.get(2).getCurveNumber());
		assertEquals(vDShiftA4.getCurveNumber(), shiftList.get(3).getCurveNumber());
	}
}