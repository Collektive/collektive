package it.unibo.collektive.test.util

import it.unibo.collektive.test.util.CompileUtils.KotlinTestingProgram

object KotlinTestingProgramDsl {

    infix fun KotlinTestingProgram.fillWith(
        init: TemplateFormatter.() -> Unit,
    ): KotlinTestingProgram {
        val templateFormatter = TemplateFormatter()
        templateFormatter.init()
        return templateFormatter.format(this)
    }

    class TemplateFormatter {

        private var formattedProperties: Map<String, String> = mapOf()

        fun format(testingProgram: KotlinTestingProgram): KotlinTestingProgram =
            testingProgram.putAll(formattedProperties)

        infix fun String.after(propertyName: String) {
            formattedProperties += (("after" concatCamelCase propertyName) to this)
        }

        infix fun String.before(propertyName: String) {
            formattedProperties += (("before" concatCamelCase propertyName) to this)
        }

        infix fun String.wrapping(propertyName: () -> String) {
            "$this {".before(propertyName())
            "}".after(propertyName())
        }

        infix fun String.inside(propertyName: String) {
            formattedProperties += (propertyName to this)
        }

        private infix fun String.concatCamelCase(other: String): String =
            this + other.replaceFirstChar { it.uppercase() }
    }
}
