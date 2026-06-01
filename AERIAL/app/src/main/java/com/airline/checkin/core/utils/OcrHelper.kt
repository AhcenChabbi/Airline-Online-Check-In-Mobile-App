package com.airline.checkin.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.airline.checkin.domain.model.PassportScanData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object OcrHelper {
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    }

    suspend fun extractPassportData(bitmap: Bitmap): PassportScanData? {
        return extractPassportDataWithRaw(bitmap).first
    }

    suspend fun extractPassportDataWithRaw(bitmap: Bitmap): Pair<PassportScanData?, String> {
        val fullText = runTextRecognition(bitmap)
        val fullParsed = parseMrzText(fullText)
        if (fullParsed != null) {
            return fullParsed to fullText
        }

        val bottomCrop = cropBottomStrip(bitmap)
        val bottomText = runTextRecognition(bottomCrop)
        val mergedText = buildString {
            appendLine(fullText)
            appendLine("--- BOTTOM CROP ---")
            append(bottomText)
        }.trim()

        Log.d("OcrHelper", "Raw OCR text (full image):\n$fullText")
        Log.d("OcrHelper", "Raw OCR text (bottom crop):\n$bottomText")

        val parsed = parseMrzText(fullText + "\n" + bottomText)
        return parsed to mergedText
    }

    fun parseMrzText(text: String): PassportScanData? {
        val lines = text.uppercase()
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val mrzLines = lines.mapNotNull { normalizeMrzLine(it) }

        val passportLine1Candidates = mrzLines.filter { line ->
            line.startsWith("P<") && line.contains("<<")
        }
        val passportLine2Candidates = mrzLines.filter { line ->
            looksLikePassportLine2(line)
        }

        if (passportLine1Candidates.isNotEmpty() && passportLine2Candidates.isNotEmpty()) {
            val line1 = passportLine1Candidates.maxByOrNull { mrzLineScore(it) }!!
            val line2 = passportLine2Candidates.maxByOrNull { mrzLineScore(it) }!!
            return parsePassportMrz(line1, line2)
        }

        val idCandidates = mrzLines.filter { looksLikeIdCardLine(it) }
        if (idCandidates.size >= 3) {
            return parseIdCardMrz(idCandidates[0], idCandidates[1], idCandidates[2])
        }

        return null
    }

    private fun parsePassportMrz(line1: String, line2: String): PassportScanData {
        val passportNumber = line2.take(9).trim('<')
        val nationality = normalizeNationality(
            line2.substringOrNull(10, 13),
            extractIssuerCountry(line1)
        )
        val dateOfBirth = extractDate(line2, 13)
        val expiryDate = extractDate(line2, 21)
        val (lastName, firstName) = extractNames(line1)
        val passportMrz = "$line1|$line2"

        return PassportScanData(
            passportNumber = passportNumber,
            passportExpiry = expiryDate.orEmpty(),
            passportMrz = passportMrz,
            passportScanUrl = null,
            firstName = firstName,
            lastName = lastName,
            nationality = nationality,
            dateOfBirth = dateOfBirth
        )
    }

    private fun parseIdCardMrz(line1: String, line2: String, line3: String): PassportScanData {
        val documentNumber = line1.substring(5, minOf(14, line1.length)).trim('<')
        val nationality = normalizeNationality(line2.take(3), extractIssuerCountry(line1))
        val dateOfBirth = extractDate(line2, 13)
        val expiryDate = extractDate(line2, 21)

        val nameSection = line1.substringAfter("<<", "")
        val lastName = nameSection.substringBefore("<<", "").replace('<', ' ').trim().ifBlank { null }
        val firstName = nameSection.substringAfter("<<", "").replace('<', ' ').trim().ifBlank { null }

        return PassportScanData(
            passportNumber = documentNumber,
            passportExpiry = expiryDate.orEmpty(),
            passportMrz = "$line1|$line2|$line3",
            passportScanUrl = null,
            firstName = firstName,
            lastName = lastName,
            nationality = nationality,
            dateOfBirth = dateOfBirth
        )
    }

    private suspend fun runTextRecognition(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image).await()
        return result.text.orEmpty()
    }

    private fun cropBottomStrip(bitmap: Bitmap): Bitmap {
        val cropHeight = maxOf((bitmap.height * 0.38f).toInt(), 1)
        val cropY = maxOf(bitmap.height - cropHeight, 0)
        val cropped = Bitmap.createBitmap(bitmap.width, cropHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(cropped)
        canvas.drawBitmap(bitmap, 0f, (-cropY).toFloat(), null)
        return cropped
    }

    private fun normalizeMrzLine(line: String): String? {
        val normalized = line
            .replace(" ", "")
            .replace("|", "<")
            .replace("—", "<")
            .replace("-", "<")
            .replace(".", "")
            .replace(",", "")
            .uppercase()

        if (normalized.length < 20) return null
        if (normalized.none { it == '<' }) return null

        return normalized
    }

    private fun looksLikePassportLine2(line: String): Boolean {
        if (line.length < 20) return false
        if (line.startsWith("P<")) return false
        val digitCount = line.count { it.isDigit() }
        val fillerCount = line.count { it == '<' }
        return digitCount >= 6 && fillerCount >= 3
    }

    private fun looksLikeIdCardLine(line: String): Boolean {
        if (line.length < 20) return false
        val digitCount = line.count { it.isDigit() }
        val fillerCount = line.count { it == '<' }
        return fillerCount >= 2 && digitCount >= 4
    }

    private fun mrzLineScore(line: String): Int {
        val fillerCount = line.count { it == '<' }
        val validCount = line.count { it.isLetterOrDigit() || it == '<' }
        return validCount + fillerCount * 2 + line.length
    }

    private fun extractNames(line1: String): Pair<String?, String?> {
        val nameSection = line1.substringAfter("P<", line1).drop(3)
        val parts = nameSection.split("<<", limit = 2)
        val lastName = parts.getOrNull(0)
            ?.replace('<', ' ')
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        val firstName = parts.getOrNull(1)
            ?.replace('<', ' ')
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        return lastName to firstName
    }

    private fun extractIssuerCountry(line1: String): String? {
        val issuerToken = line1.substringAfter("P<", "").take(3)
        return normalizeNationality(issuerToken, null)
    }

    private fun normalizeNationality(rawValue: String?, fallback: String?): String? {
        val candidate = rawValue
            ?.replace("<", "")
            ?.replace(" ", "")
            ?.uppercase()
            ?.takeIf { it.isNotBlank() }
            ?: fallback

        return when (candidate) {
            null -> null
            "D", "DE", "DEU" -> "DEU"
            "F", "FR", "FRA" -> "FRA"
            "E", "ES", "ESP" -> "ESP"
            "I", "IT", "ITA" -> "ITA"
            "P", "PT", "PRT" -> "PRT"
            "G", "GB", "GBR" -> "GBR"
            "A", "AT", "AUT" -> "AUT"
            "B", "BE", "BEL" -> "BEL"
            "CH", "CHE" -> "CHE"
            "DZ", "DZA" -> "DZA"
            else -> candidate.takeIf { it.length == 3 }
        }
    }

    private fun extractDate(line: String, startIndex: Int): String? {
        if (line.length < startIndex + 6) return null

        val year = line.substring(startIndex, startIndex + 2).toIntOrNull() ?: return null
        val month = line.substring(startIndex + 2, startIndex + 4)
        val day = line.substring(startIndex + 4, startIndex + 6)
        val fullYear = if (year > 30) 1900 + year else 2000 + year
        return "%04d-%s-%s".format(fullYear, month, day)
    }

    private fun String.substringOrNull(startIndex: Int, endIndex: Int): String? {
        if (length < endIndex) return null
        return substring(startIndex, endIndex)
    }
}
