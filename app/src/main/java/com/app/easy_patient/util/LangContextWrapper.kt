package com.app.easy_patient.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.*

/**
 * <p>
 * We need to overwrite system language to provide users with the option of changing app language,
 * So we created this context wrapper to be used in {@link android.app.Activity#attachBaseContext(Context)}.
 */
class LangContextWrapper private constructor(base: Context) : ContextWrapper(base) {

    companion object {

        val enLocale = Locale("en")
        val ptLocale = Locale("pt-rBR")

        fun wrap(baseContext: Context, language: String): ContextWrapper {
            var wrappedContext = baseContext
            val config = Configuration(baseContext.resources.configuration)

            if (language.isNotBlank()) {
                val locale = returnOrCreateLocale(language)
                Locale.setDefault(locale)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                wrappedContext = baseContext.createConfigurationContext(config)
            }
            return LangContextWrapper(wrappedContext)
        }

        /**
         * Method to return the existing local instead of creating new Locale object every time
         * For now it is only for EN ot AR locale it will return the static objects
         * @param language - the requested locale lang
         * @return the created Locale (or the static variables in case of AR or EN)
         */
        private fun returnOrCreateLocale(language: String): Locale {
            return ptLocale
            /*return when (language) {
                "en" -> enLocale
                "pt-rBR" -> ptLocale
                else -> Locale(language)
            }*/
        }
    }
}