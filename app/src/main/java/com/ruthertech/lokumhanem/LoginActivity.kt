package com.ruthertech.lokumhanem

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var isAdmin : Boolean = true
    lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyAuthStateListener()

        var prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var firstStart  : Boolean= prefs.getBoolean("firstStart",true)


        if (firstStart){
            login_btn.setOnClickListener {
                login()

            }
        }
        else{
                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("isAdmin",isAdmin)
                startActivity(intent)
                finish()


        }


        logo_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fatihmerickoc.com"))
            startActivity(intent)
        }


    }


    private fun login()  {

        if (et_email.text.toString().isNotEmpty()&&et_password.text.toString().isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(et_email.text.toString(),et_password.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    isAdmin = true
                    var intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("isAdmin",isAdmin)
                    startActivity(intent)
                    finish()

                }
                else{
                    isAdmin = false

                }

            }
            if (et_email.text.toString() == "user" && et_password.text.toString() == "4562"){
                isAdmin = false
                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("isAdmin",isAdmin)
                startActivity(intent)
            }


        }
        else{
            Toast.makeText(this,"Lütfen tüm alanları doldurun",Toast.LENGTH_LONG).show()
        }




    }

    override fun onRestart() {
        super.onRestart()

        Toast.makeText(this,"OnRestart",Toast.LENGTH_LONG).show()


    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }

    private fun initMyAuthStateListener() {

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var currentUser= p0.currentUser

                if(currentUser != null){
                    isAdmin = true
                }

            }
        }
    }





}
