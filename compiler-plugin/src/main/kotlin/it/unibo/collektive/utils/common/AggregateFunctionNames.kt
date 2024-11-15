package it.unibo.collektive.utils.common

/**
 * The names of the aggregate functions used in the plugin.
 */
object AggregateFunctionNames {
    /**
     * The simple name of the function that is used to align a block.
     */
    const val ALIGNED_ON_FUNCTION_NAME = "alignedOn"

    /**
     * The FQ name of the function that is used to align a block.
     */
    const val ALIGNED_ON_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.alignedOn"

    /**
     * The simple name of the function that is used to start an alignment delimitation.
     */
    const val ALIGN_FUNCTION_NAME = "align"

    /**
     * The FQ name of the function that is used to start an alignment delimitation.
     */
    const val ALIGN_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.align"

    /**
     * The name of the function that is used to stop an alignment delimitation.
     */
    const val DEALIGN_FUNCTION_NAME = "dealign"

    /**
     * The FQ name of the function that is used to stop an alignment delimitation.
     */
    const val DEALIGN_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.dealign"

    /**
     * The simple name of the aggregate class.
     */
    const val AGGREGATE_CLASS_NAME = "Aggregate"

    /**
     * The FQ name of the aggregate class.
     */
    const val AGGREGATE_CLASS_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate"

    /**
     * The FQ name of the field class.
     */
    const val FIELD_CLASS = "it.unibo.collektive.field.Field"

    /**
     * The name of the function that is used to project the fields.
     */
    const val PROJECT_FUNCTION = "project"
}
