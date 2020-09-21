package com.customproject.coffeeshop.api.support

import org.apache.commons.lang3.StringUtils
import java.net.InetAddress
import java.net.UnknownHostException

class HostNameSupport {
    companion object {
        fun getLocalhost() : String {
            val envHostname = System.getenv("HOSTNAME")
            if (StringUtils.isNotEmpty(envHostname) && envHostname != "localhost") {
                return envHostname
            }
            try {
                val localHost = InetAddress.getLocalHost()
                if (StringUtils.isNotEmpty(localHost.hostName)) {
                    return localHost.hostName
                }
                return localHost.hostAddress
            } catch (e: UnknownHostException) {
                throw RuntimeException(e.message, e)
            }
        }
    }
}