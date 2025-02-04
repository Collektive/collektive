package it.unibo.collektive.networking

import it.unibo.collektive.path.Path

/**
 * Network interface for the aggregate computation.
 */
interface Network<ID : Any> : MessageProvider<ID>, MessageDeliverer<ID>
