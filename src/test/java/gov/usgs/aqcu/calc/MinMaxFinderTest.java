package gov.usgs.aqcu.calc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.MinMaxData;
import gov.usgs.aqcu.model.MinMaxPoint;

@RunWith(SpringRunner.class)
public class MinMaxFinderTest {

	private MinMaxFinder service;
	private Instant nowInstant;
	private LocalDate nowLocalDate;

	@Before
	public void setup() {
		nowInstant = Instant.now();
		nowLocalDate = LocalDate.now();
		service = new MinMaxFinder();
	}

	@Test
	public void getMinMaxDataEmptyListTest() {
		MinMaxData minMaxData = service.getMinMaxData(new ArrayList<>());
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertTrue(minMaxData.getMin().isEmpty());
		assertNotNull(minMaxData.getMax());
		assertTrue(minMaxData.getMax().isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getMinMaxDataDvTest() {
		boolean endOfPeriod = true;
		ZoneOffset zoneOffset = ZoneOffset.of("-6");
		List<TimeSeriesPoint> timeSeriesPoints = getTimeSeriesPoints(endOfPeriod, zoneOffset);
		MinMaxData minMaxData = service.getMinMaxData(timeSeriesPoints);
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertEquals(3, minMaxData.getMin().size());
		assertThat(minMaxData.getMin(),
				contains(samePropertyValuesAs(getMinMaxPoint5(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint4(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint2(endOfPeriod, zoneOffset))));
		assertNotNull(minMaxData.getMax());
		assertEquals(1, minMaxData.getMax().size());
		assertThat(minMaxData.getMax(), contains(samePropertyValuesAs(getMinMaxPoint3(endOfPeriod, zoneOffset))));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getMinMaxDataTsTest() {
		boolean endOfPeriod = false;
		ZoneOffset zoneOffset = ZoneOffset.UTC;
		MinMaxData minMaxData = service.getMinMaxData(getTimeSeriesPoints(endOfPeriod, zoneOffset));
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertEquals(3, minMaxData.getMin().size());
		assertThat(minMaxData.getMin(),
				contains(samePropertyValuesAs(getMinMaxPoint5(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint4(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint2(endOfPeriod, zoneOffset))));
		assertNotNull(minMaxData.getMax());
		assertEquals(1, minMaxData.getMax().size());
		assertThat(minMaxData.getMax(), contains(samePropertyValuesAs(getMinMaxPoint3(endOfPeriod, zoneOffset))));
	}


	protected List<TimeSeriesPoint> getTimeSeriesPoints(boolean endOfPeriod, ZoneOffset zoneOffset) {
		List<TimeSeriesPoint> timeSeriesPoints = Stream
				.of(getTsPoint1(endOfPeriod, zoneOffset),
					getTsPoint2(endOfPeriod, zoneOffset),
					getTsPoint3(endOfPeriod, zoneOffset),
					getTsPoint4(endOfPeriod, zoneOffset),
					getTsPoint5(endOfPeriod, zoneOffset),
					getTsPoint6(endOfPeriod, zoneOffset))
				.collect(Collectors.toCollection(ArrayList::new));
		return timeSeriesPoints;
	}

	protected TimeSeriesPoint getTsPoint1(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("654.321").setNumeric(Double.valueOf("123.456")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 6))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint2(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 2))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint3(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("987.654").setNumeric(Double.valueOf("456.789")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 0))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint4(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 4))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint5(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 5))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint6(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("EMPTY").setNumeric(null))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 12))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected Instant getTestInstant(boolean endOfPeriod, ZoneOffset zoneOffset, long days) {
		if (endOfPeriod) {
			//In the world of Aquarius, Daily Values are at 24:00 of the day of measurement, which is actually
			//00:00 of the next day in (most) all other realities.
			//For testing, this means we need to back up one day from what would be expected.
			return nowLocalDate.atTime(0, 0, 0).toInstant(zoneOffset).minus(Duration.ofDays(days-1));
		} else {
			return nowInstant.minus(Duration.ofDays(days));
		}
	}

	protected MinMaxPoint getMinMaxPoint1(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 6), new BigDecimal("654.321"));
	}
	protected MinMaxPoint getMinMaxPoint2(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 2), new BigDecimal("321.987"));
	}
	protected MinMaxPoint getMinMaxPoint3(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 0), new BigDecimal("987.654"));
	}
	protected MinMaxPoint getMinMaxPoint4(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 4), new BigDecimal("321.987"));
	}
	protected MinMaxPoint getMinMaxPoint5(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 5), new BigDecimal("321.987"));
	}

}