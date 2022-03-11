import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.appcues.data.model.styling.ComponentStyle
import com.appcues.ui.AppcuesTraitAnimatedVisibility
import com.appcues.ui.extensions.modalStyle

@Composable
internal fun ExpandedBottomSheetModal(style: ComponentStyle?, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AppcuesTraitAnimatedVisibility(
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            Surface(
                modifier = Modifier
                    // will fill max width
                    .fillMaxWidth()
                    // will fill height in 95%
                    .fillMaxHeight(fraction = 0.95f)
                    // default modal style modifiers
                    .modalStyle(
                        style = style,
                        isDark = isSystemInDarkTheme(),
                        modifier = Modifier.bottomSheetCorner(style),
                    ),
                content = content,
            )
        }
    }
}

private fun Modifier.bottomSheetCorner(style: ComponentStyle?) = this.then(
    if (style?.cornerRadius != null && style.cornerRadius != 0) Modifier
        .clip(RoundedCornerShape(topStart = style.cornerRadius.dp, topEnd = style.cornerRadius.dp))
    else Modifier
)