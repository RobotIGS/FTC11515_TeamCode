package org.firstinspires.ftc.teamcode.opModes.autonomous;

import static org.firstinspires.ftc.teamcode.tools.steuerung.Navigation.PLATTENLAENGE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.tools.datentypen.Position2D;

@Autonomous(name = "AutoKorbZone", group = "Standard", preselectTeleOp = "FullControl")
public class AutoKorbZone extends BasisAutonomousFunktionen{
    @Override
    public void runOnce() {
        super.runOnce();
        hwMap.navi.setHalteRotation(false);
        schiessen();

        hwMap.navi.setzeZielPosition(new Position2D(3 * PLATTENLAENGE, 0), true);
        schleifeFahren();
        hwMap.navi.setzeZielPosition(new Position2D(0, PLATTENLAENGE), true);
        schleifeFahren();
    }
}