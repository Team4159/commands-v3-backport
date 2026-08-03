// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.team4159.commandsv3backport.driverstation;

/** Interface for device wrappers backed by a GenericHID. */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface HIDDevice {
    /**
     * Get the underlying GenericHID object.
     *
     * @return the wrapped GenericHID object
     */
    GenericHID getHID();
}
