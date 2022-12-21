package com.seriousgame.clyf.auth
import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore

var supportID : String = ""
var nicknameID : String = ""
var score : Int = 0
var scores : ArrayList<ArrayList<String>> = ArrayList()
@SuppressLint("StaticFieldLeak")
var db = FirebaseFirestore.getInstance()
