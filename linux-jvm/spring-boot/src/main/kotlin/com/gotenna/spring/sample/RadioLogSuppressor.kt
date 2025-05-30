package com.gotenna.spring.sample

import java.io.OutputStream

class RadioLogSuppressor(private val source: OutputStream): OutputStream() {

    override fun write(byte: Int) {
        source.write(byte)
    }

    override fun write(bytes: ByteArray, offset: Int, length: Int) {
        val message = String(bytes, offset, length)
        if (shouldBlock(message)) return
        source.write(bytes, offset, length)
    }

    private fun shouldBlock(message: String): Boolean {
        return message.contains("Device - ")
    }

}