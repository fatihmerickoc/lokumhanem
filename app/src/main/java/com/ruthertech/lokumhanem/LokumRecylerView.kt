package com.ruthertech.lokumhanem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.teksatir_layout.view.*

class LokumRecylerView(alllokums: ArrayList<LokumData>, isAdmin: Boolean, mContext: Context) : RecyclerView.Adapter<LokumRecylerView.LokumHolder>(),
    Filterable {

    var alllokums = alllokums
    var myContext = mContext
    var isAdmin = isAdmin

    var myFilter = FilterHelper(alllokums,this)


    inner class LokumHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var oankilokum = itemView as ConstraintLayout
        var lokumAdi = oankilokum.lokum_adi
        var lokumFiyat = oankilokum.lokum_fiyat
        var lokumStok = oankilokum.stok_lokum
        var lokumdelete_img = oankilokum.img_delete
        var editpen = oankilokum.edittool


        fun setData(momentLokum: LokumData, position: Int) {
            lokumAdi.text = momentLokum.lokumadi
            lokumFiyat.text = momentLokum.lokumfiyati+ "₺"
            lokumStok.text = "Stok: "+ momentLokum.stok

            if (isAdmin){
                lokumdelete_img.visibility = View.VISIBLE
                editpen.visibility  =View.VISIBLE
            }

            lokumdelete_img.setOnClickListener {
                alllokums.remove(momentLokum)
                oankilokum.removeViewAt(position)
                notifyItemRemoved(position)
                (myContext as MainActivity).init()
                FirebaseDatabase.getInstance().reference.child("lokumlar").child(momentLokum.lokumid.toString()).removeValue()
                FirebaseStorage.getInstance().reference.child("lokumimages").child("${position.toString()}/image").delete()
            }

            editpen.setOnClickListener {
                UpdateLokum(momentLokum.stok,momentLokum.lokumadi,momentLokum.lokumfiyati,momentLokum.lokumid,momentLokum.lokum_image).show((myContext as MainActivity).supportFragmentManager,"UpdateLokum")


            }
            oankilokum.setOnClickListener {
                LokumDetails(isAdmin,momentLokum.lokumid,momentLokum.lokumfiyati,momentLokum.lokumadi,momentLokum.lokum_image).show((myContext as MainActivity).supportFragmentManager,"UpdateFragment")
            }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokumRecylerView.LokumHolder {
        var inflate = LayoutInflater.from(myContext)
     var row=   inflate.inflate(R.layout.teksatir_layout,parent,false)

        return LokumHolder(row)
    }

    override fun getItemCount(): Int {
        return alllokums.size
    }

    override fun onBindViewHolder(holder: LokumRecylerView.LokumHolder, position: Int) {

        var momentLokum = alllokums.get(position)
        holder.setData(momentLokum,position)



    }

    fun setFilter(arrayList: ArrayList<LokumData>) {

        alllokums = arrayList

    }


    override fun getFilter(): Filter {
        return myFilter
    }



}