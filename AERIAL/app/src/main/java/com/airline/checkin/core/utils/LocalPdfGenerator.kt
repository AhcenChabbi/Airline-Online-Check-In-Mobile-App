package com.airline.checkin.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.DashPathEffect
import android.graphics.pdf.PdfDocument
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LocalPdfGenerator {

    fun generateBoardingPassPdf(
        passengerName: String,
        flightNumber: String,
        from: String,
        fromCity: String,
        to: String,
        toCity: String,
        date: String,
        seat: String,
        boardingTime: String,
        bookingRef: String,
        qrCodeData: String?
    ): ByteArray {
        val pdfDocument = PdfDocument()
        
        val pageWidth = 450
        val pageHeight = 800
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        
        val canvas = page.canvas
        
        val fillPaint = Paint().apply { style = Paint.Style.FILL }
        val strokePaint = Paint().apply { style = Paint.Style.STROKE }
        val textPaint = Paint().apply {
            isAntiAlias = true
        }

        fillPaint.color = Color.WHITE
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), fillPaint)

        fillPaint.color = Color.parseColor("#051849")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 180f, fillPaint)

        textPaint.color = Color.parseColor("#D4AF37")
        textPaint.textSize = 20f
        textPaint.isFakeBoldText = true
        canvas.drawText("A E R I A L", 30f, 45f, textPaint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 36f
        textPaint.isFakeBoldText = true
        canvas.drawText(from, 30f, 105f, textPaint)
        canvas.drawText(to, pageWidth - 120f, 105f, textPaint)

        textPaint.textSize = 12f
        textPaint.isFakeBoldText = false
        textPaint.color = Color.parseColor("#99FFFFFF")
        canvas.drawText(fromCity, 30f, 130f, textPaint)
        canvas.drawText(toCity, pageWidth - 120f, 130f, textPaint)

        textPaint.color = Color.parseColor("#D4AF37")
        textPaint.textSize = 14f
        textPaint.isFakeBoldText = true
        canvas.drawText("✈  $flightNumber", pageWidth / 2f - 40f, 105f, textPaint)

        textPaint.isFakeBoldText = false

        drawInfoField(canvas, textPaint, "PASSENGER", passengerName, 30f, 230f)
        drawInfoField(canvas, textPaint, "DATE", formatDate(date), pageWidth - 180f, 230f)

        drawInfoField(canvas, textPaint, "SEAT", seat, 30f, 320f)
        drawInfoField(canvas, textPaint, "BOARDING", formatTime(boardingTime), pageWidth - 180f, 320f)

        strokePaint.color = Color.LTGRAY
        strokePaint.strokeWidth = 2f
        strokePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        canvas.drawLine(30f, 420f, pageWidth - 30f, 420f, strokePaint)

        if (!qrCodeData.isNullOrBlank()) {
            val qrBitmap = try {
                generateQrBitmap(qrCodeData, 200)
            } catch (e: Exception) {
                null
            }

            if (qrBitmap != null) {
                val left = (pageWidth - 200f) / 2f
                canvas.drawBitmap(qrBitmap, left, 470f, null)
            }
        }

        textPaint.color = Color.parseColor("#051849")
        textPaint.textSize = 12f
        textPaint.isFakeBoldText = true
        canvas.drawText("BOOKING REF: $bookingRef", pageWidth / 2f - 70f, 720f, textPaint)

        pdfDocument.finishPage(page)
        
        val stream = ByteArrayOutputStream()
        pdfDocument.writeTo(stream)
        pdfDocument.close()
        
        return stream.toByteArray()
    }

    private fun drawInfoField(canvas: Canvas, paint: Paint, label: String, value: String, x: Float, y: Float) {
        paint.color = Color.GRAY
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText(label, x, y, paint)

        paint.color = Color.parseColor("#051849")
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText(value, x, y + 25f, paint)
    }

    private fun generateQrBitmap(data: String, size: Int): Bitmap {
        val matrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun formatDate(value: String?): String {
        if (value.isNullOrBlank()) return "—"
        return runCatching {
            val instant = Instant.parse(value)
            instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
        }.getOrNull() ?: value
    }

    private fun formatTime(value: String?): String {
        if (value.isNullOrBlank()) return "—"
        return runCatching {
            val instant = Instant.parse(value)
            instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))
        }.getOrNull() ?: value
    }
}
