package com.ruthertech.lokumhanem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.*
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_lokum_details.view.*
import kotlinx.android.synthetic.main.fragment_lokum_details.view.lokum_detail_fiyat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LokumDetails(isAdmin: Boolean, lokumId: String?, lokumfiyati: String?, lokumadi: String?, lokumImage: String?) : DialogFragment(){



         var gelenId  =lokumId
    var gelenFiyat  = lokumfiyati
    var gelenAd = lokumadi
     var gelenImage  : String? = null

    var isAdmin = isAdmin

    var mContext :FragmentActivity? = null
    var eskiStok : String?  = null

      lateinit  var lokum_image : ImageView
      lateinit    var lokum_adi : TextView
      lateinit    var lokum_fiyat: TextView
      lateinit  var lokum_stok: TextView
      lateinit    var layoutopener_img :ImageView
      lateinit    var sirket_adi : TextView
      lateinit   var satilan_miktar :TextView
      lateinit   var kaydet_buton  : Button
      lateinit var  addImagebtn : ImageView
      lateinit var spinnerFirma : Spinner

  lateinit  var geciciFiyat : String


    var yardimciFirmalar = ArrayList<String>()

    var number = 1






         override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_lokum_details, container, false)
        mContext = activity

        var kapatilacaklayout = view.kapatilacaklayout
         lokum_image = view.lokum_Detail_image
         lokum_adi = view.lokum_detail_lokumname
         lokum_fiyat = view.lokum_detail_fiyat
         lokum_stok = view.lokum_detail_stok

        // sirket_adi = view.lokum_detail_sirket_Adi
         satilan_miktar = view.lokum_detail_satilanmiktar
         kaydet_buton =view.lokum_detail_kaydetbutton
         layoutopener_img = view.add_img_btn
         spinnerFirma = view.spinnerFirma


             spinner()
             init()







             layoutopener_img.setOnClickListener {
                 kapatilacaklayout.visibility  =View.VISIBLE
             }
             kaydet_buton.setOnClickListener {
                     if (yardimciFirmalar.size > 0){

                         if (spinnerFirma.selectedItem.toString().isNotEmpty()&&satilan_miktar.text.toString().isNotEmpty()){

                             if (satilan_miktar.text.toString().toInt() <= eskiStok!!.toInt() && satilan_miktar!!.text.toString().toInt() > 0)  {
                                 var ref = FirebaseDatabase.getInstance().reference
                                 var siparisId = ref.push().key

                                 var obje = OrderData()
                                 obje.order_id = siparisId
                                 obje.product_price= geciciFiyat
                                 obje.product_id = gelenId
                                 obje.amount = satilan_miktar.text.toString()
                                 obje.product_name = lokum_adi.text.toString()
                                 obje.company_name = spinnerFirma.selectedItem.toString()
                                 obje.completed = false
                                 obje.date = getCurrentDate()

                                 ref.child("orders").child(obje.order_id!!).setValue(obje)




                                 ref.child("lokumlar").child(gelenId!!).addListenerForSingleValueEvent( object : ValueEventListener{
                                     override fun onCancelled(p0: DatabaseError) {

                                     }

                                     override fun onDataChange(p0: DataSnapshot) {
                                         for (data in p0.children){
                                             var obje = LokumData()
                                             obje.lokumadi = gelenAd
                                             obje.lokum_image = gelenImage
                                             obje.lokumid = gelenId
                                             obje.lokumfiyati = gelenFiyat
                                             obje.stok = (eskiStok!!.toInt()-satilan_miktar.text.toString().toInt()  ).toString()

                                             FirebaseDatabase.getInstance().reference.child("lokumlar").child(gelenId!!).setValue(obje)
                                         }
                                     }

                                 })
                                 (mContext as MainActivity).init()
                                 DynamicToast.makeSuccess(mContext!!,"Sipariş eklendi.",Toast.LENGTH_LONG).show()

                                 dialog?.dismiss()



                             }else{
                                 DynamicToast.makeError(mContext!!,"Stok miktar yetersiz! veya gecersiz  siparis miktari ",Toast.LENGTH_LONG).show()

                             }

                         }
                         else{
                             DynamicToast.makeError(mContext!!,"Tüm alanları doldurun",Toast.LENGTH_LONG).show()

                         }


                     }
                     else{
                         DynamicToast.makeError(mContext!!,"Lütfen şirket ekleyin.",500).show()
                     }

             }



             return view
    }







    private fun spinner() {
        var ref = FirebaseDatabase.getInstance().reference
        var sorgu = ref.child("firmalar").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    var gelenData =  data.getValue(FirmaData::class.java)


                    var firmaAdi =gelenData?.firmaAdi

                    if (p0.childrenCount > 0 && gelenData?.pasif== false){
                        yardimciFirmalar.add(firmaAdi!!)
                    }
                    if (yardimciFirmalar.size<0){
                        DynamicToast.makeWarning(mContext!!," Lütfen firma ekleyin").show()


                    }





                }
                var adapterSpinner = ArrayAdapter<String>(mContext!!, R.layout.spinner_custom,yardimciFirmalar)
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFirma.adapter = adapterSpinner
            }

        })






    }

    private fun getCurrentDate(): String {

        var sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return sdf.format(Date())

    }



    private fun init() {
        var ref = FirebaseDatabase.getInstance().reference
        var sorgu =  ref.child("lokumlar").orderByKey().equalTo(gelenId).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                 for (data in p0.children){

                     var imagePath = data.getValue(LokumData::class.java)?.lokum_image
                     lokum_adi.text = data.getValue(LokumData::class.java)?.lokumadi
                     lokum_fiyat.text= "Fiyat:"+ data.getValue(LokumData::class.java)?.lokumfiyati  + "‎₺"
                     lokum_stok.text = "Stok:"+ data.getValue(LokumData::class.java)?.stok
                     eskiStok = data.getValue(LokumData::class.java)?.stok
                     geciciFiyat= data.getValue(LokumData::class.java)?.lokumfiyati.toString()
                     gelenImage = data.getValue(LokumData::class.java)?.lokum_image
                     Picasso.get().load(imagePath).into(lokum_image)


                     if (eskiStok!!.toInt()>0 && isAdmin){
                         layoutopener_img.visibility = View.VISIBLE
                     }

                 }
            }

        })

    }






}
