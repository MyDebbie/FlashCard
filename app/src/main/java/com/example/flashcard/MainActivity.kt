package com.example.flashcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvAnswer = findViewById<TextView>(R.id.tvAnswer)

        tvQuestion.setOnClickListener{
            tvQuestion.visibility= View.INVISIBLE
            tvAnswer.visibility = View.VISIBLE
        }
    }
}