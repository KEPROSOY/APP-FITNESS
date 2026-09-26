package com.example.data

import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.ExerciseVisualGuide

object ExerciseDatabase {

  val allExercisesCached: List<ExerciseVisualGuide> by lazy {
    legsExercises +
      glutesExercises +
      waistExercises +
      absExercises +
      chestExercises +
      backExercises +
      shoulderExercises +
      bicepsExercises +
      tricepsExercises +
      calvesExercises
  }

  fun getAllExercises(): List<ExerciseVisualGuide> {
    return allExercisesCached
  }

  fun getMuscleGroups(): List<String> = listOf(
    "Todos",
    "Piernas",
    "Glúteos",
    "Cintura",
    "Abdomen",
    "Pecho",
    "Espalda",
    "Hombros",
    "Bíceps",
    "Tríceps",
    "Gemelos"
  )

  // =========================================================================
  // 1. PIERNAS - CUÁDRICEPS E ISQUIOTIBIALES (28 Ejercicios)
  // =========================================================================
  val legsExercises = listOf(
    guide("sentadilla_trasera_olimpica", "Sentadilla Trasera con Barra Olímpica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica y Rack", "Piernas", "SQUAT",
      listOf("Cuádriceps (recto femoral, vasto externo y medio)"), listOf("Glúteo mayor", "Aductores", "Erectores espinales"),
      listOf("Barra apoyada sobre la porción media del trapecio", "Pies separados a la anchura de hombros con puntas a 20-30°", "Descender controlando hasta romper el paralelo de 90°")),
    guide("prensa_inclinada_45", "Prensa de Piernas Inclinada a 45°", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Prensa 45°", "Piernas", "LEG_PRESS",
      listOf("Cuádriceps completo"), listOf("Glúteo mayor", "Aductores"),
      listOf("Pies en el centro de la plataforma a la anchura de caderas", "Bajar la plataforma con recorrido amplio sin despegar la pelvis", "No bloquear las rodillas violentamente en la parte alta")),
    guide("hack_squat_profundo", "Sentadilla Hack en Máquina", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Hack Squat", "Piernas", "SQUAT",
      listOf("Vasto lateral y recto femoral del cuádriceps"), listOf("Glúteo mayor"),
      listOf("Espalda y zona lumbar firmemente pegadas al respaldo acolchado", "Descenso ultra profundo con flexión de rodilla pronunciada", "Empuje desde los talones con cadencia sostenida")),
    guide("sentadilla_bulgara_mancuernas", "Sentadilla Búlgara con Mancuernas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas y Banco Plano", "Piernas", "BULGARIAN_SPLIT_SQUAT",
      listOf("Cuádriceps y glúteo medio"), listOf("Isquiotibiales", "Core estabilizador"),
      listOf("Pie trasero apoyado sobre el banco en empeine", "Descenso vertical profundo hasta que la rodilla trasera roce el suelo", "Torso erguido para mayor carga en el cuádriceps delantero")),
    guide("extensiones_cuadriceps_maquina", "Extensiones de Cuádriceps en Máquina", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Selectorizada", "Piernas", "LEG_EXTENSION",
      listOf("Recto femoral y vasto medial (teardrop)"), listOf("Tendón rotuliano"),
      listOf("Alinear el eje de la máquina con la articulación de la rodilla", "Extensión completa sosteniendo 1.5s de contracción isométrica", "Descenso en 3 segundos sintiendo el estiramiento miofibrilar")),
    guide("sentadilla_frontal_olimpica", "Sentadilla Frontal con Barra Olímpica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "SQUAT",
      listOf("Cuádriceps (aislamiento anterior)"), listOf("Abdomen superior", "Trapecios"),
      listOf("Agarre olímpico con codos altos paralelos al suelo", "Torso completamente vertical durante todo el trayecto", "Profundidad máxima para estiramiento del vasto interno")),
    guide("curl_femoral_tumbado", "Curl Femoral Tumbado en Máquina", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Femoral Tumbado", "Piernas", "LEG_CURL",
      listOf("Bíceps femoral (cabeza larga y corta)"), listOf("Gemelos", "Glúteo"),
      listOf("Pelvis firmemente pegada al banco evitando arquear la zona lumbar", "Flexión explosiva llevando los talones hacia los glúteos", "Fase excéntrica de 3 segundos resistiendo el retorno")),
    guide("curl_femoral_sentado", "Curl Femoral Sentado en Máquina", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Femoral Sentado", "Piernas", "LEG_CURL",
      listOf("Semitendinoso y semimembranoso"), listOf("Bíceps femoral"),
      listOf("Mayor estiramiento pasivo por la flexión previa de cadera a 90°", "Ajustar el rodillo sobre los tobillos justo encima del tendón de Aquiles", "Pausa de 1 segundo en máxima flexión")),
    guide("peso_muerto_rumano_barra", "Peso Muerto Rumano con Barra Olímpica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "DEADLIFT",
      listOf("Isquiotibiales en estiramiento bajo carga"), listOf("Glúteo mayor", "Erectores espinales"),
      listOf("Bisagra de cadera pura: llevar las caderas hacia atrás", "Rodillas semiextendidas con ligera flexión fija de 15°", "Bajar la barra rozando los muslos hasta sentir el tirón en isquios")),
    guide("peso_muerto_rumano_mancuernas", "Peso Muerto Rumano con Mancuernas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas Pesadas", "Piernas", "DEADLIFT",
      listOf("Isquiotibiales y glúteos"), listOf("Antebrazos", "Espalda baja"),
      listOf("Permite un plano de movimiento más libre a los costados de las piernas", "Espalda recta con escápulas retraídas", "Empuje de cadera explosivo al subir contrayendo glúteos")),
    guide("sentadilla_sissy_maquina", "Sentadilla Sissy en Estación Guiada", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Estación Sissy Squat", "Piernas", "SQUAT",
      listOf("Recto femoral en máximo estiramiento distal"), listOf("Cuádriceps completo"),
      listOf("Fijar las pantorrillas y tobillos firmemente en los rodillos", "Descender inclinando el torso hacia atrás manteniendo cadera extendida", "Enfoque brutal en la cabeza central del cuádriceps")),
    guide("zancadas_caminando_mancuernas", "Zancadas Caminando con Mancuernas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas", "Piernas", "BULGARIAN_SPLIT_SQUAT",
      listOf("Cuádriceps y glúteo"), listOf("Isquiotibiales", "Estabilizadores pélvicos"),
      listOf("Paso amplio y firme; rodilla trasera baja a 2 cm del suelo", "Mantener el torso erguido y el abdomen en tensión", "Pisar con el talón de la pierna adelantada para impulsarse")),
    guide("sentadilla_goblet_pesada", "Sentadilla Goblet con Mancuerna Pesada", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna o Kettlebell", "Piernas", "SQUAT",
      listOf("Cuádriceps y aductores"), listOf("Core anterior", "Brazos"),
      listOf("Sujetar la mancuerna verticalmente pegada al pecho", "Separación de pies algo superior a hombros", "Bajar profundo empujando las rodillas hacia afuera con los codos")),
    guide("prensa_unilateral_piernas", "Prensa de Piernas Unilateral", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Prensa Inclinada", "Piernas", "LEG_PRESS",
      listOf("Cuádriceps y glúteo unilateral"), listOf("Estabilizadores"),
      listOf("Permite corregir asimetrías de fuerza entre ambas piernas", "Recorrido profundo controlado sin rotar la pelvis", "Empuje continuo sin tirones")),
    guide("nordic_curl_isquios", "Nordic Curl para Isquiotibiales", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Banco con anclaje o soporte", "Piernas", "LEG_CURL",
      listOf("Bíceps femoral excéntrico"), listOf("Glúteos", "Gemelos"),
      listOf("Tobillos anclados firmemente en el suelo o soporte", "Dejarse caer hacia adelante de manera ultralenta frenando con isquios", "Fuerza excéntrica extrema para blindar la rodilla")),
    guide("buenos_dias_barra", "Buenos Días con Barra Olímpica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "DEADLIFT",
      listOf("Isquiotibiales y erectores espinales"), listOf("Glúteo mayor"),
      listOf("Barra apoyada en trapecios igual que en sentadilla", "Flexión de cadera hacia atrás manteniendo la columna vertebral neutra", "Bajar hasta que el torso quede casi horizontal")),
    guide("sentadilla_zercher", "Sentadilla Zercher con Barra", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "SQUAT",
      listOf("Cuádriceps anterior"), listOf("Core completo", "Bíceps", "Espalda alta"),
      listOf("Barra sostenida en el pliegue interno de los codos flexionados", "Permite un descenso vertical absoluto con nulo estrés en la columna", "Gran estímulo en la parte distal del cuádriceps")),
    guide("step_ups_cajon_cuadriceps", "Step-Ups Dinámicos en Cajón", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Cajón Pliométrico y Mancuernas", "Piernas", "BULGARIAN_SPLIT_SQUAT",
      listOf("Cuádriceps"), listOf("Glúteos", "Gemelos"),
      listOf("Apoyar toda la planta del pie en el cajón de 40-50 cm", "Subir empujando con la pierna de apoyo sin saltar con la de abajo", "Descenso lento y controlado de 2 segundos")),
    guide("sentadilla_smith_pies_adelantados", "Sentadilla en Multipower con Pies Adelantados", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Multipower", "Piernas", "SQUAT",
      listOf("Aislamiento del cuádriceps"), listOf("Glúteo"),
      listOf("Pies colocados 30 cm por delante de la línea de la barra", "Permite descender con el torso recto como un pistón", "Menor sobrecarga lumbar con máxima tensión en cuádriceps")),
    guide("curl_femoral_de_pie_maquina", "Curl Femoral de Pie Unilateral", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Unilateral", "Piernas", "LEG_CURL",
      listOf("Bíceps femoral"), listOf("Gemelos"),
      listOf("Aislar cada pierna de forma independiente para pulir simetría", "Flexionar hasta tocar el glúteo con el talón", "Contracción voluntaria intensa en la cima")),
    guide("peso_muerto_piernas_rigidas", "Peso Muerto Piernas Semirrígidas con Mancuernas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas", "Piernas", "DEADLIFT",
      listOf("Isquiotibiales proximales"), listOf("Glúteo mayor"),
      listOf("Mínima flexión en rodillas mantenida estática", "Llevar el peso hacia los talones y caderas muy atrás", "Estiramiento profundo bajo carga sin arquear espalda")),
    guide("prensa_horizontal_maquina", "Prensa Horizontal de Piernas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina de Placas", "Piernas", "LEG_PRESS",
      listOf("Cuádriceps"), listOf("Glúteos"),
      listOf("Fácil ajuste de carga ideal para series de drop sets o descansos cortos", "Empuje suave con rango completo", "Respiración continua")),
    guide("zancadas_inversas_barra", "Zancadas Hacia Atrás con Barra", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "BULGARIAN_SPLIT_SQUAT",
      listOf("Cuádriceps y glúteo"), listOf("Isquios", "Core"),
      listOf("Dar el paso hacia atrás para menor impacto en el tendón rotuliano", "Rodilla delantera se mantiene estable sobre el tobillo", "Empuje firme hacia adelante para volver a la posición")),
    guide("extension_cuadriceps_unilateral", "Extensiones de Cuádriceps a Una Pierna", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina de Extensiones", "Piernas", "LEG_EXTENSION",
      listOf("Vasto medial y lateral"), listOf("Tendón rotuliano"),
      listOf("Elimina desbalances bilaterales", "Bloqueo consciente en la cima de 2 segundos", "Tempo de bajada controlado")),
    guide("sentadilla_belt_squat", "Sentadilla con Cinturón (Belt Squat)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Belt Squat", "Piernas", "SQUAT",
      listOf("Cuádriceps completo"), listOf("Glúteo"),
      listOf("Carga suspendida directamente de la cadera liberando la columna", "Permite un volumen monstruoso de piernas sin fatiga axial", "Descenso ultra profundo")),
    guide("sentadilla_anderson_pines", "Sentadilla Anderson desde Pines", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Rack de Potencia", "Piernas", "SQUAT",
      listOf("Cuádriceps y fuerza concéntrica pura"), listOf("Glúteo", "Erectores"),
      listOf("La barra descansa sobre los pines de seguridad en el punto bajo", "Iniciar el levantamiento desde cero sin aprovechar la energía elástica", "Desarrollo masivo de potencia motriz")),
    guide("adductor_en_maquina_sentado", "Aductores en Máquina Sentado", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Selectorizada", "Piernas", "SQUAT",
      listOf("Aductor mayor, mediano y grácil"), listOf("Suelo pélvico"),
      listOf("Aumenta el grosor y volumen del muslo interno", "Cierre completo con pausa de 1 segundo", "Apertura controlada sintiendo tensión")),
    guide("sentadilla_sumo_barra", "Sentadilla Sumo con Barra", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica", "Piernas", "SQUAT",
      listOf("Aductores y vasto interno"), listOf("Glúteo mayor", "Isquios"),
      listOf("Pies muy separados con puntas abiertas a 45°", "Torso erguido bajando entre las piernas", "Empuje vertical abriendo caderas"))
  )

