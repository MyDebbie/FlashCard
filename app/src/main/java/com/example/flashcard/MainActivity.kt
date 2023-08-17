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

    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    var currentCardDisplayedIndex = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()


        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvAnswer1 = findViewById<TextView>(R.id.tvAnswer1)


        if (allFlashcards.size > 0) {
            tvQuestion.text = allFlashcards[0].question
            tvAnswer1.text = allFlashcards[0].answer
        }

        //val ivEdit = findViewById<ImageView>(R.id.ivEdit)
        val ivAdd = findViewById<ImageView>(R.id.ivAdd)
        val ivNext = findViewById<ImageView>(R.id.ivNext)

        tvQuestion.setOnClickListener {
            tvAnswer1.visibility = VISIBLE
            tvQuestion.visibility = INVISIBLE

        }

        ivNext.setOnClickListener {
            if (allFlashcards.size == 0) {
                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }

            currentCardDisplayedIndex++

            if(currentCardDisplayedIndex >= allFlashcards.size) {
                Snackbar.make(
                    findViewById<TextView>(R.id.tvQuestion),
                    "You've reached the end of the cards, going back to start.",
                    Snackbar.LENGTH_SHORT)
                    .show()
                currentCardDisplayedIndex = 0
            }
            tvAnswer1.visibility = INVISIBLE
            tvQuestion.visibility = VISIBLE

            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val (question, answer1) = allFlashcards[currentCardDisplayedIndex]

            tvQuestion.text = question
            tvAnswer1.text = answer1


        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                if (data != null) {
                    val question = data.getStringExtra("question")
                    val answer1 = data.getStringExtra("answer1")


                    flashcardDatabase.insertCard(Flashcard(question.toString(), answer1.toString()))
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()


                    tvQuestion.text = question
                    tvAnswer1.text = answer1

                    // Save newly created flashcard to database
                    if (question != null && answer1 != null) {
                        flashcardDatabase.insertCard(Flashcard(question, answer1))
                        // Update set of flashcards to include new card
                        allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                    } else {
                        Log.e("TAG", "Missing question or answer to input into database. Question is $question and answer is $answer1")
                    }
                } else {
                    Log.i("MainActivity", "Returned null data from AddCardActivity")
                }

                    val parentView = findViewById<View>(android.R.id.content)
                    Snackbar.make(parentView, R.string.snackbar_message, Snackbar.LENGTH_LONG)
                        .show()

                } else {
                }
            }


//        ivEdit.setOnClickListener() {
//
//            val intent = Intent(this, AddCardActivity::class.java)
//
//            val question_text = tvQuestion.text.toString()
//            val answer_text1 = tvAnswer1.text.toString()
//
//
//            intent.putExtra("question_edit", question_text);
//            intent.putExtra("answer_edit1", answer_text1);
//
//            resultLauncher.launch(intent)
//        }


        ivAdd.setOnClickListener() {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }

    }
}