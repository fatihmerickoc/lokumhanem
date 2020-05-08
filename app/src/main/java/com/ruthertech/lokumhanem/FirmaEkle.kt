package com.ruthertech.lokumhanem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.fragment_firma_ekle.view.*

/**
 * A simple [Fragment] subclass.
 */
class FirmaEkle : DialogFragment() {

    var mContext  :FragmentActivity?  =null
    var tumFirmalar : ArrayList<FirmaData>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.fragment_firma_ekle, container, false)

        mContext = activity

        var firmadi_et = view.firmaekle_Et
        var ekle_Btn  =view.ekle_btn


        ekle_Btn.setOnClickListener {
            if (firmadi_et.text.toString().isNotEmpty()){
                var ref = FirebaseDatabase.getInstance().reference

                var firma_id=  ref.push().key

                var obje = FirmaData()
                obje.firmaAdi = firmadi_et.text.toString()
                obje.firma_id  = firma_id
                obje.pasif = false

                ref.child("firmalar").child(firmadi_et.text.toString()).setValue(obje)





                tumFirmalar = ArrayList<FirmaData>()
                tumFirmalar!!.add(obje)

                if (mContext is  FirmaSil){
                    (mContext as FirmaSil).init()
                }


                DynamicToast.makeSuccess(mContext!!, "Firma başarıyla eklendi.").show();
                dialog?.dismiss()


            }
            else{
                DynamicToast.makeError(mContext!!, "Lütfen firma adı girin!").show();

            }

        }


        return view
    }

}
