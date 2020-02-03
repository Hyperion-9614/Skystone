package org.firstinspires.ftc.teamcode.modules

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class RectangleSampling : CvPipeline() {
    private val matYCrCb = Mat()
    private val matCb = Mat()
    private val samplingRectWidth = 175 //This is displayed as vertical on phone TODO: Tune this value for more room for error
    private val samplingRectHeight = 115 //This is displayed as horizontal on phone TODO: Tune this value for more room for error
    private val samplingRectColor = Scalar(0.0, 0.0, 255.0)
    private val samplingRectThickness = 1

    private val samplingCircleRadius = 5
    private val samplingCircleColor = Scalar(225.0, 52.0, 235.0)
    private val samplingCircleThickness = -1

    private val samplePointPercentages = arrayOf(
            arrayOf(.25, .5), arrayOf(.5, .5), arrayOf(.75, .5)
    )

    private val samplePoints = samplePointPercentages.map {
        arrayOf(
                Point(
                        it[0] * width - samplingRectWidth / 2,
                        it[1] * height - samplingRectHeight / 2
                ),
                Point(
                        it[0] * width + samplingRectWidth / 2,
                        it[1] * height + samplingRectHeight / 2
                )
        )
    }

    override fun processFrame(input: Mat): Mat {

        samplePoints.forEach {
            //Log.i("Fuck Me", "x: ${it[0].x}, ${it[0].y}  y: ${it[1].x}, ${it[1].y}")
        }

        // Convert the image from RGB to YCrCb
        Imgproc.cvtColor(input, matYCrCb, Imgproc.COLOR_RGB2YCrCb)

        // Extract the Cb channel from the image
        Core.extractChannel(matYCrCb, matCb, 2)

        // The the sample areas from the Cb channel
        var subMats = samplePoints.map {
            matCb.submat(Rect(it[0], it[1]))
        }

        // Average the sample areas
        val avgSamples = subMats.map {
            Core.mean(it).`val`[0]
        }
        //Log.i("AVG Samples", avgSamples.toString())

        // Draw rectangles around the sample areas
        samplePoints.forEach {
            Imgproc.rectangle(input, it[0], it[1], samplingRectColor, samplingRectThickness)
        }

        // Figure out which sample zone had the lowest contrast from blue (lightest color)
        val max = avgSamples.max()
        //Log.i("MAX VALUES:", max.toString())

        // Draw a circle on the detected skystone
        detectedSkystonePosition = avgSamples.indexOf(max)

        val detectedPoint = samplePoints[detectedSkystonePosition]
        Imgproc.circle(
                input,
                Point(
                        (detectedPoint[0].x + detectedPoint[1].x) / 2,
                        (detectedPoint[0].y + detectedPoint[1].y) / 2
                ),
                samplingCircleRadius,
                samplingCircleColor,
                samplingCircleThickness
        )

        // Free the allocated submat memory
        subMats.forEach {
            it.release()
        }

        //Log.i("Skystone Positions", detectedSkystonePosition.toString())
        return input
    }
}