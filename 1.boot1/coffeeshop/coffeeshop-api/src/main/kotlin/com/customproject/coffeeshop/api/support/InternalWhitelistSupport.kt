package com.customproject.coffeeshop.api.support

import org.springframework.stereotype.Component
import java.net.InetAddress

@Component
class InternalWhitelistSupport {
    fun allow(ipAddress: String): Boolean {
        val inetAddress = InetAddress.getByName(ipAddress)
        return inetAddress.isLoopbackAddress || inetAddress.isAnyLocalAddress
    }
}