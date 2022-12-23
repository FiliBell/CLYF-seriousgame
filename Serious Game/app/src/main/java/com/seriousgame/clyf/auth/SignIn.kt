package com.seriousgame.clyf.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.seriousgame.clyf.R
import com.seriousgame.clyf.admin.ViewAdminActivity
import com.seriousgame.clyf.databinding.ActivitySignInBinding
import kotlinx.android.synthetic.main.popup_forgotpass.view.*


class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding //feature that allows you to more easily write code that interacts with views (replaces findViewById) - F.
    private lateinit var firebaseAuth: FirebaseAuth //define FirebaseAuth - F.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater) //binding - S.
        setContentView(binding.root)    //get XML ActivitySignIn root with binding - F.

        firebaseAuth = FirebaseAuth.getInstance()
        //assign to firebaseAuth a method that returns an instance of this class corresponding to the default FirebaseApp instance - F.

        binding.signinButton.setOnClickListener {   //at signInButton click - F.
            val email = binding.emailET.text.toString() //email field - F.
            val pass = binding.passET.text.toString()   //pass field - F.

            //start if - S.
            if (email.isNotEmpty() && pass.isNotEmpty()){   //check form field - F.
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {    //check user data in Firebase - F.

                        //1st inner if - S.
                        if (it.isSuccessful) { //successful case - S.
                            if (firebaseAuth.currentUser?.isEmailVerified == true){
                                supportID = email
                                val intent = Intent(this, ViewAdminActivity::class.java) //define intent - S.
                                startActivity(intent) //takes you to Admin activity - S.
                            }else{
                                Toast.makeText(this, "please verify your email", Toast.LENGTH_LONG).show() //error message - F.
                            }
                        } else {
                            Toast.makeText(this, "wrong email or password", Toast.LENGTH_LONG).show() //error message - F.
                        }
                        //end 1st inner if - S.
                    }
            } else{
                Toast.makeText(this, "One of the fields is empty", Toast.LENGTH_LONG).show()    //error message - F.
            }
            //end if - S.
        }

        binding.forgotPassButton.setOnClickListener {
            //popup creation - F.
            val dialogBuilderForgot : AlertDialog.Builder
            val dialogForgot : AlertDialog?
            val viewForgot = LayoutInflater.from(this).inflate(R.layout.popup_forgotpass, null, false)
            dialogBuilderForgot = AlertDialog.Builder(this).setView(viewForgot)
            dialogForgot = dialogBuilderForgot.create()
            dialogForgot.show()

            val forgetPass = viewForgot.nextForgetPass
            forgetPass.setOnClickListener {
                val email = viewForgot.emailForgotPass.text.toString()
                if (!TextUtils.isEmpty(email)){
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "check your email", Toast.LENGTH_LONG).show()
                            dialogForgot.dismiss()
                        }else{
                            Toast.makeText(this, "entered email does not exist", Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "email field is empty", Toast.LENGTH_LONG).show()    //error message - F.
                }
            }
        }

        binding.signinSignup.setOnClickListener {   //if you are not already registered it takes us to SignUp activity - F.
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }


    }
}