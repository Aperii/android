package com.aperii.widgets.debugging.apps

import com.aperii.utilities.Utils.average
import com.aperii.utilities.rest.RestAPI
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class Ping : BaseDebugApplication(), KoinComponent {

    private val api: RestAPI by inject()

    override fun onExec(args: List<String>) {
        StringBuilder().apply {
            val totalPings = api.pings.size
            val failedPings = api.pings.filter { !it.successful }.size
            val successfulPings = totalPings - failedPings
            val lastPing = api.pings.last().duration

            val oneMinPings =
                api.pings.filter { it.time.after(Date(System.currentTimeMillis() - (60 * 1000))) }
            val oneMinAvg = oneMinPings.average()

            val fiveMinPings =
                api.pings.filter { it.time.after(Date(System.currentTimeMillis() - (5 * 60 * 1000))) }
            val fiveMinAvg = fiveMinPings.average()

            val tenMinPings =
                api.pings.filter { it.time.after(Date(System.currentTimeMillis() - (10 * 60 * 1000))) }
            val tenMinAvg = tenMinPings.average()

            append(
                """
                Average in last:
                10s: ${lastPing}ms
                1m: ${oneMinAvg}ms
                5m: ${fiveMinAvg}ms
                10m: ${tenMinAvg}ms
                All time: ${api.ping}ms
            
                Pings: $totalPings ($failedPings failed - ${(successfulPings.toFloat() / totalPings.toFloat()) * 100f}% successful)
            """.trimIndent()
            )
            send(this.toString())
        }
    }

}