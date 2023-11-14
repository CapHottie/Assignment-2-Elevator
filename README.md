Assignment 2 - Elevator
This assignment tests your ability to use the data structures we studied so far to create a simulation of
one or more elevators.

Requirement 1: Read the (possibly missing) property file.
A property file will indicate the correct values of variables in your system. Each line in the property file
contains a KEY=VALUE pair, for example:
structures=linked

Requirement 2: Run the simulation.
Running the simulation requires instantiating objects which span the duration of the simulation (eg.
elevators) at the start of the simulation. Other objects are created only as needed and de-referenced
when no longer needed. For example: passenger objects should appear only when requesting transport
and should be de-referenced once an elevator takes them to their destination floor.
The simulation runs for a number of time units, called “ticks,” as specified in the property file with the
“duration” key. During a “tick,” any the following items may occur:
A) Elevator unload & load: An elevator may stop at a floor and unload all passengers in the elevator
bound for that floor. Additionally, during the same “tick”, any passengers on the floor waiting for
an elevator going in the desired direction (up or down). You may assume that passengers never
enter an elevator going in the wrong direction.
B) Elevator travel: An elevator may travel between no more than 5 floors (eg. from the 8th floor to
the 13th floor).
C) New passengers: Given “passengers” — the probabilities in the property file — a new passenger
may appear on a floor and request transportation to another floor.

Requirement 3: Report the results of the simulation.
Once the simulation concludes, your implementation is required to report the following items:
Average length of time between passenger arrival and conveyance to the final destination.
Longest time between passenger arrival and conveyance to the final destination.
Shortest time between passenger arrival and conveyance to the final destination.
