/**
 *  Fan On Schedule
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
	name: "Fan On Schedule",
	namespace: "cestewart",
	author: "Christopher Stewart",
	description: "Turn a fan on every X minutes and run for Y minutes.",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Switch to control") {
		input "theswitch", "capability.switch", title: "Fan", required: true
	}
	section("Settings") {
		input name:"runEveryMinutes", type: "number", title: "How often shoud the fan run in minutes", required: true
		input name:"runForMinutes", type: "number", title: "How long should the fan run in minutes", required: true
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
	subscribe(theswitch, "switch", switchHandler)
	turnSwitchOn()
}

def switchHandler(evt) {
	if (theswitch.currentValue("switch").equalsIgnoreCase("on")) {
		log.debug "The fan will be turned off in ${runForMinutes} minutes."
		runIn(60*runForMinutes, turnSwitchOff)
	}
}

def turnSwitchOn() {
	theswitch.on()
	log.debug "The fan has been turned on"
	schedule(now() + runEveryMinutes * 60 * 1000, turnSwitchOn)
}

def turnSwitchOff() {
	theswitch.off()
	log.debug "The fan has been turned off"
}
