// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.breathaning.commandsv3backport.driverstation.internal;

import com.breathaning.commandsv3backport.driverstation.POVDirection;
import com.breathaning.commandsv3backport.driverstation.TouchpadFinger;
import com.breathaning.commandsv3backport.hardware.hal.RobotMode;
import edu.wpi.first.hal.AllianceStationID;
import edu.wpi.first.hal.ControlWord;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.util.Color;
import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/** Provide access to the network communication data to / from the Driver Station. */
public final class DriverStationBackend {

    /**
     * DriverStation constructor.
     *
     * <p>The single DriverStation instance is created statically with the instance static member
     * variable.
     */
    private DriverStationBackend() {}

    /**
     * Report error to Driver Station. Optionally appends Stack trace to error message.
     *
     * @param error The error to report.
     * @param printTrace If true, append stack trace to error string
     */
    public static void reportError(String error, boolean printTrace) {
        DriverStation.reportError(error, printTrace);
    }

    /**
     * Report error to Driver Station. Appends provided stack trace to error message.
     *
     * @param error The error to report.
     * @param stackTrace The stack trace to append
     */
    public static void reportError(String error, StackTraceElement[] stackTrace) {
        DriverStation.reportError(error, stackTrace);
    }

    /**
     * Report warning to Driver Station. Optionally appends Stack trace to warning message.
     *
     * @param warning The warning to report.
     * @param printTrace If true, append stack trace to warning string
     */
    public static void reportWarning(String warning, boolean printTrace) {
        DriverStation.reportWarning(warning, printTrace);
    }

    /**
     * Report warning to Driver Station. Appends provided stack trace to warning message.
     *
     * @param warning The warning to report.
     * @param stackTrace The stack trace to append
     */
    public static void reportWarning(String warning, StackTraceElement[] stackTrace) {
        DriverStation.reportWarning(warning, stackTrace);
    }

    /**
     * The state of one joystick button.
     *
     * @param stick The joystick to read.
     * @param button The button index.
     * @return The state of the joystick button.
     */
    public static boolean getStickButton(final int stick, final int button) {
        return DriverStation.getStickButton(stick, button);
    }

    /**
     * The state of one joystick button if available.
     *
     * @param stick The joystick to read.
     * @param button The button index.
     * @return The state of the joystick button, or false if the button is not available.
     */
    public static Optional<Boolean> getStickButtonIfAvailable(final int stick, final int button) {
        throw new UnsupportedOperationException();
    }

    /**
     * Whether one joystick button was pressed since the last check.
     *
     * @param stick The joystick to read.
     * @param button The button index.
     * @return Whether the joystick button was pressed since the last check.
     */
    public static boolean getStickButtonPressed(final int stick, final int button) {
        return DriverStation.getStickButtonPressed(stick, button);
    }

    /**
     * Whether one joystick button was released since the last check.
     *
     * @param stick The joystick to read.
     * @param button The button index, beginning at 0.
     * @return Whether the joystick button was released since the last check.
     */
    public static boolean getStickButtonReleased(final int stick, final int button) {
        return DriverStation.getStickButtonReleased(stick, button);
    }

    /**
     * Get the value of the axis on a joystick. This depends on the mapping of the joystick connected
     * to the specified port.
     *
     * @param stick The joystick to read.
     * @param axis The analog axis value to read from the joystick.
     * @return The value of the axis on the joystick.
     */
    public static double getStickAxis(int stick, int axis) {
        return DriverStation.getStickAxis(stick, axis);
    }

