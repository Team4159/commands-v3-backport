# commands-v3-backport

A comprehensive, but not exhaustive, backport of WPILib 2027 Commands V3 to WPILib 2026.

**DISCLAIMER:** Made for Team 4159's rookie Internal Robotics Competition. Do not use for serious or consequential robot code!

## Installation

1. Delete old commands file dependency
   - Delete the file in the `vendordeps` folder or use the WPILib GUI found on the left ribbon
2. Open the WPILib command palette by clicking the icon in the top right
3. Search for `Manage Vendor Libraries`
4. Select the option to install online
5. Paste in this URL: `https://raw.githubusercontent.com/Team4159/commands-v3-backport/refs/heads/main/vendordep.json`
6. Ensure that `CommandScheduler` is replaced with the new `Scheduler`
   - This primarily means replacing every instance of `CommandScheduler.getInstance()` with `Scheduler.getDefault()` inside `Robot.java`
7. Make sure to build the project
8. You might have to clear your language server's cache and restart it

## Usage

You may run into a loop overrun error on initialization when using this library. Try pasting in the following line into your `Robot.java` constructor:

```java
public Robot() {
    Scheduler.getDefault().run(); // Run the scheduler once to force load everything
}
```
