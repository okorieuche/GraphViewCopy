package com.udemylearn.graphviewcopy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class GraphView(
    context: Context,
    attributeSet: AttributeSet
): View(context, attributeSet) {

    // this holds the list of data
    private val dataSet = mutableListOf<DataPoint>()

    private val mainActivity = MainActivity()

    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    //circle paint without fill
    private val dataPointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    // circle paint with fill
    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    // Paint for text labels
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 25f
    }

    // graph line paint
    private val dataPointLinePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // grid line paint
    private val gridLinePaint = Paint().apply {
        color = Color.GRAY // Set the color of the grid lines
        strokeWidth = 1f // Set the width of the grid lines
    }


    // x and y axis paint
    private val axisLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val yBound = mainActivity.yBound
        val xBound = mainActivity.xBound

        // Draw equally spaced vertical grid lines
        val numVerticalLines = 10 // Adjust the number of vertical lines as needed
        val verticalSpacing = width / (numVerticalLines).toFloat()

        for (i in 1 .. numVerticalLines) {
            val x = i * verticalSpacing
            canvas.drawLine(x, 0f, x, height.toFloat(), gridLinePaint)
            // to label the x axis using the xBound
            canvas.drawText((i * (xBound / numVerticalLines)).toString(), x, height.toFloat(), textPaint)
        }


        // Draw equally spaced horizontal grid lines
        val numHorizontalLines = 10 // Adjust the number of horizontal lines as needed
        val horizontalSpacing = height / (numHorizontalLines).toFloat()

        for (i in 1..numHorizontalLines) {
            val y = i * horizontalSpacing
            canvas.drawLine(0f, y, width.toFloat(), y, gridLinePaint)
            //to reverse numbering on the y axis
            val revY = height-y
            // to label the y axis using the yBound
            canvas.drawText((i * (yBound / numHorizontalLines)).toString(), 0f, revY, textPaint)
        }

        // updated for bezier
        // Create a path to draw the Bezier curve
        val path = Path()

        // for each data dataset
        dataSet.forEachIndexed { index, currentDataPoint ->
            val realX = currentDataPoint.xVal.toRealX()
            val realY = currentDataPoint.yVal.toRealY()

            // revRealY is used to reverse numbering on the y axis so that the scale goes from bottom left to top left
            val revRealY = height - realY

            if (index < dataSet.size - 1) {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX()
                val startY = currentDataPoint.yVal.toRealY()

                // revStartY is used to reverse numbering on the y axis so that the scale starts from bottom left corner of the screen
                val revStartY = height-currentDataPoint.yVal.toRealY()

                val endX = nextDataPoint.xVal.toRealX()
                val endY = nextDataPoint.yVal.toRealY()

                // revEndY is used to reverse numbering on the y axis so that the scale ends at the top left corner of the screen
                val revEndY = height-nextDataPoint.yVal.toRealY()

                //updated for bezier
                // Move to the starting point of the curve
                if (index == 0) {
                    path.moveTo(startX, revStartY)
                }

                // Calculate control points for the Bezier curve
                val controlX1 = startX + (endX - startX) / 2
                val controlY1 = revStartY
                val controlX2 = endX - (endX - startX) / 2
                val controlY2 = revEndY

                // Draw a quadratic Bezier curve to the next data point
                path.quadTo(controlX1, controlY1, endX, revEndY)

                // end of bezier code

                // to draw straight line from the current datapoint to the next datapoint forming the graph line
                /*canvas.drawLine(startX, revStartY, endX, revEndY, dataPointLinePaint)*/
            }

            // draw circle with fill using x and y value
            canvas.drawCircle(realX, revRealY, 7f, dataPointFillPaint)

            // draw circle without fill using x and y value
            canvas.drawCircle(realX, revRealY, 7f, dataPointPaint)

            //texts for x values on the x axis
            /*canvas.drawText(currentDataPoint.xVal.toString(), realX, height.toFloat(), textPaint)*/

            //texts for y values on the y axis
            /*canvas.drawText(currentDataPoint.yVal.toString(), 0f, revRealY, textPaint)*/

        }

        // to draw the bezier curve
        canvas.drawPath(path, dataPointLinePaint)

        // to draw the y axis
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), axisLinePaint)

        // to draw the x axis
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), axisLinePaint)
    }

    fun setData(newDataSet: List<DataPoint>) {

        //this converts the values to
        xMin = newDataSet.minBy { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxBy { it.xVal }?.xVal ?: 0
        yMin = newDataSet.minBy { it.yVal }?.yVal ?: 0
        yMax = newDataSet.maxBy { it.yVal }?.yVal ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)

        // invalidate to call draw function after plotting points,
        // without this, new plots are not visible
        invalidate()
    }

    // this extension function converts the actual value generated from source and
    // converts the to the screen pixel equivalent, so that the screen holds all generated values
    private fun Int.toRealX() = toFloat() / xMax * width
    private fun Int.toRealY() = toFloat() / yMax * height

}