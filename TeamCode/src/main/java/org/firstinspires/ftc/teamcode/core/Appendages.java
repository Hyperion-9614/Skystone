package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Manages all appendages (servos, arms, etc.) on robot
 */

public class Appendages {

    public Hardware hardware;
    public int slidesSaveTicks;

    public String compWheelsStatus = "";
    public String foundationMoverStatus = "";
    public String chainBarStatus = "";
    public String clawStatus = "";
    public String capstoneStatus = "";

    public Appendages(Hardware hw) {
        this.hardware = hw;
        slidesSaveTicks = hw.constants.SLIDES_PRESET_START_TICKS;

        resetVerticalSlideEncoders();

        hw.compWheelsL.setDirection(DcMotorSimple.Direction.REVERSE);
        hw.foundationMoverL.setDirection(Servo.Direction.REVERSE);
        hw.chainBarL.setDirection(Servo.Direction.REVERSE);

        setCompWheelsStatus("stop");
        setFoundationMoverStatus("up");
        setClawStatus("open");
        setChainBarStatus("up");
        setCapstoneStatus("up");
    }

    //////////////////////// APPENDAGE CONTROL //////////////////////////

    public void setVerticalSlidePower(double power) {
        hardware.vertSlideL.setPower(power);
        hardware.vertSlideR.setPower(power);
    }

    public void setVerticalSlideMode(DcMotor.RunMode mode) {
        hardware.vertSlideL.setMode(mode);
        hardware.vertSlideR.setMode(mode);
    }

    public void setVerticalSlideTarget(int target) {
        hardware.vertSlideL.setTargetPosition(target);
        hardware.vertSlideR.setTargetPosition(target);
    }

    public void setVerticalSlidePosition(int target) {
        setVerticalSlideTarget(target);
        setVerticalSlideMode(DcMotor.RunMode.RUN_TO_POSITION);

        setVerticalSlidePower(1);
        ElapsedTime timer = new ElapsedTime();
        while ((hardware.vertSlideL.isBusy() || hardware.vertSlideR.isBusy()) && timer.milliseconds() <= 4000
                && (hardware.context.gamepad1.left_stick_x == 0 && hardware.context.gamepad1.left_stick_y == 0 && hardware.context.gamepad1.right_stick_x == 0)) {

        }

        setVerticalSlidePower(0);
        setVerticalSlideMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetVerticalSlideEncoders() {
        setVerticalSlideMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setVerticalSlideMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setCompWheelsPower(double power) {
        hardware.compWheelsL.setPower(power);
        hardware.compWheelsR.setPower(power);
    }

    public void setCompWheelsStatus(String inOffOut) {
        compWheelsStatus = inOffOut.toLowerCase();
        double power = 0;
        switch (compWheelsStatus) {
            case "in":
                power = -0.65;
                break;
            case "out":
                power = 0.65;
                break;
        }
        setCompWheelsPower(power);
    }

    public void setFoundationMoverStatus(String downUp) {
        foundationMoverStatus = downUp.toLowerCase();
        if (foundationMoverStatus.equals("down")) {
            hardware.foundationMoverL.setPosition(0.95);
            hardware.foundationMoverR.setPosition(0.95);
        } else {
            hardware.foundationMoverL.setPosition(0.3);
            hardware.foundationMoverR.setPosition(0.3);
        }
        hardware.context.sleep(500);
    }

    public void setChainBarPosition(double position) {
        hardware.chainBarL.setPosition(position);
        hardware.chainBarR.setPosition(position);
        hardware.context.sleep(500);
    }

    public void setChainBarStatus(String upInOut) {
        chainBarStatus = upInOut.toLowerCase();
        if (chainBarStatus.equals("up")) {
            setClawStatus("open");
            setChainBarPosition(0.75);
        } else if (chainBarStatus.equals("in")) {
            setChainBarPosition(0.9);
        } else if (chainBarStatus.equals("out")) {
            setChainBarPosition(0.45);
        }
        hardware.context.sleep(500);
    }

    public void cycleChainBar() {
        switch (chainBarStatus) {
            case "up":
                setChainBarStatus("in");
                break;
            case "in":
                setChainBarStatus("out");
                break;
            case "out":
                setChainBarStatus("up");
                break;
        }
    }

    public void setClawStatus(String openClosed) {
        clawStatus = openClosed.toLowerCase();
        if (clawStatus.equals("open")) {
            hardware.claw.setPower(-1.0);
        } else {
            hardware.claw.setPower(1.0);
        }
        hardware.context.sleep(500);
    }

    public void setCapstoneStatus(String upDown) {
        capstoneStatus = upDown.toLowerCase();
        if (capstoneStatus.equals("up")) {
            hardware.capstone.setPosition(0.25);
        } else {
            hardware.capstone.setPosition(0.45);
        }
    }

}
