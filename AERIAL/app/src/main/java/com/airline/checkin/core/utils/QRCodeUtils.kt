package com.airline.checkin.core.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeUtils {
    fun generateQrBitmap(data: String, sizePx: Int): Bitmap? {
        if (data.isBlank() || sizePx <= 0) return null

        return try {
            val matrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, sizePx, sizePx)
            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            for (x in 0 until sizePx) {
                for (y in 0 until sizePx) {
                    bitmap.setPixel(x, y, if (matrix.get(x, y)) 0xFF051849.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            bitmap
        } catch (_: WriterException) {
            null
        }
    }
}
