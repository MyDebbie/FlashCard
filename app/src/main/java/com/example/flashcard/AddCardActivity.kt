package com.example.flashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)



        val ivcancel = findViewById<ImageView>(R.id.ivCancel)
        val ivsave = findViewById<ImageView>(R.id.ivSave)

        val etquestion = findViewById<EditText>(R.id.etQuestion)
        val etanswer0 = findViewById<EditText>(R.id.etAnswer0)
        val etanswer1 = findViewById<EditText>(R.id.etAnswer1)
        val etanswer2 = findViewById<EditText>(R.id.etAnswer2)


        etquestion.setText(intent.getStringExtra("question_edit"))
        etanswer0.setText(intent.getStringExtra("answer_edit0"))
        etanswer1.setText(intent.getStringExtra("answer_edit1"))
        etanswer2.setText(intent.getStringExtra("answer_edit2"))



        ivsave.setOnClickListener(){
            val data = Intent()

            val etquestion = etquestion.text.toString()
            val etanswer0 = etanswer0.text.toString()
            val etanswer1 = etanswer1.text.toString()
            val etanswer2 = etanswer2.text.toString()


            if(etquestion.isEmpty() or etanswer0.isEmpty() or etanswer1.isEmpty() or etanswer2.isEmpty()){
                Toast.makeText(applicationContext, R.string.toast_message, Toast.LENGTH_SHORT).show()
            }
            else{

                data.putExtra("question", etquestion)
                data.putExtra("answer0", etanswer0)
                data.putExtra("answer1", etanswer1)
                data.putExtra("answer2", etanswer2)

                setResult(RESULT_OK, data)

                finish()
            }
        }

        ivcancel.setOnClickListener(){
            finish()
        }

    }
}