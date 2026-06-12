package com.rabpadev.app
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow
object TotpGenerator {
    private const val DIGITS = 6
    private const val PERIOD = 30L
    fun decodeBase32(input: String): ByteArray {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val clean = input.uppercase().replace("=","").replace(" ","").replace("-","")
        val bytes = ByteArray(clean.length * 5 / 8)
        var buf = 0; var bits = 0; var idx = 0
        for (ch in clean) {
            val v = chars.indexOf(ch); if (v < 0) throw IllegalArgumentException("Bad char: $ch")
            buf = buf shl 5; buf = buf or v; bits += 5
            if (bits >= 8) { bytes[idx++] = (buf shr (bits - 8)).toByte(); bits -= 8 }
        }
        return bytes
    }
    fun generate(secret: String): String {
        val step = System.currentTimeMillis() / 1000 / PERIOD
        val key = decodeBase32(secret.trim())
        val msg = ByteArray(8); var t = step
        for (i in 7 downTo 0) { msg[i] = (t and 0xFF).toByte(); t = t ushr 8 }
        val mac = Mac.getInstance("HmacSHA1"); mac.init(SecretKeySpec(key, "RAW"))
        val hash = mac.doFinal(msg)
        val off = hash[hash.size - 1].toInt() and 0x0F
        val code = ((hash[off].toInt() and 0x7F) shl 24) or ((hash[off+1].toInt() and 0xFF) shl 16) or ((hash[off+2].toInt() and 0xFF) shl 8) or (hash[off+3].toInt() and 0xFF)
        val otp = code % 10.0.pow(DIGITS.toDouble()).toInt()
        return otp.toString().padStart(DIGITS, '0').let { "${it.substring(0,3)} ${it.substring(3)}" }
    }
    fun generateRaw(secret: String) = generate(secret).replace(" ", "")
    fun secondsRemaining(): Int = (PERIOD - (System.currentTimeMillis() / 1000) % PERIOD).toInt()
    fun isValidSecret(secret: String): Boolean = try {
        val c = secret.uppercase().replace("=","").replace(" ","").replace("-","")
        c.isNotEmpty() && c.length >= 8 && decodeBase32(c).isNotEmpty()
    } catch (e: Exception) { false }
    /** Extract secret key from various formats: otpauth://, plain key, with spaces/dashes */
    fun extractSecret(raw: String): String? {
        val trimmed = raw.trim()
        // otpauth://totp/...?secret=XXXXX&...
        if (trimmed.startsWith("otpauth://")) {
            val match = Regex("[?&]secret=([A-Za-z2-7=]+)", RegexOption.IGNORE_CASE).find(trimmed)
            return match?.groupValues?.get(1)?.uppercase()
        }
        // Plain base32 key (possibly with spaces/dashes)
        val clean = trimmed.uppercase().replace(" ","").replace("-","").replace("=","")
        if (clean.matches(Regex("[A-Z2-7]+"))) return clean
        return null
    }
}
