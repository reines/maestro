package com.jamierf.maestro.settings;

import com.jamierf.maestro.api.ChannelMode;
import com.jamierf.maestro.api.HomeMode;

public class ChannelSettings {

    public static final ChannelSettings DEFAULT_SETTINGS = ChannelSettings.builder().build();

    public static class Builder {

        private ChannelMode channelMode = ChannelMode.SERVO;
        private HomeMode homeMode = HomeMode.OFF;
        private int home = 6000;        // 1500us
        private int minimum = 3968;     // 4000;     // 1000us
        private int maximum = 8000;     // 2000us
        private int neutral = 6000;     // 1500us
        private int range = 1905;       // 2000;       // 500us
        private int speed = 0;          // unlimited
        private int acceleration = 0;   // unlimited

        public Builder setChannelMode(ChannelMode channelMode) {
            this.channelMode = channelMode;
            return this;
        }

        public Builder setHomeMode(HomeMode homeMode) {
            this.homeMode = homeMode;
            return this;
        }

        private Builder setHome(int home) {
            this.home = home;
            return this;
        }

        public Builder setMinimum(int minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder setMaximum(int maximum) {
            this.maximum = maximum;
            return this;
        }

        public Builder setNeutral(int neutral) {
            this.neutral = neutral;
            return this;
        }

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public Builder setAcceleration(int acceleration) {
            this.acceleration = acceleration;
            return this;
        }

        public ChannelSettings build() {
            return new ChannelSettings(channelMode, homeMode, home, minimum, maximum, neutral, range, speed, acceleration);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static byte normalSpeedToExponentialSpeed(int mantissa) {
        byte exponent = 0;

        while (true) {
            if (mantissa < 32) {
                // We have reached the correct representation.
                return (byte) (exponent + (mantissa << 3));
            }

            if (exponent == 7) {
                // The number is too big to express in this format.
                return (byte) 0xFF;
            }

            // Try representing the number with a bigger exponent.
            exponent += 1;
            mantissa >>= 1;
        }
    }

    // Type (servo, output, input)
    private final ChannelMode channelMode;

    // HomeType (off, ignore, goto)
    private final HomeMode homeMode;

    // Home position: the place to go on startup. If type is SERVO, units are 0.25 us (qus). If type is OUTPUT
    // the threshold between high and low is 1500. This value is only saved on the device if homeType is GOTO.
    private final int home;

    // Minimum (units of 0.25 us, but stored on the device in units of 16 us).
    private final int minimum;

    // Maximum (units of 0.25 us, but stored on the device in units of 16 us).
    private final int maximum;

    // Neutral: the center of the 8-bit set target command (value at 127). If type is SERVO, units are
    // 0.25 us (qus). If type is OUTPUT, the threshold between high and low is 1500.
    private final int neutral;

    // Range: the +/- extent of the 8-bit command.
    //   8-bit(254) = neutral + range,
    //   8-bit(0) = neutral - range
    // If type is SERVO units are 0.25 us (qus) (but stored on device in units of 127*0.25us = 31.75 us).
    // Range = 0-127*255 = 0-32385 qus.
    // Increment = 127 qus
    private final int range;

    // Speed: the maximum change in position (qus) per update.  0 means no limit.
    // Units depend on your settings.
    // Stored on device in this format: [0-31]*2^[0-7]
    // Range = 0-31*2^7 = 0-3968.
    // Increment = 1.
    //
    // Note that the *current speed* is stored on the device in units of qus, and so it is not subject to the
    // restrictions above! It can be any value 0-65535.
    private final int speed;

    // Acceleration: the max change in speed every 80 ms. 0 means no limit. Units depend on your settings.
    // Range = 0-255.
    // Increment = 1.
    private final int acceleration;

    public ChannelSettings(ChannelMode channelMode, HomeMode homeMode, int home, int minimum, int maximum, int neutral, int range, int speed, int acceleration) {
        this.channelMode = channelMode;
        this.homeMode = homeMode;
        this.home = home;
        this.minimum = minimum;
        this.maximum = maximum;
        this.neutral = neutral;
        this.range = range;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    public HomeMode getHomeMode() {
        // Ensure home mode is ignore for inputs
        if (channelMode == ChannelMode.INPUT)
            return HomeMode.IGNORE;

        return homeMode;
    }

    public int getHome() {
        final HomeMode mode = this.getHomeMode();
        switch (mode) {
            case OFF:
                return 0;
            case IGNORE:
                return 1;
            default:
                return home;
        }
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getNeutral() {
        return neutral;
    }

    public int getRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }

    public byte getExponentialSpeed() {
        return ChannelSettings.normalSpeedToExponentialSpeed(speed);
    }

    public int getAcceleration() {
        return acceleration;
    }

    @Override
    public String toString() {
        return "ChannelSettings{" +
                "channelMode=" + channelMode +
                ", homeMode=" + homeMode +
                ", home=" + home +
                ", minimum=" + minimum +
                ", maximum=" + maximum +
                ", neutral=" + neutral +
                ", range=" + range +
                ", speed=" + speed +
                ", acceleration=" + acceleration +
                '}';
    }
}
