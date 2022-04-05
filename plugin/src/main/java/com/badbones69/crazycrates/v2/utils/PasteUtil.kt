package com.badbones69.crazycrates.v2.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

/**
 * A paste util using bin.bloom.host
 * Only used for easy debugging.
 */
object PasteUtil {

    fun create(text: String): String {
        val postData = text.toByteArray(StandardCharsets.UTF_8)
        val postDataLength = postData.size

        val requestURL = "https://bin.bloom.host/documents"

        val url = URL(requestURL)

        val conn = url.openConnection() as HttpsURLConnection

        conn.doOutput = true
        conn.instanceFollowRedirects = false
        conn.requestMethod = "POST"

        conn.setRequestProperty("User-Agent", "Crazy Crates | Personal Debugger")
        conn.setRequestProperty("Content-Length", postDataLength.toString())

        conn.useCaches = false

        var response: String? = null

        runCatching {
            val out = DataOutputStream(conn.outputStream)
            out.write(postData)
            val readme = BufferedReader(InputStreamReader(conn.inputStream))
            response = readme.readLine()
        }
        return "https://bin.bloom.host/$response"
    }
}