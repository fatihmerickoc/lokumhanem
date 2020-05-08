package com.ruthertech.lokumhanem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.android.synthetic.main.fragment_add_lokum.*
import kotlinx.android.synthetic.main.fragment_add_lokum.view.*
import kotlinx.android.synthetic.main.fragment_add_lokum.view.btnsuccess
import java.util.jar.Manifest

var mContext :Context? = null
class AddLokumFragment : DialogFragment() {

    var izinlerVerildi = false
    var imagePathFromGallery: Uri? = null
    var imagePathFromCamera:Bitmap? = null
    var camera : Boolean = false
    var gallery : Boolean = false



    interface onProfileImageListener{
        fun getImageWAY(imagePath : Uri?)
        fun getImageBitmap(bitmap: Bitmap)

    }


    lateinit var myProfileImageListener: onProfileImageListener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_add_lokum, container, false)
        mContext = activity
        var lokum_adi_edittext = view?.lokumadi_edittext
        var lokum_fiyat_edittext = view?.lokum_fiyati
        var lokum_adi_stok = view?.lokum_stok
        var lokum_button = view?.lokum_eklebuton
        var take_camera = view?.takeCamera
        var selectfromGallery = view.selectFromGallery

        lokum_button?.setOnClickListener {
            if (lokum_adi_edittext?.text.toString().isNotEmpty()&&lokum_fiyat_edittext?.text.toString().isNotEmpty()&&lokum_adi_stok?.text.toString().isNotEmpty()&& imagePathFromGallery != null || imagePathFromCamera != null){

                var reference = FirebaseDatabase.getInstance().reference
             var lokum_id=  reference.child("lokumlar").push().key

                var lokumObje = LokumData()
                lokumObje.lokumadi = lokum_adi_edittext?.text.toString()
                lokumObje.lokumfiyati = lokum_fiyat_edittext?.text.toString()
                lokumObje.stok = lokum_adi_stok?.text.toString()
                lokumObje.lokumid = lokum_id

                reference.child("lokumlar").child(lokum_id!!).setValue(lokumObje)
                dialog?.dismiss()
                btnsuccess.visibility = View.INVISIBLE
                (mContext as MainActivity).init()
                DynamicToast.makeSuccess(context!!, "Lokum Eklendi",3).show();


                if (imagePathFromGallery!=null){
                    myProfileImageListener.getImageWAY(imagePathFromGallery)

                }
                if (imagePathFromCamera!=null){
                    myProfileImageListener.getImageBitmap(imagePathFromCamera!!)

                }



            }
            else{
                DynamicToast.makeError(mContext!!, "Tüm alanları doldurun!").show();
            }
        }

        selectfromGallery.setOnClickListener {
            gallery = true

            if (izinlerVerildi){
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent,100)
            }else{
                checkPermission()
            }

        }

        take_camera?.setOnClickListener {
            camera = true
            if (izinlerVerildi){
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,99)

            }else{
                checkPermission()
            }

        }





        return view
    }

    private fun checkPermission() {
        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA)
        if (ContextCompat.checkSelfPermission(context!!,izinler[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!,izinler[1]) == PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(context!!,izinler[2]) == PackageManager.PERMISSION_GRANTED )
        {
            if (camera){
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,99)
            }else if (gallery){
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent,100)
            }
            izinlerVerildi = true

        }else{
            ActivityCompat.requestPermissions(activity!!,izinler,150)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //from gallery
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data !=null && izinlerVerildi){

             imagePathFromGallery = data.data
            DynamicToast.makeSuccess(mContext!!, "Resim Eklendi!").show();
            btnsuccess.visibility = View.VISIBLE




        }
        //from camera
        else if (requestCode == 99 && resultCode == Activity.RESULT_OK && data !=null&& izinlerVerildi){

             imagePathFromCamera = data.extras?.get("data") as Bitmap
            DynamicToast.makeSuccess(mContext!!, "Resim Eklendi!").show();
            btnsuccess.visibility = View.VISIBLE





        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 150){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults[1] == PackageManager.PERMISSION_GRANTED&&grantResults[2] == PackageManager.PERMISSION_GRANTED){
                izinlerVerildi
                if (camera){
                    var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent,99)
                }else if (gallery){
                    var intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(intent,100)
                }

            }
            else{
                Toast.makeText(context,"Tum izinleri kabul edin", Toast.LENGTH_LONG).show()
                !izinlerVerildi
            }


        }

    }


    override fun onAttach(context: Context) {

        myProfileImageListener = activity as onProfileImageListener

        super.onAttach(context)
    }

}
