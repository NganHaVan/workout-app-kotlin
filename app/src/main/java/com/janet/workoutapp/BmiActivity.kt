package com.janet.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.janet.workoutapp.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNIT_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNIT_VIEW = "US_UNIT_VIEW"
    }

    private lateinit var bmiBinding: ActivityBmiBinding

    private var currentView: String = METRIC_UNIT_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bmiBinding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(bmiBinding.root)

        setSupportActionBar(bmiBinding.toolbarBmi)
        supportActionBar.let {
            it?.setDisplayHomeAsUpEnabled(true)
            it?.title = "CALCULATE BMI"
        }
        bmiBinding.toolbarBmi.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        makeMetricUnitViewVisible()

        // Handle press on radio buttons to change input layout view
        bmiBinding.rgUnitsContainer.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_metric_units) {
                makeMetricUnitViewVisible()
            } else {
                makeUSUnitViewVisible()
            }
        }

        // Handle calculate btn pressed
        bmiBinding.btnCalculateUnits.setOnClickListener {
            if (validateMetricUnits()) {
                if (currentView == METRIC_UNIT_VIEW) {
                    val height: Float = bmiBinding?.edHeight?.text.toString().toFloat() / 100
                    val weight: Float = bmiBinding?.edWeight?.text.toString().toFloat()

                    val bmi = weight / (height * height)
                    displayBMIResult(bmi)
                } else {
                    val weight = bmiBinding.edWeightInPound.text.toString().toFloat()
                    val heightInFeet = bmiBinding.edHeightInFeet.text.toString().toFloat()
                    val heightInInch = bmiBinding.edHeightInInch.text.toString().toFloat()
                    val height = heightInInch + heightInFeet * 12

                    val bmi = 703 * (weight / (height * height))
                    displayBMIResult(bmi)
                }

            } else {
                Toast.makeText(this@BmiActivity, "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true
        if (currentView == METRIC_UNIT_VIEW) {
            if (bmiBinding?.edWeight?.text?.isEmpty() == true || bmiBinding?.edHeight?.text?.isEmpty() == true) {
                isValid = false
            }
        } else {
            if (bmiBinding?.edWeightInPound?.text?.isEmpty() == true || bmiBinding?.edHeightInFeet?.text?.isEmpty() == true || bmiBinding?.edHeightInInch?.text?.isEmpty() == true) {
                isValid = false
            }
        }

        return isValid
    }

    private fun displayBMIResult (bmi: Float) {
        val bmiLabel: String
        val bmiDesc: String

        when {
            bmi.compareTo(15f) <= 0 -> {
                bmiLabel = "Very severely underweight"
                bmiDesc = "Oops! You really need to take better care of yourself. Eat more"
            }

            (bmi.compareTo(15f) > 0 && bmi.compareTo(18.5f) <= 0) -> {
                bmiLabel = "Underweight"
                bmiDesc = "Oops! You really need to take better care of yourself! Eat more!"
            }

            (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) -> {
                bmiLabel = "Normal"
                bmiDesc = "Congratulations! You are in a good shape!"
            }

            (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) -> {
                bmiLabel = "Overweight"
                bmiDesc = "Oops! You really need to take care of your yourself! Workout maybe!"
            }

            (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) -> {
                bmiLabel = "Obese Class | (Moderately obese)"
                bmiDesc = "Oops! You really need to take care of your yourself! Workout maybe!"
            }

            (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) -> {
                bmiLabel = "Obese Class || (Severely obese)"
                bmiDesc = "OMG! You are in a very dangerous condition! Act now!"
            }

            else -> {
                bmiLabel = "Obese Class ||| (Very Severely obese)"
                bmiDesc = "OMG! You are in a very dangerous condition! Act now!"
            }
        }

        bmiBinding.bmiResultContainer.visibility = View.VISIBLE
        bmiBinding.tvBMIValue.text = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        bmiBinding.tvBMIType.text = bmiLabel
        bmiBinding.tvBMIDescription.text = bmiDesc
    }

    private fun makeMetricUnitViewVisible() {
        currentView = METRIC_UNIT_VIEW
        // Show metric elements
        bmiBinding.inputLayoutHeight.visibility = View.VISIBLE
        bmiBinding.inputLayoutWeight.visibility = View.VISIBLE

        // Hide US elements
        bmiBinding.inputLayoutWeightInPound.visibility = View.GONE
        bmiBinding.inputLayoutHeightInInch.visibility = View.GONE
        bmiBinding.inputLayoutHeightInFeet.visibility = View.GONE

        // Clear edit values
        bmiBinding.edWeight.text!!.clear()
        bmiBinding.edHeight.text!!.clear()

        // Hide BMI Result
        bmiBinding.bmiResultContainer.visibility = View.INVISIBLE
        // Setup layout Manager so that the bmi result container is under input layout height
        val llBmiResultContainer = bmiBinding.bmiResultContainer as LinearLayout

        val llParams = llBmiResultContainer.layoutParams as RelativeLayout.LayoutParams
        llParams.addRule(RelativeLayout.BELOW, R.id.input_layout_height)
        llBmiResultContainer.layoutParams = llParams
    }

    private fun makeUSUnitViewVisible() {
        currentView = US_UNIT_VIEW

        //Show US elements
        bmiBinding.inputLayoutWeightInPound.visibility = View.VISIBLE
        bmiBinding.inputLayoutHeightInFeet.visibility = View.VISIBLE
        bmiBinding.inputLayoutHeightInInch.visibility = View.VISIBLE

        // Hide metric elements
        bmiBinding.inputLayoutHeight.visibility = View.GONE
        bmiBinding.inputLayoutWeight.visibility = View.GONE

        // Clear edit values
        bmiBinding.edWeightInPound.text!!.clear()
        bmiBinding.edHeightInFeet.text!!.clear()
        bmiBinding.edHeightInInch.text!!.clear()

        // Hide BMI Result
        bmiBinding.bmiResultContainer.visibility = View.INVISIBLE

        // Setup layout Manager so that the bmi result container is under input layout height
        val llBmiResultContainer = bmiBinding.bmiResultContainer as LinearLayout

        val llParams = llBmiResultContainer.layoutParams as RelativeLayout.LayoutParams
        llParams.addRule(RelativeLayout.BELOW, R.id.input_layout_height_in_feet)
        llBmiResultContainer.layoutParams = llParams

    }
}
