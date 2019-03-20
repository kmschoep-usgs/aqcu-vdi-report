package gov.usgs.aqcu.calc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.MinMaxData;
import gov.usgs.aqcu.model.MinMaxPoint;
import gov.usgs.aqcu.util.BigDecimalSummaryStatistics;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;
import gov.usgs.aqcu.util.LogExecutionTime;

/**
 * Produces a summarized version of the time series including min and max points.
 * 
 * @author 
 */
public class MinMaxFinder {
	
	/**
	 * This method should only be called if the timeSeriesPoints list is not null.
	 */	
	@LogExecutionTime
	public MinMaxData getMinMaxData(List<TimeSeriesPoint> timeSeriesPoints) {
		Map<BigDecimal, List<MinMaxPoint>> minMaxPoints = timeSeriesPoints.parallelStream()
				.map(x -> {
					MinMaxPoint point = new MinMaxPoint(x.getTimestamp().getDateTimeOffset(), DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return point;
				})
				.filter(x -> x.getValue() != null)
				.collect(Collectors.groupingByConcurrent(MinMaxPoint::getValue));

		BigDecimalSummaryStatistics stats = minMaxPoints.keySet().parallelStream()
				.collect(BigDecimalSummaryStatistics::new,
						BigDecimalSummaryStatistics::accept,
						BigDecimalSummaryStatistics::combine);

		return new MinMaxData(stats.getMin(), stats.getMax(), minMaxPoints);
	}
	
}
