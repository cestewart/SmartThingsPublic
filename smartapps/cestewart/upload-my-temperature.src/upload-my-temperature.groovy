/**
 *  Upload My Temperature
 *
 *  Copyright 2016 Christopher Stewart
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Upload My Temperature",
    namespace: "cestewart",
    author: "Christopher Stewart",
    description: "Send a temperature reading to the cload",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Temperature Sensor to Upload") {
		input "theTemperatureSensor", "capability.temperatureMeasurement", title: "Pick a sensor", required: true, multiple: false
  	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(theTemperatureSensor, "temperature", theTemperatureSensorHandler)
}

def theTemperatureSensorHandler(evt) {
	log.debug "Hub id is ${GetHubId()}"
	def currentState = theTemperatureSensor.currentState("temperature")
	uploadTemperature(currentState.value)
}

def uploadTemperature(temperature) {
	def params = [
		uri:"http://autorenterapi.azurewebsites.net/api/smartthings",
        contentType: "application/json",
        body: [
            "Temperature":temperature,
			"SensorName":theTemperatureSensor.displayName,
			"HubId":GetHubId()
		]
	]
    
    try {
        httpPostJson(params) {resp ->
            log.debug "resp data: ${resp.data}"
        }
    } catch (e) {
        log.error "error: $e"
    }    
}

def GetHubId() {
	return location.hubs[0].id
}