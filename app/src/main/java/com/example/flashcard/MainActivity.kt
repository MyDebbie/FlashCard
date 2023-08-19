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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var flashcardDatabase: FlashcardDatabase
    private var allFlashcards = mutableListOf<Flashcard>()
    private var currentCardDisplayedIndex = 0
    private var cardToEdit: Flashcard? = null

    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer0: TextView
    private lateinit var tvAnswer1: TextView
    private lateinit var tvAnswer2:  TextView
    private lateinit var tvEmptyStates: TextView
    private lateinit var ivEdit: ImageView
    private lateinit var ivAdd: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var ivDelete: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tvQuestion = findViewById(R.id.tvQuestion)
        tvAnswer0 = findViewById(R.id.tvAnswer0)
        tvAnswer1 = findViewById(R.id.tvAnswer1)
        tvAnswer2 = findViewById(R.id.tvAnswer2)
        tvEmptyStates = findViewById(R.id.tvEmptyStates)
        ivEdit = findViewById(R.id.ivEdit)
        ivAdd = findViewById(R.id.ivAdd)
        ivNext = findViewById(R.id.ivNext)
        ivDelete  = findViewById(R.id.ivDelete)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()



        if (allFlashcards.size > 0) {
            tvQuestion.text = allFlashcards[0].question
            tvAnswer0.text = allFlashcards[0].wrongAnswer1
            tvAnswer1.text = allFlashcards[0].answer
            tvAnswer2.text = allFlashcards[0].wrongAnswer2

        }



        ivNext.setOnClickListener {
            if (allFlashcards.size == 0) {
                return@setOnClickListener
            }
            else {
                var randomIndex = getRandomNumber(0, allFlashcards.size - 1)

                while (randomIndex == currentCardDisplayedIndex) {
                    randomIndex = getRandomNumber(0, allFlashcards.size - 1)
                }

                currentCardDisplayedIndex = randomIndex


                allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                val (question, answer0, answer1, answer2) = allFlashcards[randomIndex]

                tvQuestion.text = question
                tvAnswer0.text = answer0
                tvAnswer1.text = answer1
                tvAnswer2.text = answer2

            }
        }


        ivDelete.setOnClickListener {

            flashcardDatabase.deleteCard(tvQuestion.text.toString())
            Snackbar.make(
                findViewById<TextView>(R.id.tvQuestion),
                "Deleted.",
                Snackbar.LENGTH_SHORT)
                .show()
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()

            if (allFlashcards.size == 0) {
                EmptyState_Show()

            } else {
                EmptyState_Hide()


                val randomIndex = getRandomNumber(0, allFlashcards.size - 1)
                val (question, answer0, answer1, answer2) = allFlashcards[randomIndex]

                tvQuestion.text = question
                tvAnswer0.text = answer0
                tvAnswer1.text = answer1
                tvAnswer2.text = answer2

            }

        }


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


                    flashcardDatabase.insertCard(Flashcard(question.toString(), answer1.toString()))
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()




                    // Save newly created flashcard to database
                    if (question != null && answer0 != null && answer1 != null && answer2 != null) {
                        flashcardDatabase.insertCard(Flashcard(question, answer0, answer1, answer2))

                        // Update set of flashcards to include new card
                        allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                    } else {
                        Log.e("TAG", "Missing data")
                    }
                } else {
                    Log.i("MainActivity", "Returned null data from AddCardActivity")
                }

                    val parentView = findViewById<View>(android.R.id.content)
                    Snackbar.make(parentView, R.string.snackbar_message, Snackbar.LENGTH_LONG)
                        .show()

                }
            }

        val editResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (data != null) {

                val question = data.getStringExtra("question")
                val answer0 = data.getStringExtra("answer0")
                val answer1 = data.getStringExtra("answer1")
                val answer2 = data.getStringExtra("answer2")


                tvQuestion.text = question
                tvAnswer0.text = answer0
                tvAnswer1.text = answer1
                tvAnswer2.text = answer2

                if (question != null) {
                    cardToEdit!!.question = question
                }
                if (answer0 != null) {
                    cardToEdit!!.wrongAnswer1 = answer0
                }
                if (answer1 != null) {
                    cardToEdit!!.answer = answer1
                }
                if (answer2 != null) {
                    cardToEdit!!.wrongAnswer2 = answer2
                }

                flashcardDatabase.updateCard(cardToEdit!!)

            } else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }


        ivEdit.setOnClickListener() {

            val intent = Intent(this, AddCardActivity::class.java)


//            val question_text = tvQuestion.text.toString()
//            val answer_text1 = tvAnswer1.text.toString()

            val currentQuestion = tvQuestion.text.toString()
            val currentAnswer0 = tvAnswer0.text.toString()
            val currentAnswer1 = tvAnswer1.text.toString()
            val currentAnswer2 = tvAnswer2.text.toString()

            cardToEdit = allFlashcards.find { it.question == currentQuestion }
//            cardToEdit = allFlashcards.find { it.answer == currentAnswer0 }
//            cardToEdit = allFlashcards.find { it.answer == currentAnswer1 }
//            cardToEdit = allFlashcards.find { it.answer == currentAnswer2 }



            intent.putExtra("question_edit", currentQuestion);
            intent.putExtra("answer_edit0", currentAnswer0);
            intent.putExtra("answer_edit1", currentAnswer1);
            intent.putExtra("answer_edit2", currentAnswer2);


            editResultLauncher.launch(intent)
        }


        ivAdd.setOnClickListener() {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }


    }
    fun getRandomNumber(minNumber: Int, maxNumber: Int): Int {
        return (minNumber..maxNumber).random()
    }
    fun EmptyState_Show(){

        tvQuestion.visibility = View.INVISIBLE
        tvAnswer0.visibility = View.INVISIBLE
        tvAnswer1.visibility = View.INVISIBLE
        tvAnswer2.visibility = View.INVISIBLE
        ivAdd.visibility = View.VISIBLE
        ivNext.visibility = View.INVISIBLE
        ivDelete.visibility = View.INVISIBLE
        ivEdit.visibility = View.INVISIBLE
        tvEmptyStates.visibility = View.VISIBLE
    }
    fun EmptyState_Hide(){
        tvQuestion.visibility = View.VISIBLE
        tvAnswer0.visibility = View.VISIBLE
        tvAnswer1.visibility = View.VISIBLE
        tvAnswer2.visibility = View.VISIBLE
        ivAdd.visibility = View.VISIBLE
        ivNext.visibility = View.VISIBLE
        ivDelete.visibility = View.VISIBLE
        ivEdit.visibility = View.VISIBLE
        tvEmptyStates.visibility = View.INVISIBLE
    }

}