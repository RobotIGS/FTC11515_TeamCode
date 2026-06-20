package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.tools.HwMap;
import org.firstinspires.ftc.teamcode.tools.steuerung.Gegensteuern;

@TeleOp(name = "FullControl", group = "FTC")
public class FullControl extends BasisTeleOp {
    final Gegensteuern gegensteuernX = new Gegensteuern("X");
    final Gegensteuern gegensteuernY = new Gegensteuern("Y");
    public boolean zweiSticks = false;
    double altVx;
    double altVy;

    /* HIER KÖNNEN VARIABLEN HINZUGEFÜGT WERDEN */

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
        fahren();
        saison();
        /* HIER KÖNNEN FUNKTIONEN AUFGERUFEN WERDEN, DIE IN JEDEM SCHLEIFENDURCHLAUF AUSGEFÜHRT WERDEN SOLLEN */
        hwMap.navi.schritt();
        telemetrie();
    }

    @Override
    public void fahren() {
        if (istTasteGedrueckt("gp1_lb", gamepad1.left_bumper)) {
            hwMap.navi.sneak = !hwMap.navi.sneak;
        }
        if (istTasteGedrueckt("gp1_rb", gamepad1.right_bumper)) {
            hwMap.navi.fahreGegensteuern = !hwMap.navi.fahreGegensteuern;
        }

        double vx = -gamepad1.left_stick_y * (hwMap.navi.sneak ? hwMap.navi.geschwindigkeitSneak : hwMap.navi.geschwindigkeitNormal);
        double vy = (zweiSticks ? -gamepad1.right_stick_x : -gamepad1.left_stick_x) * (hwMap.navi.sneak ? hwMap.navi.geschwindigkeitSneak : hwMap.navi.geschwindigkeitNormal);
        double vz = (gamepad1.left_trigger - gamepad1.right_trigger) * hwMap.navi.geschwindigkeitDrehen * (hwMap.navi.sneak ? hwMap.navi.geschwindigkeitSneak : hwMap.navi.geschwindigkeitNormal);

        hwMap.navi.setGeschwindigkeit(
                gegensteuernX.calculate(hwMap.navi.fahreGegensteuern, altVx, vx),
                gegensteuernY.calculate(hwMap.navi.fahreGegensteuern, altVy, vy),
                vz);

        altVx = vx;
        altVy = vy;
    }

    @Override
    public void saison() {

    }

    @Override
    public void telemetrie() {
        telemetry.addData("Sneak", hwMap.navi.sneak ? "JA" : "NEIN");
        telemetry.addData("Gegensteuern", hwMap.navi.fahreGegensteuern ? "JA" : "NEIN");
        telemetry.addLine();
        telemetry.addLine(hwMap.navi.debug());
        telemetry.addLine(hwMap.chassis.debug());
        telemetry.addLine(gegensteuernX.debug());
        telemetry.addLine(gegensteuernY.debug());
        telemetry.update();
    }
}
