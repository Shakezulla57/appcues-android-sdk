package com.appcues.debugger.ui

import com.appcues.R
import com.appcues.analytics.AnalyticsEvent
import com.appcues.debugger.model.EventType
import com.appcues.debugger.model.EventType.CUSTOM
import com.appcues.debugger.model.EventType.EXPERIENCE
import com.appcues.debugger.model.EventType.GROUP_UPDATE
import com.appcues.debugger.model.EventType.SCREEN
import com.appcues.debugger.model.EventType.SESSION
import com.appcues.debugger.model.EventType.USER_PROFILE

internal fun EventType?.getTitleString(): Int {
    return when (this) {
        EXPERIENCE -> R.string.debugger_recent_events_filter_experience
        GROUP_UPDATE -> R.string.debugger_recent_events_filter_group
        USER_PROFILE -> R.string.debugger_recent_events_filter_profile
        CUSTOM -> R.string.debugger_recent_events_filter_custom
        SCREEN -> R.string.debugger_recent_events_filter_screen
        SESSION -> R.string.debugger_recent_events_filter_session
        else -> R.string.debugger_recent_events_filter_all
    }
}

internal fun EventType?.toResourceId(): Int {
    return when (this) {
        EXPERIENCE -> R.drawable.appcues_ic_experience
        GROUP_UPDATE -> R.drawable.appcues_ic_group
        USER_PROFILE -> R.drawable.appcues_ic_user_profile
        CUSTOM -> R.drawable.appcues_ic_custom
        SCREEN -> R.drawable.appcues_ic_screen
        SESSION -> R.drawable.appcues_ic_session
        else -> R.drawable.appcues_ic_all
    }
}

internal fun String.toEventType(): EventType = when (this) {
    AnalyticsEvent.ScreenView.eventName -> SCREEN
    AnalyticsEvent.SessionStarted.eventName,
    AnalyticsEvent.SessionSuspended.eventName,
    AnalyticsEvent.SessionResumed.eventName,
    AnalyticsEvent.SessionReset.eventName -> SESSION
    AnalyticsEvent.ExperienceStepSeen.eventName,
    AnalyticsEvent.ExperienceStepInteraction.eventName,
    AnalyticsEvent.ExperienceStepCompleted.eventName,
    AnalyticsEvent.ExperienceStepError.eventName,
    AnalyticsEvent.ExperienceStepRecovered.eventName,
    AnalyticsEvent.ExperienceStarted.eventName,
    AnalyticsEvent.ExperienceCompleted.eventName,
    AnalyticsEvent.ExperienceDismissed.eventName,
    AnalyticsEvent.ExperienceError.eventName -> EXPERIENCE
    else -> CUSTOM
}

internal fun String.toEventTitle(): Int? = when (this) {
    AnalyticsEvent.ScreenView.eventName -> R.string.debugger_event_type_screen_view_title
    AnalyticsEvent.SessionStarted.eventName -> R.string.debugger_event_type_session_started_title
    AnalyticsEvent.SessionSuspended.eventName -> R.string.debugger_event_type_session_suspended_title
    AnalyticsEvent.SessionResumed.eventName -> R.string.debugger_event_type_session_resumed_title
    AnalyticsEvent.SessionReset.eventName -> R.string.debugger_event_type_session_reset_title
    AnalyticsEvent.ExperienceStepSeen.eventName -> R.string.debugger_event_type_step_seen_title
    AnalyticsEvent.ExperienceStepInteraction.eventName -> R.string.debugger_event_type_step_interaction_title
    AnalyticsEvent.ExperienceStepCompleted.eventName -> R.string.debugger_event_type_step_completed_title
    AnalyticsEvent.ExperienceStepError.eventName -> R.string.debugger_event_type_step_error_title
    AnalyticsEvent.ExperienceStepRecovered.eventName -> R.string.debugger_event_type_step_recovered_title
    AnalyticsEvent.ExperienceStarted.eventName -> R.string.debugger_event_type_experience_started_title
    AnalyticsEvent.ExperienceCompleted.eventName -> R.string.debugger_event_type_experience_completed_title
    AnalyticsEvent.ExperienceDismissed.eventName -> R.string.debugger_event_type_experience_dismissed_title
    AnalyticsEvent.ExperienceError.eventName -> R.string.debugger_event_type_experience_error_title
    else -> null
}