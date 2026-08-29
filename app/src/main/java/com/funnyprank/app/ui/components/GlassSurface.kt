package com.funnyprank.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.Dp
import com.funnyprank.app.ui.theme.GlassWhite

@Composable
fun GlassSurface(
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = GlassWhite
    ) {
        Box { content() }
    }
}
