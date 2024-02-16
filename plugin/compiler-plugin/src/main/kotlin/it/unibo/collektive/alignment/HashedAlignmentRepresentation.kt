package it.unibo.collektive.alignment

import java.security.MessageDigest

class HashedAlignmentRepresentation : AlignmentRepresentation {
    @OptIn(ExperimentalStdlibApi::class)
    override fun invoke(p1: String): String = MessageDigest.getInstance("MD5")
        .digest(p1.toByteArray())
        .toHexString()
}
