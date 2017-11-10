package com.jarvistechnolabs.pdfcreatorandviewer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_viewer.*
import java.io.File

class ViewerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        val pdfPath = intent.extras.get("filepath").toString()

        pdfview.fromFile(File(pdfPath))
                .defaultPage(1)
                .enableSwipe(true)
//                .onDraw(onDrawListener)
                .onLoad({ Log.e("ViewerActivity", "load complete")})
//                .onPageChange(onPageChangeListener)
                .load()
    }
}
