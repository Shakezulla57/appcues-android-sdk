package com.appcues

/**
 * Provides a method to listen for analytics that have been published.
 */
interface AnalyticsListener {
    /**
     * Notifies the listener when Appcues analytics tracking occurs.
     *
     * @param type The type of the analytic.
     * @param value Contains the primary value of the analytic being tracked. For events - the event name,
     * for screens - the screen title, for identify - the userId, for group - the groupId
     * @param properties Optional properties that provide additional context about the analytic.
     * @param isInternal True, if the analytic was internally generated by the SDK, as opposed to passed in from the host application.
     * For example, flow or session analytics are internal.
     */
    fun trackedAnalytic(type: AnalyticType, value: String?, properties: Map<String, Any>?, isInternal: Boolean)
}