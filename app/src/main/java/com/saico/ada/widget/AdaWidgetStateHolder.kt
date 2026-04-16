package com.saico.ada.widget

// Simple in-memory holder — populated by the receiver before provideGlance is called.
// This is acceptable because the receiver and widget run in the same process.
object AdaWidgetStateHolder {
    @Volatile
    private var tareas: List<WidgetTarea> = emptyList()

    fun setTareas(list: List<WidgetTarea>) {
        tareas = list
    }

    fun getTareas(): List<WidgetTarea> = tareas
}
