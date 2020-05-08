package com.ruthertech.lokumhanem

import android.widget.Filter

class FilterHelper(tumLokumlar : ArrayList<LokumData>,adapter: LokumRecylerView) : Filter() {

    var suankiListe = tumLokumlar
    var suankiAdapter = adapter


    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var sonuc = FilterResults()

        if (constraint!= null&&constraint.length >0){

            var aranilanAd = constraint.toString().toLowerCase()
            var eslesenler = ArrayList<LokumData>()

            for (lokum in suankiListe){

                var adi  = lokum.lokumadi!!.toLowerCase()

                if (adi.contains(aranilanAd.toString())){

                    eslesenler.add(lokum)


                }

            }


            sonuc.values= eslesenler
            sonuc.count = eslesenler.size



        }else{
            sonuc.values = suankiListe
            sonuc.count = suankiListe.size
        }





        return sonuc
    }

    
    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

        suankiAdapter.setFilter(results?.values as ArrayList<LokumData>)
        suankiAdapter.notifyDataSetChanged()

    }




}