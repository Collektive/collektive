package it.unibo.collektive.utils.common

/**
 * The names of the aggregate functions used in the plugin.
 */
object AggregateFunctionNames {
    /**
     * The name of the function that is used to align.
     */
    const val ALIGNED_ON_FUNCTION = "alignedOn"

    /**
     * The FQ name of the aggregate class.
     */
    const val AGGREGATE_CLASS = "it.unibo.collektive.aggregate.api.Aggregate"

    /**
     * The FQ name of the field class.
     */
    const val FIELD_CLASS = "it.unibo.collektive.field.Field"

    /**
     * The name of the function that is used to project the fields.
     */
    const val PROJECT_FUNCTION = "project"

    /**
     * The name of the function showing if the compiler plugin is applied.
     */
    const val IS_COMPILER_PLUGIN_APPLIED_FUNCTION = "isCompilerPluginApplied"
}
