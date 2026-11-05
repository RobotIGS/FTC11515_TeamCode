package org.firstinspires.ftc.teamcode.opModes.teleOp;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
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
    // ---- Einstellungen (hier anpassen) ----
    final double POWER_EINSAMMELN = 1.0;      // Motor 1 + 2
    final double POWER_TRANSPORT = 0.8;       // Motor 3
    final double POWER_SCHIESSEN = 1.0;       // Motor 4
    final int SCHUSS_ANLAUFZEIT_MS = 800;     // so lange läuft Motor 4 allein, bevor Motor 3 startet (0 = gleichzeitig)
    // ---- Zustände ----
    boolean einsammelnAn = false;
    boolean schiessenAn = false;
    final ElapsedTime schussTimer = new ElapsedTime();
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
        // Mechanismus 1: Einsammeln (Motor 1 + 2) -> Gamepad 2, Taste A
        if (istTasteGedrueckt("gp2_a", gamepad2.a)) {
            einsammelnAn = !einsammelnAn;
        }
        double einsammelnPower = einsammelnAn ? POWER_EINSAMMELN : 0.0;
        hwMap.motor1.setPower(einsammelnPower);
        hwMap.motor2.setPower(einsammelnPower);
        // Mechanismus 2: Schießen (Motor 4) + Hochtransport (Motor 3) -> Gamepad 2, Taste B
        if (istTasteGedrueckt("gp2_b", gamepad2.b)) {
            schiessenAn = !schiessenAn;
            if (schiessenAn) {
                schussTimer.reset(); // Anlaufzeit beginnt jetzt
            }
        }
        // Motor 4 dreht sofort hoch
        hwMap.motor4.setPower(schiessenAn ? POWER_SCHIESSEN : 0.0);
        // Motor 3 startet erst, wenn die Anlaufzeit vorbei ist
        boolean transportAn = schiessenAn && schussTimer.milliseconds() >= SCHUSS_ANLAUFZEIT_MS;
        hwMap.motor3.setPower(transportAn ? POWER_TRANSPORT : 0.0);
    }
    @Override
    public void telemetrie() {
        telemetry.addData("Einsammeln (A)", einsammelnAn ? "AN" : "AUS");
        telemetry.addData("Schießen (B)", schiessenAn ? "AN" : "AUS");
        telemetry.addData("Transport", (schiessenAn && schussTimer.milliseconds() >= SCHUSS_ANLAUFZEIT_MS) ? "AN" : "AUS");
        telemetry.addLine();
        telemetry.addData("Sneak", hwMap.navi.sneak ? "JA" : "NEIN");
        telemetry.addData("Gegensteuern", hwMap.navi.fahreGegensteuern ? "JA" : "NEIN");
        telemetry.addLine();
        telemetry.addLine(hwMap.navi.debug());
        telemetry.addLine(hwMap.chassis.debug());
        telemetry.addLine(gegensteuernX.debug());
        telemetry.addLine(gegensteuernY.debug());
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
