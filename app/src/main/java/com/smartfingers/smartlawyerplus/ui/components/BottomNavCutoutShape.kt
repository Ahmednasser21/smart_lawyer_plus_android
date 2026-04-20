package com.smartfingers.smartlawyerplus.ui.components

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BottomNavCutoutShape(private val fabRadius: Float = 60f) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ) = androidx.compose.ui.graphics.Outline.Generic(
        Path().apply {
            val centerX = size.width / 2f
            val cutoutWidth = fabRadius * 2.2f
            val cutoutHeight = fabRadius -25f

            moveTo(0f, 0f)
            lineTo(centerX - cutoutWidth / 1.5f, 0f)

            cubicTo(
                centerX - cutoutWidth / 3f, 0f,
                centerX - cutoutWidth / 3f, cutoutHeight,
                centerX, cutoutHeight
            )
            cubicTo(
                centerX + cutoutWidth / 3f, cutoutHeight,
                centerX + cutoutWidth / 3f, 0f,
                centerX + cutoutWidth / 1.5f, 0f
            )

            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
    )
}