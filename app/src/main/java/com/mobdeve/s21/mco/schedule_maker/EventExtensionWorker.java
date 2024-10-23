package com.mobdeve.s21.mco.schedule_maker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventExtensionWorker extends Worker {

    public EventExtensionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Logic to extend the events for another 6 months

        Calendar baseDate = Calendar.getInstance(); // Get the current date or retrieve from input
        Calendar endDate = (Calendar) baseDate.clone();
        endDate.add(Calendar.MONTH, 6);

        // Extend the event (using RecurringEventManager or your own event extension logic)
        RecurringEventManager.extendEventForNextPeriod(baseDate, endDate);

        return Result.success();
    }
}

