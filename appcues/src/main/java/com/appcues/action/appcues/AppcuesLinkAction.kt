package com.appcues.action.appcues

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.appcues.Appcues
import com.appcues.action.ActionConfigMap
import com.appcues.action.ExperienceAction
import com.appcues.action.getConfigOrDefault

internal class AppcuesLinkAction(
    override val config: ActionConfigMap,
    private val context: Context,
) : ExperienceAction {

    private val url = config.getConfigOrDefault<String?>("url", null)

    private val openExternally = config.getConfigOrDefault("openExternally", false)

    override suspend fun execute(appcues: Appcues) {
        // start web activity if url is not null
        if (url != null) {
            if (openExternally) {
                launchExternalBrowser(url)
            } else {
                launchInternalBrowser(url)
            }
        }
    }

    private fun launchExternalBrowser(url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.also {
            context.startActivity(it)
        }
    }

    private fun launchInternalBrowser(url: String) {
        CustomTabsIntent.Builder().build().apply {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.also {
            it.launchUrl(context, Uri.parse(url))
        }
    }
}
