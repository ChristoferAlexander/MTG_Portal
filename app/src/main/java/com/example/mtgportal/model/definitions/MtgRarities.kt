package com.example.mtgportal.model.definitions

import androidx.annotation.StringDef
import com.example.mtgportal.model.definitions.MtgRarities.Companion.COMMON
import com.example.mtgportal.model.definitions.MtgRarities.Companion.MYTHIC_RARE
import com.example.mtgportal.model.definitions.MtgRarities.Companion.RARE
import com.example.mtgportal.model.definitions.MtgRarities.Companion.UNCOMMON

@Target(
    AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION, AnnotationTarget.TYPE
)
@Retention(AnnotationRetention.SOURCE)
@StringDef(COMMON, UNCOMMON, RARE, MYTHIC_RARE)
annotation class MtgRarities {
    companion object {
        const val COMMON = "Common"
        const val UNCOMMON = "Uncommon"
        const val RARE = "Rare"
        const val MYTHIC_RARE = "Mythic"
    }
}
