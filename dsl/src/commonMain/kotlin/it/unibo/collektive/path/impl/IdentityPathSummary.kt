package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary

/**
 * Generate a [PathSummary] from a [path].
 */
data class IdentityPathSummary(val path: Path) : PathSummary
