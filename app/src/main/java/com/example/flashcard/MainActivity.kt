package com.example.flashcard

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var flashcardDatabase: FlashcardDatabase
    private var allFlashcards = mutableListOf<Flashcard>()
    private var currentCardDisplayedIndex = 0
    private var cardToEdit: Flashcard? = null
    private var countDownTimer: CountDownTimer? = null


    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer0: TextView
    private lateinit var tvAnswer1: TextView
    private lateinit var tvAnswer2:  TextView
    private lateinit var tvEmptyStates: TextView
    private lateinit var ivEdit: ImageView
    private lateinit var ivAdd: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var tvTimer: TextView
    private lateinit var KfView: KonfettiView
    private var previousRandomIndex: Int = -1



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
        KfView = findViewById(R.id.kfView)
        tvTimer = findViewById(R.id.tvTimer)

        startTimer()

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        countDownTimer = object : CountDownTimer(16000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {}
        }

        if (allFlashcards.size > 0) {
            startTimer()
            tvQuestion.text = allFlashcards[0].question
            tvAnswer0.text = allFlashcards[0].wrongAnswer1
            tvAnswer1.text = allFlashcards[0].answer
            tvAnswer2.text = allFlashcards[0].wrongAnswer2

        }



//
//        tvQuestion.setOnClickListener {
//            tvQuestion.animate()
//                .rotationY(90f)
//                .setDuration(200)
//                .withEndAction(
//                    Runnable {
//                        tvQuestion.setVisibility(View.INVISIBLE)
//                        tvAnswer1.visibility = View.VISIBLE
//                        // second quarter turn
//                        tvAnswer1.rotationY = -90f
//                        tvAnswer1.animate()
//                            .rotationY(0f)
//                            .setDuration(200)
//                            .start()
//                    }
//                ).start()
//            tvQuestion.setCameraDistance(25000f)
//            tvAnswer1.setCameraDistance(25000f)
//
//        }


        answerCelebration()


        tvAnswer0.setOnClickListener {
            tvAnswer0.setBackgroundColor(getColor(R.color.red))
        }
        tvAnswer2.setOnClickListener {
            tvAnswer2.setBackgroundColor(getColor(R.color.red))
        }



        ivNext.setOnClickListener {

            if (allFlashcards.size == 0) {
                return@setOnClickListener
            }
            else {
                startTimer()

                var randomIndex = getRandomNumber(0, allFlashcards.size - 1)

                while (randomIndex == currentCardDisplayedIndex) {
                    randomIndex = getRandomNumber(0, allFlashcards.size - 1)
                }


//                tvQuestion.visibility = View.VISIBLE
//                tvAnswer1.visibility = View.INVISIBLE
//                tvAnswer1.rotationY = 90f
//                tvQuestion.rotationY = 0f

                allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                val (question, answer0, answer1, answer2) = allFlashcards[randomIndex]


                val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
                val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)

                leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        tvQuestion.startAnimation(leftOutAnim)

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        tvQuestion.startAnimation(rightInAnim)

                    }

                    override fun onAnimationRepeat(animation: Animation?) {

                    }
                })
                tvQuestion.startAnimation(leftOutAnim)

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

            val currentQuestion = tvQuestion.text.toString()
            val currentAnswer0 = tvAnswer0.text.toString()
            val currentAnswer1 = tvAnswer1.text.toString()
            val currentAnswer2 = tvAnswer2.text.toString()

            cardToEdit = allFlashcards.find { it.question == currentQuestion }
            cardToEdit = allFlashcards.find { it.answer == currentAnswer0 }
            cardToEdit = allFlashcards.find { it.answer == currentAnswer1 }
            cardToEdit = allFlashcards.find { it.answer == currentAnswer2 }



            intent.putExtra("question_edit", currentQuestion);
            intent.putExtra("answer_edit0", currentAnswer0);
            intent.putExtra("answer_edit1", currentAnswer1);
            intent.putExtra("answer_edit2", currentAnswer2);


            editResultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }


        ivAdd.setOnClickListener() {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }


    }
    fun getRandomNumber(minNumber: Int, maxNumber: Int): Int {
        return (minNumber..maxNumber).random()
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer?.start()
    }

    private fun answerCelebration(){
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.1)
        )
        tvAnswer1.setOnClickListener {
            tvAnswer1.setBackgroundColor(getColor(R.color.green))
            KfView.start(party)

        }
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
        tvTimer.visibility = View.INVISIBLE
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
        tvTimer.visibility = View.VISIBLE
        tvEmptyStates.visibility = View.INVISIBLE
    }

}