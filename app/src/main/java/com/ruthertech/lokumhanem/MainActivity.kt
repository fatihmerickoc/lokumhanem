package com.ruthertech.lokumhanem

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity(),AddLokumFragment.onProfileImageListener, PopupMenu.OnMenuItemClickListener {

    var allLokums : ArrayList<LokumData>? = null
    var kameradanGelenBitmap : Bitmap? = null
    var galeridenGelenUri :Uri? = null
    var perlokum_id : String? = null
    var number: Int =0
    lateinit var myAdapter : LokumRecylerView


    lateinit var mAdView : AdView

    var isAdmin : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)




        var prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var editor = prefs.edit()
        editor.putBoolean("firstStart",false)
        editor.apply()


        init()

        val extras = intent.extras
        if (extras != null){
            val istrue = extras!!.getBoolean("isAdmin")
            isAdmin = istrue

        }


        searchViewDost.setOnClickListener {
           // searchViewDost.onActionViewExpanded();
            searchViewDost.isIconified = false;

        }




        if (isAdmin){
            floatingActionButton.visibility = View.VISIBLE
        }
        else{
            floatingActionButton.visibility = View.INVISIBLE

        }





    }





    override fun onResume() {
        super.onResume()

        var currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser ==null){
            FirebaseAuth.getInstance().signOut()
            floatingActionButton.visibility = View.INVISIBLE
            isAdmin = false
        }


    }

     fun init() {
        readDataFromFirebase()
             floatingActionButton.setOnClickListener {
                 AddLokumFragment().show(supportFragmentManager,"Lokum Ekle")
             }

    }

    private fun readDataFromFirebase() {
        allLokums = ArrayList<LokumData>()
        var reference = FirebaseDatabase.getInstance().reference
        var sorgu = reference.child("lokumlar").orderByKey().addListenerForSingleValueEvent( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (gelenData in p0.children){
                    var lokumObje  = LokumData()
                    lokumObje?.lokumadi= gelenData.getValue(LokumData::class.java)?.lokumadi
                    lokumObje?.lokumfiyati= gelenData.getValue(LokumData::class.java)?.lokumfiyati
                    lokumObje?.stok= gelenData.getValue(LokumData::class.java)?.stok
                    lokumObje?.lokumid= gelenData.getValue(LokumData::class.java)?.lokumid

                    lokumObje?.lokum_image = gelenData.getValue(LokumData::class.java)?.lokum_image

                    allLokums!!.add(lokumObje)

                }

                if (allLokums!!.size > 0){
                     myAdapter = LokumRecylerView(allLokums!!,isAdmin,this@MainActivity)
                    lokum_recylerview.adapter = myAdapter
                    lokum_recylerview.layoutManager =  LinearLayoutManager(applicationContext)
                    searchViewDost.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {

                            myAdapter.filter.filter(newText)
                            return false
                        }

                    })


                }
                else{
                    DynamicToast.makeWarning(this@MainActivity, "Lokum Yok!").show();

                }



            }

        })
    }

    override fun getImageWAY(imagePath: Uri?) {
        galeridenGelenUri = imagePath
        val cr = baseContext.contentResolver
        val inputStream: InputStream? = cr.openInputStream(imagePath!!)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
      var   data = baos.toByteArray()
        uploadPhotoFirebase(data)

    }

    public fun showMenu(v: View){
        var popup = PopupMenu(this,v)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.anamenu)
        popup.show()

    }


    override fun onMenuItemClick(itema: MenuItem?): Boolean {

        when(itema?.itemId){
            R.id.exitmenu->{
                    exit()
                return true
            }
            R.id.lokumlar->{
                return true
            }
            R.id.firmalar->{

                if (isAdmin){
                    FirmaEkle().show(supportFragmentManager,"Firma EKle")
                }
                else{
                    DynamicToast.makeError(this,"Yetkiniz yok!",Toast.LENGTH_LONG).show()
                }
                return true
            }
            R.id.siparissorgula->{
                if (isAdmin){
                    goSiparisSorgula()
                }
                else{
                    DynamicToast.makeError(this,"Yetkiniz yok!",Toast.LENGTH_LONG).show()
                }
            }
            R.id.firmaSil->{
                if (isAdmin){
                    var intent = Intent(this,FirmaSil::class.java)
                    startActivity(intent)
                }
                else{
                    DynamicToast.makeError(this,"Yetkiniz yok!",Toast.LENGTH_LONG).show()
                }
            }

        }

        return true
    }

    private fun goSiparisSorgula() {
        var intent = Intent(this,OrderSiralama::class.java)
        startActivity(intent)
    }

    private fun exit() {

        var prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var editor = prefs.edit()
        editor.putBoolean("firstStart",true)
        editor.apply()

        FirebaseAuth.getInstance().signOut()
        isAdmin  = false

        var intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()




    }

    override fun getImageBitmap(bitmap: Bitmap) {
        kameradanGelenBitmap = bitmap

        var stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,90,stream)
        uploadPhotoFirebase(stream.toByteArray())

     }

  private fun uploadPhotoFirebase(result: ByteArray? ) {

        var lokumid : String? = null

        var reference = FirebaseDatabase.getInstance().reference
        var sorgu = reference.child("lokumlar").addListenerForSingleValueEvent( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (gelenData in p0.children){
                    var lokumObje  = LokumData()
                    lokumid = gelenData.getValue(LokumData::class.java)?.lokumid!!

                }


            }

        })



        var key = FirebaseDatabase.getInstance().reference.push().key

        var storageRef = FirebaseStorage.getInstance().reference
        val ref = storageRef.child("lokumimages/${key}/image")
        var uploadTask = ref.putBytes(result!!)



        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl

        }

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                var photoURL:String = ""
                ref.downloadUrl.addOnSuccessListener {uri ->

                    photoURL = uri.toString()


                    FirebaseDatabase.getInstance().reference
                        .child("lokumlar")
                        .child(lokumid!!)
                        .child("lokum_image")
                        .setValue(photoURL)


                }
            }
            else {

            }


        }




    }

    fun random(): Int? {
        return number++
    }



}
