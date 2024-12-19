@file:JvmName("Hashing")

package it.unibo.collektive.compiler.util

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.BaseNCodec
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms

/**
 * Hashes the string using SHA-256.
 */
fun String.sha256(): ByteArray = DigestUtils(MessageDigestAlgorithms.SHA_256).digest(this)

/**
 * Hashes the string using SHA-1.
 */
fun String.sha1(): ByteArray = DigestUtils(MessageDigestAlgorithms.SHA_1).digest(this)

/**
 * Hashes the string using MD5.
 */
fun String.md5(): ByteArray = DigestUtils(MessageDigestAlgorithms.MD5).digest(this)

/**
 * Hashes the string using MD5.
 */
fun String.md2(): ByteArray = DigestUtils(MessageDigestAlgorithms.MD2).digest(this)

/**
 * Encodes a byte array in base N String.
 */
fun ByteArray.toBaseString(
    encoder: BaseNCodec,
    preservePadding: Boolean = false,
): String = encoder.encodeAsString(this).let { if (preservePadding) it else it.trimEnd('=') }

/**
 * Interprets a byte array in base 32.
 */
fun ByteArray.toBase32(preservePadding: Boolean = false): String = toBaseString(Base32(), preservePadding)

/**
 * Interprets a byte array in base 64.
 */
fun ByteArray.toBase64(preservePadding: Boolean = false): String = toBaseString(Base64(), preservePadding)
