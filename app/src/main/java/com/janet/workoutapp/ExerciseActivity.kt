package com.janet.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.janet.workoutapp.databinding.ActivityExerciseBinding
import com.janet.workoutapp.databinding.BackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var exerciseBinding: ActivityExerciseBinding
    lateinit var backConfirmationBinding: BackConfirmationBinding
    // Time left
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseStatusAdapter: ExerciseStatusAdapter? = null

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExerciseIndex = -1

    private var tts: TextToSpeech? = null

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseBinding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(exerciseBinding.root)

        setSupportActionBar(exerciseBinding.toolbarExercise)
        supportActionBar.let {
            it?.setDisplayHomeAsUpEnabled(true)
        }
        exerciseBinding.toolbarExercise.setNavigationOnClickListener {
            // Do the same thing as you click back btn on your phone
            showBackConfirmation()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setupExerciseStatusRecyclerView()

        setupRestView()


    }

    private fun showBackConfirmation() {
        val customDialog = Dialog(this)
        // Data binding with custom dialog (map with dialog XML file)
        backConfirmationBinding = BackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(backConfirmationBinding.root)
        // Cannot cancel the dialog when tapping outside the dialog
        customDialog.setCanceledOnTouchOutside(false)
        backConfirmationBinding.yesBtn.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }

        backConfirmationBinding.noBtn.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView() {
        exerciseBinding.rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList!!)
        exerciseBinding.rvExerciseStatus.adapter = exerciseStatusAdapter
    }

    private fun setupRestView() {
        exerciseBinding.demonstrationImage.visibility = View.GONE
        exerciseBinding.llUpcomingExcContainer.visibility = View.VISIBLE

        // Play Media Player
        try {
            val soundURI = Uri.parse("android.resource://com.janet.workoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (currentExerciseIndex >= 0) {
            // Setup llUpcomingExContainer to be above the FrameLayout
            var flLayout = exerciseBinding.flProgressBarContainer.layoutParams as ConstraintLayout.LayoutParams
            flLayout.topToBottom = exerciseBinding.llUpcomingExcContainer.id
            exerciseBinding.flProgressBarContainer.requestLayout()

            if (currentExerciseIndex <= exerciseList?.size!! - 1) {
                exerciseBinding.upcomingExerciseName.text = exerciseList?.get(currentExerciseIndex)?.getName()
            }

        } else {
            exerciseList?.let {
                exerciseBinding.upcomingExerciseName.text = it.get(0).getName()
            }
        }
        if (restTimer!== null) {
            restTimer?.cancel()
            restProgress = 0
        }

        startRestTimer()
    }

    private fun setupExerciseView() {
        exerciseBinding.llUpcomingExcContainer.visibility = View.GONE
        if (exerciseTimer !== null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        startExerciseTimer()
    }

    private fun startRestTimer() {
        val REST_TIME = 2

        // Reset progress bar with rest value
        exerciseBinding.tvTitle.text = "GET READY IN"
        exerciseBinding.progressBar.progress = restProgress
        exerciseBinding.progressBar.max = REST_TIME
        exerciseBinding.progressBar.progress = REST_TIME
        exerciseBinding.tvTimer.text = REST_TIME.toString()

        restTimer = object : CountDownTimer((REST_TIME * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                exerciseBinding.progressBar.progress = REST_TIME - restProgress
                exerciseBinding.tvTimer.text = (REST_TIME - restProgress).toString()
            }

            override fun onFinish() {
                if (currentExerciseIndex < 0) {
                    currentExerciseIndex++
                }
                setupExerciseView()
            }
        }.start()
    }

    private fun startExerciseTimer() {

        speakOut(exerciseList!!.get(currentExerciseIndex)!!.getName())

        val EXERCISE_TIME = 5
        // Reset progress bar with exercise value
        exerciseBinding.progressBar.max = EXERCISE_TIME
        exerciseBinding.progressBar.progress = EXERCISE_TIME
        exerciseBinding.tvTimer.text = EXERCISE_TIME.toString()

        // Setup constraint layout of FrameLayout container to be beneath image view
        var flLayout = exerciseBinding.flProgressBarContainer.layoutParams as ConstraintLayout.LayoutParams
        flLayout.topToBottom = exerciseBinding.demonstrationImage.id
        exerciseBinding.flProgressBarContainer.requestLayout()

        if (currentExerciseIndex >= 0) {
            exerciseBinding.demonstrationImage.visibility = View.VISIBLE
            exerciseList?.get(currentExerciseIndex)
                ?.let {
                    exerciseBinding.demonstrationImage.setImageResource(it.getImage())
                    exerciseBinding.tvTitle.text = it.getName()

                    it.setIsSelected(true)
                    // NOTE: notify the adapter that data set has changed otherwise the UI is not updated
                    exerciseStatusAdapter!!.notifyDataSetChanged()
                }
        }

        exerciseTimer = object: CountDownTimer((EXERCISE_TIME * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                exerciseBinding.progressBar.progress = (EXERCISE_TIME - exerciseProgress)
                exerciseBinding.tvTimer.text = (EXERCISE_TIME - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExerciseIndex < exerciseList?.size!! - 1) {
                    exerciseList?.get(currentExerciseIndex)?.let {
                        it.setIsCompleted(true)
                        it.setIsSelected(false)
                        exerciseStatusAdapter?.notifyDataSetChanged()
                    }
                    currentExerciseIndex++
                    setupRestView()
                } else {
//                    Toast.makeText(this@ExerciseActivity, "Exercise ends", Toast.LENGTH_LONG).show()
                    // NOTE: finish -> Call this when your activity is done and should be closed. It closes the current activity so that the Finish Activity can not navigate back to the Exercise Activity
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer!== null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer!== null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if (tts !== null) {
            tts?.stop()
            tts?.shutdown()
        }
        if (player !== null) {
            player?.stop()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language is not supported")
            }
        } else {
            Log.e("TTS", "Initialization Failed")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}
