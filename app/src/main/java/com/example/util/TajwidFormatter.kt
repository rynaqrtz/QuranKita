package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.ui.theme.TajwidGhunnah
import com.example.ui.theme.TajwidIdgham
import com.example.ui.theme.TajwidIkhfa
import com.example.ui.theme.TajwidMad
import com.example.ui.theme.TajwidQalqalah

object TajwidFormatter {

    private val QALQALAH_CHARS = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val MAD_CHARS = setOf('آ', 'ى', 'ٓ', 'ٰ')

    fun formatWithTajwid(text: String, isTajwidEnabled: Boolean): AnnotatedString {
        if (!isTajwidEnabled) {
            return AnnotatedString(text)
        }

        return buildAnnotatedString {
            append(text)

            var i = 0
            while (i < text.length) {
                val ch = text[i]

                // Ghunnah: Nun or Mim with Shaddah (ّ)
                if ((ch == 'ن' || ch == 'م') && i + 1 < text.length && text[i + 1] == 'ّ') {
                    addStyle(
                        style = SpanStyle(color = TajwidGhunnah),
                        start = i,
                        end = (i + 2).coerceAtMost(text.length)
                    )
                    i += 2
                    continue
                }

                // Qalqalah letters
                if (ch in QALQALAH_CHARS) {
                    addStyle(
                        style = SpanStyle(color = TajwidQalqalah),
                        start = i,
                        end = i + 1
                    )
                }

                // Mad letters / symbols
                if (ch in MAD_CHARS) {
                    addStyle(
                        style = SpanStyle(color = TajwidMad),
                        start = i,
                        end = i + 1
                    )
                }

                // Tanween / Noon Sakinah patterns (Ikhfa / Idgham indicators)
                if (ch == 'ً' || ch == 'ٌ' || ch == 'ٍ') {
                    addStyle(
                        style = SpanStyle(color = TajwidIkhfa),
                        start = i,
                        end = i + 1
                    )
                }

                // Idgham symbols (Shaddah on Waw, Ya, Lam, Ra)
                if (ch == 'و' || ch == 'ي' || ch == 'ل' || ch == 'ر') {
                    if (i + 1 < text.length && text[i + 1] == 'ّ') {
                        addStyle(
                            style = SpanStyle(color = TajwidIdgham),
                            start = i,
                            end = (i + 2).coerceAtMost(text.length)
                        )
                        i += 2
                        continue
                    }
                }

                i++
            }
        }
    }
}
