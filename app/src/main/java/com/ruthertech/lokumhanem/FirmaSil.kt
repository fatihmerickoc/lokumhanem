package com.ruthertech.lokumhanem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_firma_sil.*
import kotlinx.android.synthetic.main.fragment_firma_sil.view.*


class FirmaSil : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {


    lateinit var tumFirmalar : ArrayList<FirmaData>





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_firma_sil)

        init()

        firmalar_menu_img.setOnClickListener {
            showMenu(firmalar_menu_img)
        }
        firma_ara.setOnClickListener {
            firma_ara.isIconified = false;
        }







    }



         public fun  init() {
        getAllCompanies()
    }

         private fun getAllCompanies() {
             tumFirmalar = ArrayList<FirmaData>()
             var ref = FirebaseDatabase.getInstance().reference
             var sorgu = ref.child("firmalar").addListenerForSingleValueEvent( object  :
                 ValueEventListener {

                 override fun onCancelled(p0: DatabaseError) {

                 }
                 override fun onDataChange(p0: DataSnapshot) {
                     for (data in p0.children){
                         var obje = FirmaData()
                         obje.firmaAdi = data?.getValue(FirmaData::class.java)?.firmaAdi
                         obje.firma_id = data?.getValue(FirmaData::class.java)?.firma_id
                         obje.pasif = data?.getValue(FirmaData::class.java)?.pasif
                         tumFirmalar.add(obje)
                     }

                     if (tumFirmalar.size >0){
                          var myAdapter = FirmaSilRecylerView(tumFirmalar!!,this@FirmaSil)
                         firmalar_recyler_view.adapter = myAdapter
                         firmalar_recyler_view.layoutManager =  LinearLayoutManager(applicationContext)
                         firma_ara.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
                         DynamicToast.makeWarning(this@FirmaSil, "Firma Yok!").show();

                     }


                 }
             })


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
                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("isAdmin",true)
                startActivity(intent)
            }
            R.id.firmalar->{
                    FirmaEkle().show(supportFragmentManager,"Firma EKle")
                return true
            }
            R.id.siparissorgula->{
                var intent = Intent(this,OrderSiralama::class.java)
                startActivity(intent)
            }
            R.id.firmaSil->{
           return true
            }

        }

        return true
    }

        private fun exit() {

        var prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var editor = prefs.edit()
        editor.putBoolean("firstStart",true)
        editor.apply()

        FirebaseAuth.getInstance().signOut()

        var intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()




    }





}
