package com.ruthertech.lokumhanem

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.firma_sil_taksatir_layout.view.*

class FirmaSilRecylerView(allFirmalar : ArrayList<FirmaData>,mContext: Context) :  RecyclerView.Adapter<FirmaSilRecylerView.FirmaHolder>(),Filterable {

    var allFirmalar = allFirmalar
    var myContext = mContext
    var myFilter = FilterHelperSilRecylerView(allFirmalar,this)



    inner class FirmaHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var layout =  itemView as ConstraintLayout
        var firma_adi  = layout.firma_sil_firmaAdi
        var firma_sil_img = layout.firma_sil_img_delete
        var firma_sil_edit = layout.firma_sil_firmaAdi_edittool


        fun setData(momentFirma: FirmaData, position: Int) {

            firma_adi.text= momentFirma.firmaAdi
            firma_sil_img.setOnClickListener{
            var ref = FirebaseDatabase.getInstance().reference
            ref.child("firmalar").child(momentFirma.firmaAdi.toString()).removeValue()
                DynamicToast.makeSuccess(myContext,"Firma silindi!")
                allFirmalar.remove(momentFirma)
                layout.removeViewAt(position)
                notifyItemRemoved(position)
                (myContext as FirmaSil).init()
            }

            firma_sil_edit.setOnClickListener {
                var ref = FirebaseDatabase.getInstance().reference

                if (momentFirma?.pasif == false){
                    var obje = FirmaData()
                    obje.firmaAdi = momentFirma.firmaAdi
                    obje.firma_id = momentFirma.firma_id
                    obje.pasif = true
                    ref.child("firmalar").child(momentFirma?.firmaAdi.toString()).setValue(obje)
                }
                 if (momentFirma?.pasif == true){
                    var obje = FirmaData()
                    obje.firmaAdi = momentFirma.firmaAdi
                    obje.firma_id = momentFirma.firma_id
                    obje.pasif = false
                    ref.child("firmalar").child(momentFirma?.firmaAdi.toString()).setValue(obje)
                }

                (myContext as FirmaSil).init()

            }

            if (momentFirma?.pasif == true){
                firma_sil_img.visibility = View.INVISIBLE
                layout.setBackgroundColor(Color.GRAY)
            }
            if (momentFirma?.pasif == false){
                firma_sil_img.visibility = View.VISIBLE
                layout.setBackgroundColor(Color.rgb(0,0,0))
            }


        }

    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmaSilRecylerView.FirmaHolder {
            var inflate = LayoutInflater.from(myContext)
            var row=   inflate.inflate(R.layout.firma_sil_taksatir_layout,parent,false)

            return FirmaHolder(row)
          }

    override fun getItemCount(): Int {
      return  allFirmalar.size
    }

    fun setFilter(arrayList: ArrayList<FirmaData>) {

        allFirmalar = arrayList

    }


    override fun getFilter(): Filter {
        return myFilter
    }

    override fun onBindViewHolder(holder: FirmaSilRecylerView.FirmaHolder, position: Int) {
        var momentFirma = allFirmalar.get(position)
        holder.setData(momentFirma,position)
    }




}