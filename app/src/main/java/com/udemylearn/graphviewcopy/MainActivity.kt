package com.udemylearn.graphviewcopy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.udemylearn.graphviewcopy.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    val xBound = 10 // Example value
    val yBound = 30 // Example value

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.graphView.setData(generateRandomDataPoints())

    }

    // data source
    private fun generateRandomDataPoints(): List<DataPoint> {
        val random = Random()
        return (0..xBound).map {
            DataPoint(it, random.nextInt(yBound) + 1)
        }
    }

}