package org.firstinspires.ftc.teamcode.tools;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.tools.chassis.Chassis;
import org.firstinspires.ftc.teamcode.tools.chassis.MecanumChassis;
import org.firstinspires.ftc.teamcode.tools.datentypen.Position2D;
import org.firstinspires.ftc.teamcode.tools.steuerung.BeschleunigungsProfil;
import org.firstinspires.ftc.teamcode.tools.steuerung.Navigation;
import org.firstinspires.ftc.teamcode.tools.steuerung.PidRegler;
public class HwMap {
    public final Navigation navi;
    public final Chassis chassis;
    public final VoltageSensor batterieSpannungsSensor;
    /* HIER KÖNNEN VARIABLEN UND HARDWARE TEILE HINZUGEFÜGT WERDEN */
    public final DcMotor motor1; // Einsammeln
    public final DcMotor motor2; // Einsammeln
    public final DcMotor motor3; // Ball hochtransportieren
    public final DcMotor motor4; // Schießen
    public HwMap(com.qualcomm.robotcore.hardware.HardwareMap hardwareMap) {
        // vx Vorwärtsgeschwindigkeit (+ => vorwärts)
        // vy Seitwärtsgeschwindigkeit (+ => links)
        // vz Rotationsgeschwindigkeit (+ => nach links drehen => mathematisch positiv)
        chassis = new MecanumChassis(17, 17, new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD, RevHubOrientationOnRobot.UsbFacingDirection.UP));
        chassis.erstelleMotorArray(hardwareMap);
        chassis.setStartRotation(0.0);
        chassis.setMotorWerte(MotorWerte.M_435_RPM);
        navi = new Navigation(chassis, new Position2D(0.0, 0.0), new PidRegler(0.55, 0.01, 0.0));
        navi.setHalteRotation(false);
        navi.setGeschwindigkeitNormal(0.5);
        navi.setGeschwindigkeitSchleichend(0.3);
        navi.setGeschwindigkeitDrehen(1.0);
        navi.setGeschwindigkeitAuto(0.75);
        navi.setBeschleunigungsProfil(new BeschleunigungsProfil(30, 1));
        navi.setRotationsGenauigkeit(1.0); // in Grad
        navi.setFahrGenauigkeit(0.5); // in cm
        batterieSpannungsSensor = hardwareMap.voltageSensor.iterator().next();
        /* HIER KANN HARDWARE HINZUGEFÜGT WERDEN */
        // Namen müssen exakt so in der Robot-Konfiguration auf dem Driver Hub stehen
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor3 = hardwareMap.get(DcMotor.class, "motor3");
        motor4 = hardwareMap.get(DcMotor.class, "motor4");
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Drehrichtung: Falls ein Motor falsch herum läuft, bei genau diesem
        // Motor FORWARD <-> REVERSE tauschen.
        motor1.setDirection(DcMotor.Direction.FORWARD);
        motor2.setDirection(DcMotor.Direction.REVERSE);
        motor3.setDirection(DcMotor.Direction.FORWARD);
        motor4.setDirection(DcMotor.Direction.FORWARD);
        // Motoren ausrollen lassen statt hart bremsen (für Schussmotor sinnvoll)
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
    public static class MotorWerte {
        public static final double[] M_30_RPM = new double[]{30, 5281.1};
        public static final double[] M_117_RPM = new double[]{117, 1425.1};
        public static final double[] M_223_RPM = new double[]{223, 751.8};
        public static final double[] M_435_RPM = new double[]{435.0, 384.5};
        public static final double[] M_6000_RPM = new double[]{6000, 28};
        public static double ticksProSekundeErrechnen(double[] werte) {
            return (werte[0] / 60.0) * werte[1];
        }
    }
}

