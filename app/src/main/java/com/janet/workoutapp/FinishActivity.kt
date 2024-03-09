package com.janet.workoutapp

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.widget.Toast
import com.janet.workoutapp.databinding.ActivityFinishBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {
    private lateinit var finishBinding: ActivityFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishBinding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(finishBinding.root)

        // Setup db to use DAO
        val historyDAO = (application as WorkoutApp).db.historyDAO()

        setSupportActionBar(finishBinding.toolbarFinish)
        supportActionBar.let {
            it?.setDisplayHomeAsUpEnabled(true)
        }

        finishBinding.finishBtn.setOnClickListener {

            addToHistory(historyDAO)

            // finish() stands alone -> Close the current activity and propagate back to the active activity (Main Activity). If the MainActivity calls finish(), it auto closes your app
            finish()
        }
    }

    private fun addToHistory(historyDAO: HistoryDAO) {
        val c = Calendar.getInstance()
        val dateTime = c.time
        val simpleDateFormat = SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = simpleDateFormat.format(dateTime)

        lifecycleScope.launch {
            historyDAO.insert(HistoryEntity(currentDate))
            // Switch to the UI thread
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "A new workout added to your history", Toast.LENGTH_SHORT)
            }
        }
    }
}
