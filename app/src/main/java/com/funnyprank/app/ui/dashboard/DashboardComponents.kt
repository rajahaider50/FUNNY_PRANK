package com.funnyprank.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.R
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.GlassHi
import com.funnyprank.app.ui.theme.GlassLow
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextWhite

@Composable
fun DashboardBackdrop(content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit) {
    PrankBackdrop { content() }
}

@Composable
fun DashboardScaffold(
    eyebrow: String,
    titlePrefix: String,
    titleAccent: String,
    accentGreen: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 17.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    eyebrow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFF646D76)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        titlePrefix,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.8).sp,
                        color = TextWhite
                    )
                    Text(
                        titleAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.8).sp,
                        color = if (accentGreen) BrandGreen else com.funnyprank.app.ui.theme.BrandRed
                    )
                }
            }
            trailing?.invoke()
        }
        content()
    }
}

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 20,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(GlassHi, GlassLow)))
            .border(1.dp, GlassBorder, shape)
    ) { content() }
}

@Composable
fun CountBadge(text: String) {
    GlassBox(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), cornerRadius = 10) {
        Text(text, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun BrandLogo(size: Int = 42) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Funny Prank logo",
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(13.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(13.dp)),
        alignment = Alignment.Center
    )
}

@Composable
fun IconBox(
    icon: ImageVector,
    tint: Color,
    size: Int = 46,
    contentDescription: String? = null
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.08f))
            .border(1.dp, tint.copy(alpha = 0.22f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(20.dp))
    }
}
