package com.janet.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.janet.workoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyBinding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        historyBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(historyBinding.root)

        val historyDAO = (application as WorkoutApp).db.historyDAO()

        setSupportActionBar(historyBinding.toolbarHistory)
        supportActionBar.let {
            it?.setDisplayHomeAsUpEnabled(true)
            it?.title = "HISTORY"
        }

        // Fetch history data
        getAllCompleteDate(historyDAO)

        historyBinding.toolbarHistory.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getAllCompleteDate(historyDAO: HistoryDAO) {
        lifecycleScope.launch {
            historyDAO.fetchAllDates().collect{ completedDates ->
                if (completedDates.isNotEmpty()) {
                    historyBinding.historyTitle.visibility = View.VISIBLE
                    historyBinding.rvHistoryList.visibility = View.VISIBLE
                    historyBinding.historyEmptyTitle.visibility = View.GONE

                    historyBinding.rvHistoryList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                    // Convert completedDates to ArrayList of its date
                    val dates = ArrayList<String>()
                    for (d in completedDates) {
                        dates.add(d.date)
                    }
                    val adapter = HistoryItemAdapter(dates)
                    historyBinding.rvHistoryList.adapter = adapter
                } else {
                    historyBinding.historyEmptyTitle.visibility = View.VISIBLE
                    historyBinding.rvHistoryList.visibility = View.GONE
                    historyBinding.historyTitle.visibility = View.GONE

                }
            }
        }
    }
}
