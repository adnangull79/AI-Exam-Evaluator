package com.example.aiexamevaluator.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.aiexamevaluator.data.model.EvaluationResponse
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ──────────────────────────────────────────────────────────────────────────────
// PdfReportGenerator
// Uses Android's built-in PdfDocument — zero extra dependencies
// Saves to Downloads folder on Android 10+ via MediaStore
// Falls back to external storage on Android 9
// Returns the saved file name on success, null on failure
// ──────────────────────────────────────────────────────────────────────────────
object PdfReportGenerator {

    // Page size A4 at 72 dpi
    private const val PAGE_WIDTH  = 595
    private const val PAGE_HEIGHT = 842

    // Margins
    private const val MARGIN_LEFT  = 40f
    private const val MARGIN_RIGHT = 555f
    private const val LINE_HEIGHT  = 20f

    fun generate(
        context     : Context,
        studentName : String,
        rollNumber  : String,
        totalMarks  : Int,
        result      : EvaluationResponse,
        evaluatedAt : Long = System.currentTimeMillis(),
    ): String? {
        return try {
            val pdf      = PdfDocument()
            var pageNum  = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
            var page     = pdf.startPage(pageInfo)
            var canvas   = page.canvas
            var y        = 50f

            // ── Paints ────────────────────────────────────────────────────────
            val titlePaint = Paint().apply {
                color     = Color.parseColor("#1B5E3C")
                textSize  = 22f
                isFakeBoldText = true
                isAntiAlias    = true
            }
            val headingPaint = Paint().apply {
                color     = Color.parseColor("#1B5E3C")
                textSize  = 14f
                isFakeBoldText = true
                isAntiAlias    = true
            }
            val labelPaint = Paint().apply {
                color     = Color.parseColor("#757575")
                textSize  = 11f
                isAntiAlias = true
            }
            val bodyPaint = Paint().apply {
                color     = Color.BLACK
                textSize  = 12f
                isAntiAlias = true
            }
            val boldBodyPaint = Paint().apply {
                color     = Color.BLACK
                textSize  = 12f
                isFakeBoldText = true
                isAntiAlias    = true
            }
            val correctPaint = Paint().apply {
                color     = Color.parseColor("#2E7D32")
                textSize  = 11f
                isAntiAlias = true
            }
            val incorrectPaint = Paint().apply {
                color     = Color.parseColor("#C62828")
                textSize  = 11f
                isAntiAlias = true
            }
            val partialPaint = Paint().apply {
                color     = Color.parseColor("#E65100")
                textSize  = 11f
                isAntiAlias = true
            }
            val dividerPaint = Paint().apply {
                color       = Color.parseColor("#E0E0E0")
                strokeWidth = 1f
                isAntiAlias = true
            }
            val bgPaint = Paint().apply {
                color     = Color.parseColor("#E8F5E9")
                isAntiAlias = true
            }

            // ── Helper: new page ──────────────────────────────────────────────
            fun newPage(): Canvas {
                pdf.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
                page     = pdf.startPage(pageInfo)
                y        = 50f
                return page.canvas
            }

            // ── Helper: check space, new page if needed ───────────────────────
            fun checkSpace(needed: Float) {
                if (y + needed > PAGE_HEIGHT - 40f) {
                    canvas = newPage()
                }
            }

            // ── Helper: draw wrapped text, returns new y ──────────────────────
            fun drawWrapped(text: String, paint: Paint, x: Float, maxWidth: Float): Float {
                val words = text.split(" ")
                var line  = ""
                for (word in words) {
                    val test = if (line.isEmpty()) word else "$line $word"
                    if (paint.measureText(test) > maxWidth) {
                        checkSpace(LINE_HEIGHT)
                        canvas.drawText(line, x, y, paint)
                        y   += LINE_HEIGHT
                        line = word
                    } else {
                        line = test
                    }
                }
                if (line.isNotEmpty()) {
                    checkSpace(LINE_HEIGHT)
                    canvas.drawText(line, x, y, paint)
                    y += LINE_HEIGHT
                }
                return y
            }

            // ── HEADER ────────────────────────────────────────────────────────
            canvas.drawText("ExamEval AI — Evaluation Report", MARGIN_LEFT, y, titlePaint)
            y += 30f
            canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, dividerPaint)
            y += 16f

            // ── STUDENT INFO ──────────────────────────────────────────────────
            val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(evaluatedAt))
            canvas.drawText("Student Name",  MARGIN_LEFT, y, labelPaint);  y += LINE_HEIGHT
            canvas.drawText(studentName.ifBlank { "—" }, MARGIN_LEFT, y, boldBodyPaint); y += LINE_HEIGHT + 4f
            canvas.drawText("Roll Number",   MARGIN_LEFT, y, labelPaint);  y += LINE_HEIGHT
            canvas.drawText(rollNumber.ifBlank { "—" },  MARGIN_LEFT, y, boldBodyPaint); y += LINE_HEIGHT + 4f
            canvas.drawText("Evaluated On",  MARGIN_LEFT, y, labelPaint);  y += LINE_HEIGHT
            canvas.drawText(dateStr,          MARGIN_LEFT, y, bodyPaint);   y += LINE_HEIGHT + 8f

