package com.ruthertech.lokumhanem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_order_siralama.*

class OrderSiralama : AppCompatActivity(),AdapterView.OnItemSelectedListener,
    PopupMenu.OnMenuItemClickListener {

    lateinit var tumFirmalar : ArrayList<String>
    var oFirmayaOzelSiparisler = ArrayList<OrderData> ()

    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_siralama)
        getAllCompanies()


        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)



        spinner.onItemSelectedListener = this

        menuView.setOnClickListener {
            showMenuForOrderActivity(menuView)
        }




    }

     fun showMenuForOrderActivity(v: View){
        var popup = PopupMenu(this,v)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.anamenu)
        popup.show()

    }

    private fun oFirmayaOzelSiparisleriGetir(pos : Int) {
        var ref  = FirebaseDatabase.getInstance().reference
        var sorgu = ref.child("orders").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                  var  oankiSiparisNesnesi= data.getValue(OrderData::class.java)

                    if (oankiSiparisNesnesi?.company_name.toString() == tumFirmalar[pos]){

                        var orderValue  = OrderData()

                               orderValue.order_id = oankiSiparisNesnesi?.order_id
                               orderValue.amount = oankiSiparisNesnesi?.amount
                               orderValue.company_name = oankiSiparisNesnesi?.company_name
                               orderValue.date = oankiSiparisNesnesi?.date
                               orderValue.product_id = oankiSiparisNesnesi?.product_id
                               orderValue.product_name = oankiSiparisNesnesi?.product_name
                                orderValue.product_price = oankiSiparisNesnesi?.product_price
                                orderValue.completed = oankiSiparisNesnesi?.completed

                               oFirmayaOzelSiparisler.add(orderValue)


                        var adapter = OrderRecylerView(applicationContext,oFirmayaOzelSiparisler)
                        gelenSiparisData.adapter = adapter
                        gelenSiparisData.layoutManager =  LinearLayoutManager(applicationContext)



                    }
                    }




            }



        })



    }

    private fun getAllCompanies() {
        tumFirmalar = ArrayList<String>()
        var ref = FirebaseDatabase.getInstance().reference
        var sorgu = ref.child("firmalar").addListenerForSingleValueEvent( object  : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    var gelenData = data?.getValue(FirmaData::class.java)
                    if (gelenData?.pasif == false){
                        var firmaAdi  = gelenData?.firmaAdi
                        tumFirmalar.add(firmaAdi!!)
                    }

                }

             var  adapter =  ArrayAdapter<String>(this@OrderSiralama,R.layout.spinner_custom,tumFirmalar)
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinner.adapter = adapter

            }
        })

        
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        oFirmayaOzelSiparisleriGetir(position)
        oFirmayaOzelSiparisler.clear()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {


        when(item?.itemId){
            R.id.exitmenu->{
                exit()
                return true
            }
            R.id.lokumlar->{

                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("isAdmin",true)
                startActivity(intent)
                finish()
                return true

            }
            R.id.firmalar->{
                FirmaEkle().show(supportFragmentManager,"Firma EKle")
                return true
            }
            R.id.firmaSil->{
                var intent = Intent(this,FirmaSil::class.java)
                startActivity(intent)
                return true
            }
            R.id.siparissorgula->{
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
        var intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this,MainActivity::class.java)
        intent.putExtra("isAdmin",true)
        startActivity(intent)
        finish()
    }

}
