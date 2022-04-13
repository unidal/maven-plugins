package org.unidal.cat.report;

import java.util.Date;

public interface Report {
	// 2020-01-01 00:00:00
	public static final long EPOCH = 1577808000000L;

	public int getDomainId();

	public ReportPeriod getPeriod();

	public Date getStartTime();

	public int getHour();
}
