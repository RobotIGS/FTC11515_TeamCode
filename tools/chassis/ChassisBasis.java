package org.firstinspires.ftc.teamcode.tools.chassis;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.tools.HwMap;
import org.firstinspires.ftc.teamcode.tools.datentypen.Geschwindigkeit;
import org.firstinspires.ftc.teamcode.tools.datentypen.Position2D;
import org.firstinspires.ftc.teamcode.tools.datentypen.Rotation;

public abstract class ChassisBasis implements Chassis {
    // Reihenfolge der Räder überall: 0 = FL, 1 = FR, 2 = BL, 3 = BR
    // Namen müssen exakt so in der Robot-Konfiguration auf dem Driver Hub stehen
    public static final String[] RAD_MOTOR_NAMEN = {
            "front_left_motor", "front_right_motor", "back_left_motor", "back_right_motor"
    };
    // Drehrichtungen so wählen, dass positive Geschwindigkeit das Rad VORWÄRTS dreht
    // (prüfen mit dem OpMode "TestZuordnungMotoren")
    public static final DcMotorEx.Direction[] RAD_MOTOR_RICHTUNGEN = {
            DcMotorEx.Direction.REVERSE, // FL
            DcMotorEx.Direction.FORWARD, // FR
            DcMotorEx.Direction.FORWARD, // BL
            DcMotorEx.Direction.REVERSE  // BR
    };

    protected final RevHubOrientationOnRobot hubAusrichtung;
    protected final Position2D gefahreneDistanz;
    protected final double[] radGeschwindigkeiten;
    protected final int[] deltaRadMotorSchritte;
    private final DcMotorEx[] radMotoren;
    private final int[] radMotorSchritte;
    private final Rotation rotation;
    public IMU imu;
    protected double[] motorWerte;
    protected Geschwindigkeit geschwindigkeit;
    private double startRotation;

    public ChassisBasis(int anzahlRaeder, RevHubOrientationOnRobot hubAusrichtung) {
        // Rotations-Einstellungen
        this.hubAusrichtung = hubAusrichtung;
        this.startRotation = 0;
        this.rotation = new Rotation(0.0);

        // Fahr-Einstellungen
        this.gefahreneDistanz = new Position2D();
        this.radMotoren = new DcMotorEx[anzahlRaeder];
        this.radGeschwindigkeiten = new double[anzahlRaeder];
        this.radMotorSchritte = new int[anzahlRaeder];
        this.deltaRadMotorSchritte = new int[anzahlRaeder];
    }

    @Override
    public void setMotorWerte(double[] motorWerte) {
        this.motorWerte = motorWerte;
    }

    @Override
    public void erstelleMotorArray(HardwareMap hwMap) {
        for (int i = 0; i < this.radMotoren.length; i++) {
            radMotoren[i] = hwMap.get(DcMotorEx.class, RAD_MOTOR_NAMEN[i]);
            radGeschwindigkeiten[i] = 0.0;
            radMotoren[i].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            radMotoren[i].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            radMotoren[i].setDirection(RAD_MOTOR_RICHTUNGEN[i]);
            radMotorSchritte[i] = 0;
            deltaRadMotorSchritte[i] = 0;
        }

        imu = hwMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(this.hubAusrichtung));
        imu.resetYaw();
    }

    private void setzeMotorGeschwindigkeiten() {
        for (int i = 0; i < radMotoren.length; i++) {
            radMotoren[i].setVelocity(radGeschwindigkeiten[i] * HwMap.MotorWerte.ticksProSekundeErrechnen(motorWerte));
        }
    }

    @Override
    public void setGeschwindigkeit(Geschwindigkeit geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }

    @Override
    public Position2D getGefahreneDistanz() {
        return gefahreneDistanz;
    }

    @Override
    public void stoppeMotoren() {
        setGeschwindigkeit(new Geschwindigkeit());
        setzeMotorGeschwindigkeiten();
    }

    private void updateMotorSchritte() {
        int schritte;
        for (int i = 0; i < deltaRadMotorSchritte.length; i++) {
            schritte = radMotoren[i].getCurrentPosition();
            deltaRadMotorSchritte[i] = schritte - radMotorSchritte[i];
            radMotorSchritte[i] = schritte;
        }
    }

    @Override
    public double getRotation() {
        return rotation.get();
    }

    @Override
    public void setStartRotation(double startRotation) {
        this.startRotation = -getRohRotation() + startRotation;
    }

    private double getRohRotation() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private void berechneRotation() {
        rotation.set(getRohRotation() + startRotation);
    }

    @Override
    public void schritt() {
        berechneRotation();
        setzeMotorGeschwindigkeiten();
        updateMotorSchritte();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String debug() {
        StringBuilder ret = new StringBuilder("--- Chassis ---\n");
        ret.append(String.format("Gefahrene Distanz: x=%+2.2f y=%+2.2f\n", gefahreneDistanz.getX(), gefahreneDistanz.getY()));
        for (int i = 0; i < radMotoren.length; i++) {
            ret.append(String.format("Rad %d: v=%+1.2f  Schritte=%+5d  Delta Schritte=%+3d\n", i, radGeschwindigkeiten[i], radMotoren[i].getCurrentPosition(), deltaRadMotorSchritte[i]));
        }
        return ret.toString();
    }
}
