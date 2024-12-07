package com.mobdeve.s21.mco.schedule_maker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alamkanak.weekview.WeekView // Import the WeekView class
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import java.io.IOException


class WeeklySchedActivity : AppCompatActivity() {
    private val viewModel by viewModels<WeeklySchedViewModel>()
    private lateinit var weekView: WeekView // Change this to WeekView
    private lateinit var adapter: MyCustomPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        weekView = findViewById(R.id.weekView) // Ensure this ID matches your XML layout

        weekView.minHour = 0;
        weekView.maxHour = 24;

        adapter = MyCustomPagingAdapter(object : LoadMoreHandler {
            override fun loadMore(startDate: Calendar, endDate: Calendar) {
                // Implement your logic to load more events here
            }
        })
        weekView.adapter = adapter

        viewModel.events.observe(this) { events ->
            val myEvents = adapter.convertEventsToMyEvents(events) // Convert Events to MyEvent
            adapter.submitList(myEvents) // Updates the adapter with the new event list
        }

        // Load events (for example purposes)
        viewModel.loadEvents() // Call your method to load events

        // Set up the back button
        val backBtn: FloatingActionButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            // Navigate back to EventListActivity
            val intent = Intent(this, EventListActivity::class.java)
            startActivity(intent)
            finish() // Optionally finish BasicActivity to remove it from the back stack
        }

        val cptBtn: FloatingActionButton = findViewById(R.id.screenshotBtn)
        cptBtn.setOnClickListener {
            // Get the part of the screen you want to capture
            val targetView: android.view.View = findViewById(R.id.screenshotArea) // Replace with your target view

            // Create a Bitmap to store the screenshot
            val bitmap = Bitmap.createBitmap(targetView.width, targetView.height, Bitmap.Config.ARGB_8888)

            // Create a Canvas to draw the view into the Bitmap
            val canvas = Canvas(bitmap)
            targetView.draw(canvas)

            // Optionally save the bitmap to a file (e.g., internal storage)
            saveBitmapToGallery(bitmap, this)

            // Optionally show a Toast or message to confirm screenshot capture
            Toast.makeText(this, "Screenshot captured!", Toast.LENGTH_SHORT).show()
        }

    }
}

private fun saveBitmapToGallery(bitmap: Bitmap, context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, save to Pictures directory using MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "schedule_screenshot.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")  // Save in the Pictures folder
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        } else {
            // For devices below Android 10, save to internal storage or legacy external storage
            val file = File(context.getExternalFilesDir(null), "schedule_screenshot.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving screenshot", Toast.LENGTH_SHORT).show()
    }
}