    /**
     * Get the state of a touchpad finger on the joystick.
     *
     * @param stick The joystick to read.
     * @param touchpad The touchpad to read.
     * @param finger The finger to read.
     * @return the state of the touchpad finger.
     */
    public static TouchpadFinger getStickTouchpadFinger(int stick, int touchpad, int finger) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get whether a touchpad finger on the joystick is available.
     *
     * @param stick The joystick to read.
     * @param touchpad The touchpad to read.
     * @param finger The finger to read.
     * @return whether the touchpad finger is available.
     */
    public static boolean getStickTouchpadFingerAvailable(int stick, int touchpad, int finger) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of the axis on a joystick if available. This depends on the mapping of the
     * joystick connected to the specified port.
     *
     * @param stick The joystick to read.
     * @param axis The analog axis value to read from the joystick.
     * @return The value of the axis on the joystick, or 0 if the axis is not available.
     */
    public static OptionalDouble getStickAxisIfAvailable(int stick, int axis) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the state of a POV on the joystick.
     *
     * @param stick The joystick to read.
     * @param pov The POV to read.
     * @return the angle of the POV.
     */
    public static POVDirection getStickPOV(int stick, int pov) {
        double angle = DriverStation.getStickPOV(stick, pov);
        if (angle == -1.0) {
            return POVDirection.CENTER;
        }
        for (POVDirection povDirection : EnumSet.allOf(POVDirection.class)) {
            Optional<Rotation2d> otherAngle = povDirection.getAngle();
            if (otherAngle.isEmpty()) {
                continue;
            }
            if (angle == otherAngle.get().getDegrees()) {
                return povDirection;
            }
        }
        return POVDirection.CENTER;
    }

    /**
     * The state of the buttons on the joystick.
     *
     * @param stick The joystick to read.
     * @return The state of the buttons on the joystick.
     */
    public static long getStickButtons(final int stick) {
        return DriverStation.getStickButtons(stick);
    }

    /**
     * Gets the maximum index of axes on a given joystick port.
     *
     * @param stick The joystick port number
     * @return The maximum index of axes on the indicated joystick
     */
    public static int getStickAxesMaximumIndex(int stick) {
        return DriverStation.getStickAxisCount(stick);
    }

    /**
     * Returns the available bitmask of axes on a given joystick port.
     *
     * @param stick The joystick port number
     * @return The number of axes available on the indicated joystick
     */
    public static int getStickAxesAvailable(int stick) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the maximum index of povs on a given joystick port.
     *
     * @param stick The joystick port number
     * @return The maximum index of povs on the indicated joystick
     */
    public static int getStickPOVsMaximumIndex(int stick) {
        return DriverStation.getStickPOVCount(stick);
    }

    /**
     * Returns the available bitmask of povs on a given joystick port.
     *
     * @param stick The joystick port number
     * @return The number of povs available on the indicated joystick
     */
    public static int getStickPOVsAvailable(int stick) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the maximum index of buttons on a given joystick port.
     *
     * @param stick The joystick port number
     * @return The maximum index of buttons on the indicated joystick
     */
    public static int getStickButtonsMaximumIndex(int stick) {
        return DriverStation.getStickButtonCount(stick);
    }

    /**
     * Gets the bitmask of buttons available.
     *
     * @param stick The joystick port number
     * @return The buttons available on the indicated joystick
     */
    public static long getStickButtonsAvailable(int stick) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the value of isGamepad on a joystick.
     *
     * @param stick The joystick port number
     * @return A boolean that returns the value of isGamepad
     */
    public static boolean getJoystickIsGamepad(int stick) {
        return DriverStation.getJoystickIsXbox(stick);
    }

    /**
     * Gets the value of type on a gamepad.
     *
     * @param stick The joystick port number
     * @return The value of type
     */
    public static int getJoystickGamepadType(int stick) {
        return DriverStation.getJoystickIsXbox(stick) ? 1 : 0;
    }

    /**
     * Gets the value of supported outputs on a joystick.
     *
     * @param stick The joystick port number
     * @return The value of supported outputs
     */
    public static int getJoystickSupportedOutputs(int stick) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the name of the joystick at a port.
     *
     * @param stick The joystick port number
     * @return The value of name
     */
    public static String getJoystickName(int stick) {
        return DriverStation.getJoystickName(stick);
    }

