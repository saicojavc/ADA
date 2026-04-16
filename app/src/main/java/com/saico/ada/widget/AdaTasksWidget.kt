package com.saico.ada.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent

class AdaTasksWidget : GlanceAppWidget() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Data is injected by the receiver via state, fetched externally
        val tareas = AdaWidgetStateHolder.getTareas()
        provideContent {
            AdaTasksWidgetContent(tareas = tareas)
        }
    }
}
