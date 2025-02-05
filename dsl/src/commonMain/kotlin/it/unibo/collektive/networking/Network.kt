package it.unibo.collektive.networking

/**
 * Network interface for the aggregate computation.
 */
interface Network<ID : Any> : MessageProvider<ID>, MessageDeliverer<ID>
