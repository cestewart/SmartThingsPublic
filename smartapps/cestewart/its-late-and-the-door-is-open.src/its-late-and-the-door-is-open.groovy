/**
 *  It's Late and the Door Is Open
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
    name: "It's Late and the Door Is Open",
    namespace: "cestewart",
    author: "Christopher Stewart",
    description: "Pick a start and end time and if the door is left open, or opened furing that time you will be alerted.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Door To Monitor") {
    	input "theContactSensor", "capability.contactSensor", title: "Pick a door", required: true, multiple: false
  	}
  	section("Settings") {
        input name:"startTime", type: "time", title: "Time to start monitoring", required: true
        input name:"endTime", type: "time", title: "Time to stop monitoring", required: true
        input name:"phoneNumber", type: "text", title: "Enter a phone number to be notified via SMS", required: false
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
	subscribe(theContactSensor, "contact", theContactSensorHandler)
	shouldDoorBeOpen()
}

def theContactSensorHandler(evt) {
	log.debug "theContactSensorHandler sensor event fired.  Status = ${theContactSensor.contactState.value}"
    shouldDoorBeOpen()
}

def shouldDoorBeOpen() {	
	log.debug "shouldDoorBeOpen called"
	if (theContactSensor.contactState.value.equalsIgnoreCase("open") && timeOfDayIsBetween(startTime, endTime, new Date(), TimeZone.getTimeZone("UTC"))) {
    	alertDoorIsOpen()
		schedule(now() + 30 * 60 * 1000, shouldDoorBeOpen)
    }
}

def alertDoorIsOpen() {
	def message = "${theContactSensor.displayName} is open."
	log.debug message
	sendPush(message)
    if (phoneNumber) {
    	sendSms(phoneNumber, message)
    }
}