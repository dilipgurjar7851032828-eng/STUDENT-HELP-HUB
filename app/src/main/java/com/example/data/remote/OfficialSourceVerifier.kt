package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class LiveVerificationResult(
    val url: String,
    val status: String, // "VERIFIED", "NEEDS VERIFICATION", "EXPIRED"
    val lastVerifiedDate: String,
    val httpCode: Int = 0,
    val isReachable: Boolean = false,
    val message: String = ""
)

data class OfficialSourceConnectionStatus(
    val name: String,
    val authority: String,
    val portalUrl: String,
    val isLive: Boolean,
    val latencyMs: Long = 0,
    val httpStatus: Int = 0,
    val connectionNote: String = "",
    val requiresBackendConfig: Boolean = false,
    val configurationKey: String? = null
)

/**
 * Service to connect to and verify official government, university, scholarship,
 * and examination web portals.
 */
object OfficialSourceVerifier {

    private const val TAG = "OfficialSourceVerifier"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .callTimeout(8, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    // List of major official national student portals to monitor
    val officialPortalsToMonitor = listOf(
        OfficialSourceConnectionStatus(
            name = "National Scholarship Portal (NSP)",
            authority = "Ministry of Electronics & IT / MoE, Govt of India",
            portalUrl = "https://scholarships.gov.in",
            isLive = true,
            connectionNote = "Central government DBT scholarship distribution gateway"
        ),
        OfficialSourceConnectionStatus(
            name = "National Testing Agency (NTA)",
            authority = "Department of Higher Education, Govt of India",
            portalUrl = "https://nta.ac.in",
            isLive = true,
            connectionNote = "Conducting body for CUET, JEE Main, NEET UG"
        ),
        OfficialSourceConnectionStatus(
            name = "CUET Samarth UG Portal",
            authority = "National Testing Agency / Ministry of Education",
            portalUrl = "https://cuet.samarth.ac.in",
            isLive = true,
            connectionNote = "Official Central & State University Admission Portal"
        ),
        OfficialSourceConnectionStatus(
            name = "JEE (Main) Examination Portal",
            authority = "National Testing Agency",
            portalUrl = "https://jeemain.nta.nic.in",
            isLive = true,
            connectionNote = "Official portal for IIT/NIT/IIIT entrance & JoSAA eligibility"
        ),
        OfficialSourceConnectionStatus(
            name = "NEET (UG) Medical Portal",
            authority = "National Testing Agency",
            portalUrl = "https://neet.nta.nic.in",
            isLive = true,
            connectionNote = "Official gateway for MBBS/BDS/BAMS nationwide admissions"
        ),
        OfficialSourceConnectionStatus(
            name = "Delhi University CSAS Admission Portal",
            authority = "University of Delhi (Central University)",
            portalUrl = "https://admission.uod.ac.in",
            isLive = true,
            connectionNote = "Common Seat Allocation System (CSAS) for DU colleges"
        ),
        OfficialSourceConnectionStatus(
            name = "AICTE Student Schemes Portal",
            authority = "All India Council for Technical Education",
            portalUrl = "https://www.aicte-india.org",
            isLive = true,
            connectionNote = "Pragati, Saksham & Swanath engineering scholarships"
        ),
        OfficialSourceConnectionStatus(
            name = "PM Vidyalaxmi Portal",
            authority = "Department of Higher Education / NSDL",
            portalUrl = "https://www.vidyalakshmi.co.in",
            isLive = true,
            connectionNote = "Official education loan interest subsidy & credit scheme"
        ),
        OfficialSourceConnectionStatus(
            name = "IIT Delhi Official Portal",
            authority = "Institute of National Importance",
            portalUrl = "https://home.iitd.ac.in",
            isLive = true,
            connectionNote = "Official IIT Delhi admissions & academic announcements"
        ),
        OfficialSourceConnectionStatus(
            name = "Delhi State e-District Portal",
            authority = "Govt. of NCT of Delhi",
            portalUrl = "https://edistrict.delhigovt.nic.in",
            isLive = true,
            connectionNote = "State scholarship & income/domicile certificate gateway"
        ),
        OfficialSourceConnectionStatus(
            name = "CLAT Consortium of NLUs",
            authority = "Consortium of National Law Universities",
            portalUrl = "https://consortiumofnlus.ac.in",
            isLive = true,
            connectionNote = "Official national law admissions & rank counseling"
        ),
        OfficialSourceConnectionStatus(
            name = "Open Government Data (OGD) API",
            authority = "National Informatics Centre / data.gov.in",
            portalUrl = "https://data.gov.in",
            isLive = false,
            connectionNote = "Requires DATA_GOV_IN_API_KEY in Secrets panel",
            requiresBackendConfig = true,
            configurationKey = "DATA_GOV_IN_API_KEY"
        )
    )

    /**
     * Checks if a deadline has expired relative to current date (format: yyyy-MM-dd)
     */
    fun isDeadlineExpired(deadlineDate: String): Boolean {
        if (deadlineDate.isBlank()) return false
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return deadlineDate < todayStr
    }

    /**
     * Returns today's date formatted as yyyy-MM-dd
     */
    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Performs a live HTTP connectivity check on an official portal URL.
     * If the portal responds with a valid HTTP code (200..399), it is verified as reachable.
     */
    suspend fun verifyOfficialSource(
        url: String,
        deadlineDate: String = ""
    ): LiveVerificationResult = withContext(Dispatchers.IO) {
        val today = getTodayDate()

        // 1. Deadline expiration check
        if (isDeadlineExpired(deadlineDate)) {
            return@withContext LiveVerificationResult(
                url = url,
                status = "EXPIRED",
                lastVerifiedDate = today,
                message = "Official application window closed on $deadlineDate"
            )
        }

        // 2. Validate URL format
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return@withContext LiveVerificationResult(
                url = url,
                status = "NEEDS VERIFICATION",
                lastVerifiedDate = today,
                message = "Invalid or unverified URL structure"
            )
        }

        // 3. Perform live HTTP probe (HEAD with GET fallback)
        try {
            var request = Request.Builder()
                .url(url)
                .head()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            var response = httpClient.newCall(request).execute()
            var code = response.code
            response.close()

            // Some government firewalls reject HEAD with 403 or 405; fallback to GET
            if (code == 403 || code == 405) {
                request = Request.Builder()
                    .url(url)
                    .get()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .build()
                response = httpClient.newCall(request).execute()
                code = response.code
                response.close()
            }

            if (code in 200..399) {
                LiveVerificationResult(
                    url = url,
                    status = "VERIFIED",
                    lastVerifiedDate = today,
                    httpCode = code,
                    isReachable = true,
                    message = "Official source actively responding (HTTP $code)"
                )
            } else {
                LiveVerificationResult(
                    url = url,
                    status = "NEEDS VERIFICATION",
                    lastVerifiedDate = today,
                    httpCode = code,
                    isReachable = false,
                    message = "Server returned status HTTP $code"
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Official source check failed for $url: ${e.message}")
            // When network is unavailable or server is unreachable, strictly flag NEEDS VERIFICATION
            LiveVerificationResult(
                url = url,
                status = "NEEDS VERIFICATION",
                lastVerifiedDate = today,
                httpCode = 0,
                isReachable = false,
                message = "Live verification pending: ${e.message ?: "Host unreachable"}"
            )
        }
    }

    /**
     * Probes all monitored official student gateways and returns their live connectivity status.
     * Runs independent portal checks concurrently on Dispatchers.IO with timeouts to prevent slow sequential blocking.
     */
    suspend fun checkAllOfficialPortals(): List<OfficialSourceConnectionStatus> = withContext(Dispatchers.IO) {
        supervisorScope {
            officialPortalsToMonitor.map { portal ->
                async {
                    probePortalSafely(portal)
                }
            }.awaitAll()
        }
    }

    private fun probePortalSafely(portal: OfficialSourceConnectionStatus): OfficialSourceConnectionStatus {
        if (portal.requiresBackendConfig) {
            return portal.copy(
                isLive = false,
                latencyMs = 0,
                httpStatus = 0,
                connectionNote = "Backend API key required (${portal.configurationKey}) in AI Studio Secrets"
            )
        }

        return try {
            var request = Request.Builder()
                .url(portal.portalUrl)
                .head()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            val startTime = System.currentTimeMillis()
            var response = httpClient.newCall(request).execute()
            var latency = System.currentTimeMillis() - startTime
            var code = response.code
            response.close()

            if (code == 403 || code == 405) {
                request = Request.Builder()
                    .url(portal.portalUrl)
                    .get()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .build()
                val subStart = System.currentTimeMillis()
                response = httpClient.newCall(request).execute()
                latency = System.currentTimeMillis() - subStart
                code = response.code
                response.close()
            }

            val isOk = code in 200..399
            portal.copy(
                isLive = isOk,
                latencyMs = latency,
                httpStatus = code,
                connectionNote = if (isOk) "Active & Responding ($latency ms)" else "Server status HTTP $code"
            )
        } catch (e: Exception) {
            Log.w(TAG, "Probe failed for ${portal.portalUrl}: ${e.message}")
            portal.copy(
                isLive = false,
                latencyMs = 0,
                httpStatus = 0,
                connectionNote = "Live probe pending: ${e.localizedMessage ?: "No direct connection"}"
            )
        }
    }
}