  // =========================================================================
  // 2. GLÚTEOS (24 Ejercicios Especializados de Hipertrofia y Estética)
  // =========================================================================
  val glutesExercises = listOf(
    guide("hip_thrust_barra_olimpica", "Hip Thrust con Barra Olímpica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra Olímpica y Banco Acolchado", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor (contracción máxima en acortamiento)"), listOf("Isquiotibiales", "Cuádriceps", "Erectores espinales"),
      listOf("Espalda alta apoyada justo por debajo del borde de los omóplatos", "Al elevar la cadera las rodillas deben formar un ángulo perfecto de 90°", "Bloqueo horizontal completo de cadera aguantando 1.5s arriba")),
    guide("patada_gluteo_polea_baja", "Patada de Glúteo en Polea Baja", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Polea Baja con Tobillera", "Glúteos", "GLUTE_KICKBACK",
      listOf("Glúteo mayor y glúteo medio"), listOf("Isquios"),
      listOf("Tobillera conectada a la polea baja; inclinarse levemente hacia adelante", "Extender la pierna hacia atrás y ligeramente hacia afuera en diagonal a 30°", "Apretar el glúteo en el punto de máxima contracción sin arquear la zona lumbar")),
    guide("hip_thrust_unilateral_mancuerna", "Hip Thrust Unilateral con Mancuerna", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Banco", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor unilateral"), listOf("Core y estabilizadores de pelvis"),
      listOf("Una pierna flexionada en el suelo y la otra elevada a 90°", "Mancuerna apoyada sobre la cresta ilíaca del lado de empuje", "Elimina asimetrías de activación glútea")),
    guide("abduccion_cadera_maquina_inclinado", "Abducción de Cadera en Máquina con Torso Inclinado", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Abductora", "Glúteos", "HIP_THRUST",
      listOf("Glúteo medio y glúteo menor"), listOf("Tensor de la fascia lata"),
      listOf("Inclinar el torso 45° hacia adelante despegando la espalda del respaldo", "Apertura amplia explosiva; sostener 2 segundos la apertura máxima", "Retorno lento en 3 segundos manteniendo la tensión")),
    guide("sentadilla_bulgara_enfoque_gluteo", "Sentadilla Búlgara con Inclinación para Glúteo", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas y Banco", "Glúteos", "BULGARIAN_SPLIT_SQUAT",
      listOf("Glúteo mayor en estiramiento máximo"), listOf("Isquiotibiales", "Cuádriceps"),
      listOf("Paso delantero más amplio que en la versión de cuádriceps", "Inclinar el torso hacia adelante a 45° sobre el muslo delantero", "Llevar la cadera hacia atrás para cargar todo el peso en el glúteo")),
    guide("step_ups_altos_cajon_gluteos", "Step-Ups Altos en Cajón para Glúteos", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Cajón Alto (50-60 cm) y Mancuernas", "Glúteos", "BULGARIAN_SPLIT_SQUAT",
      listOf("Glúteo mayor y glúteo medio"), listOf("Isquiotibiales"),
      listOf("Altura del cajón que cree un ángulo menor a 90° en la cadera", "Subir empujando únicamente con el talón de la pierna apoyada", "Cero impulso con el pie que está en el suelo")),
    guide("puente_gluteo_suelo_barra", "Puente de Glúteo en Suelo con Barra", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra con Almohadilla", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor"), listOf("Isquios", "Core"),
      listOf("Tumbado boca arriba en la colchoneta con rodillas a 90°", "Barra acolchada sobre las caderas", "Elevación de pelvis explosiva contrayendo los glúteos intensamente")),
    guide("frog_pumps_mancuerna_banda", "Frog Pumps con Mancuerna y Banda Elástica", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Banda Circular", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor y glúteo medio (bombeo metabólico)"), listOf("Aductores"),
      listOf("Plantas de los pies juntas estilo mariposa en el suelo", "Banda elástica por encima de las rodillas empujando hacia afuera", "Mancuerna pesada en la pelvis; repeticiones fluidas de alto volumen")),
    guide("cable_pull_through_gluteos", "Cable Pull-Through con Cuerda en Polea Baja", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Polea Baja y Cuerda", "Glúteos", "DEADLIFT",
      listOf("Glúteo mayor"), listOf("Isquiotibiales", "Erectores espinales"),
      listOf("De espaldas a la polea pasando la cuerda entre las piernas", "Bisagra de cadera hacia atrás estirando los glúteos", "Extensión de cadera explosiva al frente apretando glúteos en la cima")),
    guide("peso_muerto_sumo_gluteo", "Peso Muerto Estilo Sumo con Mancuerna Pesada", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna o Barra", "Glúteos", "DEADLIFT",
      listOf("Glúteo mayor e isquiotibiales"), listOf("Aductores", "Core"),
      listOf("Pies muy abiertos con rotación externa de caderas", "Torso vertical; agarrar la carga y extender la cadera apretando glúteos", "Tensión constante en la cadena posterior")),
    guide("hip_thrust_multipower", "Hip Thrust en Máquina Multipower (Smith)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Multipower", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor (sobrecarga guiada)"), listOf("Isquios"),
      listOf("La trayectoria fija de la Smith permite máxima concentración sin balanceos", "Pies fijos a 90°; empuje vertical absoluto", "Permite aplicar pausas isométricas de 3s al fallo")),
    guide("extension_cadera_banco_45", "Extensión de Cadera en Banco a 45° con Rotación Externa", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Banco Lumbar 45° y Disco", "Glúteos", "DEADLIFT",
      listOf("Glúteo mayor (porción superior)"), listOf("Isquios"),
      listOf("Puntas de los pies abiertas a 45° (rotación externa)", "Redondear ligeramente la espalda alta para desactivar los lumbares", "Subir únicamente usando la extensión de los glúteos")),
    guide("curtsy_lunges_gluteo_medio", "Curtsy Lunges (Zancadas en Reverencia Cruzadas)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas", "Glúteos", "BULGARIAN_SPLIT_SQUAT",
      listOf("Glúteo medio y glúteo menor"), listOf("Cuádriceps"),
      listOf("Cruzar la pierna trasera por detrás de la delantera en diagonal", "Descenso suave sintiendo el estiramiento en la parte lateral de la cadera", "Empuje con el talón delantero para volver")),
    guide("patada_burro_multipower", "Patada de Burro en Multipower (Smith Donkey Kick)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Multipower", "Glúteos", "GLUTE_KICKBACK",
      listOf("Glúteo mayor en rango acortado"), listOf("Isquios"),
      listOf("En cuatro apoyos bajo la barra de la Smith; apoyar la planta del pie en la barra", "Empujar la barra verticalmente hacia el techo con la suela del zapato", "Pausa de 1 segundo arriba apretando con fuerza")),
    guide("abduccion_polea_baja_de_pie", "Abducción de Cadera de Pie en Polea Baja", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Polea Baja y Tobillera", "Glúteos", "GLUTE_KICKBACK",
      listOf("Glúteo medio (forma de manzana y estética pélvica)"), listOf("Tensor de la fascia lata"),
      listOf("De costado a la polea con la tobillera en la pierna externa", "Elevar la pierna lateralmente hacia arriba y ligeramente atrás", "Evitar inclinar el torso; movimiento articular puro de cadera")),
    guide("sentadilla_sumo_deficit_mancuerna", "Sentadilla Sumo con Déficit sobre Steps", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Steps y Mancuerna Pesada", "Glúteos", "SQUAT",
      listOf("Glúteo mayor en estiramiento ultra profundo"), listOf("Aductores"),
      listOf("Pies apoyados sobre dos steps para mayor profundidad", "Bajar la mancuerna por debajo del nivel de los pies", "Gran activación por estiramiento miofibrilar")),
    guide("puente_gluteo_elevado_banco", "Puente de Glúteo con Pies Elevados en Banco", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Banco y Colchoneta", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor e isquiotibiales"), listOf("Core"),
      listOf("Espalda en el suelo y talones apoyados en el borde del banco", "Mayor rango de extensión que en suelo plano", "Bloqueo pélvico arriba con máxima compresión")),
    guide("clamshells_banda_pesada", "Clamshells (La Almeja) con Banda Elástica Pesada", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Banda Circular Gruesa", "Glúteos", "HIP_THRUST",
      listOf("Glúteo medio y rotadores externos"), listOf("Glúteo menor"),
      listOf("Tumbado de lado con rodillas flexionadas a 90° y talones juntos", "Abrir la rodilla superior separándola como una almeja", "Excelente ejercicio correctivo para estabilidad femoropatelar")),
    guide("patada_gluteo_maquina_kickback", "Patada en Máquina Selectorizada de Glúteos", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina de Glúteos", "Glúteos", "GLUTE_KICKBACK",
      listOf("Glúteo mayor"), listOf("Isquios"),
      listOf("Apoyar el pecho en la almohadilla y el pie en la plataforma móvil", "Empujar hacia atrás con el talón hasta la extensión completa", "Retorno lento de 3 segundos")),
    guide("hip_thrust_banda_isometrica", "Hip Thrust con Banda en Rodillas y Pausa Isométrica 3s", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Barra y Banda de Resistencia", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor y glúteo medio combinados"), listOf("Isquios"),
      listOf("Banda tensa en rodillas empujando hacia afuera durante todo el set", "Sostener 3 segundos en horizontal en cada una de las repeticiones", "Activación mioeléctrica máxima")),
    guide("peso_muerto_b-stance_gluteo", "Peso Muerto Rumano B-Stance (Pies Asimétricos)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas", "Glúteos", "DEADLIFT",
      listOf("Glúteo mayor de la pierna de apoyo"), listOf("Isquiotibiales"),
      listOf("Pierna trasera apoyada solo en la punta como apoyo secundario (15%)", "La pierna delantera recibe el 85% de la carga de la bisagra de cadera", "Aisla el glúteo sin los problemas de equilibrio del apoyo a una pierna")),
    guide("step_down_lateral_gluteo", "Step-Downs Laterales Controlados en Cajón", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Cajón o Step Alto", "Glúteos", "BULGARIAN_SPLIT_SQUAT",
      listOf("Glúteo medio excéntrico"), listOf("Cuádriceps"),
      listOf("De pie al borde del escalón con una pierna en el aire", "Descender la cadera flexionando la pierna de apoyo hasta tocar el suelo con el talón", "No impulsarse al subir")),
    guide("sentadilla_isquios_gluteo_pausa", "Sentadilla Profunda con Pausa en Sentadilla y Banda", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Banda", "Glúteos", "SQUAT",
      listOf("Glúteo mayor"), listOf("Aductores", "Cuádriceps"),
      listOf("Pausa de 2 segundos en el fondo antes de comenzar a subir", "Elimina el rebote osteoarticular forzando al glúteo a generar potencia concéntrica pura", "Mantener rodillas hacia afuera")),
    guide("puente_mariposa_disco", "Puente Mariposa con Disco sobre la Pelvis", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Disco Olímpico", "Glúteos", "HIP_THRUST",
      listOf("Glúteo mayor y medio"), listOf("Aductores"),
      listOf("Pies juntos plantas con plantas, elevando la cadera con disco en abdomen", "Estimula fibras glúteas desde una angulación de rotación externa", "Excelente finalizador de congestión"))
  )

  // =========================================================================
  // 3. CINTURA Y OBLICUOS (24 Ejercicios para Estrechar Cintura y V-Taper)
  // =========================================================================
  val waistExercises = listOf(
    guide("vacio_abdominal_clasico", "Vacío Abdominal Clásico en Ayunas (Stomach Vacuum)", AestheticMuscleCategory.CORE_WAIST, "Peso Corporal / Espejo", "Cintura", "VACUUM",
      listOf("Músculo transverso del abdomen (la faja natural del cuerpo)"), listOf("Diafragma", "Suelo pélvico"),
      listOf("De pie con manos en las caderas; vaciar todo el aire de los pulmones por completo", "Succionar el ombligo hacia adentro y arriba como si tocara la columna vertebral", "Sostener la contracción isométrica durante 20 a 30 segundos sin respirar hondo")),
    guide("vacio_abdominal_cuadrupedia", "Vacío Abdominal en Cuadrupedia (4 Apoyos)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "VACUUM",
      listOf("Transverso del abdomen contra la gravedad"), listOf("Multífidos lumbares"),
      listOf("Colocarse a cuatro patas con manos bajo hombros y rodillas bajo caderas", "Exhalar todo el aire y absorber el vientre hacia arriba venciendo la fuerza de gravedad", "Mantiene la cintura estrecha y el abdomen plano permanentemente")),
    guide("plancha_lateral_torsion", "Plancha Lateral con Torsión Escapular", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "PLANK_SIDE",
      listOf("Oblicuo interno y externo"), listOf("Transverso del abdomen", "Deltoides"),
      listOf("En plancha lateral sobre antebrazo, pasar el brazo superior por debajo del torso", "Rotar controlando la cadera sin que caiga hacia el suelo", "Vuelve a abrir el pecho hacia el techo")),
    guide("press_pallof_polea", "Press Pallof Anti-Rotacional en Polea", AestheticMuscleCategory.CORE_WAIST, "Polea Media o Banda de Resistencia", "Cintura", "PALLOF_PRESS",
      listOf("Core anti-rotacional (oblicuos y transverso)"), listOf("Deltoides", "Pectoral"),
      listOf("De costado a la polea sujetando el agarre con ambas manos a la altura del esternón", "Extender los brazos rectos al frente resistiendo el jalón rotacional de la polea", "Pausa de 2 segundos con brazos extendidos; la cintura debe permanecer inmóvil")),
    guide("giros_rusos_controlados_disco", "Giros Rusos Controlados con Disco (Russian Twists)", AestheticMuscleCategory.CORE_WAIST, "Disco de 5-10 kg", "Cintura", "RUSSIAN_TWIST",
      listOf("Oblicuos y pared abdominal lateral"), listOf("Recto abdominal"),
      listOf("Sentado con piernas elevadas en el aire a 45° formando una V con el torso", "Rotar el torso de izquierda a derecha tocando el suelo suavemente con el disco", "Movimiento lento y deliberado evitando movimientos balísticos descontrolados")),
    guide("elevacion_cadera_plancha_lateral", "Elevaciones de Cadera en Plancha Lateral (Hip Dips)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos inferiores y cuadrado lumbar"), listOf("Transverso"),
      listOf("En plancha lateral, descender la cadera hasta rozar el suelo con el muslo", "Elevar la cadera lo más alto posible por encima de la línea neutra", "Contracción concéntrica pura en la cintura")),
    guide("vacio_abdominal_silla_romana", "Vacío Abdominal Suspendido en Silla Romana", AestheticMuscleCategory.CORE_WAIST, "Silla Romana / Torre de Fondos", "Cintura", "VACUUM",
      listOf("Transverso del abdomen profundo"), listOf("Flexores de cadera"),
      listOf("Apoyar antebrazos en la torre de fondos con piernas colgando relajadas", "Exhalar todo el oxígeno y meter el estómago profundamente durante 15-20 segundos", "Fortalece la pared visceral para reducir el perímetro de cintura")),
    guide("crunch_oblicuo_polea_arrodillado", "Crunch Oblicuo en Polea Arrodillado", AestheticMuscleCategory.CORE_WAIST, "Polea Alta y Cuerda", "Cintura", "CRUNCH",
      listOf("Oblicuo externo e interno"), listOf("Serratos", "Recto abdominal"),
      listOf("Arrodillado de costado a la polea alta con la cuerda pegada a las orejas", "Flexionar la columna diagonalmente llevando el codo hacia la cadera opuesta", "Exhalar con fuerza al flexionar")),
    guide("plancha_lateral_isometrica_45s", "Plancha Lateral Isométrica Estricta", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos y transverso abdominal"), listOf("Glúteo medio", "Hombros"),
      listOf("Cuerpo en una línea recta perfecta desde tobillos hasta cabeza", "Brazo superior elevado vertical hacia el techo", "Mantener la pelvis levantada activando la pared lateral del abdomen")),
    guide("giros_cintura_pica_madera", "Giros de Cintura con Pica de Madera", AestheticMuscleCategory.CORE_WAIST, "Pica Ligera de Madera", "Cintura", "RUSSIAN_TWIST",
      listOf("Movilidad torácica y tono de oblicuos"), listOf("Transverso"),
      listOf("Sentado a caballo en un banco para bloquear las caderas por completo", "Rotar el torso controladamente lado a lado apretando el abdomen", "Cero peso adicional para evitar hipertrofia excesiva de los laterales")),
    guide("vacio_abdominal_exhalacion_forzada", "Vacío Abdominal con Exhalación Forzada Diafragmática", AestheticMuscleCategory.CORE_WAIST, "Colchoneta o Silla", "Cintura", "VACUUM",
      listOf("Transverso del abdomen y diafragma"), listOf("Intercostales"),
      listOf("Inhalar profundo en 4 segundos, exhalar en 8 segundos hasta vaciar la última gota", "Bloquear glotis y succionar pared abdominal manteniendo costillas abiertas", "Estiliza la silueta V-Taper")),
    guide("woodchoppers_polea_alta_baja", "Woodchoppers (Leñador) en Polea Alta a Baja", AestheticMuscleCategory.CORE_WAIST, "Polea Alta con Agarre Simple", "Cintura", "RUSSIAN_TWIST",
      listOf("Oblicuos y cadena cruzada anterior"), listOf("Deltoides", "Dorsales"),
      listOf("Diagonal descendente desde hombro opuesto hacia la cadera baja", "Girar el torso con control manteniendo brazos casi extendidos", "Excelente para esculpir la línea lateral del torso")),
    guide("woodchoppers_polea_baja_alta", "Woodchoppers Inversos en Polea Baja a Alta", AestheticMuscleCategory.CORE_WAIST, "Polea Baja", "Cintura", "RUSSIAN_TWIST",
      listOf("Oblicuos y serratos"), listOf("Core"),
      listOf("Diagonal ascendente desde la rodilla hacia el hombro opuesto", "Impulsar con la rotación del tronco manteniendo el core ultra rígido", "Retorno lento sintiendo el frenado excéntrico")),
    guide("paseo_granjero_unilateral_suitcase", "Paseo del Granjero Unilateral (Suitcase Carry)", AestheticMuscleCategory.CORE_WAIST, "Mancuerna o Kettlebell Pesada", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos contralaterales y cuadrado lumbar"), listOf("Antebrazos", "Trapecios"),
      listOf("Sujetar una mancuerna pesada con una sola mano; caminar 30 metros", "El torso debe permanecer milimétricamente vertical sin inclinarse hacia el peso", "Fuerza extrema anti-flexión lateral que compacta la cintura")),
    guide("plancha_spiderman_lenta", "Plancha Spiderman con Pausa Isométrica", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos y transverso"), listOf("Recto abdominal", "Hombros"),
      listOf("En posición de plancha de antebrazos, llevar la rodilla por fuera hasta tocar el codo", "Pausa de 1 segundo en el punto de máxima flexión lateral", "Alternar de forma pausada")),
    guide("vacio_abdominal_tumbado_supino", "Vacío Abdominal Tumbado Supino con Rodillas Flexionadas", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "VACUUM",
      listOf("Transverso del abdomen (nivel introductorio a avanzado)"), listOf("Suelo pélvico"),
      listOf("Tumbado boca arriba con pies en el suelo; lumbar apoyada plana", "Exhalar todo el aire y absorber el ombligo como si quisiera pegarse al piso", "Mantener de 20 a 30 segundos")),
    guide("press_pallof_arrodillado", "Press Pallof en Posición de Caballero (Half-Kneeling)", AestheticMuscleCategory.CORE_WAIST, "Polea o Banda", "Cintura", "PALLOF_PRESS",
      listOf("Core estabilizador y oblicuos"), listOf("Glúteo"),
      listOf("Una rodilla en el suelo y la otra al frente en 90°", "Empujar las manos al frente y regresar lentamente", "Mayor aislamiento al anular la compensación de piernas")),
    guide("crunch_bicicleta_lento_oblicuos", "Crunch Bicicleta con Pausa de 2 Segundos", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "RUSSIAN_TWIST",
      listOf("Oblicuos y recto abdominal"), listOf("Flexores de cadera"),
      listOf("Cruzar el codo hacia la rodilla opuesta manteniendo la otra pierna estirada", "Pausa de 2 segundos en el toque de codo-rodilla antes de cambiar", "Sin tirones del cuello")),
    guide("rotaciones_torso_polea_horizontal", "Rotaciones Horizontales de Torso en Polea", AestheticMuscleCategory.CORE_WAIST, "Polea Media", "Cintura", "RUSSIAN_TWIST",
      listOf("Oblicuo externo e interno"), listOf("Core"),
      listOf("Girar el torso 90° hacia el lado opuesto manteniendo brazos firmes", "Controlar la vuelta en 3 segundos sin dejarse jalar por el cable", "Tono firme y compacto")),
    guide("flexion_lateral_mancuerna_unilateral", "Flexión Lateral Unilateral con Mancuerna Ligera", AestheticMuscleCategory.CORE_WAIST, "Mancuerna Liviana (4-8 kg)", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos y cuadrado lumbar"), listOf("Transverso"),
      listOf("Mancuerna solo en una mano (nunca usar dos a la vez)", "Flexionar lateralmente el torso y volver a la vertical exacta", "Cargas ligeras con altas repeticiones para definir sin ensanchar")),
    guide("plancha_lateral_estrella", "Plancha Lateral en Estrella (Star Plank)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "PLANK_SIDE",
      listOf("Oblicuos y glúteo medio"), listOf("Hombros"),
      listOf("En plancha lateral, elevar el brazo y la pierna superior abriéndose como estrella", "Sostener 20-30 segundos por lado", "Control neuromuscular superior")),
    guide("bird_dog_isometrico", "Bird-Dog Cruzado con Contracción Abdominal", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "VACUUM",
      listOf("Transverso del abdomen y multífidos"), listOf("Glúteo", "Deltoides"),
      listOf("En cuatro apoyos, extender brazo derecho y pierna izquierda en línea recta", "Meter el ombligo manteniendo la espalda plana como una mesa", "Sostener 5 segundos por repetición")),
    guide("deadbug_transverso", "Deadbug (Bicho Muerto) con Presión Lumbar", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Cintura", "VACUUM",
      listOf("Transverso del abdomen y core profundo"), listOf("Recto abdominal"),
      listOf("Tumbado boca arriba, extender brazo y pierna opuesta sin despegar la zona lumbar", "La espalda baja debe aplastar la colchoneta durante todo el set", "Respiración diafragmática controlada")),
    guide("vacio_abdominal_inclinado_pared", "Vacío Abdominal Inclinado Apoyado en Pared", AestheticMuscleCategory.CORE_WAIST, "Pared o Barra", "Cintura", "VACUUM",
      listOf("Transverso del abdomen"), listOf("Diafragma"),
      listOf("Manos en la pared a la altura de los hombros con el torso a 45°", "Exhalar todo el aire y contraer el transverso al máximo", "Excelente postura para sentir la activación"))
  )

  // =========================================================================
  // 4. ABDOMEN (24 Ejercicios de Hipertrofia y Definición de Six-Pack)
  // =========================================================================
  val absExercises = listOf(
    guide("crunch_polea_alta_cuerda", "Crunch en Polea Alta con Cuerda (Cable Crunch)", AestheticMuscleCategory.CORE_WAIST, "Polea Alta y Cuerda", "Abdomen", "CRUNCH",
      listOf("Recto abdominal completo (énfasis en porción superior)"), listOf("Oblicuos"),
      listOf("Arrodillado frente a la polea con las manos de la cuerda junto a la frente", "Flexionar la columna vertebral llevando la cabeza hacia las rodillas", "La cadera debe permanecer inmóvil; doblar el torso como un acordeón")),
    guide("elevacion_piernas_colgado_barra", "Elevaciones de Piernas Colgado en Barra", AestheticMuscleCategory.CORE_WAIST, "Barra de Dominadas", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior y flexores profundos"), listOf("Antebrazos", "Dorsales"),
      listOf("Colgado con agarre prono a la anchura de hombros", "Elevar las piernas rectas hasta superar los 90° realizando retroversión pélvica", "Bajar en 3 segundos sin balanceos ni inercias")),
    guide("rueda_abdominal_completa", "Rueda Abdominal Completa desde Rodillas (Ab Wheel)", AestheticMuscleCategory.CORE_WAIST, "Rueda Abdominal (Ab Wheel)", "Abdomen", "AB_WHEEL",
      listOf("Recto abdominal completo en tensión excéntrica masiva"), listOf("Dorsales", "Tríceps", "Transverso"),
      listOf("Rodar hacia adelante extendiendo el cuerpo en línea recta sin que caiga la cadera", "Mantener la espalda ligeramente curvada en hollow body sin arquear lumbares", "Regresar contrayendo el abdomen con fuerza")),
    guide("dragon_flags_bruce_lee", "Dragon Flags de Bruce Lee en Banco Plano", AestheticMuscleCategory.CORE_WAIST, "Banco Plano", "Abdomen", "CRUNCH",
      listOf("Recto abdominal completo y core total"), listOf("Dorsales", "Glúteos"),
      listOf("Sujetarse firmemente al borde superior del banco detrás de la cabeza", "Elevar todo el cuerpo en una sola línea recta apoyado únicamente sobre las escápulas", "Descenso ultra lento resistiendo la gravedad")),
    guide("crunch_declinado_disco", "Crunch en Banco Declinado con Disco en Pecho", AestheticMuscleCategory.CORE_WAIST, "Banco Declinado y Disco", "Abdomen", "CRUNCH",
      listOf("Recto abdominal superior"), listOf("Oblicuos"),
      listOf("Pies asegurados en los rodillos del banco declinado a 30-45°", "Flexionar enrollando la columna hacia adelante despegando solo escápulas", "No jalar del cuello ni subir con flexores de cadera")),
    guide("tijeras_abdominales_suelo", "Tijeras Abdominales en Suelo (Flutter Kicks)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior"), listOf("Psoas mayor"),
      listOf("Tumbado boca arriba con manos bajo los glúteos para proteger lumbares", "Piernas estiradas a 15 cm del suelo realizando patadas alternadas", "Mantener el abdomen en tensión isométrica continua")),
    guide("hollow_body_hold_gimnastico", "Hollow Body Hold Gimnástico (Postura Hueca)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Pared abdominal completa y transverso"), listOf("Flexores de cadera", "Cuádriceps"),
      listOf("Brazos extendidos hacia atrás y piernas juntas flotando a 20 cm del suelo", "Solo la zona lumbar y pelvis apoyadas en el suelo formando una cuchara", "Resistir 30 a 45 segundos respirando corto")),
    guide("elevacion_rodillas_silla_romana", "Elevación de Rodillas al Pecho en Silla Romana", AestheticMuscleCategory.CORE_WAIST, "Silla Romana", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior"), listOf("Flexores de cadera"),
      listOf("Antebrazos apoyados en almohadillas y espalda en respaldo", "Llevar las rodillas hacia el pecho realizando retroversión de pelvis al final", "Descender lento sin balancearse")),
    guide("toques_talon_colchoneta", "Toques al Talón Alternados en Colchoneta", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Oblicuos y recto superior"), listOf("Core"),
      listOf("Tumbado boca arriba con rodillas flexionadas y escápulas despegadas del suelo", "Alcanzar con la mano el talón del mismo lado alternando derecha e izquierda", "Mantener la contracción isométrica durante toda la serie")),
    guide("crunch_invertido_banco_plano", "Crunch Invertido en Banco Plano", AestheticMuscleCategory.CORE_WAIST, "Banco Plano", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior"), listOf("Oblicuos"),
      listOf("Sujetarse del borde superior del banco con las rodillas dobladas a 90°", "Enrollar la pelvis hacia la cabeza despegando el sacro del banco", "Descenso de 3 segundos sin dejar caer las piernas")),
    guide("bicicleta_abdominal_cruzada", "Bicicleta Abdominal Cruzada Estricta", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal y oblicuos cruzados"), listOf("Flexores"),
      listOf("Manos detrás de la cabeza sin tirar de la nuca", "Tocar rodilla con codo opuesto rotando el tronco mientras la otra pierna se extiende", "Cadencia fluida pero controlada")),
    guide("escaladores_lentos_isometricos", "Escaladores Lentos con Pausa Isométrica (Mountain Climbers)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior y core completo"), listOf("Deltoides", "Cuádriceps"),
      listOf("En posición de plancha alta de flexiones, llevar rodilla al pecho despacio", "Sostener 2 segundos la rodilla contra el pecho apretando el abdomen", "Cero rebotes rápidos; máxima tensión muscular")),
    guide("crunch_pelota_fitball", "Crunch Abdominal sobre Pelota Suiza (Fitball)", AestheticMuscleCategory.CORE_WAIST, "Fitball (Pelota de Pilates)", "Abdomen", "CRUNCH",
      listOf("Recto abdominal en rango extendido"), listOf("Oblicuos"),
      listOf("Zona lumbar apoyada sobre la curva del fitball permitiendo hiperextensión segura", "Iniciar desde el estiramiento profundo y flexionar el abdomen hacia arriba", "Rango de movimiento biomecánicamente superior al suelo")),
    guide("toes_to_bar_estrictos", "Pies a la Barra Estrictos (Toes to Bar)", AestheticMuscleCategory.CORE_WAIST, "Barra de Dominadas", "Abdomen", "CRUNCH",
      listOf("Recto abdominal completo y core de tracción"), listOf("Dorsal", "Antebrazo"),
      listOf("Colgado en barra, llevar las puntas de los pies a tocar la barra metálica", "Cero kip o balanceo de CrossFit; subida y bajada puramente musculares", "Extrema exigencia física")),
    guide("l_sit_paralelas", "L-Sit Sostenido en Barras Paralelas", AestheticMuscleCategory.CORE_WAIST, "Barras Paralelas o Push-Up Bars", "Abdomen", "CRUNCH",
      listOf("Recto abdominal y flexores de cadera"), listOf("Tríceps", "Dorsales"),
      listOf("Sostener el cuerpo con brazos extendidos y elevar las piernas a 90° horizontales", "Mantener las rodillas bloqueadas y las puntas apuntando al frente", "Sostener series de 15 a 25 segundos")),
    guide("crunch_maquina_selectorizada", "Crunch en Máquina Selectorizada de Placas", AestheticMuscleCategory.CORE_WAIST, "Máquina Abdominal", "Abdomen", "CRUNCH",
      listOf("Recto abdominal superior"), listOf("Oblicuos"),
      listOf("Ajustar asiento para que el eje coincida con el ombligo", "Permite aplicar sobrecarga progresiva exacta con pesos medidos", "Pausa de 1 segundo en máxima compresión")),
    guide("plancha_frontal_rkc", "Plancha Frontal Estilo RKC (Máxima Tensión)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Pared abdominal completa y glúteos"), listOf("Dorsales", "Pectoral"),
      listOf("En plancha de antebrazos, apretar puños, activar dorsales jalando codos a pies", "Apretar glúteos y cuádriceps al 100% como si recibieras un golpe", "Genera 3 veces más activación que una plancha convencional en solo 20 segundos")),
    guide("crunch_con_piernas_verticales", "Crunch con Piernas Elevadas a 90° Verticales", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal superior"), listOf("Flexores"),
      listOf("Piernas rectas hacia el techo perpendicular al suelo", "Elevar las manos intentando tocar la punta de las zapatillas", "Aisla el abdomen alto al bloquear el psoas")),
    guide("rollouts_barra_olimpica", "Rollouts con Barra Olímpica y Discos Pequeños", AestheticMuscleCategory.CORE_WAIST, "Barra Olímpica con Discos de 5kg", "Abdomen", "AB_WHEEL",
      listOf("Recto abdominal excéntrico"), listOf("Dorsales", "Core"),
      listOf("Mismo principio que la rueda abdominal pero con agarre más ancho", "Permite una estabilidad superior para atletas de torso amplio", "Control de descenso milimétrico")),
    guide("limpiaparabrisas_suelo", "Limpiaparabrisas en Suelo (Floor Wipers)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta y Barra Ligera", "Abdomen", "CRUNCH",
      listOf("Recto abdominal y oblicuos"), listOf("Hombros"),
      listOf("Tumbado sujetando una barra sobre el pecho con brazos rectos", "Elevar piernas y llevarlas juntas hacia la derecha y luego a la izquierda", "Rozar el suelo sin soltar la tensión")),
    guide("crunch_en_v_jackknife", "Crunch en V Simultáneo (V-Ups Jackknife)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal superior e inferior"), listOf("Psoas"),
      listOf("Elevar al mismo tiempo el torso y las piernas rectas encontrándose en la cima", "El cuerpo queda apoyado únicamente sobre los glúteos en forma de V", "Bajar controlando sin azotar")),
    guide("plancha_con_toque_de_hombros", "Plancha Alta con Toque de Hombros", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Core anti-rotacional y recto abdominal"), listOf("Hombros", "Pectorales"),
      listOf("En plancha sobre manos, tocar el hombro opuesto con una mano", "Evitar que la cadera se balancee de lado a lado; mantenerla paralela al suelo", "Excelente estabilidad")),
    guide("elevacion_cadera_vela", "Elevación de Cadera en Vela (Candle Raise)", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal inferior profundo"), listOf("Lumbares"),
      listOf("Elevar las piernas verticalmente y luego empujar los pies hacia el techo despegando la pelvis", "La fuerza proviene del abdomen bajo sin apoyarse en las manos", "Bajar vértebra por vértebra")),
    guide("crunch_isometrico_mariposa", "Crunch Isométrico con Brazos en Mariposa", AestheticMuscleCategory.CORE_WAIST, "Colchoneta", "Abdomen", "CRUNCH",
      listOf("Recto abdominal superior"), listOf("Intercostales"),
      listOf("Despegar escápulas del suelo con brazos abiertos en cruz flotando", "Sostener la contracción isométrica durante 5 segundos por repetición", "Gran congestión finalizadora"))
  )

  // =========================================================================
  // 5. PECHO (21 Ejercicios)
  // =========================================================================
  val chestExercises = listOf(
    guide("press_banca_plano", "Press de Banca Plano con Barra", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Barra Olímpica", "Pecho", "BENCH_PRESS",
      listOf("Pectoral mayor (haz esternal)", "Pectoral mayor (haz clavicular)"), listOf("Tríceps braquial", "Deltoides anterior"),
      listOf("Retracción escapular activa durante todo el rango", "Pies plantados firmes en el suelo (Leg drive)", "Codos a 45-75° respecto al torso")),
    guide("press_inclinado_mancuerna", "Press Inclinado con Mancuernas (30°)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuernas y Banco 30°", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral mayor (haz clavicular)"), listOf("Deltoides anterior", "Tríceps"),
      listOf("Inclinación óptima a 30° para aislar la porción superior", "Descenso profundo sintiendo apertura pectoral", "Convergencia natural arriba sin chocar mancuernas")),
    guide("press_declinado_barra", "Press Declinado con Barra", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Barra Olímpica y Banco Declinado", "Pecho", "BENCH_PRESS",
      listOf("Pectoral mayor (haz costal/inferior)"), listOf("Tríceps braquial", "Deltoides anterior"),
      listOf("Menor estrés glenohumeral que el press plano", "Fijar bien los tobillos en los rodillos", "Empuje vertical perpendicular al piso")),
    guide("fondos_paralelas_pecho", "Fondos en Paralelas para Pecho (Dips)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Barras Paralelas", "Pecho", "BENCH_PRESS",
      listOf("Pectoral inferior y externo"), listOf("Tríceps braquial", "Deltoides anterior"),
      listOf("Torso inclinado 30° hacia adelante", "Codos ligeramente abiertos al bajar", "Romper ángulo de 90° en codos con control")),
    guide("aperturas_mancuernas_plano", "Aperturas con Mancuernas en Banco Plano", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuernas", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral mayor en estiramiento"), listOf("Bíceps corto (estabilizador)", "Deltoides anterior"),
      listOf("Leve flexión fija en los codos (abrazar un barril)", "Priorizar estiramiento profundo controlado", "No rebotar en el punto bajo")),
    guide("cruces_polea_alta", "Cruces de Poleas de Arriba a Abajo", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Polea Doble Alta", "Pecho", "BENCH_PRESS",
      listOf("Pectoral inferior y surco esternal"), listOf("Deltoides anterior"),
      listOf("Paso al frente para estabilidad", "Juntar manos a la altura de la cadera", "Apretar el pectoral 1 segundo abajo")),
    guide("cruces_polea_baja", "Cruces de Poleas de Abajo a Arriba", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Polea Doble Baja", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral superior (clavicular)"), listOf("Deltoides anterior"),
      listOf("Tracción diagonal ascendente hacia el mentón", "Mantener tensión constante del cable", "No encoger los trapecios")),
    guide("press_maquina_chest_press", "Press en Máquina Sentado (Chest Press)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Máquina Selectorizada", "Pecho", "BENCH_PRESS",
      listOf("Pectoral mayor completo"), listOf("Tríceps braquial"),
      listOf("Ajustar asiento para que agarres coincidan con pezones", "Permite llevar la serie al fallo con seguridad", "Escápulas pegadas al respaldo")),
    guide("press_smith_inclinado", "Press en Máquina Smith Inclinado", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Máquina Multipower", "Pecho", "INCLINE_PRESS",
      listOf("Haz clavicular del pectoral"), listOf("Deltoides anterior", "Tríceps"),
      listOf("Banco a 30° centrado bajo la barra guiada", "Descenso hasta la parte alta del esternón", "Permite sobrecarga guiada estricta")),
    guide("aperturas_maquina_pec_deck", "Aperturas en Máquina Pec Deck (Contractor)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Máquina Pec Deck", "Pecho", "BENCH_PRESS",
      listOf("Pectoral esternal y medial"), listOf("Deltoides anterior"),
      listOf("Codos alineados a la altura media del pecho", "Pausa isométrica de 1s al juntar las almohadillas", "Fase excéntrica frenada")),
    guide("pullover_mancuerna_pecho", "Pullover con Mancuerna para Caja Torácica", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuerna y Banco Plano", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral mayor (cabeza esternal)", "Serrato anterior"), listOf("Dorsal ancho", "Tríceps (cabeza larga)"),
      listOf("Apoyar solo la parte alta de la espalda sobre el banco", "Bajar la mancuerna en arco por detrás de la cabeza", "Inspirar hondo al bajar para expandir la caja torácica")),
    guide("press_guillotina_gironda", "Press Guillotina (Estilo Vince Gironda)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Barra con Topes de Seguridad", "Pecho", "BENCH_PRESS",
      listOf("Pectoral superior y clavicular en máximo estiramiento"), listOf("Deltoides anterior"),
      listOf("Agarre más ancho que el press convencional", "Bajar la barra hacia la clavícula / garganta con codos abiertos", "Usar peso moderado con técnica estricta")),
    guide("press_suelo_floor_press", "Floor Press con Mancuernas (Press en el Suelo)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuernas y Suelo", "Pecho", "FLOOR_PRESS",
      listOf("Pectoral medio y tríceps"), listOf("Deltoides anterior"),
      listOf("Tumbado en el suelo, brazos bajan hasta que los tríceps tocan el piso", "Elimina el rebote y protege el manguito rotador", "Potencia concéntrica pura")),
    guide("aperturas_inclinadas_mancuernas", "Aperturas Inclinadas con Mancuernas (30°)", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuernas y Banco Inclinado", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral clavicular superior"), listOf("Deltoides anterior"),
      listOf("Descenso curvo sintiendo estiramiento en la zona superior", "Subir hasta la vertical sin chocar pesas")),
    guide("flexiones_suelo_lastradas", "Flexiones de Brazos (Push-Ups) Lastradas", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Disco de Peso en Espalda", "Pecho", "BENCH_PRESS",
      listOf("Pectoral mayor"), listOf("Tríceps", "Core anterior"),
      listOf("Cuerpo en plancha rígida perfecta", "Pecho roza el suelo en cada repetición", "Empuje completo con protracción escapular final")),
    guide("press_svend_disco", "Press Svend de Pie con Discos", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Dos Discos Pequeños (2.5 a 5kg)", "Pecho", "BENCH_PRESS",
      listOf("Línea medial y surco esternal del pectoral"), listOf("Deltoides anterior"),
      listOf("Comprimir dos discos entre las palmas de las manos frente al esternón", "Extender los brazos al frente apretando continuamente", "Contracción isométrica brutal")),
    guide("press_plano_mancuernas_neutro", "Press Plano con Mancuernas con Agarre Neutro", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Mancuernas", "Pecho", "BENCH_PRESS",
      listOf("Pectoral esternal y tríceps"), listOf("Deltoides"),
      listOf("Palmas enfrentadas durante todo el recorrido", "Menor estrés sobre el tendón bicipital y hombro", "Descenso profundo y empuje cerrado")),
    guide("cruces_polea_media_plano", "Cruces en Polea Media a la Altura del Pecho", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Polea Doble Media", "Pecho", "BENCH_PRESS",
      listOf("Pectoral mayor completo"), listOf("Deltoides anterior"),
      listOf("Poleas a la altura del hombro", "Abrazo horizontal cerrando al frente del esternón", "Pico de contracción sostenido")),
    guide("fondos_lastrados_cadena", "Fondos de Pecho Lastrados con Cadena/Cinturón", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Cinturón de Lastre y Paralelas", "Pecho", "BENCH_PRESS",
      listOf("Pectoral inferior y masa torácica global"), listOf("Tríceps braquial"),
      listOf("Para atletas intermedios/avanzados", "Bajar con tempo estricto 3-1-1-0", "Codos estables")),
    guide("press_inclinado_barra_olimpica", "Press Inclinado con Barra Olímpica", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Barra Olímpica y Banco Inclinado", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral mayor (haz clavicular)"), listOf("Deltoides anterior", "Tríceps"),
      listOf("Ángulo de 30 a 45 grados", "Bajar la barra rozando la parte alta del esternón", "Pies firmes en el suelo")),
    guide("flexiones_declinadas_pies_elevados", "Flexiones Declinadas con Pies en Banco", AestheticMuscleCategory.CHEST_UPPER_LOWER, "Banco Plano y Suelo", "Pecho", "INCLINE_PRESS",
      listOf("Pectoral superior (clavicular)"), listOf("Deltoides anterior", "Tríceps"),
      listOf("Pies apoyados en el banco y manos en el suelo", "Bajar hasta que la nariz casi toque el piso", "Empuje explosivo"))
  )

  // =========================================================================
  // 6. ESPALDA (22 Ejercicios)
  // =========================================================================
  val backExercises = listOf(
    guide("dominadas_pronadas_lastradas", "Dominadas Pronadas Estrictas (Pull-Ups)", AestheticMuscleCategory.V_TAPER_LATS, "Barra de Dominadas", "Espalda", "PULLUP",
      listOf("Dorsal ancho (amplitud V-Taper)", "Redondo mayor"), listOf("Bíceps braquial", "Braquiorradial", "Romboides"),
      listOf("Agarre prono ligeramente más ancho que los hombros", "Iniciar con depresión escapular antes de flexionar codos", "Llevar el pecho a la barra con barbilla superando el tubo")),
    guide("remo_barra_pendlay", "Remo Pendlay con Barra Olímpica", AestheticMuscleCategory.V_TAPER_LATS, "Barra Olímpica y Discos", "Espalda", "ROW",
      listOf("Dorsal ancho", "Romboides", "Trapecio medio"), listOf("Bíceps", "Erectores espinales", "Core"),
      listOf("Torso completamente paralelo al suelo (90°)", "La barra descansa en el suelo en cada repetición", "Tirón explosivo al esternón sin elevar el torso")),
    guide("jalon_pecho_agarre_ancho", "Jalón al Pecho en Polea (Lat Pulldown)", AestheticMuscleCategory.V_TAPER_LATS, "Polea Alta de Dorsal", "Espalda", "LAT_PULLDOWN",
      listOf("Dorsal ancho (fibras ilíacas y costales)"), listOf("Bíceps", "Braquial"),
      listOf("Pecho elevado mirando hacia la polea", "Tirar dirigiendo los codos hacia los bolsillos traseros", "Fase excéntrica de 3s dejando estirar el dorsal arriba")),
    guide("remo_mancuerna_unilateral", "Remo con Mancuerna a Una Mano (Kroc Row)", AestheticMuscleCategory.V_TAPER_LATS, "Mancuerna Pesada y Banco", "Espalda", "ROW",
      listOf("Dorsal ancho unilateral", "Redondo mayor"), listOf("Romboides", "Bíceps"),
      listOf("Apoyar rodilla y mano opuesta en el banco", "Estirar la mancuerna hacia adelante y abajo", "Remar hacia la cadera en arco manteniendo el codo cerrado")),
    guide("remo_barra_t_pecho_apoyado", "Remo en Barra T con Pecho Apoyado", AestheticMuscleCategory.V_TAPER_LATS, "Máquina Barra T Acolchada", "Espalda", "ROW",
      listOf("Grosor de espalda media y lats"), listOf("Trapecios", "Bíceps"),
      listOf("Elimina sobrecarga lumbar al estar apoyado el torso", "Apertura escapular completa abajo", "Retracción máxima sosteniendo 1s arriba")),
    guide("pullover_polea_alta_brazos_rectos", "Pullover en Polea Alta con Brazos Rectos", AestheticMuscleCategory.V_TAPER_LATS, "Polea Alta y Cuerda o Barra Recta", "Espalda", "LAT_PULLDOWN",
      listOf("Dorsal ancho en aislamiento puro"), listOf("Tríceps (cabeza larga)", "Redondo mayor"),
      listOf("Ligera inclinación del torso a 30° con codos fijos casi rectos", "Bajar la barra en arco hacia los muslos comprimiendo los dorsales", "Inhalar arriba sintiendo el estiramiento subaxilar")),
    guide("remo_gironda_polea_baja", "Remo Sentado en Polea Baja (Gironda)", AestheticMuscleCategory.V_TAPER_LATS, "Polea Baja y Agarre V", "Espalda", "ROW",
      listOf("Espalda media (romboides y dorsal inferior)"), listOf("Bíceps", "Erectores espinales"),
      listOf("Espalda recta perpendicular al suelo (sin mecerse)", "Tirar de la empuñadura hacia el abdomen bajo", "Apretar escápulas atrás 1 segundo")),
    guide("peso_muerto_convencional", "Peso Muerto Convencional con Barra Olímpica", AestheticMuscleCategory.V_TAPER_LATS, "Barra Olímpica", "Espalda", "DEADLIFT",
      listOf("Cadena posterior completa", "Erectores espinales", "Trapecios"), listOf("Dorsales", "Isquios", "Glúteos"),
      listOf("Barra rozando las tibias en el despegue", "Pecho erguido y lats activados (proteger columna)", "Extensión simultánea de rodillas y caderas")),
    guide("jalon_polea_agarre_neutro_cerrado", "Jalón al Pecho con Agarre Neutro Cerrado (V-Bar)", AestheticMuscleCategory.V_TAPER_LATS, "Polea Alta y Agarre V", "Espalda", "LAT_PULLDOWN",
      listOf("Dorsal ancho inferior"), listOf("Bíceps braquial", "Braquial anterior"),
      listOf("Permite un rango de tracción más profundo hacia el esternón", "Codos viajan pegados por delante del cuerpo", "Menor estrés articular en hombros")),
    guide("remo_seal_row_banco_alto", "Remo Seal Row (Torso Plano en Banco Alto)", AestheticMuscleCategory.V_TAPER_LATS, "Banco Elevado y Barra / Mancuernas", "Espalda", "ROW",
      listOf("Romboides, trapecio medio y dorsal"), listOf("Bíceps"),
      listOf("Tumbado prono sobre banco horizontal elevado", "Aislamiento biomecánico sin posibilidad de trampear con balanceo", "Tirón estricto hasta tocar la base del banco")),
    guide("dominadas_neutras", "Dominadas con Agarre Neutro (Palmas Enfrentadas)", AestheticMuscleCategory.V_TAPER_LATS, "Barra Multiuso", "Espalda", "PULLUP",
      listOf("Dorsal ancho y braquial"), listOf("Bíceps", "Trapecio"),
      listOf("Muy amigable con los hombros", "Rango completo hasta tocar el pecho con el manillar", "Bajada en 3 segundos")),
    guide("remo_invertido_australiano", "Remo Invertido (Australian Pull-Ups)", AestheticMuscleCategory.V_TAPER_LATS, "Barra Multipower Baja", "Espalda", "ROW",
      listOf("Espalda alta y deltoides posterior"), listOf("Core", "Bíceps"),
      listOf("Cuerpo recto como una tabla suspendido bajo la barra", "Tirar con los codos hasta tocar la barra con el pecho", "Gran constructor de densidad escapular")),
    guide("remo_meadows_unilateral", "Remo Meadows con Barra en Esquina (Landmine)", AestheticMuscleCategory.V_TAPER_LATS, "Landmine y Barra", "Espalda", "ROW",
      listOf("Dorsal ancho lateral y romboides"), listOf("Antebrazo", "Bíceps"),
      listOf("De pie de lado a la punta de la barra", "Agarre overhand en el extremo metálico", "Tirar hacia la cadera con codo abierto a 60°")),
    guide("face_pull_polea_alta", "Face Pull en Polea Alta con Cuerda", AestheticMuscleCategory.V_TAPER_LATS, "Polea Alta y Cuerda", "Espalda", "ROW",
      listOf("Deltoides posterior, manguito rotador y trapecio medio"), listOf("Romboides"),
      listOf("Polea a la altura de los ojos", "Tirar hacia la frente separando los extremos de la cuerda", "Rotación externa al final del movimiento")),
    guide("remo_mancuernas_banco_inclinado", "Remo con Mancuernas en Banco Inclinado (Chest Supported)", AestheticMuscleCategory.V_TAPER_LATS, "Banco a 45° y Mancuernas", "Espalda", "ROW",
      listOf("Dorsal y romboides"), listOf("Bíceps"),
      listOf("Pecho apoyado en el banco a 45°", "Tirar con los codos dirigidos hacia atrás", "Pausa arriba de 1 segundo")),
    guide("encogimientos_barra_trapecios", "Encogimientos de Hombros con Barra (Shrugs)", AestheticMuscleCategory.V_TAPER_LATS, "Barra Olímpica", "Espalda", "ROW",
      listOf("Trapecio superior"), listOf("Antebrazos"),
      listOf("Pies a la anchura de hombros con barra al frente", "Elevar los hombros verticalmente hacia las orejas", "No rotar los hombros; subida y bajada recta")),
    guide("jalon_unilateral_polea", "Jalón Unilateral en Polea Alta", AestheticMuscleCategory.V_TAPER_LATS, "Polea Alta", "Espalda", "LAT_PULLDOWN",
      listOf("Dorsal ancho fibras ilíacas"), listOf("Bíceps"),
      listOf("Arrodillado trabajando un lado a la vez", "Permite mayor flexión lateral de columna para acortar el dorsal al 100%", "Conexión mente-músculo inigualable")),
    guide("remo_kroc_pesado", "Remo Kroc Pesado a Altas Repeticiones", AestheticMuscleCategory.V_TAPER_LATS, "Mancuerna de Gran Calibre", "Espalda", "ROW",
      listOf("Dorsal y agarre antebrazo"), listOf("Espalda media"),
      listOf("Serie pesada de 15 a 20 repeticiones con mínimo impulso corporal", "Enorme densidad muscular")),
    guide("dominadas_supinadas_chin_ups", "Dominadas Supinadas (Chin-Ups)", AestheticMuscleCategory.V_TAPER_LATS, "Barra de Dominadas", "Espalda", "PULLUP",
      listOf("Dorsal ancho y bíceps braquial"), listOf("Braquial anterior"),
      listOf("Palmas mirando hacia la cara", "Mayor reclutamiento de la porción inferior del dorsal y flexores del codo", "Extensión completa en la bajada")),
    guide("hiperextensiones_banco_romano", "Hiperextensiones en Banco Romano a 45°", AestheticMuscleCategory.V_TAPER_LATS, "Banco de Hiperextensiones", "Espalda", "DEADLIFT",
      listOf("Erectores espinales y espalda baja"), listOf("Glúteos", "Isquios"),
      listOf("Ajustar el cojín justo por debajo del pliegue de la cadera", "Bajar flexionando la columna y subir hasta la alineación neutra", "No hiperextender en exceso arriba")),
    guide("remo_yates_supinado", "Remo Yates con Agarre Supinado (Estilo Dorian Yates)", AestheticMuscleCategory.V_TAPER_LATS, "Barra Olímpica", "Espalda", "ROW",
      listOf("Dorsal bajo y bíceps"), listOf("Trapecio medio"),
      listOf("Torso inclinado a 70° (más vertical)", "Tirar de la barra hacia la cintura rozando los muslos", "Permite manejar altas cargas con seguridad")),
    guide("rack_pulls_sobre_rodillas", "Rack Pulls desde Pines sobre las Rodillas", AestheticMuscleCategory.V_TAPER_LATS, "Barra y Rack de Potencia", "Espalda", "DEADLIFT",
      listOf("Trapecios y erectores espinales"), listOf("Dorsal ancho", "Glúteo"),
      listOf("Iniciar con la barra descansando sobre los pines a nivel de la rodilla", "Extender cadera y bloquear espalda con enorme sobrecarga", "Desarrollo de densidad brutal"))
  )

  // =========================================================================
  // 7. HOMBROS (22 Ejercicios)
  // =========================================================================
  val shoulderExercises = listOf(
    guide("press_militar_barra_pie", "Press Militar de Pie con Barra Olímpica (OHP)", AestheticMuscleCategory.V_TAPER_DELTS, "Barra Olímpica", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior y lateral"), listOf("Tríceps", "Trapecios", "Core"),
      listOf("Pies a la anchura de hombros con glúteos y abdomen apretados", "La barra sube vertical en una línea recta limpia", "Bloqueo arriba con la cabeza adelantándose levemente")),
    guide("elevaciones_laterales_mancuerna", "Elevaciones Laterales Estrictas con Mancuernas", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral (anchura V-Taper)"), listOf("Trapecio superior"),
      listOf("Ligera inclinación del torso hacia adelante (10°)", "Elevar las mancuernas en el plano escapular (30° adelantado)", "Codos lideran el movimiento hasta la altura de hombros")),
    guide("elevaciones_laterales_polea_baja", "Elevaciones Laterales en Polea Baja", AestheticMuscleCategory.V_TAPER_DELTS, "Polea Baja", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral en tensión continua"), listOf("Trapecio"),
      listOf("El cable pasa por detrás o entre las piernas", "Tensión mecánica máxima desde el inicio del recorrido", "Descenso frenado en 3 segundos")),
    guide("press_arnold_mancuernas", "Press Arnold Sentado con Mancuernas", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas y Banco 90°", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior y lateral completo"), listOf("Tríceps", "Serratos"),
      listOf("Iniciar con palmas mirando al pecho", "Rotar hacia afuera mientras se empuja hacia arriba", "Terminar con palmas al frente en la cima")),
    guide("pajaros_deltoides_posterior", "Pájaros con Mancuernas (Rear Delt Flyes)", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas y Banco", "Hombros", "REAR_DELT_FLYE",
      listOf("Deltoides posterior (hombro 3D redondo)"), listOf("Romboides", "Trapecio medio"),
      listOf("Torso inclinado a 90° apoyado en banco", "Elevar los brazos hacia los costados manteniendo codos semirrígidos", "Evitar encoger los trapecios")),
    guide("press_sentado_mancuernas_pesado", "Press de Hombros Sentado con Mancuernas", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas y Banco a 75-80°", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior y lateral"), listOf("Tríceps"),
      listOf("Inclinación a 75° para no estresar el manguito rotador", "Bajar hasta que las mancuernas toquen los hombros", "Empuje vertical convergente")),
    guide("face_pull_polea_alta_rotacion", "Face Pulls con Doble Cuerda y Rotación Externa", AestheticMuscleCategory.V_TAPER_DELTS, "Polea Alta y 2 Cuerdas", "Hombros", "REAR_DELT_FLYE",
      listOf("Deltoides posterior e infraespinoso"), listOf("Trapecios"),
      listOf("Dos cuerdas en el mosquetón para mayor recorrido", "Tirar hacia las sienes separando las manos al máximo", "Mantener los codos altos")),
    guide("elevaciones_frontales_disco", "Elevaciones Frontales con Disco Olímpico", AestheticMuscleCategory.V_TAPER_DELTS, "Disco Olímpico", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides anterior"), listOf("Serrato anterior"),
      listOf("Sujetar el disco a las 9 y 3 en punto del reloj", "Elevar hasta la altura de los ojos", "Bajar despacio sin balanceo lumbar")),
    guide("remo_al_menton_polea_baja", "Remo al Mentón en Polea con Barra Z (Upright Row)", AestheticMuscleCategory.V_TAPER_DELTS, "Polea Baja y Barra Z", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral y trapecio"), listOf("Bíceps"),
      listOf("Agarre a la anchura de hombros (nunca muy cerrado)", "Subir liderando con los codos hasta el pecho medio", "No forzar la articulación de la muñeca")),
    guide("reverse_pec_deck_maquina", "Pájaros en Máquina Pec Deck Inversa", AestheticMuscleCategory.V_TAPER_DELTS, "Máquina Pec Deck", "Hombros", "REAR_DELT_FLYE",
      listOf("Deltoides posterior en aislamiento estricto"), listOf("Romboides"),
      listOf("Pecho pegado al respaldo", "Brazos a la altura de los hombros", "Apertura amplia apretando la parte trasera del hombro")),
    guide("elevaciones_laterales_apoyado_banco", "Elevaciones Laterales Inclinado en Banco (Leaning Lateral)", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuerna y Banco Inclinado", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral en estiramiento"), listOf("Trapecio"),
      listOf("De lado recostado sobre banco a 45-60°", "Aísla la cabeza lateral eliminando cualquier inercia de piernas")),
    guide("press_landmine_unilateral_pie", "Press Landmine Unilateral de Pie", AestheticMuscleCategory.V_TAPER_DELTS, "Barra en Landmine", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior y serrato"), listOf("Tríceps", "Core"),
      listOf("Trayectoria angular diagonal muy natural para el hombro", "Empuje firme hacia adelante y arriba con apoyo unilateral")),
    guide("lu_raises_halterofilia", "Lu Raises (Elevaciones Laterales Completas Estilo Chino)", AestheticMuscleCategory.V_TAPER_DELTS, "Discos Pequeños (2.5 a 5kg)", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral, serratos y trapecios"), listOf("Manguito"),
      listOf("Subir las pesas lateralmente hasta juntarlas por encima de la cabeza", "Control estricto de la escápula en todo el arco de 180°")),
    guide("elevaciones_posteriores_polea_cruzada", "Pájaros en Polea Doble Cruzada sin Manerales", AestheticMuscleCategory.V_TAPER_DELTS, "Polea Doble Alta", "Hombros", "REAR_DELT_FLYE",
      listOf("Deltoides posterior"), listOf("Trapecio medio"),
      listOf("Agarrar los cables directamente cruzando los brazos frente a la cara", "Abrir hacia atrás en plano horizontal")),
    guide("press_militar_smith_trasnuca", "Press Trasnuca en Multipower (Técnica Controlada)", AestheticMuscleCategory.V_TAPER_DELTS, "Máquina Multipower", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides lateral y trapecio medio"), listOf("Tríceps"),
      listOf("Solo para personas con buena movilidad glenohumeral", "Bajar solo hasta la altura de las orejas", "Carga moderada")),
    guide("elevaciones_laterales_pesadas_cheat", "Elevaciones Laterales Pesadas con Impulso Controlado", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas Pesadas", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral excéntrico"), listOf("Trapecios"),
      listOf("Ligero impulso concéntrico y frenado excéntrico estricto de 4 segundos")),
    guide("elevacion_frontal_polea_baja", "Elevación Frontal en Polea Baja con Cuerda", AestheticMuscleCategory.V_TAPER_DELTS, "Polea Baja", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides anterior"), listOf("Pectoral superior"),
      listOf("Cuerda entre las piernas", "Elevación recta hasta los ojos", "Tensión constante del cable")),
    guide("press_pike_pushups", "Flexiones en Pica (Pike Push-Ups en Suelo)", AestheticMuscleCategory.V_TAPER_DELTS, "Suelo / Cajón", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior y tríceps"), listOf("Core"),
      listOf("Cadera elevada formando una V invertida", "Bajar la cabeza hacia el suelo adelantándola levemente", "Empuje vertical hacia atrás")),
    guide("y_raises_polea_baja", "Y-Raises en Banco Inclinado con Mancuernas", AestheticMuscleCategory.V_TAPER_DELTS, "Banco a 30° y Mancuernas", "Hombros", "LATERAL_RAISE",
      listOf("Trapecio inferior y deltoides lateral"), listOf("Deltoides posterior"),
      listOf("Tumbado prono, elevar brazos formando una letra Y", "Salud escapular y postura erguida")),
    guide("elevaciones_laterales_sentado", "Elevaciones Laterales Sentado Estrictas", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas y Banco Plano", "Hombros", "LATERAL_RAISE",
      listOf("Deltoides lateral puro"), listOf("Trapecio"),
      listOf("Sentado sin posibilidad de balanceo pélvico", "Pausa arriba de medio segundo")),
    guide("press_militar_mancuernas_pie", "Press Militar de Pie con Mancuernas", AestheticMuscleCategory.V_TAPER_DELTS, "Mancuernas", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior"), listOf("Tríceps", "Core"),
      listOf("Mayor trabajo estabilizador que la barra", "Empuje fluido simultáneo")),
    guide("fondos_en_pica_pies_elevados", "Pike Push-Ups con Pies Elevados en Cajón", AestheticMuscleCategory.V_TAPER_DELTS, "Cajón y Suelo", "Hombros", "OVERHEAD_PRESS",
      listOf("Deltoides anterior"), listOf("Tríceps"),
      listOf("Mayor sobrecarga que en el suelo acercándose al pino", "Cadera en vertical"))
  )

  // =========================================================================
  // 8. BÍCEPS (21 Ejercicios)
  // =========================================================================
  val bicepsExercises = listOf(
    guide("curl_barra_biceps_estricto", "Curl de Bíceps con Barra Recta Olímpica", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Olímpica", "Bíceps", "BICEP_CURL",
      listOf("Bíceps braquial (ambas cabezas)"), listOf("Braquial anterior", "Flexores antebrazo"),
      listOf("Codos anclados a los costados del torso", "Flexión de brazos sin balancear la espalda baja", "Apretar bíceps en la cima")),
    guide("curl_inclinado_mancuerna_estiramiento", "Curl Inclinado con Mancuernas en Banco a 45°", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuernas y Banco a 45°", "Bíceps", "BICEP_CURL",
      listOf("Cabeza larga del bíceps (pico)"), listOf("Braquial anterior"),
      listOf("Banco a 45° para posicionar el hombro en hiperextensión", "Estiramiento pasivo masivo de la cabeza larga", "Supinación completa arriba")),
    guide("curl_scott_predicador_barra_z", "Curl Predicador en Banco Scott con Barra Z", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Banco Scott y Barra Z", "Bíceps", "BICEP_CURL",
      listOf("Cabeza corta del bíceps y braquial"), listOf("Antebrazos"),
      listOf("Brazos completamente apoyados en la almohadilla", "Elimina cualquier posibilidad de ayuda del hombro", "Descenso hasta dejar 5° de flexión")),
    guide("curl_martillo_mancuernas_pesado", "Curl Martillo Pesado con Mancuernas (Hammer Curl)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuernas", "Bíceps", "BICEP_CURL",
      listOf("Braquial anterior (empuja el bíceps hacia afuera)", "Braquiorradial"), listOf("Bíceps"),
      listOf("Agarre neutro con palmas enfrentadas", "Desarrollo del grosor y anchura frontal del brazo", "Control excéntrico estricto")),
    guide("curl_concentrado_apoyado_muslo", "Curl Concentrado con Mancuerna (Estilo Arnold)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuerna y Banco", "Bíceps", "BICEP_CURL",
      listOf("Pico de bíceps y cabeza corta"), listOf("Braquial"),
      listOf("Codo fijado en la parte interna del muslo", "Rotación de muñeca hacia afuera en la subida", "Aisla el músculo al 100%")),
    guide("curl_arana_spider_curl", "Spider Curl en Banco Inclinado con Barra Z", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Banco a 45° y Barra Z", "Bíceps", "BICEP_CURL",
      listOf("Cabeza corta del bíceps en máxima contracción"), listOf("Braquial"),
      listOf("Pecho apoyado en el respaldo inclinado con brazos colgando verticales", "Tensión máxima en la fase acortada del músculo")),
    guide("curl_polea_baja_barra_recta", "Curl en Polea Baja con Barra Recta", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Baja", "Bíceps", "BICEP_CURL",
      listOf("Bíceps braquial"), listOf("Antebrazos"),
      listOf("Tensión constante ininterrumpida a lo largo de todo el ROM", "Pausa de 1 segundo arriba")),
    guide("curl_bayesian_polea_espaldas", "Curl Bayesiano en Polea (De Espaldas a la Máquina)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Baja", "Bíceps", "BICEP_CURL",
      listOf("Cabeza larga del bíceps"), listOf("Braquial"),
      listOf("De espaldas a la polea con el brazo estirado hacia atrás", "Curva de resistencia perfecta que coincide con la biomecánica")),
    guide("curl_martillo_cuerda_polea", "Curl Martillo en Polea Baja con Cuerda", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Baja y Cuerda", "Bíceps", "BICEP_CURL",
      listOf("Braquial anterior y braquiorradial"), listOf("Bíceps"),
      listOf("Separar los extremos de la cuerda arriba", "Tensión constante en los antebrazos")),
    guide("curl_21s_barra_z", "Método de las 21 Repeticiones con Barra Z", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Z", "Bíceps", "BICEP_CURL",
      listOf("Bíceps completo (bombeo láctico extremo)"), listOf("Braquial"),
      listOf("7 reps mitad inferior + 7 reps mitad superior + 7 reps completas", "Quema muscular absoluta")),
    guide("curl_drag_curl_barra", "Drag Curl con Barra (Curl de Arrastre)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Olímpica", "Bíceps", "BICEP_CURL",
      listOf("Cabeza larga del bíceps y braquial"), listOf("Deltoides posterior"),
      listOf("Llevar los codos hacia atrás subiendo la barra pegada al torso")),
    guide("curl_zottman_mancuernas", "Curl Zottman con Mancuernas (Supinado y Pronado)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuernas", "Bíceps", "BICEP_CURL",
      listOf("Bíceps al subir, braquiorradial al bajar"), listOf("Flexores y extensores"),
      listOf("Subir con palmas arriba; girar palmas hacia abajo para bajar lento")),
    guide("curl_hercules_doble_polea_alta", "Curl Hércules en Doble Polea Alta", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Doble Polea Alta", "Bíceps", "BICEP_CURL",
      listOf("Pico de bíceps (cabeza corta)"), listOf("Braquial"),
      listOf("Brazos en cruz a 90°; flexionar manos hacia las orejas", "Pose de culturismo doble bíceps bajo tensión")),
    guide("curl_predicador_unilateral_mancuerna", "Curl Predicador a Una Mano con Mancuerna", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuerna y Banco Scott", "Bíceps", "BICEP_CURL",
      listOf("Cabeza corta del bíceps"), listOf("Braquial"),
      listOf("Aislamiento unilateral estricto corrigiendo desbalances de volumen")),
    guide("curl_waiter_curl_disco", "Waiter Curl con Disco o Mancuerna", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuerna Vertical", "Bíceps", "BICEP_CURL",
      listOf("Pico de bíceps central"), listOf("Braquial"),
      listOf("Sostener la cabeza de la mancuerna con ambas palmas planas como bandeja")),
    guide("curl_invertido_barra_recta", "Curl Invertido con Barra Recta (Agarre Prono)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Recta", "Bíceps", "BICEP_CURL",
      listOf("Braquiorradial y extensores de muñeca"), listOf("Braquial anterior"),
      listOf("Palmas hacia abajo durante todo el trayecto", "Desarrollo del antebrazo")),
    guide("curl_predicador_maquina", "Curl en Máquina Predicadora Selectorizada", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Máquina Scott", "Bíceps", "BICEP_CURL",
      listOf("Bíceps y braquial"), listOf("Antebrazo"),
      listOf("Ideal para series al fallo descendentes")),
    guide("curl_martillo_cruzado_pecho", "Curl Martillo Cruzado al Pecho (Pinwheel Curl)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuernas", "Bíceps", "BICEP_CURL",
      listOf("Braquial anterior"), listOf("Braquiorradial"),
      listOf("Flexionar la mancuerna hacia el hombro contrario por delante del pecho")),
    guide("curl_barra_recta_pared", "Curl con Espalda Apoyada en la Pared (Strict Curl)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra y Pared", "Bíceps", "BICEP_CURL",
      listOf("Bíceps en aislamiento competitivo"), listOf("Antebrazo"),
      listOf("Cabeza, espalda y glúteos pegados a la pared sin moverse")),
    guide("curl_concentrado_polea", "Curl Concentrado de Pie en Polea Baja", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Baja", "Bíceps", "BICEP_CURL",
      listOf("Pico de bíceps"), listOf("Braquial"),
      listOf("Tensión continua del cable sin puntos muertos")),
    guide("curl_isometrico_toalla", "Curl Isométrico con Toalla / Barra Inmóvil", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Toalla y Soporte", "Bíceps", "BICEP_CURL",
      listOf("Reclutamiento de unidades motoras rápidas"), listOf("Antebrazo"),
      listOf("Tirar con máxima fuerza voluntaria durante 6 segundos sostenidos"))
  )

  // =========================================================================
  // 9. TRÍCEPS (21 Ejercicios)
  // =========================================================================
  val tricepsExercises = listOf(
    guide("press_frances_barra_z", "Press Francés Tumbado con Barra Z (Skull Crushers)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Z y Banco Plano", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga y medial del tríceps"), listOf("Cabeza lateral"),
      listOf("Brazos inclinados 10° hacia atrás para mantener tensión constante", "Bajar la barra hacia la frente o coronilla doblando solo codos", "Extensión potente")),
    guide("jalon_triceps_cuerda_polea", "Extensión de Tríceps en Polea con Cuerda (Pushdown)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Alta y Cuerda", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza lateral del tríceps (herradura de caballo)"), listOf("Cabeza medial"),
      listOf("Codos clavados a los costados del cuerpo", "Abrir la cuerda al final del recorrido bloqueando el tríceps", "No subir los codos")),
    guide("fondos_paralelas_triceps", "Fondos en Paralelas para Tríceps (Cuerpo Erguido)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barras Paralelas", "Tríceps", "BENCH_PRESS",
      listOf("Tríceps braquial completo (alta sobrecarga)"), listOf("Pectoral inferior", "Deltoides"),
      listOf("Mantener el torso completamente vertical (sin inclinarse)", "Codos pegados al cuerpo al descender", "Extensión completa arriba")),
    guide("extension_triceps_trasnuca_mancuerna", "Extensión de Tríceps Tras Nuca a Dos Manos", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuerna Pesada", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga del tríceps en estiramiento"), listOf("Cabeza lateral"),
      listOf("Sujetar la mancuerna por la base con ambas manos", "Bajarla por detrás de la nuca sintiendo el estiramiento profundo", "Extender arriba")),
    guide("press_banca_agarre_cerrado", "Press de Banca con Agarre Cerrado", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Olímpica y Banco Plano", "Tríceps", "BENCH_PRESS",
      listOf("Tríceps braquial (cabeza medial y lateral)"), listOf("Pectoral esternal", "Deltoides"),
      listOf("Separación de manos a la anchura de hombros (unos 30-40 cm)", "Codos viajan pegados a los costados del torso")),
    guide("patada_triceps_polea", "Patada de Tríceps en Polea (Sin Agarre)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Media", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza lateral del tríceps en acortamiento"), listOf("Cabeza medial"),
      listOf("Torso inclinado, codo elevado paralelo al suelo", "Extender el antebrazo hacia atrás apretando el tríceps 1.5s")),
    guide("extension_katatumba_polea_baja", "Extensión Katatumba / Tríceps Katana en Polea", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Alta Unilateral", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga del tríceps en máxima extensión"), listOf("Hombro posterior"),
      listOf("El cable viaja por detrás del hombro como una katana", "Extensión en diagonal cruzada alineada con las fibras")),
    guide("jalon_triceps_barra_v", "Jalón de Tríceps en Polea con Barra en V", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Alta y Barra V", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza lateral y medial"), listOf("Antebrazos"),
      listOf("Permite mover mayores cargas que la cuerda", "Bloqueo abajo sin mover los hombros")),
    guide("extension_triceps_overhead_polea", "Extensión Overhead en Polea Baja con Cuerda", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Baja y Cuerda", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga del tríceps"), listOf("Cabeza lateral"),
      listOf("De espaldas a la polea baja, sostener cuerda tras nuca", "Extender los brazos hacia arriba y al frente")),
    guide("fondos_entre_bancos", "Fondos de Tríceps entre Dos Bancos", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Dos Bancos Planos y Disco", "Tríceps", "BENCH_PRESS",
      listOf("Tríceps braquial"), listOf("Deltoides anterior"),
      listOf("Manos en un banco y pies en el otro", "Descender flexionando codos a 90°", "Empuje explosivo")),
    guide("press_tate_mancuernas", "Tate Press con Mancuernas en Banco Plano", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuernas", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza medial del tríceps"), listOf("Cabeza lateral"),
      listOf("Mancuernas sobre el pecho; flexionar codos hacia afuera apuntando las pesas al esternón")),
    guide("press_frances_inclinado_mancuernas", "Press Francés en Banco Inclinado con Mancuernas", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Banco a 30° y Mancuernas", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga del tríceps"), listOf("Cabeza lateral"),
      listOf("Mayor rango de estiramiento que en banco plano", "Bajar las pesas a los lados de la cabeza")),
    guide("flexiones_diamante_suelo", "Flexiones Diamante en Suelo (Diamond Push-Ups)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Suelo", "Tríceps", "BENCH_PRESS",
      listOf("Tríceps medial y lateral"), listOf("Pectoral central", "Core"),
      listOf("Índices y pulgares juntos formando un diamante", "Bajar el pecho hacia las manos con codos pegados")),
    guide("jalon_triceps_barra_recta", "Jalón de Tríceps en Polea con Barra Recta", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Alta y Barra Recta", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza lateral y medial"), listOf("Tríceps"),
      listOf("Agarre prono firme", "Empuje hacia el piso con bloqueo")),
    guide("extension_triceps_unilateral_mancuerna", "Extensión Tras Nuca Unilateral con Mancuerna", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Mancuerna", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza larga"), listOf("Tríceps"),
      listOf("Un brazo a la vez para balancear ambos brazos", "Descenso profundo")),
    guide("press_californiano_barra", "Press Californiano con Barra (Híbrido Francés / Cerrado)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Barra Olímpica", "Tríceps", "BENCH_PRESS",
      listOf("Masa global del tríceps"), listOf("Pectoral"),
      listOf("Bajar como press francés a la garganta y empujar como press de banca cerrado")),
    guide("jalon_triceps_invertido_supinado", "Jalón de Tríceps Invertido (Agarre Supinado)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Polea Alta", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza medial del tríceps"), listOf("Antebrazo"),
      listOf("Palmas hacia arriba", "Aisla la cabeza medial")),
    guide("extension_triceps_trx", "Extensión de Tríceps en Suspensión (TRX)", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Correas TRX", "Tríceps", "TRICEP_EXTENSION",
      listOf("Tríceps y core"), listOf("Estabilizadores"),
      listOf("Cuerpo inclinado en plancha; doblar codos llevando manos a la frente")),
    guide("press_frances_declinado", "Press Francés en Banco Declinado", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Banco Declinado y Barra Z", "Tríceps", "TRICEP_EXTENSION",
      listOf("Cabeza lateral y medial"), listOf("Tríceps"),
      listOf("Permite descender la barra más allá de la cabeza")),
    guide("extension_triceps_maquina_sentado", "Extensión de Tríceps en Máquina Sentado", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Máquina Selectorizada", "Tríceps", "TRICEP_EXTENSION",
      listOf("Tríceps completo"), listOf("Antebrazo"),
      listOf("Espalda pegada al respaldo; empuje hacia abajo hasta el bloqueo")),
    guide("fondos_banco_paralelas_maquina", "Fondos de Tríceps en Máquina Asistida", AestheticMuscleCategory.ARMS_BICEPS_TRICEPS, "Máquina de Fondos", "Tríceps", "BENCH_PRESS",
      listOf("Tríceps"), listOf("Pectoral"),
      listOf("Ideal para series largas con peso calibrado"))
  )

  // =========================================================================
  // 10. GEMELOS (21 Ejercicios)
  // =========================================================================
  val calvesExercises = listOf(
    guide("elevacion_talones_pie_maquina", "Elevación de Talones de Pie en Máquina", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina de Gemelos de Pie", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio (cabeza medial y lateral)"), listOf("Sóleo", "Tendón de Aquiles"),
      listOf("Rodillas bloqueadas semirrígidas", "Descenso hasta sentir estiramiento profundo del talón", "Pausa de 2s abajo y elevación en puntas con pausa de 2s arriba")),
    guide("elevacion_talones_sentado_soleo", "Elevación de Talones Sentado en Máquina (Sóleo)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina de Gemelos Sentado", "Gemelos", "CALF_RAISE",
      listOf("Sóleo (ensancha la pantorrilla por debajo)"), listOf("Gastrocnemio"),
      listOf("Rodillas flexionadas a 90° desactivando el gastrocnemio", "Movimiento de recorrido amplio", "Series largas de 15 a 25 repeticiones")),
    guide("elevacion_talones_prensa_piernas", "Elevación de Talones en Prensa Inclinada", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Prensa 45°", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio completo"), listOf("Sóleo"),
      listOf("Solo los metatarsos apoyados en el borde inferior", "Pausa de 2s en el estiramiento")),
    guide("elevacion_talones_unilateral_mancuerna", "Elevación de Talones Unilateral con Mancuerna", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Step", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio y sóleo"), listOf("Estabilizadores"),
      listOf("Una pierna a la vez sosteniendo mancuerna en la misma mano", "Elimina asimetrías de volumen")),
    guide("elevacion_talones_donkey_burro", "Elevación de Talones Estilo Burro (Donkey Calf)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Donkey o Compañero", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio en estiramiento superior"), listOf("Sóleo"),
      listOf("Cadera flexionada a 90° con peso sobre la pelvis")),
    guide("elevacion_talones_smith_step", "Elevación de Talones en Multipower sobre Step", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Multipower y Step", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Sóleo"),
      listOf("Barra acolchada en trapecios; talones bajan por debajo del step")),
    guide("elevacion_tibial_anterior", "Elevación de Puntas / Tibial Anterior en Pared", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Pared o Tibialis Trainer", "Gemelos", "CALF_RAISE",
      listOf("Tibial anterior (espesor frontal y salud de espinilla)"), listOf("Tobillos"),
      listOf("Espalda apoyada en la pared; elevar las puntas hacia las espinillas")),
    guide("elevacion_talones_hack_squat", "Elevación de Talones en Máquina Hack", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Hack", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Sóleo"),
      listOf("Cuerpo apoyado en el respaldo", "Descenso ultra profundo")),
    guide("paseo_de_puntillas_granjero", "Paseo de Puntillas con Mancuernas (Tip-Toe Carry)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuernas Pesadas", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio y sóleo en isometría"), listOf("Antebrazos"),
      listOf("Caminar 40 metros manteniéndose sobre la punta de los pies sin bajar talones")),
    guide("saltos_cuerda_boxeo_gemelos", "Saltos a la Comba Reactivos de Boxeo", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Cuerda de Saltar", "Gemelos", "CALF_RAISE",
      listOf("Elasticidad del tendón de Aquiles y fibras rápidas"), listOf("Sóleo"),
      listOf("Rebotes rápidos sin tocar con los talones")),
    guide("elevacion_talones_kettlebell_deficit", "Elevación de Talones con Kettlebell en Déficit", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Kettlebell y Step", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Sóleo"),
      listOf("Equilibrio y rango articular")),
    guide("elevaciones_soleo_mancuerna_rodillas", "Elevación de Sóleo Sentado con Mancuerna en Rodillas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Step", "Gemelos", "CALF_RAISE",
      listOf("Sóleo"), listOf("Gemelos"),
      listOf("Sentado en banco plano con pesas sobre las rodillas")),
    guide("elevacion_talones_isometrica_sostener", "Elevación de Talones Isométrica en Punta (Pausa 5s)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Step y Mancuerna", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Tendón"),
      listOf("Pausar 5 segundos arriba en cada repetición")),
    guide("elevacion_puntas_con_mancuerna", "Elevación de Tibial Sentado con Mancuerna en Pies", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Mancuerna y Banco", "Gemelos", "CALF_RAISE",
      listOf("Tibial anterior"), listOf("Tobillos"),
      listOf("Sujetar la mancuerna entre ambos pies y subir puntas")),
    guide("elevacion_talones_búlgaro", "Elevación de Talón Unilateral en Posición Búlgara", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Banco y Peso Corporal", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Cuádriceps"),
      listOf("Pie trasero en banco; elevar el talón del pie delantero")),
    guide("elevacion_talones_polea_baja", "Elevación de Talones en Polea Baja con Cinturón", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Polea Baja y Step", "Gemelos", "CALF_RAISE",
      listOf("Gastrocnemio"), listOf("Sóleo"),
      listOf("Cinturón de lastre enganchado a la polea baja")),
    guide("saltos_tijera_puntillas", "Saltos en Tijera Rebotando en Puntas", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Peso Corporal", "Gemelos", "CALF_RAISE",
      listOf("Fibras rápidas del gemelo"), listOf("Cardio"),
      listOf("Impacto elástico controlado")),
    guide("elevacion_talones_rotacion_interna", "Elevación de Talones con Puntas Hacia Adentro", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Gemelos", "Gemelos", "CALF_RAISE",
      listOf("Cabeza lateral del gastrocnemio"), listOf("Sóleo"),
      listOf("Puntas convergentes hacia adentro")),
    guide("elevacion_talones_rotacion_externa", "Elevación de Talones con Puntas Hacia Afuera", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Máquina Gemelos", "Gemelos", "CALF_RAISE",
      listOf("Cabeza medial del gastrocnemio"), listOf("Sóleo"),
      listOf("Puntas abiertas a 45° estilo pingüino")),
    guide("elevacion_talones_de_rodillas_suelo", "Elevaciones de Talón Invertidas Arrodillado", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Colchoneta", "Gemelos", "CALF_RAISE",
      listOf("Fascia plantar y tibial"), listOf("Gemelos"),
      listOf("Estiramiento profundo de los dedos")),
    guide("caminata_en_talones", "Caminata sobre los Talones (Heel Walking)", AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS, "Suelo", "Gemelos", "CALF_RAISE",
      listOf("Tibial anterior"), listOf("Peroneo"),
      listOf("Caminar 30 metros despegando las puntas por completo"))
  )

  private fun guide(
    id: String,
    exerciseName: String,
    category: AestheticMuscleCategory,
    equipment: String,
    targetMuscleGroup: String,
    animationType: String,
    primaryMuscles: List<String>,
    secondaryMuscles: List<String>,
    keyPoints: List<String>
  ): ExerciseVisualGuide {
    val tempo = when (animationType) {
      "BENCH_PRESS", "INCLINE_PRESS", "FLOOR_PRESS" -> "3-1-1-0 (3s descenso controlado, 1s pausa pectoral, explosión)"
      "SQUAT", "LEG_PRESS" -> "3-1-X-0 (3s excéntrica profunda, 1s ruptura de paralelo, subida potente)"
      "DEADLIFT" -> "2-1-1-0 (Tracción firme sin tirones, extensión de cadera completa)"
      "HIP_THRUST" -> "2-1-2-1 (2s subida pélvica, 1.5s compresión isométrica de glúteos, 2s bajada)"
      "GLUTE_KICKBACK" -> "2-0-1-1 (Extensión controlada en 30°, apretar glúteo arriba 1 segundo)"
      "BULGARIAN_SPLIT_SQUAT" -> "3-1-1-0 (3s descenso profundo vertical, subida con el talón)"
      "LEG_EXTENSION" -> "2-1-1-2 (Extensión explosiva, 2s bloqueo isométrico en cuádriceps)"
      "LEG_CURL" -> "2-1-1-1 (Flexión rápida, 1s pico en isquios, 3s bajada frenada)"
      "VACUUM" -> "4-20-4-0 (Exhalación total, succión del ombligo 20s, relajación lenta)"
      "PALLOF_PRESS" -> "2-2-2-0 (Empuje frontal, 2s resistencia anti-rotación, retorno)"
      "RUSSIAN_TWIST" -> "2-1-2-1 (Giro controlado de 45°, pausa en torsión, rotación fluida)"
      "PLANK_SIDE" -> "30-45s (Isometría continua manteniendo pelvis elevada)"
      "AB_WHEEL" -> "3-1-1-0 (Rodar despacio al frente, pausa en hollow body, recoger con abdomen)"
      "PULLUP", "LAT_PULLDOWN", "ROW", "SEATED_ROW" -> "3-0-1-1 (Apertura escapular, 1s contracción isométrica dorsal)"
      "BICEP_CURL" -> "3-0-1-1 (Fase negativa lenta de 3s, apretar bíceps en el pico)"
      "TRICEP_EXTENSION", "TRICEP_PUSHDOWN" -> "3-1-1-0 (Estiramiento controlado, bloqueo firme sin mover hombros)"
      "LATERAL_RAISE", "REAR_DELT_FLYE", "FRONT_RAISE" -> "2-1-1-1 (Control estricto sin inercia, pausa arriba)"
      "CALF_RAISE" -> "3-2-1-2 (3s estiramiento profundo abajo, 2s pausa arriba en punta)"
      "CRUNCH" -> "2-1-1-1 (Exhalación forzada al flexionar, mantener tensión)"
      else -> "3-1-1-0 (Tempo biomecánico controlado)"
    }

    val breathingCue = when (targetMuscleGroup) {
      "Pecho" -> "Inhala expandiendo la caja torácica durante el descenso controlado; exhala con fuerza al empujar la carga hacia arriba."
      "Espalda" -> "Inhala profundamente en el estiramiento completo; exhala reteniendo el core al llevar los codos hacia atrás."
      "Hombros" -> "Inhala con el diafragma al inicio; bloquea el core (bracing) y exhala al superar la altura de los ojos."
      "Bíceps" -> "Inhala al descender el peso de forma pausada; exhala al flexionar el codo contrayendo el pico de bíceps."
      "Tríceps" -> "Inhala mientras flexionas los codos sintiendo el estiramiento; exhala al extender los brazos bloqueando el tríceps."
      "Piernas" -> "Inhala hondo inflando el abdomen (Maniobra de Valsalva), desciende rompiendo el paralelo y exhala al subir."
      "Glúteos" -> "Inhala en el punto bajo de estiramiento; exhala con potencia extendiendo la cadera y bloquea glúteos arriba."
      "Cintura" -> "Exhala absolutamente todo el aire de los pulmones, succiona el ombligo hacia la columna vertebral y sostén la respiración."
      "Abdomen" -> "Exhala forzadamente por la boca vaciando los pulmones al contraer el abdomen; inhala controladamente al extender."
      "Gemelos" -> "Inhala en el estiramiento máximo del talón; exhala al elevarse sobre los metatarsos sosteniendo la punta."
      else -> "Exhala todo el aire vaciando los pulmones en el punto de máxima flexión abdominal; inhala al extender."
    }

    val visualCues = listOf(
      "Alineación articular neutra",
      "Tensión mecánica continua en el músculo diana",
      "Rango de movimiento completo (ROM estricto)"
    )

    return ExerciseVisualGuide(
      id = id,
      exerciseName = exerciseName,
      category = category,
      equipment = equipment,
      tempo = tempo,
      primaryMuscles = primaryMuscles,
      secondaryMuscles = secondaryMuscles,
      keyPoints = keyPoints,
      breathingCue = breathingCue,
      visualCues = visualCues,
      targetMuscleGroup = targetMuscleGroup,
      animationType = animationType
    )
  }

  /**
   * Búsqueda inteligente de ejercicio por ID o nombre aproximado.
   * Permite interactuar con los ejercicios de cualquier sección (Rutina, Entrenamientos, etc.)
   * y abrir de inmediato su modelo 3D y guía de ejecución correcta.
   */
  fun findExercise(query: String): ExerciseVisualGuide? {
    val all = getAllExercises()
    if (query.isBlank()) return all.firstOrNull()
    val clean = query.trim().lowercase()
      .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")

    // 1. Coincidencia exacta por ID
    val byId = all.find { it.id.equals(clean, ignoreCase = true) }
    if (byId != null) return byId

    // 2. Coincidencia por nombre exacto normalizado
    val byExactName = all.find {
      val norm = it.exerciseName.lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
      norm == clean
    }
    if (byExactName != null) return byExactName

    // 3. Coincidencia por subcadena contenida
    val bySubstring = all.find {
      val norm = it.exerciseName.lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
      norm.contains(clean) || clean.contains(norm)
    }
    if (bySubstring != null) return bySubstring

    // 4. Coincidencia por tokens clave
    val stopWords = setOf("para", "sobre", "entre", "barra", "mancuerna", "mancuernas", "con", "en", "de", "del")
    val queryTokens = clean.split(" ", "_", "-").filter { it.length > 2 && it !in stopWords }
    if (queryTokens.isNotEmpty()) {
      val byTokens = all.maxByOrNull { guide ->
        val norm = (guide.exerciseName + " " + guide.id).lowercase()
          .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        queryTokens.count { token -> norm.contains(token) }
      }
      if (byTokens != null) return byTokens
    }

    return all.firstOrNull()
  }
}

