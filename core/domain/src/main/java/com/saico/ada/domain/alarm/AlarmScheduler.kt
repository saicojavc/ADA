package com.saico.ada.domain.alarm

import com.saico.ada.model.Tarea

interface AlarmScheduler {
    fun schedule(tarea: Tarea)
    fun cancel(tarea: Tarea)
}
