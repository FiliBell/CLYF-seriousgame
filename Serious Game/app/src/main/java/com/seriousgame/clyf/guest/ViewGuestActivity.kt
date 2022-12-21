package com.seriousgame.clyf.guest

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.Query
import com.seriousgame.clyf.MainActivity
import com.seriousgame.clyf.R
import com.seriousgame.clyf.auth.*
import kotlinx.android.synthetic.main.activity_view_guest.*
import kotlinx.android.synthetic.main.popup_score.view.*

class ViewGuestActivity : AppCompatActivity() {

    fun adderStructure (x : ArrayList<String>, questionTV : TextView, answer1TV : Button, answer2TV : Button, answer3TV : Button){

        val indexSupport = x.get(0)

        db.collection(supportID).whereEqualTo("Question", indexSupport).get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val question = indexSupport
                    val answer1 = document.data["Answer1"].toString()
                    val answer2 = document.data["Answer2"].toString()
                    val answer3 = document.data["Answer3"].toString()
                    val correctAnswer = document.data["Correct_answer"].toString()

                    questionTV.text = question
                    answer1TV.text = answer1
                    answer2TV.text = answer2
                    answer3TV.text = answer3

                    correctAnswers.add(correctAnswer)

                    x.removeAt(0)
                }

            }


    }

    lateinit var questions : ArrayList<String>
    lateinit var correctAnswers : ArrayList<String>
    lateinit var guestCorrectAnswers : ArrayList<String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_guest)

        val title = titleVT
        val questionView = questionGuestView
        val answer1View = answer1GuestView
        val answer2View = answer2GuestView
        val answer3View = answer3GuestView
        val save = guestSave

        correctAnswers = ArrayList()
        questions = ArrayList()
        guestCorrectAnswers = ArrayList()

        var contatore = -1

        db.collection(supportID).whereNotEqualTo("Quiz_name", null).get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val supportTitle = document.data["Quiz_name"].toString()
                    title.text = "Welcome to ${supportTitle} Quiz "
                }
            }

        db.collection(supportID).whereNotEqualTo("Question", null).get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val questionSupport = document.data["Question"].toString()
                    questions.add(questionSupport)
                }

                val originalSize = questions.size

                contatore += 1
                adderStructure(questions, questionView, answer1View, answer2View, answer3View)
                answer1View.setOnClickListener {
                    if (contatore < originalSize){
                        if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                            guestCorrectAnswers.set(contatore, answer1View.text.toString())
                        }else{
                            guestCorrectAnswers.add(contatore, answer1View.text.toString())
                        }
                    }else{
                        Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                    }
                }
                answer2View.setOnClickListener {
                    if (contatore < originalSize){
                        if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                            guestCorrectAnswers.set(contatore, answer2View.text.toString())
                        }else{
                            guestCorrectAnswers.add(contatore, answer2View.text.toString())
                        }
                    }else{
                        Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                    }
                }
                answer3View.setOnClickListener {
                    if (contatore < originalSize){
                        if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                            guestCorrectAnswers.set(contatore, answer3View.text.toString())
                        }else{
                            guestCorrectAnswers.add(contatore, answer3View.text.toString())
                        }
                    }else{
                        Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                    }
                }

                save.setOnClickListener {
                    if (!guestCorrectAnswers.size.equals(correctAnswers.size)){
                        guestCorrectAnswers.add("")
                    }
                    contatore+= 1
                    if (contatore >= originalSize){
                        if (contatore == originalSize){
                            for (i in 0 until guestCorrectAnswers.size){
                                if (correctAnswers.get(i).equals(guestCorrectAnswers.get(i))){
                                    score += 1
                                }else{
                                    continue
                                }
                            }
                        }
                        val dialogBuilderScore : AlertDialog.Builder
                        val dialogScore : AlertDialog?
                        val viewScore = LayoutInflater.from(this).inflate(R.layout.popup_score, null, false)
                        dialogBuilderScore = AlertDialog.Builder(this).setView(viewScore)
                        dialogScore = dialogBuilderScore.create()
                        dialogScore.show()

                        val scoreTV = viewScore.scoreTV
                        val leaderboardBtn = viewScore.leaderboardButton
                        val homeBtn = viewScore.homeButton
                        val scoreSupport : MutableMap<String, Any> = hashMapOf()
                        scoreSupport["ID"] = supportID
                        scoreSupport["Nickname"] = nicknameID
                        scoreSupport["Score"] = score.toString()

                        scoreTV.text = "${score}/${correctAnswers.size}"
                        leaderboardBtn.setOnClickListener {
                            db.collection("scores").document().set(scoreSupport)
                            db.collection("scores").whereEqualTo("ID", supportID).orderBy("Score", Query.Direction.DESCENDING).get()
                                .addOnSuccessListener { result ->
                                    scores.clear()
                                    for (document in result){
                                        val arraySupport : ArrayList<String> = ArrayList()
                                        arraySupport.add(document.data["Nickname"].toString())
                                        arraySupport.add(document.data["Score"].toString())
                                        scores.add(arraySupport)
                                    }
                                    val intent = Intent(this, LeaderboardActivity::class.java)
                                    startActivity(intent)
                                }
                        }
                        homeBtn.setOnClickListener {
                            db.collection("scores").document().set(scoreSupport)
                            db.collection("scores").whereEqualTo("ID", supportID).orderBy("Score", Query.Direction.DESCENDING).get()
                                .addOnSuccessListener { result ->
                                    scores.clear()
                                    for (document in result){
                                        val arraySupport : ArrayList<String> = ArrayList()
                                        arraySupport.add(document.data["Nickname"].toString())
                                        arraySupport.add(document.data["Score"].toString())
                                        scores.add(arraySupport)
                                    }
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                        }
                    }else{
                        adderStructure(questions, questionView, answer1View, answer2View, answer3View)
                        answer1View.setOnClickListener {
                            if (contatore < originalSize){
                                if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                                    guestCorrectAnswers.set(contatore, answer1View.text.toString())
                                }else{
                                    guestCorrectAnswers.add(contatore, answer1View.text.toString())
                                }
                            }else{
                                Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                            }
                        }
                        answer2View.setOnClickListener {
                            if (contatore < originalSize){
                                if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                                    guestCorrectAnswers.set(contatore, answer2View.text.toString())
                                }else{
                                    guestCorrectAnswers.add(contatore, answer2View.text.toString())
                                }
                            }else{
                                Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                            }
                        }
                        answer3View.setOnClickListener {
                            if (contatore < originalSize){
                                if (guestCorrectAnswers.size.equals(correctAnswers.size)){
                                    guestCorrectAnswers.set(contatore, answer3View.text.toString())
                                }else{
                                    guestCorrectAnswers.add(contatore, answer3View.text.toString())
                                }
                            }else{
                                Toast.makeText(this, "you can no longer edit the answers", Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                }


            }

    }
}