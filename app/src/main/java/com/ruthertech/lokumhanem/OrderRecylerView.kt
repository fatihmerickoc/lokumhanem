package com.ruthertech.lokumhanem

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.siparistek_satir.view.*

class OrderRecylerView(mContext : Context,allOrders : ArrayList<OrderData>) : RecyclerView.Adapter<OrderRecylerView.OrderHolder>() {

    var mContext = mContext
    var allOrders = allOrders


    inner class OrderHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var layout = itemView as ConstraintLayout
        var urunAdi = layout.siparis_tek_satir_urunadi
        var urunMiktar = layout.siparis_tek_satir_miktar
        var urunFiyat = layout.siparis_fiyat
        var date = layout.siparis_tek_satir_tarih
        var delete_order = layout.deleteordera
        var btn_onay = layout.btnOnay
        var eskiStok : Int = 0


        fun setData(momentOrder: OrderData, position: Int){
            var ref = FirebaseDatabase.getInstance().reference


            if (momentOrder?.completed == true){
                delete_order.visibility = View.INVISIBLE
                btn_onay.visibility = View.INVISIBLE
                layout.setBackgroundColor(Color.rgb(46, 134, 193 ))
            }

            urunAdi.text = momentOrder.product_name
            urunMiktar.text =  "Miktar: "+momentOrder.amount
            urunFiyat.text = momentOrder.product_price + "₺"
            date.text = momentOrder.date

            delete_order.setOnClickListener {
                allOrders.remove(momentOrder)
                layout.removeViewAt(position)
                notifyItemRemoved(position)
                ref.child("orders").child(momentOrder.order_id.toString()).removeValue()
                DynamicToast.makeSuccess(itemView.context,"Ürün başarıyla silindi.").show()

                ref.child("lokumlar").orderByKey().equalTo(momentOrder?.product_id.toString()).addListenerForSingleValueEvent( object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children){
                            var gelenData = data.getValue(LokumData::class.java)
                            eskiStok = gelenData?.stok.toString().toInt()

                            var yeniObje = LokumData()
                            yeniObje.lokum_image = gelenData?.lokum_image
                            yeniObje.lokumid = gelenData?.lokumid
                            yeniObje.lokumfiyati = gelenData?.lokumfiyati
                            yeniObje.lokumadi = gelenData?.lokumadi
                            yeniObje.stok = ((eskiStok)+(momentOrder.amount!!.toInt())).toString()

                            FirebaseDatabase.getInstance().reference.child("lokumlar").child(gelenData?.lokumid.toString()).setValue(yeniObje)
                        }

                    }

                })



            }
            btn_onay.setOnClickListener {
                var ref = FirebaseDatabase.getInstance().reference
                ref.child("orders").child(momentOrder.order_id.toString()).child("completed").setValue(true)
                delete_order.visibility = View.INVISIBLE
                btn_onay.visibility = View.INVISIBLE
                layout.setBackgroundColor(Color.rgb(46, 134, 193 ))
            }





        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRecylerView.OrderHolder {
        var inflate = LayoutInflater.from(mContext)
        var row=   inflate.inflate(R.layout.siparistek_satir,parent,false)

        return OrderHolder(row)
    }

    override fun getItemCount(): Int {

        return allOrders.size
    }

    override fun onBindViewHolder(holder: OrderRecylerView.OrderHolder, position: Int) {

        var momentOrder = allOrders.get(position)
        holder.setData(momentOrder,position)


    }
}