package org.firstinspires.ftc.teamcode.opModes.teleOp.testing;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.opModes.teleOp.BasisTeleOp;
import org.firstinspires.ftc.teamcode.tools.HwMap;
import org.firstinspires.ftc.teamcode.tools.chassis.ChassisBasis;

/**
 * Test für die Rad-Motoren: Zuordnung (welcher Name steckt an welchem Rad),
 * Drehrichtung und Encoder.
 *
 * ROBOTER AUFBOCKEN, sodass die Räder frei drehen!
 *
 * Einzelne Räder (Taste gedrückt halten), Anordnung wie am Roboter:
 *   Steuerkreuz hoch  = FL (vorne links)     Y = FR (vorne rechts)
 *   Steuerkreuz runter = BL (hinten links)   A = BR (hinten rechts)
 *   zusätzlich linker Bumper halten = rückwärts
 *   Erwartung: das richtige Rad dreht sich so, dass der Roboter VORWÄRTS fahren würde.
 *
 * Fahrmuster (direkt auf die Motoren, ohne Mecanum-Rechnung):
 *   rechter Bumper = nächstes Muster wählen, rechten Trigger halten = Muster fahren
 *
 * Encoder:
 *   Back = Encoder auf 0 setzen
 *   Ohne Strom lassen sich die Räder von Hand drehen: eine Radumdrehung vorwärts
 *   muss ca. +1.00 Umdrehungen anzeigen.
 */
@TeleOp(name = "TestZuordnungMotoren", group = "Testing")
public class TestZuordnungMotoren extends BasisTeleOp {
    private static final double TEST_POWER = 0.3;
    private static final double[] MOTOR_WERTE = HwMap.MotorWerte.M_435_RPM;
    private static final String[] KURZNAMEN = {"FL", "FR", "BL", "BR"};

    // Vorzeichen pro Rad (FL, FR, BL, BR), passend zur Matrix in MecanumChassis
    private static final String[] MUSTER_NAMEN = {"Vorwärts", "Seitlich links", "Drehen links"};
    private static final double[][] MUSTER = {
            {+1, +1, +1, +1},
            {-1, +1, +1, -1},
            {-1, +1, -1, +1}
    };

    // Ab dieser Laufzeit wird der Encoder bewertet (Motor muss erst anlaufen)
    private static final double ANLAUFZEIT_MS = 400;
    // Darunter gilt der Encoder als "zählt nicht" (Ticks pro Sekunde)
    private static final double MIN_ENCODER_TICKS_PRO_S = 20;

    private final DcMotorEx[] motoren = new DcMotorEx[4];
    private final double[] power = new double[4];
    private final ElapsedTime[] laufzeit = new ElapsedTime[4];
    private int musterIndex = 0;

    @Override
    public void initialisieren() {
        super.initialisieren();
        // bewusst ohne HwMap/Navigation: nur die Rad-Motoren, genau wie im Chassis eingestellt
        for (int i = 0; i < 4; i++) {
            motoren[i] = hardwareMap.get(DcMotorEx.class, ChassisBasis.RAD_MOTOR_NAMEN[i]);
            motoren[i].setDirection(ChassisBasis.RAD_MOTOR_RICHTUNGEN[i]);
            motoren[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // von Hand drehbar
            laufzeit[i] = new ElapsedTime();
        }
        encoderZuruecksetzen();
        telemetry.addLine("Roboter aufbocken! Dann Play drücken.");
        telemetry.update();
    }

    @Override
    public void runOnce() {
    }

    @Override
    public void runLoop() {
        if (istTasteGedrueckt("gp1_back", gamepad1.back)) {
            encoderZuruecksetzen();
        }
        if (istTasteGedrueckt("gp1_rb", gamepad1.right_bumper)) {
            musterIndex = (musterIndex + 1) % MUSTER.length;
        }

        double[] neu = new double[4];
        if (gamepad1.right_trigger > 0.5) {
            for (int i = 0; i < 4; i++) {
                neu[i] = MUSTER[musterIndex][i] * TEST_POWER;
            }
        } else {
            double richtung = gamepad1.left_bumper ? -1 : 1;
            boolean[] gedrueckt = {gamepad1.dpad_up, gamepad1.y, gamepad1.dpad_down, gamepad1.a};
            for (int i = 0; i < 4; i++) {
                neu[i] = gedrueckt[i] ? richtung * TEST_POWER : 0.0;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (neu[i] != power[i]) {
                laufzeit[i].reset(); // Anlaufzeit neu starten, wenn sich der Befehl ändert
            }
            power[i] = neu[i];
            motoren[i].setPower(power[i]);
        }

        telemetrie();
    }

    private void encoderZuruecksetzen() {
        for (DcMotorEx motor : motoren) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // ohne Encoder-Regelung: ein defekter Encoder führt so nicht zum Durchdrehen
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    private String encoderBewertung(int i, double geschwindigkeit) {
        if (power[i] == 0.0) {
            return "-";
        }
        if (laufzeit[i].milliseconds() < ANLAUFZEIT_MS) {
            return "läuft an...";
        }
        if (Math.abs(geschwindigkeit) < MIN_ENCODER_TICKS_PRO_S) {
            return "KEIN ENCODER-SIGNAL (Kabel?)";
        }
        if (Math.signum(geschwindigkeit) != Math.signum(power[i])) {
            return "ENCODER ZÄHLT FALSCH HERUM";
        }
        return "OK";
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void telemetrie() {
        telemetry.addLine("Hoch/Runter = FL/BL | Y/A = FR/BR | LB = rückwärts");
        telemetry.addData("Muster (RB wählen, RT halten)", MUSTER_NAMEN[musterIndex]);
        telemetry.addData("  erwartet", musterBeschreibung(musterIndex));
        telemetry.addLine("Back = Encoder auf 0");
        telemetry.addLine();

        for (int i = 0; i < 4; i++) {
            int schritte = motoren[i].getCurrentPosition();
            double geschwindigkeit = motoren[i].getVelocity();
            telemetry.addLine(String.format("%s (%s, %s)",
                    KURZNAMEN[i], ChassisBasis.RAD_MOTOR_NAMEN[i], ChassisBasis.RAD_MOTOR_RICHTUNGEN[i]));
            telemetry.addLine(String.format("   Power=%+.2f  Schritte=%+6d  Umdr.=%+.2f  v=%+6.0f t/s",
                    power[i], schritte, schritte / MOTOR_WERTE[1], geschwindigkeit));
            telemetry.addLine("   Encoder: " + encoderBewertung(i, geschwindigkeit));
        }
        telemetry.update();
    }

    private String musterBeschreibung(int index) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            ret.append(KURZNAMEN[i]).append(MUSTER[index][i] > 0 ? " vor  " : " rück  ");
        }
        return ret.toString();
    }

    @Override
    public void beenden() {
        // nicht super.beenden(): ohne HwMap gibt es keine Navigation
        for (DcMotorEx motor : motoren) {
            motor.setPower(0);
        }
    }
}
