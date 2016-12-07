/**
 *  Fancy Bathroom Fan Timer
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
	name: "Fancy Bathroom Fan Timer",
	namespace: "cestewart",
	author: "Christopher Stewart",
	description: "Turn on the bathroom fan when the light comes on and shut if off on timer.  Optional regular fan schedule is also available.",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Light Switch") {
		input "theLightSwitch", "capability.switch", title: "Light", required: true
	}
	section("Fan Switch") {
		input "theFanSwitch", "capability.switch", title: "Fan", required: true
	}
	section("Settings") {
		input name:"runForMinutes", type: "number", title: "How long should the fan run in minutes", required: true
		input name:"runEveryMinutes", type: "number", title: "How often shoud the fan run in minutes", required: false
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
	subscribe(theLightSwitch, "switch", theLightSwitchHandler)
	subscribe(theFanSwitch, "switch", theFanSwitchHandler)
	turnFanSwitchOn()
}

def theLightSwitchHandler(evt) {
	if (theLightSwitch.currentValue("switch").equalsIgnoreCase("on")) {
		turnFanSwitchOn()
	}
}

def theFanSwitchHandler(evt) {
	if (theFanSwitch.currentValue("switch").equalsIgnoreCase("on")) {
		turnFanSwitchOn()
	}
}

def turnFanSwitchOn() {
	theFanSwitch.on()
	log.debug "The fan has been turned on"
	runIn(60*runForMinutes, turnFanSwitchOff)
	log.debug "The fan will be turned off in ${runForMinutes} minutes."
    if (runEveryMinutes > 0) {
		log.debug "The fan has been scheduled to run every ${runEveryMinutes} minutes."
		schedule(now() + runEveryMinutes * 60 * 1000, turnFanSwitchOn)
	}
}

def turnFanSwitchOff() {
	theFanSwitch.off()
	log.debug "The fan has been turned off"
}