            // ── SCORE SUMMARY ─────────────────────────────────────────────────
            canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, dividerPaint); y += 12f
            canvas.drawText("Score Summary", MARGIN_LEFT, y, headingPaint); y += 24f

            val scoreText = "${result.totalObtained} / $totalMarks"
            val percent   = if (totalMarks > 0) (result.totalObtained * 100f / totalMarks).toInt() else 0
            canvas.drawText("Total Score:      $scoreText  ($percent%)", MARGIN_LEFT, y, boldBodyPaint); y += LINE_HEIGHT + 2f
            canvas.drawText("Confidence:       ${result.confidenceScore}%",            MARGIN_LEFT, y, bodyPaint);     y += LINE_HEIGHT + 2f

            val questions      = result.questions
            val correctCount   = questions.count { it.marksObtained == it.maxMarks && it.maxMarks > 0 }
            val incorrectCount = questions.count { it.marksObtained == 0 }
            val partialCount   = questions.count { it.marksObtained > 0 && it.marksObtained < it.maxMarks }
            canvas.drawText("Correct:          $correctCount",   MARGIN_LEFT, y, correctPaint);   y += LINE_HEIGHT + 2f
            canvas.drawText("Incorrect:        $incorrectCount", MARGIN_LEFT, y, incorrectPaint); y += LINE_HEIGHT + 2f
            canvas.drawText("Partially Correct:$partialCount",   MARGIN_LEFT, y, partialPaint);   y += LINE_HEIGHT + 8f

            // ── OVERALL FEEDBACK ──────────────────────────────────────────────
            if (result.overallFeedback.isNotBlank()) {
                canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, dividerPaint); y += 12f
                canvas.drawText("Overall Feedback", MARGIN_LEFT, y, headingPaint); y += 22f
                drawWrapped(result.overallFeedback, bodyPaint, MARGIN_LEFT, MARGIN_RIGHT - MARGIN_LEFT)
                y += 8f
            }

            // ── QUESTION ANALYSIS ─────────────────────────────────────────────
            canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, dividerPaint); y += 12f
            checkSpace(24f)
            canvas.drawText("Question Analysis", MARGIN_LEFT, y, headingPaint); y += 24f

            questions.forEach { q ->
                checkSpace(60f)

                val isCorrect   = q.marksObtained == q.maxMarks && q.maxMarks > 0
                val isIncorrect = q.marksObtained == 0
                val statusLabel = when { isCorrect -> "Correct"; isIncorrect -> "Incorrect"; else -> "Partial" }
                val statusPaint = when { isCorrect -> correctPaint; isIncorrect -> incorrectPaint; else -> partialPaint }

                // Q badge background
                canvas.drawRoundRect(
                    MARGIN_LEFT, y - 14f,
                    MARGIN_LEFT + 36f, y + 4f,
                    4f, 4f, bgPaint
                )
                canvas.drawText("Q${q.questionNumber}", MARGIN_LEFT + 4f, y, headingPaint)

                canvas.drawText("${q.maxMarks} marks", MARGIN_LEFT + 44f, y, labelPaint)
                canvas.drawText(statusLabel, MARGIN_RIGHT - 80f, y, statusPaint)
                canvas.drawText("${q.marksObtained}/${q.maxMarks}", MARGIN_RIGHT - 30f, y, statusPaint)
                y += LINE_HEIGHT + 4f

                // Correct points
                if (q.correctPoints.isNotEmpty()) {
                    checkSpace(LINE_HEIGHT)
                    canvas.drawText("What's Correct:", MARGIN_LEFT + 8f, y, correctPaint); y += LINE_HEIGHT
                    q.correctPoints.filter { it.isNotBlank() }.forEach { point ->
                        drawWrapped("  • $point", bodyPaint, MARGIN_LEFT + 12f, MARGIN_RIGHT - MARGIN_LEFT - 12f)
                    }
                }

                // Mistakes
                if (q.mistakes.isNotEmpty()) {
                    checkSpace(LINE_HEIGHT)
                    val mistakeLabel = when {
                        isCorrect   -> "Why No Marks Deducted:"
                        isIncorrect -> "What's Incorrect:"
                        else        -> "Why ${q.maxMarks - q.marksObtained} Mark(s) Deducted:"
                    }
                    canvas.drawText(mistakeLabel, MARGIN_LEFT + 8f, y, incorrectPaint); y += LINE_HEIGHT
                    q.mistakes.filter { it.isNotBlank() }.forEach { point ->
                        drawWrapped("  • $point", bodyPaint, MARGIN_LEFT + 12f, MARGIN_RIGHT - MARGIN_LEFT - 12f)
                    }
                }

                // Reasoning
                val reason = q.reasoning.trim()
                if (reason.isNotBlank() && reason != "Attempted" && reason != "Not Attempted") {
                    checkSpace(LINE_HEIGHT)
                    canvas.drawText("What You Should Include:", MARGIN_LEFT + 8f, y, partialPaint); y += LINE_HEIGHT
                    drawWrapped("  • $reason", bodyPaint, MARGIN_LEFT + 12f, MARGIN_RIGHT - MARGIN_LEFT - 12f)
                }

                // Divider between questions
                y += 6f
                checkSpace(4f)
                canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, dividerPaint)
                y += 12f
            }

            // ── FOOTER ────────────────────────────────────────────────────────
            checkSpace(30f)
            y += 10f
            val footerPaint = Paint().apply {
                color     = Color.parseColor("#BDBDBD")
                textSize  = 10f
                isAntiAlias = true
            }
            canvas.drawText("Generated by ExamEval AI  •  $dateStr", MARGIN_LEFT, y, footerPaint)

            pdf.finishPage(page)

            // ── SAVE ──────────────────────────────────────────────────────────
            val fileName = "EvalReport_${studentName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val saved = savePdf(context, pdf, fileName)
            pdf.close()
            if (saved) fileName else null

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ── Save PDF to Downloads ─────────────────────────────────────────────────
    private fun savePdf(context: Context, pdf: PdfDocument, fileName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ — use MediaStore, no permissions needed
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri      = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return false
                resolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } else {
                // Android 9 — write directly to Downloads folder
                val dir  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { pdf.writeTo(it) }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}