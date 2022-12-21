package com.seriousgame.clyf.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seriousgame.clyf.MainActivity
import com.seriousgame.clyf.R
import com.seriousgame.clyf.auth.db
import com.seriousgame.clyf.auth.supportID
import kotlinx.android.synthetic.main.activity_view_admin.*
import kotlinx.android.synthetic.main.popup_create.view.*
import kotlinx.android.synthetic.main.popup_delete.view.*
import kotlinx.android.synthetic.main.popup_exit.view.*
import kotlinx.android.synthetic.main.popup_modify.view.*
import kotlinx.android.synthetic.main.popup_questions.view.*
import kotlinx.android.synthetic.main.popup_quizname.view.*

class ViewAdminActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var contenitore: ArrayList<ArrayList<String>>
    lateinit var adapter: QuizAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshDatabase(x: ArrayList<String>) {
        if (contenitore.size == 0){
            contenitore.add(x)
            adapter.notifyDataSetChanged()
        }
        else{
            var contatore = 0
            for (i in 0 until contenitore.size){
                contatore += 1
                val confrontoSupport = contenitore.get(i)

                if (x.get(0) == confrontoSupport.get(0)){
                    break
                }
                else if (contatore != contenitore.size){
                    continue
                }
                else{
                    contenitore.add(x)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_admin)

        recyclerView = findViewById(R.id.recyclerViewId)
        contenitore = ArrayList()
        layoutManager = LinearLayoutManager(this)

        adapter = QuizAdapter(contenitore, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        refresh.setOnClickListener {
            db.collection(supportID).whereNotEqualTo("Question", null).get()
                .addOnSuccessListener { result ->
                    for (document in result){
                        val question = document.data["Question"].toString()
                        val answer1 = document.data["Answer1"].toString()
                        val answer2 = document.data["Answer2"].toString()
                        val answer3 = document.data["Answer3"].toString()
                        val correctAnswer = document.data["Correct_answer"].toString()

                        val arraySupport : ArrayList<String> = ArrayList()
                        arraySupport.add(question)
                        arraySupport.add(answer1)
                        arraySupport.add(answer2)
                        arraySupport.add(answer3)
                        arraySupport.add(correctAnswer)

                        refreshDatabase(arraySupport)
                    }
                }
        }

        adapter.notifyDataSetChanged()

        create.setOnClickListener {
            //popup creation - Y.
            val dialogBuilderCreate : AlertDialog.Builder
            val dialogCreate : AlertDialog?
            val viewCreate = LayoutInflater.from(this).inflate(R.layout.popup_create, null, false)
            dialogBuilderCreate = AlertDialog.Builder(this).setView(viewCreate)
            dialogCreate = dialogBuilderCreate.create()
            dialogCreate.show()

            db.collection(supportID).whereNotEqualTo("Quiz_name", null).get()
                .addOnSuccessListener { result ->
                    var control = true
                    for (document in result){
                        control = false
                        Toast.makeText(this, "you have already created a quiz", Toast.LENGTH_LONG).show()
                        dialogCreate.dismiss()
                    }
                    if (control){
                        //association of popup elements to variables - Y.
                        val quizNameField = viewCreate.editText
                        val questionButton = viewCreate.question_menu
                        val save1 = viewCreate.save1

                        var quiz : MutableMap<String, Any>  //map that will contain the data that the user wants to enter - Y.
                        val quizName : MutableMap<String, Any> = hashMapOf()    //map that will contain the name of the quiz - Y.

                        val container : ArrayList<MutableMap<String, Any>> = ArrayList()  //array containing all the questions the user wants to enter (array of maps) - Y.

                        questionButton.setOnClickListener {
                            //popup creation - G.
                            val dialogBuilderQuestions : AlertDialog.Builder
                            val dialogQuestions : AlertDialog?
                            val viewQuestions = LayoutInflater.from(this).inflate(R.layout.popup_questions, null, false)
                            dialogBuilderQuestions = AlertDialog.Builder(this).setView(viewQuestions)
                            dialogQuestions = dialogBuilderQuestions.create()
                            dialogQuestions.show()

                            //association of popup elements to variables - G.
                            val question = viewQuestions.question
                            val answer1 = viewQuestions.answer1
                            val answer2 = viewQuestions.answer2
                            val answer3 = viewQuestions.answer3
                            val correctAnswer = viewQuestions.correctanswer
                            val save1 = viewQuestions.save2

                            save1.setOnClickListener {
                                //check if the fields are not empty - G.
                                if (!TextUtils.isEmpty(question.text) && !TextUtils.isEmpty(answer1.text) && !TextUtils.isEmpty(answer2.text) && !TextUtils.isEmpty(answer3.text) && !TextUtils.isEmpty(correctAnswer.text)){
                                    //check if two answers are identical - G.
                                    if ((answer1.text.toString() != answer2.text.toString()) && ((answer2.text.toString() != answer3.text.toString()) && (answer3.text.toString() != answer1.text.toString()))){
                                        //check if the correct answer is equal to one of the answers - G.
                                        if ((correctAnswer.text.toString() == answer1.text.toString()) || ((correctAnswer.text.toString() == answer2.text.toString()) || (correctAnswer.text.toString() == answer3.text.toString()))){

                                            //map creation and assignment of values entered by the user - G.
                                            quiz = hashMapOf()
                                            quiz["Question"] = question.text.toString()
                                            quiz["Answer1"] = answer1.text.toString()
                                            quiz["Answer2"] = answer2.text.toString()
                                            quiz["Answer3"] = answer3.text.toString()
                                            quiz["Correct_answer"] = correctAnswer.text.toString()

                                            container.add(quiz)   //adding quizzes to container - G.
                                            dialogQuestions.dismiss()   //pop-up close - G.
                                        }else{
                                            Toast.makeText(this, "The correct answer must be equal to one of the answers", Toast.LENGTH_LONG).show()    //error message - G.
                                        }
                                    }else{
                                        Toast.makeText(this, "You cannot enter two identical answers", Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    Toast.makeText(this, "One of the fields is empty", Toast.LENGTH_LONG).show()    //error message - G.
                                }
                            }
                        }

                        save1.setOnClickListener {
                            if (!TextUtils.isEmpty(quizNameField.text)){ //check if the "quiz name" field is not empty - S.
                                quizName["Quiz_name"] = quizNameField.text.toString()    //database field creation - S.
                                db.collection(supportID).document().set(quizName)    //add quiz name to database - S.
                                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                                for (i in 0 until container.size){
                                    val support = container.get(i)  //we take the i-th element from the container and insert it into support - S.

                                    db.collection(supportID).document().set(support)    //inserting the i-th element into the database - S.
                                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                        .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                                }

                                dialogCreate.dismiss()  //pop-up close - S.
                            }else{
                                Toast.makeText(this, "Quiz name field empty", Toast.LENGTH_LONG).show() //error message - S.
                            }
                        }
                    }

                }

        }

        add.setOnClickListener {
            //popup creation - F.
            val dialogBuilderAdd : AlertDialog.Builder
            val dialogAdd : AlertDialog?
            val viewAdd = LayoutInflater.from(this).inflate(R.layout.popup_questions, null, false)
            dialogBuilderAdd = AlertDialog.Builder(this).setView(viewAdd)
            dialogAdd = dialogBuilderAdd.create()
            dialogAdd.show()

            //association of popup elements to variables - F.
            val question = viewAdd.question
            val answer1 = viewAdd.answer1
            val answer2 = viewAdd.answer2
            val answer3 = viewAdd.answer3
            val correctAnswer = viewAdd.correctanswer
            val save = viewAdd.save2

            var quiz : MutableMap<String, Any>  //map that will contain the data that the user wants to enter - F.

            save.setOnClickListener {
                //check if the fields are not empty - F.
                if (!TextUtils.isEmpty(question.text) && !TextUtils.isEmpty(answer1.text) && !TextUtils.isEmpty(answer2.text) && !TextUtils.isEmpty(answer3.text) && !TextUtils.isEmpty(correctAnswer.text)){
                    //check if two answers are identical - G.
                    if ((answer1.text.toString() != answer2.text.toString()) && ((answer2.text.toString() != answer3.text.toString()) && (answer3.text.toString() != answer1.text.toString()))){
                        //check if the correct answer is equal to one of the answers - F.
                        if ((correctAnswer.text.toString() == answer1.text.toString()) || ((correctAnswer.text.toString() == answer2.text.toString()) || (correctAnswer.text.toString() == answer3.text.toString()))){

                            db.collection(supportID).whereNotEqualTo("Quiz_name", null).get()
                                .addOnSuccessListener { result ->
                                    var controlSupport = false
                                    for (document in result){
                                        controlSupport = true
                                    }
                                    if (controlSupport){
                                        //map creation and assignment of values entered by the user - F.
                                        quiz = hashMapOf()
                                        quiz["Question"] = question.text.toString()
                                        quiz["Answer1"] = answer1.text.toString()
                                        quiz["Answer2"] = answer2.text.toString()
                                        quiz["Answer3"] = answer3.text.toString()
                                        quiz["Correct_answer"] = correctAnswer.text.toString()

                                        db.collection(supportID).document().set(quiz)   //entering data into the database - F.
                                            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                                        dialogAdd.dismiss() //pop-up close - F.
                                    }else{

                                        //map creation and assignment of values entered by the user - F.
                                        quiz = hashMapOf()
                                        quiz["Question"] = question.text.toString()
                                        quiz["Answer1"] = answer1.text.toString()
                                        quiz["Answer2"] = answer2.text.toString()
                                        quiz["Answer3"] = answer3.text.toString()
                                        quiz["Correct_answer"] = correctAnswer.text.toString()

                                        //popup creation - F.
                                        val dialogBuilderAddName : AlertDialog.Builder
                                        val dialogAddName : AlertDialog?
                                        val viewAddName = LayoutInflater.from(this).inflate(R.layout.popup_quizname, null, false)
                                        dialogBuilderAddName = AlertDialog.Builder(this).setView(viewAddName)
                                        dialogAddName = dialogBuilderAddName.create()
                                        dialogAddName.show()

                                        var quizNameAdder : MutableMap<String, Any>
                                        val saveAddQuizName = viewAddName.saveAddQuizName

                                        saveAddQuizName.setOnClickListener {
                                            val quizNameET = viewAddName.addQuizName.text.toString()
                                            if (!TextUtils.isEmpty(quizNameET)){
                                                db.collection(supportID).document().set(quiz)   //entering data into the database - F.
                                                quizNameAdder = hashMapOf()
                                                quizNameAdder["Quiz_name"] = quizNameET
                                                db.collection(supportID).document().set(quizNameAdder)

                                                dialogAddName.dismiss() //pop-up close - F.
                                                dialogAdd.dismiss() //pop-up close - F.
                                            }else{
                                                Toast.makeText(this, "quizName field is empty", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                    }

                                }

                        }else{
                            Toast.makeText(this, "The correct answer must be equal to one of the answers", Toast.LENGTH_LONG).show()    //error message - F.
                        }
                    }else{
                        Toast.makeText(this, "You cannot enter two identical answers", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "One of the fields is empty", Toast.LENGTH_LONG).show()    //error message - F.
                }
            }

        }

        modify.setOnClickListener {

            //creation of support variables - Y.
            val questionList : ArrayList<String> = ArrayList()
            var questionToUpdate : String? = null

            //popup creation - Y.
            val dialogBuilderModify : AlertDialog.Builder
            val dialogModify : AlertDialog?
            val viewModify = LayoutInflater.from(this).inflate(R.layout.popup_modify, null, false)
            dialogBuilderModify = AlertDialog.Builder(this).setView(viewModify)
            dialogModify = dialogBuilderModify.create()
            dialogModify.show()

            val modifyButton = viewModify.modify1

            db.collection(supportID).whereNotEqualTo("Quiz_name", null).get()
                .addOnSuccessListener { result ->
                    var controlSupport = false
                    var quizID = ""
                    for (document in result){
                        controlSupport = true
                        quizID = document.id
                    }
                    if (controlSupport){
                        //query the database and take all the documents with the component question not null - Y.
                        db.collection(supportID).whereNotEqualTo("Question", null).get()
                            .addOnSuccessListener { result ->
                                var controlSupport = false
                                for (document in result) {
                                    questionList.add(document.data["Question"].toString())   //insert the data contained in the question field into the array - Y.
                                    controlSupport = true
                                }

                                if (controlSupport){
                                    //creating the spinner and inserting data into it - Y.
                                    val spinner : Spinner = viewModify.spinner
                                    val adapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionList)
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner.adapter = adapter

                                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                            questionToUpdate = questionList[position]   //insertion of the question chosen by the user in the support variable - Y.
                                        }
                                        override fun onNothingSelected(p0: AdapterView<*>?) {
                                            Toast.makeText(applicationContext, "Nothing selected", Toast.LENGTH_LONG).show()    //error message - Y.
                                        }
                                    }
                                }else{
                                    db.collection(supportID).document(quizID).delete()
                                    Toast.makeText(this, "there is nothing to change", Toast.LENGTH_LONG).show()
                                    dialogModify.dismiss()
                                }
                            }

                        modifyButton.setOnClickListener {
                            if (questionToUpdate != null){
                                val originalSize = contenitore.size
                                for (i in 0 until originalSize){
                                    val supporto = contenitore.get(i)
                                    val supporto2 = supporto.get(0)

                                    if (supporto2 == questionToUpdate){
                                        contenitore.removeAt(i)
                                    }
                                }

                                //popup creation - G.
                                val dialogBuilderQuestion : AlertDialog.Builder
                                val dialogQuestion : AlertDialog?
                                val viewQuestion = LayoutInflater.from(this).inflate(R.layout.popup_questions, null, false)
                                dialogBuilderQuestion = AlertDialog.Builder(this).setView(viewQuestion)
                                dialogQuestion = dialogBuilderQuestion.create()
                                dialogQuestion.show()

                                //association of popup elements to variables - G.
                                val question = viewQuestion.question
                                val answer1 = viewQuestion.answer1
                                val answer2 = viewQuestion.answer2
                                val answer3 = viewQuestion.answer3
                                val correctAnswer = viewQuestion.correctanswer
                                val save = viewQuestion.save2

                                var quiz : MutableMap<String, Any>  //map that will contain the data that the user wants to enter - G.

                                save.setOnClickListener {
                                    //check if the fields are not empty - G.
                                    if (!TextUtils.isEmpty(question.text) && !TextUtils.isEmpty(answer1.text) && !TextUtils.isEmpty(answer2.text) && !TextUtils.isEmpty(answer3.text) && !TextUtils.isEmpty(correctAnswer.text)){
                                        //check if two answers are identical - G.
                                        if ((answer1.text.toString() != answer2.text.toString()) && ((answer2.text.toString() != answer3.text.toString()) && (answer3.text.toString() != answer1.text.toString()))){
                                            //check if the correct answer is equal to one of the answers - G.
                                            if ((correctAnswer.text.toString() == answer1.text.toString()) || ((correctAnswer.text.toString() == answer2.text.toString()) || (correctAnswer.text.toString() == answer3.text.toString()))){

                                                //map creation and assignment of values entered by the user - G.
                                                quiz = hashMapOf()
                                                quiz["Question"] = question.text.toString()
                                                quiz["Answer1"] = answer1.text.toString()
                                                quiz["Answer2"] = answer2.text.toString()
                                                quiz["Answer3"] = answer3.text.toString()
                                                quiz["Correct_answer"] = correctAnswer.text.toString()

                                                //query the database and take all the documents with the component question equals to questionToUpdate - G.
                                                db.collection(supportID)
                                                    .whereEqualTo("Question", questionToUpdate)
                                                    .get()
                                                    .addOnSuccessListener { documents ->
                                                        for (document in documents) {
                                                            val id = document.id    //we take the document ID and put it into a support variable - G.
                                                            db.collection(supportID).document(id).update(quiz)  //update - G.
                                                        }
                                                    }

                                                dialogQuestion.dismiss()    //pop-up close - G.
                                                dialogModify.dismiss()  //pop-up close - G.
                                            }else{
                                                Toast.makeText(this, "The correct answer must be equal to one of the answers", Toast.LENGTH_LONG).show()    //error message - G.
                                            }
                                        }
                                        else{
                                            Toast.makeText(this, "You cannot enter two identical answers", Toast.LENGTH_LONG).show()
                                        }
                                    }else{
                                        Toast.makeText(this, "One of the fields is empty", Toast.LENGTH_LONG).show()    //error message - G.
                                    }
                                }

                            }else{
                                Toast.makeText(this, "You need to select one question first!", Toast.LENGTH_LONG).show()    //error message - G.
                            }
                        }
                    }else{
                        Toast.makeText(this, "there is nothing to change", Toast.LENGTH_LONG).show()
                        dialogModify.dismiss()
                    }

                }
        }

        delete.setOnClickListener {

            //creation of support variables - S.
            val questionList : ArrayList<String> = ArrayList()
            var questionToDelete : String? = null

            //popup creation - S.
            val dialogBuilderDelete : AlertDialog.Builder
            val dialogDelete : AlertDialog?
            val viewDelete = LayoutInflater.from(this).inflate(R.layout.popup_delete, null, false)
            dialogBuilderDelete = AlertDialog.Builder(this).setView(viewDelete)
            dialogDelete = dialogBuilderDelete.create()
            dialogDelete.show()

            val deleteButton = viewDelete.delete1

            db.collection(supportID).whereNotEqualTo("Quiz_name", null).get()
                .addOnSuccessListener { result ->
                    var quizID = ""
                    var controlSupport = false
                    for (document in result){
                        controlSupport = true
                        quizID = document.id

                    }
                    if (controlSupport){
                        //query the database and take all the documents with the component question not null - S.
                        db.collection(supportID).whereNotEqualTo("Question", null)
                            .get()
                            .addOnSuccessListener { result ->
                                var controlSupport = false
                                for (document in result) {
                                    questionList.add(document.data["Question"].toString())  //insert the data contained in the question field into the array - S.
                                    controlSupport = true
                                }

                                if (controlSupport){
                                    //creating the spinner and inserting data into it - S.
                                    val spinner : Spinner = viewDelete.spinner_delete
                                    val adapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionList)
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner.adapter = adapter

                                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                            questionToDelete = questionList[position]   //insertion of the question chosen by the user in the support variable - S.
                                        }
                                        override fun onNothingSelected(p0: AdapterView<*>?) {
                                            Toast.makeText(applicationContext, "Nothing selected", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }else{
                                    db.collection(supportID).document(quizID).delete()
                                    Toast.makeText(this, "there is nothing to delete", Toast.LENGTH_LONG).show()
                                    dialogDelete.dismiss()
                                }
                            }

                        deleteButton.setOnClickListener {
                            if (questionToDelete != null){
                                val originalSize = contenitore.size
                                Log.d("CONTENITORE", contenitore.toString())
                                for (i in 0 until originalSize){
                                    val supporto = contenitore.get(i)
                                    val supporto2 = supporto.get(0)

                                    if (supporto2 == questionToDelete){
                                        contenitore.removeAt(i)
                                        adapter.notifyDataSetChanged()
                                        break
                                    }
                                }

                                //query the database and take all the documents with the component question equals to questionToDelete - S.
                                db.collection(supportID)
                                    .whereEqualTo("Question", questionToDelete)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            val id = document.id    //we take the document ID and put it into a support variable - S.
                                            db.collection(supportID).document(id).delete()  //delete - S.
                                        }
                                    }

                                dialogDelete.dismiss()  //pop-up close - S.
                            }
                        }
                    }else{
                        Toast.makeText(this, "there is nothing to delete", Toast.LENGTH_LONG).show()
                        dialogDelete.dismiss()
                    }

                }
        }

        exit.setOnClickListener {

            db.collection("quizPasswords").whereEqualTo("ID", supportID).get()
                .addOnSuccessListener { result ->
                    var controlSupport = true
                    for (document in result){
                        controlSupport = false
                    }
                    if (controlSupport){

                        val dialogBuilderExit : AlertDialog.Builder
                        val dialogExit : AlertDialog?
                        val viewExit = LayoutInflater.from(this).inflate(R.layout.popup_exit, null, false)
                        dialogBuilderExit = AlertDialog.Builder(this).setView(viewExit)
                        dialogExit = dialogBuilderExit.create()
                        dialogExit.show()

                        val quizPasswordET = viewExit.QuizPasswordEditText
                        val quizPasswordSave = viewExit.QuizPasswordSave
                        var quizPassword : String
                        var dataAdder : MutableMap<String, Any>

                        quizPasswordSave.setOnClickListener {
                            quizPassword = quizPasswordET.text.toString()
                            if (!TextUtils.isEmpty(quizPassword)){
                                dataAdder = hashMapOf()
                                dataAdder["quizPassword"] = quizPassword
                                dataAdder["ID"] = supportID


                                db.collection("quizPasswords").whereEqualTo("quizPassword", quizPassword).get()
                                    .addOnSuccessListener{ result ->
                                        var supportPass = ""
                                        for (document in result){
                                            supportPass = document.data["quizPassword"].toString()
                                        }
                                        if (supportPass == quizPassword){
                                            Toast.makeText(this, "Password already used", Toast.LENGTH_LONG).show()
                                        }else{
                                            db.collection("quizPasswords").document().set(dataAdder)
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                            }else{
                                Toast.makeText(this, "Quiz password field empty", Toast.LENGTH_LONG).show()
                            }
                        }

                    }else{
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                }

        }

    }



}