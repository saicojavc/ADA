package com.saico.ada.common.util

import com.saico.ada.model.EventCategory

object CategorySuggester {

    private val palabrasMaternidad = listOf(
        "bebe", "nino", "nina", "hijo", "hija", "hijos", "hijas", "recien nacido", "lactancia", "leche materna",
        "biberon", "chupete", "cuna", "carrito", "cochecito", "pañal", "panal", "pediatra", "guarderia", "jardin infantil",
        "colegio", "escuela", "tarea escolar", "mochila", "uniforme", "cumpleanos nino", "cumpleanos nina", "fiesta infantil",
        "embarazo", "embarazada", "ecografia", "ultrasonido", "obstetra", "ginecologo", "parto", "preparto", "posparto",
        "cesarea", "semana de gestacion", "primer trimestre", "nana", "bua", "berrinche", "rabieta", "siesta bebe",
        "introduccion alimentaria", "papilla", "puré bebe", "abuela cuida", "abuela bebe"
    )

    private val palabrasTrabajo = listOf(
        "reunion", "junta", "scrum", "daily", "standup", "sprint", "retrospectiva", "planning", "one on one", "1 on 1",
        "llamada de trabajo", "videollamada trabajo", "zoom work", "meet work", "teams", "slack", "cliente", "proyecto",
        "entregable", "deadline", "plazo", "informe", "reporte", "presentacion trabajo", "propuesta", "cotizacion",
        "factura", "cobro", "pago proveedor", "contrato", "firma contrato", "revision codigo", "deploy", "lanzamiento",
        "release", "soporte", "ticket", "incidencia", "trabajo", "oficina", "office", "home office", "remoto", "jefe",
        "jefa", "gerente", "director", "ceo", "rrhh", "recursos humanos", "entrevista trabajo", "evaluacion",
        "capacitacion", "formacion laboral", "curso trabajo", "networking", "conferencia", "congreso", "taller profesional",
        "workshop", "webinar", "seminario"
    )

    private val palabrasHogar = listOf(
        "limpiar", "limpeza", "barrer", "fregar", "trapear", "aspirar", "ordenar", "organizar casa", "desinfectar",
        "lavar ropa", "tender ropa", "planchar", "doblar ropa", "guardar ropa", "cocinar", "receta", "preparar comida",
        "hornear", "amasar", "descongelar", "marinar", "meal prep", "compra", "compras", "supermercado", "mercado",
        "tienda", "lista compras", "mandado", "verduleria", "carniceria", "ferreteria", "farmacia hogar", "reparar",
        "arreglar", "plomero", "electricista", "pintar casa", "pared", "goteras", "fuga", "instalar", "montar mueble",
        "ikea", "perforacion", "taladro", "tornillo", "casa", "hogar", "habitacion", "cuarto", "bano", "cocina",
        "jardin", "patio", "balcon", "terraza", "garage", "perro", "gato", "mascota", "veterinario", "pasear perro",
        "comida perro", "comida gato", "vacuna mascota"
    )

    private val palabrasBienestar = listOf(
        "yoga", "gym", "gimnasio", "ejercicio", "entrenamiento", "crossfit", "pilates", "spinning", "nadar", "natacion",
        "correr", "running", "caminar", "senderismo", "ciclismo", "bicicleta", "pesas", "cardio", "estiramientos",
        "flexiones", "abdominales", "zumba", "baile", "kickboxing", "boxeo", "medico", "doctor", "cita medica",
        "consulta", "chequeo", "analisis", "sangre", "presion arterial", "vacuna", "vacunacion", "farmacia",
        "medicamento", "pastilla", "tratamiento", "terapia fisica", "fisioterapia", "quiropraxia", "masaje", "acupuntura",
        "dentista", "odontologo", "oculista", "optomentista", "meditar", "meditacion", "mindfulness", "respiracion",
        "psicologo", "psiquiatra", "terapia", "sesion terapia", "diario emocional", "journaling", "gratitud",
        "descanso", "dormir", "siesta", "nap", "descansar", "relajar", "relajacion", "spa", "bano relajante",
        "dieta", "nutricion", "nutricionista", "ayuno", "comer sano", "ensalada", "proteina", "suplemento", "vitamina",
        "hidratacion", "agua", "infusion", "batido saludable", "piel", "skincare", "crema", "serum", "rutina facial",
        "mascarilla", "protector solar", "hidratante", "peluqueria", "corte pelo", "manicura", "pedicura", "bienestar",
        "autocuidado", "autoestima", "habito saludable", "reto saludable", "paso diario", "pasos"
    )

    private val palabrasPersonal = listOf(
        "banco", "transferencia", "pago", "factura personal", "impuesto", "declaracion", "seguro", "poliza",
        "inversion", "ahorro", "presupuesto", "tramite", "cita gobierno", "cita banco", "renovar", "pasaporte",
        "dni", "cedula", "licencia conducir", "registro", "notaria", "abogado", "amigo", "amiga", "cena", "comida con",
        "cafe con", "cumpleanos", "aniversario", "boda", "evento", "pelicula", "concierto", "teatro", "exposicion",
        "viaje", "vuelo", "hotel", "reserva", "leer", "libro", "curso", "aprender", "idioma", "ingles", "podcast",
        "video tutorial"
    )

    fun suggestCategory(titulo: String, isMother: Boolean): EventCategory {
        val text = titulo.normalize()
        if (text.isBlank()) return EventCategory.UNCATEGORIZED

        val candidates = mutableListOf(
            EventCategory.WORK to palabrasTrabajo,
            EventCategory.HOME to palabrasHogar,
            EventCategory.WELLNESS to palabrasBienestar,
            EventCategory.PERSONAL to palabrasPersonal
        )

        if (isMother) {
            candidates.add(EventCategory.MATERNITY to palabrasMaternidad)
        }

        val winner = candidates
            .map { (category, keywords) ->
                val hits = keywords.count { kw -> text.contains(kw.normalize()) }
                category to hits
            }
            .filter { it.second > 0 }
            .maxByOrNull { it.second }

        return winner?.first ?: EventCategory.UNCATEGORIZED
    }
}
