/**
 *  Alert If Off
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
    name: "Alert If Off",
    namespace: "cestewart",
    author: "Christopher Stewart",
    description: "Will send an alert if a switch is turned off and turn the switch back on.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Switch to monitor") {
        input "theswitch", "capability.switch", title: "switch", required: true
    }
    section("Settings") {
        input name:"turnBackOn", type: "enum", title: "Turn switch back on?", required: true, options: ["Yes", "No"]
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
}

def switchHandler(evt) {
    log.debug "switchOffHandler called: $evt"
	if (theswitch.currentValue("switch").equalsIgnoreCase("off")) {
        switchOffHandler()
    }
}

def switchOffHandler() {
    if (turnBackOn.equalsIgnoreCase("yes")) {
        sendPush("${theswitch.displayName} has been turned off and back on!")
        theswitch.on()
        log.debug "${theswitch.displayName} has been turned back on."        
    }
    else {
        sendPush("${theswitch.displayName} has been turned off!")
        log.debug "${theswitch.displayName} has NOT been turned back on."
    }
}