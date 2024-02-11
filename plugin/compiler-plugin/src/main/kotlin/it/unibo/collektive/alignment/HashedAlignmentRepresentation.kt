package it.unibo.collektive.alignment

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.digest.SHA256

class HashedAlignmentRepresentation : AlignmentRepresentation {
    override fun invoke(p1: String): String = CryptographyProvider.Default
        .get(SHA256)
        .hasher()
        .hashBlocking(p1.toByteArray())
        .joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}
