package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "FullControl 2 Sticks", group = "FTC")
public class FullControl2Sticks extends FullControl {
    @Override
    public void initialisieren() {
        super.initialisieren();
        zweiSticks = true;
    }
}
