package org.gradle.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import org.gradle.point.TelemetryDataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

@RestController
@RequestMapping(path="/Send")
@SuppressWarnings("rawtypes")
public class SendController {

	private String hostName = "hostName";
	private String deviceId = "deviceId";
	private String SharedAccessKeyName = "SharedAccessKeyName";
	private String SharedAccessKey = "SharedAccessKey";
	private String connString = "HostName="+hostName+";DeviceId="+deviceId+";SharedAccessKeyName="+SharedAccessKeyName+";SharedAccessKey="+SharedAccessKey;
	private IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity send() throws URISyntaxException, IOException{

		DeviceClient client = new DeviceClient(connString, protocol);
		client.open();
		double avgWindSpeed = 10; // m/s
		Random rand = new Random();
		
		double currentWindSpeed = avgWindSpeed + rand.nextDouble() * 4 - 2;
		TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
		telemetryDataPoint.deviceId = deviceId;
		telemetryDataPoint.windSpeed = currentWindSpeed;

		System.out.println("connString");
		String msgStr = telemetryDataPoint.serialize();
		Message msg = new Message(msgStr);
		System.out.println("Sending: " + msgStr);

		Object lockobj = new Object();
		EventCallback callback = new EventCallback();
		client.sendEventAsync(msg, callback, lockobj);
		client.close();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	private static class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			System.out.println("IoT Hub responded to message with status: " + status.name());

			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}
}
