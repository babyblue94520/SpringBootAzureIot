package org.gradle.point;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelemetryDataPoint {
	public String deviceId;
	public double windSpeed;

	public String serialize() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}
