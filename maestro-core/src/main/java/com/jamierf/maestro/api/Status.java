package com.jamierf.maestro.api;

import com.google.common.base.Objects;

import java.nio.ByteBuffer;

public class Status {

    public static final int BYTE_LENGTH = 2 + 2 + 2 + 1;

    public static Status decode(ByteBuffer payload) {
        final int position = payload.getShort() & 0xFFFF;
        final int target = payload.getShort() & 0xFFFF;
        final int speed = payload.getShort() & 0xFFFF;
        final int acceleration = payload.get() & 0xFF;

        return new Status(position, target, speed, acceleration);
    }

    private final int position;
    private final int target;
    private final int speed;
    private final int acceleration;

    public Status(int position, int target, int speed, int acceleration) {
        this.position = position;
        this.target = target;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    public int getPosition() {
        return position;
    }

    public int getTarget() {
        return target;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAcceleration() {
        return acceleration;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("position", position)
                .add("target", target)
                .add("speed", speed)
                .add("acceleration", acceleration)
                .toString();
    }
}
