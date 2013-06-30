package com.jamierf.maestro.api;

public enum Parameter {
    INITIALIZED(0, Range.u8),                               // 1 byte - 0 or 0xFF

    SERVOS_AVAILABLE(1, Range.u8),                          // 1 byte - 0-5
    SERVO_PERIOD(2, Range.u8),                              // 1 byte - ticks allocated to each servo/256

    SERIAL_MODE(3, new Range(1, 0, 3)),                     // 1 byte unsigned value.  Valid values are SERIAL_MODE_*.  Init variable.
    SERIAL_FIXED_BAUD_RATE(4, Range.u16),                   // 2-byte unsigned value; 0 means autodetect.  Init parameter.
    SERIAL_TIMEOUT(6, Range.u16),                           // 2-byte unsigned value
    SERIAL_ENABLE_CRC(8, Range.b),                          // 1 byte boolean value
    SERIAL_NEVER_SUSPEND(9, Range.b),                       // 1 byte boolean value
    SERIAL_DEVICE_NUMBER(10, new Range(1, 0, 127)),         // 1 byte unsigned value, 0-127
    SERIAL_BAUD_DETECT_TYPE(11, new Range(1, 0, 1)),        // 1 byte value
    SERIAL_MINI_SSC_OFFSET(25, new Range(1, 0, 254)),       // 1 byte (0-254)

    MINI_MAESTRO_SERVO_PERIOD_L(18, Range.u24),             // servo period: 3-byte unsigned values, units of quarter microseconds
    MINI_MAESTRO_SERVO_PERIOD_HU(19, Range.u24),

    ENABLE_PULLUPS(21, Range.b),                            // 1 byte: 0 or 1

    IO_MASK_C(16, Range.u8),                                // 1 byte - pins used for I/O instead of servo
    OUTPUT_MASK_C(17, Range.u8),                            // 1 byte - outputs that are enabled

    SCRIPT_DONE(24, Range.b),                               // 1 byte - copied to scriptDone on startup

    SERVO_HOME(30, new Range(2, 0, 32440)),                 // 2 byte home position (0=off; 1=ignore)
    SERVO_MIN(32, Range.u8),                                // 1 byte min allowed value (x2^6)
    SERVO_MAX(33, Range.u8),                                // 1 byte max allowed value (x2^6)
    SERVO_NEUTRAL(34, new Range(2, 0, 32440)),              // 2 byte neutral position
    SERVO_RANGE(36, new Range(1, 1, 50)),                   // 1 byte range
    SERVO_SPEED(37, Range.u8),                              // 1 byte (5 mantissa,3 exponent) us per 10ms
    SERVO_ACCELERATION(38, Range.u8);                       // 1 byte (speed changes that much every 10ms)

    public static class Range {

        public static Range u32 = new Range(4, 0, 0x7FFFFFFF);
        public static Range u24 = new Range(3, 0, 0xFFFFFF);
        public static Range u16 = new Range(2, 0, 0xFFFF);
        public static Range u8 = new Range(1, 0, 0xFF);
        public static Range b = new Range(1, 0, 1);

        private final byte bytes;
        private final int minimum;
        private final int maximum;

        public Range(int bytes, int minimum, int maximum) {
            this.bytes = (byte) bytes;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public byte getBytes() {
            return bytes;
        }

        public int getMinimum() {
            return minimum;
        }

        public int getMaximum() {
            return maximum;
        }
    }

    private final byte code;
    private final Range range;

    private Parameter(int code, Range range) {
        this.code = (byte) code;
        this.range = range;
    }

    public byte getCode() {
        return code;
    }

    public Range getRange() {
        return range;
    }
}
