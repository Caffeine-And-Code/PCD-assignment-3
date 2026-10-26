PCD a.y. 2025-2026 - ISI LM UNIBO - Cesena Campus

# Assignment #03 `

v2.0.1-20260506

The assignment is about concurrent programming based on message passing, in particular asynchronous message passing based on actors (first exercise) and synchronous message passing based on processes and channels (second exercise).


### Exercise #1 - *Smart Home Alarm System* 

# Smart Home Alarm System

## Overview

The goal of this assignment is to implement the control system of a smart home alarm. The system receives signals from a network of peripheral sensors (e.g., motion detectors and door/window sensors) and interacts with users through a numeric keypad for arming and disarming operations.

The alarm control unit reacts differently depending on its current state. When the system is disarmed, sensor events are ignored or simply logged, allowing normal movement inside the house. The system can be armed by entering a correct PIN code via the keypad.

To prevent false alarms, the system must handle time delays during both exit and entry phases.

---

## Functional Requirements

### 1. System States

The alarm system must support the following states:

* **Disarmed**

    * Sensors do not trigger alarms
    * Normal activity is allowed

* **Exit Delay**

    * Triggered after entering the correct PIN to arm the system
    * Sensors are still inactive
    * Allows the user to leave the house
    * After a fixed timeout, transitions to *Armed*

* **Armed**

    * Sensors are active
    * Any intrusion event triggers the *Entry Delay*

* **Entry Delay**

    * Triggered when a sensor detects an intrusion while armed
    * Starts a countdown
    * If the correct PIN is entered before timeout → transition to *Disarmed*
    * Otherwise → transition to *Alarm*

* **Alarm (Emergency State)**

    * Siren is activated
    * The only way to stop it is by entering the correct PIN
    * On correct PIN → transition to *Disarmed*

---

### 2. User Interaction

* The system must accept input from a keypad:

    * PIN entry for:

        * Arming the system
        * Disarming the system
        * Stopping the alarm
* The PIN must be validated before any state transition occurs

---

### 3. Sensor Handling

* The system receives events from:

    * Motion sensors
    * Door/window sensors
* Behavior depends on the current state:

    * In *Disarmed* and *Exit Delay*: ignore or log events
    * In *Armed*: trigger *Entry Delay*
    * In *Entry Delay*: no additional effect
    * In *Alarm*: already triggered

---

### 4. Timing Constraints

* Implement configurable delays:

    * **Exit Delay** (e.g., 20–60 seconds)
    * **Entry Delay** (e.g., 10–30 seconds)
* Timeouts must automatically trigger state transitions

---

## Bonus: Zone-Based Control and Partial Arming

As an extension, the system can support dividing the house into multiple **zones** (e.g., living area, sleeping area, perimeter).

### Additional Requirements

* Each sensor is associated with a zone
* During arming, the user can choose:

    * Full arming (all zones active)
    * Partial arming (only selected zones active)

### Behavior

* Only sensors in **active zones** can trigger:

    * Entry Delay
    * Alarm
* Sensors in **inactive zones** are ignored

### Example Use Case

* Night mode:

    * Activate perimeter and ground floor zones
    * Leave upper floor inactive
    * Users can move freely upstairs without triggering the alarm

- [Description](https://github.com/nicolasfara/seminar-pcd-actor-pekko-code/blob/master/assignment_3_smart_home_alarm.md) by N. Farabegoli
- To be implemented using Apache Pekko, used as reference framework in lab 
  - alternatively, you may use any other actor-based framework or platform: in that case, ask teachers before proceeding

### Exercise #2 - *Odds-and-Evens Game*

The goal of the exercise is to design and implement in Go language a `Odds-and-Evens` game played by `N` players (as a e.g. decision process to select who is going to do some task). The number of players `N` is equal to 2<sup>`m`</sup>, so that  the game is organized in `m` rounds: at each round, games run concurrently and the winners goes to the next round, until the final round. For instance: with `m = 3`, we have 8 players, at the first round playing 4 games concurently; the 4 winners go on playing the next round, playing 2 games concurrently (i.e. the semi-finals); finally, the 2 winners play the final game and we have a winner. 
- To be implemented in Go using an interaction model based on message passing
  - no shared memory is allowed


### The deliverable

The deliverable must be a zipped folder `Assignment-03`, to be submitted on the course web site, including for each problem/exercise:  
- `src` directory with sources
- `doc` directory with a short report in PDF (`report.pdf`). The report should include:
	- A brief analysis of the problem, focusing in particular aspects that are relevant from a  concurrent point of view.
	- A brief description of the strategy adopted


