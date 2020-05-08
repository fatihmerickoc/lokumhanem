package com.ruthertech.lokumhanem


import android.widget.Filter

class FilterHelperSilRecylerView(tumFirmalar : ArrayList<FirmaData>,adapter: FirmaSilRecylerView) : Filter() {

    var suankiListe = tumFirmalar
    var suankiAdapter = adapter


    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var sonuc = FilterResults()

        if (constraint!= null&&constraint.length >0){

            var aranilanAd = constraint.toString().toLowerCase()
            var eslesenler = ArrayList<FirmaData>()

            for (firma in suankiListe){

                var adi  = firma.firmaAdi!!.toLowerCase()

                if (adi.contains(aranilanAd.toString())){

                    eslesenler.add(firma)


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

        suankiAdapter.setFilter(results?.values as ArrayList<FirmaData>)
        suankiAdapter.notifyDataSetChanged()

    }




}