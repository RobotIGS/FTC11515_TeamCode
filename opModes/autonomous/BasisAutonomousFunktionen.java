package org.firstinspires.ftc.teamcode.opModes.autonomous;

public class BasisAutonomousFunktionen extends BasisAutonomous {
    public void runOnce() {
        super.runOnce();
    }
    /* HIER KÖNNEN FUNKTIONEN HINZUGEFÜGT WERDEN, DIE IN ALLEN AUTONOMEN VARIANTEN GENUTZT WERDEN SOLLEN */
    public void schiessen(){
        hwMap.motor4.setPower(hwMap.POWER_SCHIESSEN);
        schleifeWarten(hwMap.SCHUSS_ANLAUFZEIT_MS);
        hwMap.motor3.setPower(hwMap.POWER_TRANSPORT);
    }
}