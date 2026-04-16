package com.saico.ada.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.domain.use_case.GenerateTareaInstancesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AdaTasksWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = AdaTasksWidget()

    @Inject
    lateinit var tareaRepository: TareaRepository

    @Inject
    lateinit var excepcionRepository: TareaExcepcionRepository

    @Inject
    lateinit var generateTareaInstancesUseCase: GenerateTareaInstancesUseCase

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refreshWidget(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        refreshWidget(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshWidget(context: Context) {
        scope.launch {
            try {
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")

                val allTareas = tareaRepository.getAllTareas().first()
                val allExcepciones = excepcionRepository.getAllExcepciones().first()

                val plantillas = allTareas.filter { it.esPlantilla }
                val normales = allTareas.filter { !it.esPlantilla }

                val instanciasHoy = generateTareaInstancesUseCase(
                    plantillas, allExcepciones, today
                )
                val normalesHoy = normales.filter {
                    it.fechaHoraInicio.toLocalDate() == today
                }

                val todasHoy = (normalesHoy + instanciasHoy)
                    .sortedBy { it.fechaHoraInicio }

                val widgetTareas = todasHoy.map { tarea ->
                    WidgetTarea(
                        id = tarea.id,
                        titulo = tarea.titulo,
                        hora = tarea.fechaHoraInicio.format(formatter),
                        colorHex = tarea.colorHex,
                        estaCompletada = tarea.estaCompletada,
                        categoria = tarea.categoria
                    )
                }

                AdaWidgetStateHolder.setTareas(widgetTareas)
                glanceAppWidget.updateAll(context)

            } catch (e: Exception) {
                AdaWidgetStateHolder.setTareas(emptyList())
                glanceAppWidget.updateAll(context)
            }
        }
    }
}
