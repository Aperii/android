package com.aperii.utilities.update

import java.io.IOException
import java.io.InputStream

class ObservableInputStream(
    private val wrapped: InputStream,
    private val onBytesRead: (Long) -> Unit
) : InputStream() {
    private var bytesRead: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        val res = wrapped.read()
        if (res > -1) {
            bytesRead++
        }
        onBytesRead(bytesRead)
        return res
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        val res = wrapped.read(b)
        if (res > -1) {
            bytesRead += res
            onBytesRead(bytesRead)
        }
        return res
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val res = wrapped.read(b, off, len)
        if (res > -1) {
            bytesRead += res
            onBytesRead(bytesRead)
        }
        return res
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val res = wrapped.skip(n)
        if (res > -1) {
            bytesRead += res
            onBytesRead(bytesRead)
        }
        return res
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return wrapped.available()
    }

    override fun markSupported(): Boolean {
        return wrapped.markSupported()
    }

    override fun mark(readlimit: Int) {
        wrapped.mark(readlimit)
    }

    @Throws(IOException::class)
    override fun reset() {
        wrapped.reset()
    }

    @Throws(IOException::class)
    override fun close() {
        wrapped.close()
    }
}