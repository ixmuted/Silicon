package com.silicon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.silicon.R

@Composable
fun SiliconLogo(
    size: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.ic_silicon_emblem),
        contentDescription = "Silicon",
        modifier = modifier.size(size)
    )
}
