package org.firstinspires.ftc.teamcode.opModes.teleOp.testing;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.opModes.teleOp.BasisTeleOp;
import org.firstinspires.ftc.teamcode.tools.HwMap;
import org.firstinspires.ftc.teamcode.tools.steuerung.Gegensteuern;

@TeleOp(name = "FullControl", group = "FTC")
public class TestZuordnungMotoren extends BasisTeleOp {
    @Override
    public void initialisieren() {
        super.initialisieren();
        hwMap = new HwMap(hardwareMap);
        /* HIER KANN CODE HINZUGEFÜGT WERDEN, DER AUSGEFÜHRT WIRD, WENN MAN INIT DRÜCKT */
    }
    @Override
    public void runOnce() {
        /* HIER KANN CODE HINZUGEFÜGT WERDEN, DER AUSGEFÜHRT WIRD, WENN MAN PLAY DRÜCKT */
    }
    @Override
    public void runLoop() {
        if (gamepad1.a) {

        }

        /* HIER KÖNNEN FUNKTIONEN AUFGERUFEN WERDEN, DIE IN JEDEM SCHLEIFENDURCHLAUF AUSGEFÜHRT WERDEN SOLLEN */
        hwMap.navi.schritt();
        telemetrie();
    }
    @Override
    public void telemetrie() {
        telemetry.addLine(hwMap.navi.debug());
        telemetry.addLine(hwMap.chassis.debug());
        telemetry.update();
    }
    @Override
    public void beenden() {
        super.beenden();
        hwMap.motor1.setPower(0);
        hwMap.motor2.setPower(0);
        hwMap.motor3.setPower(0);
        hwMap.motor4.setPower(0);
    }
}
