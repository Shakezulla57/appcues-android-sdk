package com.appcues.debugger.screencapture

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appcues.BuildConfig
import com.appcues.R
import com.appcues.ViewElement
import com.appcues.util.ContextResources
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date
import java.util.UUID

@JsonClass(generateAdapter = true)
internal data class Capture(
    val id: UUID = UUID.randomUUID(),
    val appId: String,
    var displayName: String,
    val screenshotImageUrl: String?,
    val layout: ViewElement,
    val metadata: Metadata,
    val timestamp: Date,
) {
    // this is a lateinit var instead of a constructor arg so we can ignore it in JSON
    // serialization and a default value is not needed for the Moshi generated adapter
    @Json(ignore = true)
    lateinit var screenshot: Bitmap

    @JsonClass(generateAdapter = true)
    internal data class Metadata(
        val appName: String,
        val appBuild: String,
        val appVersion: String,
        val deviceModel: String,
        val deviceWidth: Int,
        val deviceHeight: Int,
        val deviceOrientation: String,
        val deviceType: String,
        val bundlePackageId: String,
        val sdkVersion: String,
        val sdkName: String,
        val osName: String,
        val osVersion: String,
        val insets: Insets,
    )

    @JsonClass(generateAdapter = true)
    internal data class Insets(
        val left: Int,
        val right: Int,
        val top: Int,
        val bottom: Int,
    )
}

internal fun ContextResources.generateCaptureMetadata(rootView: View): Capture.Metadata {
    val density = displayMetrics.density
    val width = rootView.width.toDp(density)
    val height = rootView.height.toDp(density)
    val insets = ViewCompat.getRootWindowInsets(rootView)?.getInsets(WindowInsetsCompat.Type.systemBars())

    return Capture.Metadata(
        appName = getAppName(),
        appBuild = getAppBuild().toString(),
        appVersion = getAppVersion(),
        deviceModel = getDeviceName(),
        deviceWidth = width,
        deviceHeight = height,
        deviceOrientation = orientation,
        deviceType = getString(R.string.appcues_device_type),
        bundlePackageId = getPackageName(),
        sdkVersion = BuildConfig.SDK_VERSION,
        sdkName = "appcues-android",
        osName = "android",
        osVersion = "${VERSION.SDK_INT}",
        insets = Capture.Insets(
            left = insets?.left?.toDp(density) ?: 0,
            right = insets?.right?.toDp(density) ?: 0,
            top = insets?.top?.toDp(density) ?: 0,
            bottom = insets?.bottom?.toDp(density) ?: 0,
        )
    )
}

internal fun Int.toDp(density: Float) =
    (this / density).toInt()
