package com.jarvistechnolabs.pdfcreatorandviewer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ProductListAllPDF {
    //creating a PdfWriter variable. PdfWriter class is available at com.itextpdf.text.pdf.PdfWriter
    private var pdfWriter: PdfWriter? = null

    @SuppressLint("LogNotTimber")
    fun createPDF(context: Context, reportName: String): Boolean {

        try {
            //creating a directory in SD card
//            val mydir = File(getExternalStorageDirectory().absolutePath + StaticValue.PATH_PRODUCT_REPORT) //PATH_PRODUCT_REPORT="/SIAS/REPORT_PRODUCT/"

//            val mydir = File(context.filesDir, StaticValue.PATH_PRODUCT_REPORT)
            val mydir = File(context.filesDir.toString())

            if (!mydir.exists()) {
                Log.e("1", "111")
                Log.e("!", mydir.mkdirs().toString())
            }

            //getting the full path of the PDF report name
//            val mPath = (Environment.getExternalStorageDirectory().toString()
//                    + StaticValue.PATH_PRODUCT_REPORT //PATH_PRODUCT_REPORT="/SIAS/REPORT_PRODUCT/"
//                    + reportName + ".pdf") //reportName could be any name

            val mPath = context.filesDir.toString() +
                    reportName + ".pdf"

            Log.e("1", mPath)

            //constructing the PDF file
            val pdfFile = File(mPath)

            //Creating a Document with size A7. Document class is available at  com.itextpdf.text.Document
            val document = Document(PageSize.A7)

            //assigning a PdfWriter instance to pdfWriter
            pdfWriter = PdfWriter.getInstance(document, FileOutputStream(pdfFile))

            //PageFooter is an inner class of this class which is responsible to create Header and Footer
            val event = PageHeaderFooter()
            pdfWriter!!.pageEvent = event

            //Before writing anything to a document it should be opened first
            document.open()

            //Adding meta-data to the document
            addMetaData(document)
            //Adding Title(s) of the document
            addTitlePage(document)
            //Adding main contents of the document
            addContent(document)
            //Closing the document
            document.close()
        } catch (e: Exception) {
            e.printStackTrace()

            return false
        }

        return true
    }

    /**
     * This is an inner class which is used to create header and footer
     * @author XYZ
     */

    internal inner class PageHeaderFooter : PdfPageEventHelper() {

        override fun onEndPage(writer: PdfWriter, document: Document) {

            /**
             * PdfContentByte is an object containing the user positioned text and graphic contents
             * of a page. It knows how to apply the proper font encoding.
             */
            val cb = writer.directContent

            /**
             * In iText a Phrase is a series of Chunks.
             * A chunk is the smallest significant part of text that can be added to a document.
             * Most elements can be divided in one or more Chunks. A chunk is a String with a certain Font
             */
            val footer_poweredBy = Phrase("Powered By POS Team", StaticValue.FONT_HEADER_FOOTER) //public static Font FONT_HEADER_FOOTER = new Font(Font.FontFamily.UNDEFINED, 7, Font.ITALIC);
            val footer_pageNumber = Phrase("Page " + document.getPageNumber(), StaticValue.FONT_HEADER_FOOTER)

            // Header
            // ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header,
            // (document.getPageSize().getWidth()-10),
            // document.top() + 10, 0);

            // footer: show page number in the bottom right corner of each age
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer_pageNumber,
                    document.getPageSize().getWidth() - 10,
                    document.bottom() - 10, 0f)
            //			// footer: show page number in the bottom right corner of each age
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer_poweredBy, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0f)
        }
    }

    companion object {

        //we will add some products to arrayListRProductModel to show in the PDF document
        private val arrayListRProductModel = ArrayList<ProductModel>()

        /**
         * iText allows to add metadata to the PDF which can be viewed in your Adobe Reader. If you right click
         * on the file and to to properties then you can see all these information.
         * @param document
         */
        private fun addMetaData(document: Document) {
            document.addTitle("All Product Names")
            document.addSubject("none")
            document.addKeywords("Java, PDF, iText")
            document.addAuthor("SIAS ERP")
        }

        /**
         * In this method title(s) of the document is added.
         * @param document
         * @throws DocumentException
         */
        @Throws(DocumentException::class)
        private fun addTitlePage(document: Document) {
            val paragraph = Paragraph()

            // Adding several title of the document. Paragraph class is available in  com.itextpdf.text.Paragraph
            var childParagraph = Paragraph("Customer Name 1", StaticValue.FONT_TITLE)
            childParagraph.alignment = Element.ALIGN_CENTER
            paragraph.add(childParagraph)

            childParagraph = Paragraph("Product List", StaticValue.FONT_SUBTITLE)
            childParagraph.alignment = Element.ALIGN_CENTER
            paragraph.add(childParagraph)

            childParagraph = Paragraph("Report generated on: 17.12.2015", StaticValue.FONT_SUBTITLE)
            childParagraph.alignment = Element.ALIGN_CENTER
            paragraph.add(childParagraph)

            addEmptyLine(paragraph, 2)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)
            //End of adding several titles

        }

        /**
         * In this method the main contents of the documents are added
         * @param document
         * @throws DocumentException
         */

        @Throws(DocumentException::class)
        private fun addContent(document: Document) {

            val reportBody = Paragraph()
            reportBody.font = StaticValue.FONT_BODY //public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL);

            // Creating a table
            createTable(reportBody)

            // now add all this to the document
            document.add(reportBody)

        }

        /**
         * This method is responsible to add table using iText
         * @param reportBody
         * @throws BadElementException
         */
        @Throws(BadElementException::class)
        private fun createTable(reportBody: Paragraph) {

            val columnWidths = floatArrayOf(5f, 5f, 5f) //total 4 columns and their width. The first three columns will take the same width and the fourth one will be 5/2.
            val table = PdfPTable(columnWidths)

            table.widthPercentage = 100f //set table with 100% (full page)
            table.defaultCell.isUseAscender = true


            //Adding table headers
            var cell = PdfPCell(Phrase("Product Name", //Table Header
                    StaticValue.FONT_TABLE_HEADER)) //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.BOLD);
            cell.horizontalAlignment = Element.ALIGN_CENTER //alignment
            cell.backgroundColor = GrayColor(0.75f) //cell background color
            cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT //cell height
            table.addCell(cell)

            cell = PdfPCell(Phrase("Category Name",
                    StaticValue.FONT_TABLE_HEADER))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = GrayColor(0.75f)
            cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT
            table.addCell(cell)

            cell = PdfPCell(Phrase("Unit",
                    StaticValue.FONT_TABLE_HEADER))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = GrayColor(0.75f)
            cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT
            table.addCell(cell)

            //End of adding table headers

            //This method will generate some static data for the table
            generateTableData()

            //Adding data into table
            for (i in 0 until arrayListRProductModel.size) { //
                cell = PdfPCell(Phrase(arrayListRProductModel[i].name, StaticValue.FONT_BODY))
                cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT
                table.addCell(cell)
//
//                cell = PdfPCell(Phrase(arrayListRProductModel[i].brandName))
//                cell.fixedHeight = 28f
//                table.addCell(cell)

                cell = PdfPCell(Phrase(arrayListRProductModel[i].categoryName, StaticValue.FONT_BODY))
                cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT
                table.addCell(cell)

                cell = PdfPCell(Phrase(arrayListRProductModel[i].unitName, StaticValue.FONT_BODY))
                cell.fixedHeight = StaticValue.CELL_FIXED_HEIGHT
                table.addCell(cell)
            }

            reportBody.add(table)

        }

        /**
         * This method is used to add empty lines in the document
         * @param paragraph
         * @param number
         */
        private fun addEmptyLine(paragraph: Paragraph, number: Int) {
            for (i in 0 until number) {
                paragraph.add(Paragraph(" "))
            }
        }

        /**
         * Generate static data for table
         */

        private fun generateTableData() {
            var productModel = ProductModel("Samsung Galaxy Note", "Piece", "Samsung", "Smartphone")
            arrayListRProductModel.add(productModel)

            productModel = ProductModel("HTC One", "Piece", "HTC", "Smartphone")
            arrayListRProductModel.add(productModel)

            productModel = ProductModel("LG Mini", "Piece", "LG", "Smartphone")
            arrayListRProductModel.add(productModel)

            productModel = ProductModel("iPhone 6", "Piece", "Apple", "Smartphone")
            arrayListRProductModel.add(productModel)
        }

    }
}