package com.example.mtgportal.model.definitions

import androidx.annotation.StringDef
import com.example.mtgportal.model.definitions.MtgColors.Companion.BLACK
import com.example.mtgportal.model.definitions.MtgColors.Companion.BLUE
import com.example.mtgportal.model.definitions.MtgColors.Companion.GREEN
import com.example.mtgportal.model.definitions.MtgColors.Companion.RED
import com.example.mtgportal.model.definitions.MtgColors.Companion.WHITE

@Target(
    AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION, AnnotationTarget.TYPE
)
@Retention(AnnotationRetention.SOURCE)
@StringDef(WHITE, BLACK, RED, GREEN, BLUE)
annotation class MtgColors {
    companion object {
        const val COLORLESS = "colorless"
        const val WHITE = "white"
        const val BLACK = "black"
        const val GREEN = "green"
        const val BLUE = "blue"
        const val RED = "red"
    }
}
