package net.ojm.web_backend.util

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import java.util.Random

object ShortIdGenerator {
    private val random: Random = Random();
    private const val ALPHABET: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generateShortId(): String =
        NanoIdUtils.randomNanoId(random, ALPHABET.toCharArray(), 6);
}