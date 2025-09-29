package org.example.app.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R

/**
 * Simple, subtle divider for lists with Ocean Professional color.
 */
class ListDividerDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.ocean_surface_edge)
        strokeWidth = context.resources.displayMetrics.density
    }
    private val bounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.save()
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width - parent.paddingRight).toFloat()

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child: View = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom.toFloat()
            c.drawLine(left, bottom, right, bottom, paint)
        }
        c.restore()
    }
}
