package com.ruthertech.lokumhanem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.fragment_update_lokum.view.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateLokum(var stok: String?, var lokumadi: String?, var lokumfiyati: String?,var  lokumid: String?,var  lokumImage: String?) : DialogFragment() {


     var mContext : FragmentActivity? = null
    lateinit var btn_Guncelle : Button
    lateinit var yeniStok : EditText
    lateinit var  yeniFiyat : EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view=  inflater.inflate(R.layout.fragment_update_lokum, container, false)
        mContext  = activity

        btn_Guncelle = view.btn_Guncelle
        yeniStok = view.yeni_stok
        yeniFiyat = view.yeni_fiyat


      btn_Guncelle.setOnClickListener {
            if (yeniStok.text.toString().isNotEmpty() || yeniFiyat.text.toString().isNotEmpty()){
                var ref = FirebaseDatabase.getInstance().reference


                if (yeniFiyat.text.toString().isNotEmpty()){
                    ref.child("lokumlar").child(lokumid!!).child("lokumfiyati").setValue(yeniFiyat.text.toString())
                }


                if (yeniStok.text.toString().isNotEmpty()){

                    var toplamStok = ((stok!!.toInt()) + (yeniStok.text.toString().toInt()))

                    var lokumObje = LokumData()
                    lokumObje.stok = toplamStok.toString()
                    lokumObje.lokumadi = lokumadi
                    lokumObje.lokumfiyati = lokumfiyati
                    lokumObje.lokumid = lokumid
                    lokumObje.lokum_image = lokumImage



                    ref.child("lokumlar").child(lokumid!!).setValue(lokumObje)

                }





                (mContext as MainActivity).init()
                dialog!!.dismiss()

            }
            else{
                DynamicToast.makeError(mContext!!,"Güncelleyeceğiniz alanı yazınız!")
            }
        }


        return view
    }

}
