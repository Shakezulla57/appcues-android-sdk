package com.appcues.trait.appcues

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.appcues.trait.ContentHolderTrait

internal class DefaultContentHolderTrait(override val config: HashMap<String, Any>?) : ContentHolderTrait {

    @Composable
    override fun BoxScope.CreateContentHolder(pages: List<@Composable () -> Unit>, pageIndex: Int) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            pages[pageIndex]()
        }
    }
}