    /**
     * Returns if a joystick is connected to the Driver Station.
     *
     * <p>This makes a best effort guess by looking at the reported number of axis, buttons, and POVs
     * attached.
     *
     * @param stick The joystick port number
     * @return true if a joystick is connected
     */
    public static boolean isJoystickConnected(int stick) {
        return DriverStation.isJoystickConnected(stick);
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be enabled.
     *
     * @return True if the robot is enabled, false otherwise.
     */
    public static boolean isEnabled() {
        return DriverStation.isEnabled();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be disabled.
     *
     * @return True if the robot should be disabled, false otherwise.
     */
    public static boolean isDisabled() {
        return DriverStation.isDisabled();
    }

    /**
     * Gets a value indicating whether the Robot is e-stopped.
     *
     * @return True if the robot is e-stopped, false otherwise.
     */
    public static boolean isEStopped() {
        return DriverStation.isEStopped();
    }

    /**
     * Gets the current robot mode.
     *
     * <p>Note that this does not indicate whether the robot is enabled or disabled.
     *
     * @return robot mode
     */
    public static RobotMode getRobotMode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in
     * autonomous mode.
     *
     * @return True if autonomous mode should be enabled, false otherwise.
     */
    public static boolean isAutonomous() {
        return DriverStation.isAutonomous();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in
     * autonomous mode and enabled.
     *
     * @return True if autonomous should be set and the robot should be enabled.
     */
    public static boolean isAutonomousEnabled() {
        return DriverStation.isAutonomousEnabled();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in
     * operator-controlled mode.
     *
     * @return True if operator-controlled mode should be enabled, false otherwise.
     */
    public static boolean isTeleop() {
        return DriverStation.isTeleop();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in
     * operator-controller mode and enabled.
     *
     * @return True if operator-controlled mode should be set and the robot should be enabled.
     */
    public static boolean isTeleopEnabled() {
        return DriverStation.isTeleopEnabled();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in Utility
     * mode.
     *
     * @return True if utility mode should be enabled, false otherwise.
     */
    public static boolean isUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be running in Utility
     * mode and enabled.
     *
     * @return True if utility mode should be set and the robot should be enabled.
     */
    public static boolean isUtilityEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds an operating mode option. It's necessary to call publishOpModes() to make the added modes
     * visible to the driver station.
     *
     * @param mode robot mode
     * @param name name of the operating mode
     * @param group group of the operating mode
     * @param description description of the operating mode
     * @param textColor text color, or null for default
     * @param backgroundColor background color, or null for default
     * @return unique ID used to later identify the operating mode
     * @throws IllegalArgumentException if name is empty or an operating mode with the same robot mode
     *     and name already exists
     */
    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    public static long addOpMode(
        RobotMode mode,
        String name,
        String group,
        String description,
        Color textColor,
        Color backgroundColor
    ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds an operating mode option. It's necessary to call publishOpModes() to make the added modes
     * visible to the driver station.
     *
     * @param mode robot mode
     * @param name name of the operating mode
     * @param group group of the operating mode
     * @param description description of the operating mode
     * @return unique ID used to later identify the operating mode
     * @throws IllegalArgumentException if name is empty or an operating mode with the same name
     *     already exists
     */
    public static long addOpMode(RobotMode mode, String name, String group, String description) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds an operating mode option. It's necessary to call publishOpModes() to make the added modes
     * visible to the driver station.
     *
     * @param mode robot mode
     * @param name name of the operating mode
     * @param group group of the operating mode
     * @return unique ID used to later identify the operating mode
     * @throws IllegalArgumentException if name is empty or an operating mode with the same name
     *     already exists
     */
    public static long addOpMode(RobotMode mode, String name, String group) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds an operating mode option. It's necessary to call publishOpModes() to make the added modes
     * visible to the driver station.
     *
     * @param mode robot mode
     * @param name name of the operating mode
     * @return unique ID used to later identify the operating mode
     * @throws IllegalArgumentException if name is empty or an operating mode with the same name
     *     already exists
     */
    public static long addOpMode(RobotMode mode, String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes an operating mode option. It's necessary to call publishOpModes() to make the removed
     * mode no longer visible to the driver station.
     *
     * @param mode robot mode
     * @param name name of the operating mode
     * @return unique ID for the opmode, or 0 if not found
     */
    public static long removeOpMode(RobotMode mode, String name) {
        throw new UnsupportedOperationException();
    }

    /** Publishes the operating mode options to the driver station. */
    public static void publishOpModes() {
        throw new UnsupportedOperationException();
    }

    /** Clears all operating mode options and publishes an empty list to the driver station. */
    public static void clearOpModes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the program starting flag in the DS. This will also allow {@link #getOpModeId()} and
     * {@link #getOpMode()} to return values for the selected OpMode in the DS application, if the DS
     * is connected by the time this method is called.
     *
     * <p>Most users will not need to use this method; the {@link TimedRobot} and {@link OpModeRobot}
     * robot framework classes will call it automatically after the main robot class is instantiated.
     * However, teams using the commandsv3 library and a custom main robot class need to be careful to
     * only call this method after all mechanisms and global trigger bindings are set up. If not, any
     * setup performed in the main robot class may be incorrectly bound to the opmode selected in the
     * DS if it's connected by the time the robot program boots up.
     *
     * <p>This is what changes the DS to showing robot code ready.
     *
     * @see #getOpMode()
     * @see #getOpModeId()
     */
    public static void observeUserProgramStarting() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the operating mode selected on the driver station. Note this does not mean the robot is
     * enabled; use isEnabled() for that. In a match, this will indicate the operating mode selected
     * for auto before the match starts (i.e., while the robot is disabled in auto mode); after the
     * auto period ends, this will change to reflect the operating mode selected for teleop.
     *
     * <p>This method always returns {@code 0} while the main robot class is being constructed and
     * initialized (more specifically, it returns {@code 0} until {@link
     * #observeUserProgramStarting()} is called, which the WPILib framework will automatically call
     * during {@link TimedRobot#startCompetition()} and {@link OpModeRobot#startCompetition()}).
     *
     * @return the unique ID provided by the addOpMode() function; may return 0 or a unique ID not
     *     added, so callers should be prepared to handle that case
     */
    public static long getOpModeId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the operating mode selected on the driver station. Note this does not mean the robot is
     * enabled; use isEnabled() for that. In a match, this will indicate the operating mode selected
     * for auto before the match starts (i.e., while the robot is disabled in auto mode); after the
     * auto period ends, this will change to reflect the operating mode selected for teleop.
     *
     * <p>This method always returns an empty string {@code ""} while the main robot class is being
     * constructed and initialized (more specifically, it returns {@code ""} until {@link
     * #observeUserProgramStarting()} is called, which the WPILib framework will automatically call
     * during {@link TimedRobot#startCompetition()} and {@link OpModeRobot#startCompetition()}).
     *
     * @return Operating mode string; may return a string not in the list of options, so callers
     *     should be prepared to handle that case
     */
    public static String getOpMode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Check to see if the selected operating mode is a particular value. Note this does not mean the
     * robot is enabled; use isEnabled() for that.
     *
     * @param id operating mode unique ID
     * @return True if that mode is the current mode
     */
    public static boolean isOpMode(long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Check to see if the selected operating mode is a particular value. Note this does not mean the
     * robot is enabled; use isEnabled() for that.
     *
     * @param mode operating mode
     * @return True if that mode is the current mode
     */
    public static boolean isOpMode(String mode) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a value indicating whether the Driver Station is attached.
     *
     * @return True if Driver Station is attached, false otherwise.
     */
    public static boolean isDSAttached() {
        return DriverStation.isDSAttached();
    }

    /**
     * Gets if the driver station attached to a Field Management System.
     *
     * @return true if the robot is competing on a field being controlled by a Field Management System
     */
    public static boolean isFMSAttached() {
        return DriverStation.isFMSAttached();
    }

    /**
     * Get the game specific message from the FMS.
     *
     * <p>If the FMS is not connected, it is set from the game data setting on the driver station.
     *
     * @return the game specific message
     */
    public static Optional<String> getGameData() {
        String data = DriverStation.getGameSpecificMessage();
        return data.length() == 0 ? Optional.empty() : Optional.of(data);
    }

    /**
     * Get the event name from the FMS.
     *
     * @return the event name
     */
    public static String getEventName() {
        return DriverStation.getEventName();
    }

    /**
     * Get the match type from the FMS.
     *
     * @return the match type
     */
    public static MatchType getMatchType() {
        return DriverStation.getMatchType();
    }

    /**
     * Get the match number from the FMS.
     *
     * @return the match number
     */
    public static int getMatchNumber() {
        return DriverStation.getMatchNumber();
    }

    /**
     * Get the replay number from the FMS.
     *
     * @return the replay number
     */
    public static int getReplayNumber() {
        return DriverStation.getReplayNumber();
    }

    /**
     * Get the current alliance from the FMS.
     *
     * <p>If the FMS is not connected, it is set from the team alliance setting on the driver station.
     *
     * @return The alliance (red or blue) or an empty optional if the alliance is invalid
     */
    public static Optional<Alliance> getAlliance() {
        return DriverStation.getAlliance();
    }

    /**
     * Gets the location of the team's driver station controls from the FMS.
     *
     * <p>If the FMS is not connected, it is set from the team alliance setting on the driver station.
     *
     * @return the location of the team's driver station controls: 1, 2, or 3
     */
    public static OptionalInt getLocation() {
        return DriverStation.getLocation();
    }

    /**
     * Gets the raw alliance station of the teams driver station.
     *
     * <p>This returns the raw low level value. Prefer getLocation or getAlliance unless necessary for
     * performance.
     *
     * @return The raw alliance station id.
     */
    public static AllianceStationID getRawAllianceStation() {
        return DriverStation.getRawAllianceStation();
    }

    /**
     * Return the approximate match time. The FMS does not send an official match time to the robots,
     * but does send an approximate match time. The value will count down the time remaining in the
     * current period (auto or teleop). Warning: This is not an official time (so it cannot be used to
     * dispute ref calls or guarantee that a function will trigger before the match ends).
     *
     * <p>When connected to the real field, this number only changes in full integer increments, and
     * always counts down.
     *
     * <p>When the DS is in practice mode, this number is a floating point number, and counts down.
     *
     * <p>When the DS is in teleop or autonomous mode, this number returns -1.0.
     *
     * <p>Simulation matches DS behavior without an FMS connected.
     *
     * @return Time remaining in current match period (auto or teleop) in seconds
     */
    public static double getMatchTime() {
        return DriverStation.getMatchTime();
    }

    /**
     * Allows the user to specify whether they want joystick connection warnings to be printed to the
     * console. This setting is ignored when the FMS is connected -- warnings will always be on in
     * that scenario.
     *
     * @param silence Whether warning messages should be silenced.
     */
    public static void silenceJoystickConnectionWarning(boolean silence) {
        DriverStation.silenceJoystickConnectionWarning(silence);
    }

    /**
     * Returns whether joystick connection warnings are silenced. This will always return false when
     * connected to the FMS.
     *
     * @return Whether joystick connection warnings are silenced.
     */
    public static boolean isJoystickConnectionWarningSilenced() {
        return DriverStation.isJoystickConnectionWarningSilenced();
    }

    /**
     * Refresh the passed in control word to contain the current control word cache.
     *
     * @param word Word to refresh.
     */
    public static void refreshControlWordFromCache(ControlWord word) {
        DriverStation.refreshControlWordFromCache(word);
    }

    /**
     * Copy data from the DS task for the user. If no new data exists, it will just be returned,
     * otherwise the data will be copied from the DS polling loop.
     */
    public static void refreshData() {
        DriverStation.refreshData();
    }

    /**
     * Registers the given handle for DS data refresh notifications.
     *
     * @param handle The event handle.
     */
    public static void provideRefreshedDataEventHandle(int handle) {
        DriverStation.provideRefreshedDataEventHandle(handle);
    }

    /**
     * Unregisters the given handle from DS data refresh notifications.
     *
     * @param handle The event handle.
     */
    public static void removeRefreshedDataEventHandle(int handle) {
        DriverStation.removeRefreshedDataEventHandle(handle);
    }

    /**
     * Starts logging DriverStation data to data log. Repeated calls are ignored.
     *
     * @param log data log
     * @param logJoysticks if true, log joystick data
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static void startDataLog(DataLog log, boolean logJoysticks) {
        DriverStation.startDataLog(log, logJoysticks);
    }

    /**
     * Starts logging DriverStation data to data log, including joystick data. Repeated calls are
     * ignored.
     *
     * @param log data log
     */
    public static void startDataLog(DataLog log) {
        DriverStation.startDataLog(log);
    }
}
