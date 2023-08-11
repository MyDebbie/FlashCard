package com.example.flashcard

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvAnswer0 = findViewById<TextView>(R.id.tvAnswer0)
        val tvAnswer1 = findViewById<TextView>(R.id.tvAnswer1)
        val tvAnswer2 = findViewById<TextView>(R.id.tvAnswer2)


        val ivEdit = findViewById<ImageView>(R.id.ivEdit)
        val ivAdd = findViewById<ImageView>(R.id.ivAdd)



        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                if (data != null) {
                    val question = data.getStringExtra("question")
                    val answer0 = data.getStringExtra("answer0")
                    val answer1 = data.getStringExtra("answer1")
                    val answer2 = data.getStringExtra("answer2")


                    tvQuestion.text = question
                    tvAnswer0.text = answer0
                    tvAnswer1.text = answer1
                    tvAnswer2.text = answer2


                    val parentView = findViewById<View>(android.R.id.content)
                    Snackbar.make(parentView, R.string.snackbar_message, Snackbar.LENGTH_LONG)
                        .show()

                } else {
                }
            }
        }

        ivEdit.setOnClickListener() {

            val intent = Intent(this, AddCardActivity::class.java)

            val question_text = tvQuestion.text.toString()
            val answer_text0 = tvAnswer0.text.toString()
            val answer_text1 = tvAnswer1.text.toString()
            val answer_text2 = tvAnswer2.text.toString()


            intent.putExtra("question_edit", question_text);
            intent.putExtra("answer_edit0", answer_text0);
            intent.putExtra("answer_edit1", answer_text1);
            intent.putExtra("answer_edit2", answer_text2);

            resultLauncher.launch(intent)
        }


        ivAdd.setOnClickListener() {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }

    }
}