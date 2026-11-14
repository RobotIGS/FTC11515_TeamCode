package org.firstinspires.ftc.teamcode.opModes.teleOp.testing;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.opModes.teleOp.BasisTeleOp;
import org.firstinspires.ftc.teamcode.tools.HwMap;
import org.firstinspires.ftc.teamcode.tools.chassis.ChassisBasis;

@TeleOp(name = "TestZuordnungMotoren", group = "TESTING")
public class TestZuordnungMotoren extends BasisTeleOp {
    private static final double TEST_POWER = 0.3;
    private static final double[] MOTOR_WERTE = HwMap.MotorWerte.M_435_RPM;
    private static final String[] KURZNAMEN = {"FL", "FR", "BL", "BR"};

    private static final double MIN_ENCODER_TICKS_PRO_S = 20;

    private final DcMotorEx[] motoren = new DcMotorEx[4];
    private final double[] power = new double[4];

    @Override
    public void initialisieren() {
        super.initialisieren();
        for (int i = 0; i < 4; i++) {
            motoren[i] = hardwareMap.get(DcMotorEx.class, ChassisBasis.RAD_MOTOR_NAMEN[i]);
            motoren[i].setDirection(ChassisBasis.RAD_MOTOR_RICHTUNGEN[i]);
            motoren[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
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

        boolean[] gedrueckt = {gamepad1.dpad_up, gamepad1.y, gamepad1.dpad_down, gamepad1.a};
        for (int i = 0; i < 4; i++) {
            power[i] = gedrueckt[i] ? TEST_POWER : 0.0;
        }

        for (int i = 0; i < 4; i++) {
            motoren[i].setPower(power[i]);
        }

        telemetrie();
    }

    private void encoderZuruecksetzen() {
        for (DcMotorEx motor : motoren) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private String encoderBewertung(int i, double geschwindigkeit) {
        if (power[i] == 0.0) {
            return "-";
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
        telemetry.addLine("DPad Up/Dpad Down = FL/BL\nY/A = FR/BR");
        telemetry.addLine("Back = Encoder auf 0");
        telemetry.addLine();

        for (int i = 0; i < 4; i++) {
            int schritte = motoren[i].getCurrentPosition();
            double geschwindigkeit = motoren[i].getVelocity();

            telemetry.addLine(String.format("%s: Power=%+.2f  Schritte=%+6d  Umdr.=%+.2f  v=%+6.0f t/s",
                    KURZNAMEN[i], power[i], schritte, schritte / MOTOR_WERTE[1], geschwindigkeit));
            telemetry.addLine("   Encoder: " + encoderBewertung(i, geschwindigkeit));
        }
        telemetry.update();
    }

    @Override
    public void beenden() {
        for (DcMotorEx motor : motoren) {
            motor.setPower(0);
        }
    }
}
