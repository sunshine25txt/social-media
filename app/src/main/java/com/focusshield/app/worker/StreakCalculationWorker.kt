package com.focusshield.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.focusshield.app.data.local.entity.StreakEntity
import com.focusshield.app.domain.repository.BlockedEventRepository
import com.focusshield.app.domain.repository.StreakRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@HiltWorker
class StreakCalculationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val streakRepository: StreakRepository,
    private val blockedEventRepository: BlockedEventRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDayMillis = cal.timeInMillis

        // Calculate focus score based on today's events
        val timeSaved = blockedEventRepository.getTimeSavedToday(startOfDayMillis).first()
        val blockedCount = blockedEventRepository.getBlockedCountToday(startOfDayMillis).first()
        val score = ((timeSaved / 60) * 2 + blockedCount * 5).coerceAtMost(100)
        
        val metGoal = score >= 50 // simplistic goal

        val streakEntity = StreakEntity(
            date = todayDateString,
            focusScore = score,
            metGoal = metGoal
        )
        
        streakRepository.insertOrUpdate(streakEntity)
        
        return Result.success()
    }
}
