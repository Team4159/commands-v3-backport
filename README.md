# commands-v3-backport

A comprehensive, but not exhaustive, backport of WPILib 2027 Commands V3 to WPILib 2026.

Derived from WPILib 2027_alpha6.

## Installation

1. Delete old commands file dependency in your project's `vendordeps` folder
2. Open the WPILib command palette by clicking the icon in the top right
3. Search for `Manage Vendor Libraries`
4. Select the option to install online
5. Paste in this URL: https://raw.githubusercontent.com/breathaning/commands-v3-backport/refs/heads/main/vendordep.json
6. Ensure that `CommandScheduler` is replaced with the new `Scheduler`
    - This primarily means replacing every instance of `CommandScheduler.getInstance()` with `Scheduler.getDefault()` inside `Robot.java`
