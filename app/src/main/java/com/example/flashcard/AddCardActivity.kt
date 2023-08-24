package com.example.flashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class AddCardActivity : AppCompatActivity() {

    private lateinit var ivcancel: ImageView
    private lateinit var ivsave: ImageView
    private lateinit var etquestion: EditText
    private lateinit var etanswer0: EditText
    private lateinit var etanswer1: EditText
    private lateinit var etanswer2: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)



        ivcancel = findViewById(R.id.ivCancel)
        ivsave = findViewById(R.id.ivSave)
        etquestion = findViewById(R.id.etQuestion)
//        etanswer0 = findViewById(R.id.etAnswer0)
        etanswer1 = findViewById(R.id.etAnswer1)
//        etanswer2 = findViewById(R.id.etAnswer2)




        etquestion.setText(intent.getStringExtra("question_edit"))
//        etanswer0.setText(intent.getStringExtra("answer_edit0"))
        etanswer1.setText(intent.getStringExtra("answer_edit1"))
//        etanswer2.setText(intent.getStringExtra("answer_edit2"))



        ivsave.setOnClickListener(){
            val data = Intent()

            val etquestion = etquestion.text.toString()
//            val etanswer0 = etanswer0.text.toString()
            val etanswer1 = etanswer1.text.toString()
//            val etanswer2 = etanswer2.text.toString()


            if(etquestion.isEmpty() or etanswer1.isEmpty()){
                Toast.makeText(applicationContext, R.string.toast_message, Toast.LENGTH_SHORT).show()
            }
            else{

                data.putExtra("question", etquestion)
//                data.putExtra("answer0", etanswer0)
                data.putExtra("answer1", etanswer1)
//                data.putExtra("answer2", etanswer2)

                setResult(RESULT_OK, data)

                finish()
            }
        }

        ivcancel.setOnClickListener(){
            finish()
            overridePendingTransition(R.anim.left_in, R.anim.right_out);

        }

    }
}