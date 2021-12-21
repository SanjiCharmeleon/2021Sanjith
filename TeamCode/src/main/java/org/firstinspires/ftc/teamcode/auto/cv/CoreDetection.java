package org.firstinspires.ftc.teamcode.auto.cv;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class CoreDetection extends OpenCvPipeline {
    Telemetry telemetry;
    Mat mat = new Mat();
    public enum Location {
        LEFT,
        RIGHT,
        MID,
        NOT_FOUND
    }
    private Location location;

    static final Rect MID_ROI = new Rect(
            new Point(700, 400),
            new Point(1000, 600));
    static final Rect LEFT_ROI = new Rect(
            new Point(140,400),
            new Point(420,600));


    static double PERCENT_COLOR_THRESHOLD = 0.4;

    public CoreDetection (Telemetry t) { telemetry = t; }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        Scalar lowHSV = new Scalar(35, 30, 42);
        Scalar highHSV = new Scalar(45, 255, 255);

        Core.inRange(mat, lowHSV, highHSV, mat);

        Mat left = mat.submat(LEFT_ROI);
        Mat mid = mat.submat(MID_ROI);

        double leftValue = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
        double midValue = Core.sumElems(mid).val[0] / MID_ROI.area() / 255;

        left.release();
        mid.release();

        boolean stoneLeft = leftValue > PERCENT_COLOR_THRESHOLD;
        boolean stoneMid = midValue > PERCENT_COLOR_THRESHOLD;

        if (stoneMid) {
            location = Location.MID;
            telemetry.addData("Duck Location", "mid");
        }
        else if (stoneLeft) {
            location = Location.LEFT;
            telemetry.addData("Duck Location", "left");
        }
        else{
            location = Location.RIGHT;
            telemetry.addData("Duck Location", "right");
        }
        telemetry.update();

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

        Scalar colorStone = new Scalar(255, 0, 0);
        Scalar colorSkystone = new Scalar(0, 255, 0);
        Scalar colorDuck = new Scalar(0,0,255);

        Imgproc.rectangle(mat, LEFT_ROI, location == Location.LEFT? colorSkystone:colorStone);
        Imgproc.rectangle(mat, MID_ROI, location == Location.MID? colorDuck:colorDuck);

        return mat;
    }

    public Location getLocation() {
        return location;
    }
}