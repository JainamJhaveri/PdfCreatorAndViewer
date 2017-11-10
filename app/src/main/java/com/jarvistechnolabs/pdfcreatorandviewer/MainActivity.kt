package com.jarvistechnolabs.pdfcreatorandviewer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : Activity() {

    private val TAG: String = "MainActivity";
    val reportName = "Rep1";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val productListAllPDF: ProductListAllPDF = ProductListAllPDF()
        productListAllPDF.createPDF(this, reportName)


        val mPath = filesDir.toString() +
                StaticValue.PATH_PRODUCT_REPORT +
                reportName + ".pdf"


        viewPDF.setOnClickListener {
            Log.e(TAG, "showing pdf")
            viewPdf(mPath)

        }

    }


    fun viewPdf(filePath: String) {
        val path = Uri.fromFile(File(filePath))
        val pdfOpenintent = Intent(Intent.ACTION_VIEW)
//        pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenintent.setDataAndType(path, "application/pdf")
        try {
            startActivity(pdfOpenintent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


}
