package org.unidal.maven.plugin.source.pipeline;

public enum SourceScope {
	PROJECT,

	MODULE,

	SOURCE,

	TEST_SOURCE,

	RESOURCE,

	TEST_RESOURCE,

	FILE;

	public boolean isSource() {
		return this == SOURCE || this == TEST_SOURCE;
	}

	public boolean isTest() {
		return this == TEST_SOURCE || this == TEST_RESOURCE;
	}
}
