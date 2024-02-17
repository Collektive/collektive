package it.unibo.collektive.compiler.util

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms

/**
 * Utility object for hashing and encoding.
 */
object Hashing {

    /**
     * Hashes the string using SHA-256.
     */
    fun String.sha256(): ByteArray = DigestUtils(MessageDigestAlgorithms.SHA_256).digest(this)

    /**
     * Interprets a byte array in base 32.
     */
    fun ByteArray.toBase32(): String = Base32().encodeAsString(this)

    /**
     * Interprets a byte array in base 64.
     */
    fun ByteArray.toBase64(): String = Base64().encodeAsString(this)
}
