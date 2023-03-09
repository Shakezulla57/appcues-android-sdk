package com.appcues.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.appcues.di.AppcuesKoinContext
import com.appcues.logging.Logcues
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

internal class InAppReviewActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_TIMEOUT_MILLISECONDS = 3000L

        private const val EXTRA_SCOPE_ID = "EXTRA_SCOPE_ID"

        // since there can only be one review activity running at a time, this companion level
        // deferred can be used to await completion
        var completion: CompletableDeferred<Boolean>? = null

        fun getIntent(context: Context, scopeId: String): Intent =
            Intent(context, InAppReviewActivity::class.java).apply {
                putExtras(
                    bundleOf(
                        EXTRA_SCOPE_ID to scopeId
                    )
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    private var success = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove enter animation from this activity
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)

        val requestCompletion = CompletableDeferred<Boolean>()

        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->

            // will never get here on non-play store builds - need a timeout fallback, which is handled
            // below.  This completion handler will short-circuit that timeout and run the normal review flow,
            // when available.
            requestCompletion.complete(true)

            if (task.isSuccessful) {
                val reviewInfo = task.result

                // We got the ReviewInfo object
                if (reviewInfo != null) {
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        success = true
                        finish()
                    }
                } else {
                    finish()
                }
            } else {
                finish()
            }
        }

        lifecycleScope.launch {
            try {
                withTimeout(REQUEST_TIMEOUT_MILLISECONDS) {
                    requestCompletion.await()
                }
            } catch (e: TimeoutCancellationException) {
                val scope = AppcuesKoinContext.koin.getScope(intent.getStringExtra(EXTRA_SCOPE_ID)!!)
                val logcues = scope.get<Logcues>()
                logcues.info("In-App Review not available for this application")
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        // remove exit animation from this activity
        overridePendingTransition(0, 0)

        completion?.complete(true)
    }
}
