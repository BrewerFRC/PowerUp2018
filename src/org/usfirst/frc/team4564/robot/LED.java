package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.SerialPort;

class LED {
    // NEVER USE 255
    public static final int[] YELLOW = { 60, 254, 254 }, ORANGE = { 15, 254, 254 }, GREEN = { 90, 254, 251 };

    public static final int OFF = 0, AUTO = 1, TELEOP = 2, CLIMB_TIME = 3;

    public int mode = OFF;

    private SerialPort sp;

    public LED() {
        sp = new SerialPort(9600, SerialPort.Port.kUSB1);

    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void update() {
        switch (mode) {
        case OFF:
            sendColor(0, 0, 0);

            break;

        case TELEOP:
            sendColor(GREEN);

            break;

        }
    }

    public void sendColor(int H, int S, int V) {
        byte[] data = new byte[] { (byte) 0xFF, (byte) H, (byte) S, (byte) V };
        sp.write(data, 4);
    }

    public void sendColor(int[] hsv) {
        sendColor(hsv[0], hsv[1], hsv[2]);
    }

}