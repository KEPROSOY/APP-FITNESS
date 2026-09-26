package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ExerciseVisualGuide

enum class CameraPreset(val label: String, val yaw: Float, val pitch: Float) {
  FREE_3D("3D Libre 360°", 35f, 20f),
  FRONTAL("Frontal (0°)", 0f, 10f),
  SAGITTAL_RIGHT("Perfil (90°)", 90f, 10f),
  POSTERIOR("Espalda (180°)", 180f, 15f),
  ISOMETRIC("Isométrica", 45f, 30f)
}

/**
 * Humanoid3DViewer
 *
 * Visor biomecánico 3D interactivo que renderiza un modelo humanoide articulado
 * ejecutando el ejercicio en tiempo real con:
 * - Cinemática de articulaciones en 3D (codos, hombros, cadera, rodillas, columna).
 * - Mapa de calor muscular (Heatmap) donde los músculos diana se iluminan con gradiente vivo.
 * - Rotación orbital 3D interactiva táctil 360°.
 * - Presets de cámara (Frontal, Perfil, Posterior, Isométrica, Libre).
 * - Telemetría de ángulos articulares en tiempo real.
 * - Sincronización completa con Tema Claro y Oscuro.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Humanoid3DViewer(
  guide: ExerciseVisualGuide,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  var selectedCamera by remember { mutableStateOf(CameraPreset.FREE_3D) }
  var isPlaying by remember { mutableStateOf(true) }
  var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
  var manualScrubProgress by remember { mutableFloatStateOf(0f) }
  var isManualScrubbing by remember { mutableStateOf(false) }
  var showTelemetry by remember { mutableStateOf(true) }

  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  // Detectar grupo muscular principal para el shader de activación
  val targetGroup = guide.targetMuscleGroup
  val animType = guide.animationType

  val htmlContent = remember(guide.id, animType, targetGroup, isDark) {
    generate3DHumanoidHtml(
      exerciseName = guide.exerciseName,
      animType = animType,
      targetGroup = targetGroup,
      tempo = guide.tempo,
      isDark = isDark
    )
  }

  // Notificar cambios de cámara a WebView si cambia el preset
  LaunchedEffect(selectedCamera) {
    webViewRef?.evaluateJavascript(
      "if (window.setCameraPreset) { window.setCameraPreset(${selectedCamera.yaw}, ${selectedCamera.pitch}); }",
      null
    )
  }

  // Notificar reproducción
  LaunchedEffect(isPlaying) {
    webViewRef?.evaluateJavascript(
      "if (window.setPlaying) { window.setPlaying($isPlaying); }",
      null
    )
  }

  // Notificar velocidad
  LaunchedEffect(playbackSpeed) {
    webViewRef?.evaluateJavascript(
      "if (window.setSpeed) { window.setSpeed($playbackSpeed); }",
      null
    )
  }

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("humanoid_3d_viewer_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Cabecera con indicador de estado 3D
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isPlaying) Color(0xFF10B981) else Color(0xFFEF4444))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "MODELADO 3D HUMANO BIOMECÁNICO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
          Text(
            text = "360° ORBITAL",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Viewport 3D en WebView
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(if (isDark) Color(0xFF0A0F1D) else Color(0xFFF1F5F9))
          .border(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.3f else 0.4f),
            RoundedCornerShape(18.dp)
          )
      ) {
        AndroidView(
          factory = { context ->
            WebView(context).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )
              settings.javaScriptEnabled = true
              settings.domStorageEnabled = true
              settings.cacheMode = WebSettings.LOAD_NO_CACHE
              // Security hardening: Disable local file and content schema access
              settings.allowFileAccess = false
              settings.allowContentAccess = false
              setBackgroundColor(if (isDark) 0xFF0A0F1D.toInt() else 0xFFF1F5F9.toInt())
              tag = "${guide.id}_${isDark}"
              loadDataWithBaseURL("about:blank", htmlContent, "text/html", "UTF-8", null)
              webViewRef = this
            }
          },
          update = { webView ->
            val expectedTag = "${guide.id}_${isDark}"
            if (webView.tag != expectedTag) {
              webView.tag = expectedTag
              webView.setBackgroundColor(if (isDark) 0xFF0A0F1D.toInt() else 0xFFF1F5F9.toInt())
              webView.loadDataWithBaseURL("about:blank", htmlContent, "text/html", "UTF-8", null)
            }
          },
          modifier = Modifier.fillMaxSize()
        )

        // Overlay de asistencia de gestos
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = (if (isDark) Color.Black else Color.White).copy(alpha = 0.75f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          ) {
            Text(
              text = "Arrastra para rotar 3D",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Presets de Cámara
      Text(
        text = "ÁNGULO DE CÁMARA 3D",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CameraPreset.values().forEach { preset ->
          val isSelected = selectedCamera == preset
          FilterChip(
            selected = isSelected,
            onClick = {
              selectedCamera = preset
              webViewRef?.evaluateJavascript(
                "if (window.setCameraPreset) { window.setCameraPreset(${preset.yaw}, ${preset.pitch}); }",
                null
              )
            },
            label = {
              Text(
                text = preset.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = Color.White,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Controles de Reproducción y Velocidad
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Play / Pause
          IconButton(
            onClick = {
              isPlaying = !isPlaying
              webViewRef?.evaluateJavascript(
                "if (window.setPlaying) { window.setPlaying($isPlaying); }",
                null
              )
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer)
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pausar" else "Reanudar",
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          // Velocidad
          listOf(0.5f, 1.0f, 1.5f).forEach { speed ->
            val isCurrent = playbackSpeed == speed
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier
                .clickable {
                  playbackSpeed = speed
                  webViewRef?.evaluateJavascript(
                    "if (window.setSpeed) { window.setSpeed($speed); }",
                    null
                  )
                }
            ) {
              Text(
                text = "${speed}x",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        // Switch telemetría
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { showTelemetry = !showTelemetry }
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Telemetría",
            tint = if (showTelemetry) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Ángulos",
            style = MaterialTheme.typography.labelSmall,
            color = if (showTelemetry) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Panel de Telemetría Biomecánica
      AnimatedVisibility(visible = showTelemetry) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "MÚSCULO DIANA",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = targetGroup,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "CINEMÁTICA",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = animType.replace("_", " "),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.secondary
                )
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "CONTRACCIÓN",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Dinámica 3D",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF10B981)
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Genera el motor 3D completo del humanoide biomecánico en HTML5 Canvas con proyección 3D,
 * rotación orbital táctil en 360°, cinemática de huesos y músculos con mapa de calor (Heatmap).
 */
private fun generate3DHumanoidHtml(
  exerciseName: String,
  animType: String,
  targetGroup: String,
  tempo: String,
  isDark: Boolean
): String {
  val bgColor = if (isDark) "#0A0F1D" else "#F1F5F9"
  val gridColor = if (isDark) "rgba(56, 189, 248, 0.15)" else "rgba(100, 116, 139, 0.2)"
  val mannequinBaseColor = if (isDark) "#334155" else "#94A3B8"
  val mannequinMuscularColor = if (isDark) "#475569" else "#64748B"
  val mannequinJointColor = if (isDark) "#64748B" else "#475569"
  val activeMuscleColor = if (isDark) "#F59E0B" else "#D97706"
  val barColor = if (isDark) "#CBD5E1" else "#475569"
  val plateColor = if (isDark) "#DC2626" else "#B91C1C"
  val benchColor = if (isDark) "#1E293B" else "#CBD5E1"
  val textColor = if (isDark) "#F8FAFC" else "#0F172A"
  val textSubColor = if (isDark) "#94A3B8" else "#64748B"

  return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; touch-action: none; }
    body, html { width: 100%; height: 100%; overflow: hidden; background: $bgColor; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    canvas { width: 100%; height: 100%; display: block; }
    #hud {
      position: absolute;
      top: 10px;
      left: 12px;
      pointer-events: none;
      color: $textColor;
    }
    .hud-title { font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }
    .hud-phase { font-size: 11px; font-weight: 600; color: #38BDF8; margin-top: 2px; }
    .hud-angle { font-size: 10px; color: $textSubColor; font-family: monospace; margin-top: 2px; }
    #heatmap-legend {
      position: absolute;
      bottom: 10px;
      left: 12px;
      pointer-events: none;
      display: flex;
      align-items: center;
      gap: 6px;
      background: ${if (isDark) "rgba(15,23,42,0.85)" else "rgba(255,255,255,0.85)"};
      padding: 4px 8px;
      border-radius: 6px;
      border: 1px solid ${if (isDark) "rgba(255,255,255,0.1)" else "rgba(0,0,0,0.1)"};
    }
    .dot { width: 8px; height: 8px; border-radius: 50%; background: #F59E0B; box-shadow: 0 0 6px #F59E0B; }
    .legend-text { font-size: 10px; font-weight: 700; color: $textColor; }
  </style>
</head>
<body>
  <div id="hud">
    <div class="hud-title" id="hudExercise">$exerciseName</div>
    <div class="hud-phase" id="hudPhase">Fase: Excéntrica (Descenso)</div>
    <div class="hud-angle" id="hudAngle">Cinemática Articular Activa</div>
  </div>

  <div id="heatmap-legend">
    <div class="dot"></div>
    <div class="legend-text">Activación: $targetGroup</div>
  </div>

  <canvas id="c"></canvas>

  <script>
    const canvas = document.getElementById('c');
    const ctx = canvas.getContext('2d');
    const hudPhase = document.getElementById('hudPhase');
    const hudAngle = document.getElementById('hudAngle');

    let width, height;
    function resize() {
      const dpr = window.devicePixelRatio || 1;
      width = canvas.clientWidth;
      height = canvas.clientHeight;
      canvas.width = width * dpr;
      canvas.height = height * dpr;
      ctx.scale(dpr, dpr);
    }
    window.addEventListener('resize', resize);
    resize();

    // Estado 3D de Cámara
    let yaw = 35 * Math.PI / 180;
    let pitch = 15 * Math.PI / 180;
    let distance = 330;
    let isPlaying = true;
    let speed = 1.0;
    let animTime = 0;

    // Control táctil interactivo 360°
    let isDragging = false;
    let lastX = 0, lastY = 0;
    canvas.addEventListener('pointerdown', (e) => {
      isDragging = true;
      lastX = e.clientX;
      lastY = e.clientY;
    });
    window.addEventListener('pointermove', (e) => {
      if (!isDragging) return;
      const dx = e.clientX - lastX;
      const dy = e.clientY - lastY;
      lastX = e.clientX;
      lastY = e.clientY;
      yaw += dx * 0.01;
      pitch = Math.max(-0.5, Math.min(1.3, pitch + dy * 0.01));
    });
    window.addEventListener('pointerup', () => { isDragging = false; });
    window.addEventListener('pointercancel', () => { isDragging = false; });

    // API pública para Compose
    window.setCameraPreset = (targetYawDeg, targetPitchDeg) => {
      yaw = targetYawDeg * Math.PI / 180;
      pitch = targetPitchDeg * Math.PI / 180;
    };
    window.setPlaying = (p) => { isPlaying = p; };
    window.setSpeed = (s) => { speed = s; };

    // Proyección 3D en perspectiva
    function project(x, y, z) {
      const cosY = Math.cos(yaw), sinY = Math.sin(yaw);
      const x1 = x * cosY - z * sinY;
      const z1 = x * sinY + z * cosY;

      const cosP = Math.cos(pitch), sinP = Math.sin(pitch);
      const y2 = y * cosP - z1 * sinP;
      const z2 = y * sinP + z1 * cosP + distance;

      const fov = 320;
      const scale = fov / Math.max(z2, 10);
      return {
        x: width / 2 + x1 * scale,
        y: height / 2 - y2 * scale + 25,
        scale: scale,
        depth: z2
      };
    }

    const renderQueue = [];
    function queueSphere(x, y, z, radius, color, glow = false) {
      const p = project(x, y, z);
      renderQueue.push({
        type: 'sphere',
        x: p.x, y: p.y, r: radius * p.scale,
        depth: p.depth,
        color: color,
        glow: glow
      });
    }

    function queueMuscle(x1, y1, z1, x2, y2, z2, radius, color, glow = false) {
      const p1 = project(x1, y1, z1);
      const p2 = project(x2, y2, z2);
      const midDepth = (p1.depth + p2.depth) / 2;
      renderQueue.push({
        type: 'muscle',
        x1: p1.x, y1: p1.y,
        x2: p2.x, y2: p2.y,
        w: radius * ((p1.scale + p2.scale) / 2),
        depth: midDepth,
        color: color,
        glow: glow
      });
    }

    function queueCylinder(x1, y1, z1, x2, y2, z2, radius, color, glow = false) {
      const p1 = project(x1, y1, z1);
      const p2 = project(x2, y2, z2);
      const midDepth = (p1.depth + p2.depth) / 2;
      renderQueue.push({
        type: 'cylinder',
        x1: p1.x, y1: p1.y,
        x2: p2.x, y2: p2.y,
        w: radius * ((p1.scale + p2.scale) / 2),
        depth: midDepth,
        color: color,
        glow: glow
      });
    }

    function queueLine(x1, y1, z1, x2, y2, z2, color, width = 1) {
      const p1 = project(x1, y1, z1);
      const p2 = project(x2, y2, z2);
      renderQueue.push({
        type: 'line',
        x1: p1.x, y1: p1.y,
        x2: p2.x, y2: p2.y,
        depth: (p1.depth + p2.depth) / 2,
        color: color,
        w: width
      });
    }

    const animType = "$animType";
    const targetGroup = "$targetGroup";

    function computeKinematics(t) {
      let phase = 'Fase Excéntrica (3s)';
      let progress = 0;
      let tension = 0;

      if (t < 0.5) {
        progress = t / 0.5;
        phase = 'Fase Excéntrica • Elongación y Tensión';
        tension = 0.4 + progress * 0.45;
      } else if (t < 0.7) {
        progress = 1.0;
        phase = 'Pausa Isométrica • Tensión Máxima';
        tension = 1.0;
      } else {
        progress = 1.0 - (t - 0.7) / 0.3;
        phase = 'Fase Concéntrica • Aceleración y Bloqueo';
        tension = 0.9;
      }

      hudPhase.innerText = phase;
      return { progress, tension };
    }

    function loop() {
      if (isPlaying) {
        animTime = (animTime + 0.0055 * speed) % 1.0;
      }

      const { progress, tension } = computeKinematics(animTime);

      ctx.clearRect(0, 0, width, height);
      renderQueue.length = 0;

      // 1. Rejilla de suelo atlética 3D
      const gridSize = 160;
      const step = 40;
      for (let x = -gridSize; x <= gridSize; x += step) {
        queueLine(x, -95, -gridSize, x, -95, gridSize, '$gridColor');
      }
      for (let z = -gridSize; z <= gridSize; z += step) {
        queueLine(-gridSize, -95, z, gridSize, -95, z, '$gridColor');
      }

      // Paleta atlética
      const baseCol = '$mannequinBaseColor';
      const muscCol = '$mannequinMuscularColor';
      const jointCol = '$mannequinJointColor';
      const glowCol = `rgba(245, 158, 11, ${'$'}{0.5 + tension * 0.5})`;

      // Detección de grupos musculares
      const isChest = targetGroup.includes('Pecho');
      const isBack = targetGroup.includes('Espalda');
      const isShoulder = targetGroup.includes('Hombros');
      const isBiceps = targetGroup.includes('Bíceps');
      const isTriceps = targetGroup.includes('Tríceps');
      const isPiernas = targetGroup.includes('Piernas') || targetGroup.includes('Cuádriceps');
      const isGlutes = targetGroup.includes('Glúteos') || targetGroup.includes('Femorales');
      const isWaist = targetGroup.includes('Cintura') || targetGroup.includes('Oblicuos');
      const isAbs = targetGroup.includes('Abdomen') || targetGroup.includes('Core');

      // Variables cinemáticas articulares
      let headX = 0, headY = 82, headZ = 0;
      let spineX = 0, spineY = 46, spineZ = 0;
      let pelvisX = 0, pelvisY = 5, pelvisZ = 0;
      let shLeftX = -26, shRightX = 26, shY = 60, shZ = 0;

      let elLX = -35, elLY = 32, elLZ = 0;
      let elRX = 35, elRY = 32, elRZ = 0;
      let handLX = -35, handLY = 5, handLZ = 0;
      let handRX = 35, handRY = 5, handRZ = 0;

      let kneeLX = -18, kneeLY = -42, kneeLZ = 0;
      let kneeRX = 18, kneeRY = -42, kneeRZ = 0;
      let footLX = -18, footLY = -95, footLZ = 0;
      let footRX = 18, footRY = -95, footRZ = 0;

      let eqType = 'NONE';
      let eqX1 = 0, eqY1 = 0, eqZ1 = 0, eqX2 = 0, eqY2 = 0, eqZ2 = 0;
      let eqW = 3;

      // =========================================================================
      // CINEMÁTICA BIOMECÁNICA PRECISA PARA CADA TIPO DE EJERCICIO
      // =========================================================================
      if (animType === 'HIP_THRUST') {
        // Hip Thrust: Torso apoyado sobre banco a nivel de escápulas, cadera baja y sube
        const thrustP = progress; // 0 = abajo, 1 = puente horizontal bloqueado
        shY = 24; shZ = -25;
        headY = 32; headZ = -30;
        pelvisY = -25 + thrustP * 48; // Sube de -25 a +23 (paralelo con rodillas)
        pelvisZ = 5 + thrustP * 5;
        spineY = (shY + pelvisY) / 2;
        spineZ = (shZ + pelvisZ) / 2;

        kneeLX = -18; kneeLY = 23; kneeLZ = 25;
        kneeRX = 18; kneeRY = 23; kneeRZ = 25;
        footLX = -18; footLY = -95; footLZ = 25;
        footRX = 18; footRY = -95; footRZ = 25;

        // Manos estabilizando la barra sobre la pelvis
        handLX = -22; handLY = pelvisY + 5; handLZ = pelvisZ;
        handRX = 22; handRY = pelvisY + 5; handRZ = pelvisZ;
        elLX = -30; elLY = pelvisY + 12; elLZ = pelvisZ - 5;
        elRX = 30; elRY = pelvisY + 12; elRZ = pelvisZ - 5;

        // Banco acolchado detrás de la espalda
        queueCylinder(-35, 18, -26, 35, 18, -26, 12, '$benchColor');
        queueCylinder(-35, -95, -26, -35, 18, -26, 4, '$benchColor');
        queueCylinder(35, -95, -26, 35, 18, -26, 4, '$benchColor');

        // Barra con discos sobre la cadera
        eqType = 'BARBELL';
        eqY1 = pelvisY + 5; eqZ1 = pelvisZ;

        hudAngle.innerText = `Extensión de Cadera: ${'$'}{Math.round(thrustP * 180)}° • Activación Glúteo: ${'$'}{Math.round(tension * 100)}%`;
      } else if (animType === 'BULGARIAN_SPLIT_SQUAT') {
        // Sentadilla Búlgara: Pierna trasera elevada en banco, delantera desciende
        const splitP = progress;
        pelvisY = 5 - splitP * 42;
        spineY = 46 - splitP * 40;
        headY = 82 - splitP * 40;

        // Pierna delantera (Izquierda)
        kneeLX = -16; kneeLY = pelvisY - 20; kneeLZ = 28;
        footLX = -16; footLY = -95; footLZ = 28;

        // Pierna trasera (Derecha) apoyada en banco atrás
        kneeRX = 16; kneeRY = pelvisY - 25; kneeRZ = -25;
        footRX = 16; footRY = -30; footRZ = -65;

        // Banco trasero
        queueCylinder(5, -32, -65, 30, -32, -65, 8, '$benchColor');
        queueCylinder(16, -95, -65, 16, -32, -65, 3, '$benchColor');

        // Mancuernas en las manos a los costados
        handLX = -28; handLY = pelvisY + 5; handLZ = 5;
        handRX = 28; handRY = pelvisY + 5; handRZ = 5;
        elLX = -30; elLY = spineY - 5; elLZ = 2;
        elRX = 30; elRY = spineY - 5; elRZ = 2;

        eqType = 'DUMBBELLS';
        hudAngle.innerText = `Flexión Rodilla Delantera: ${'$'}{Math.round(180 - splitP * 90)}° • Carga Unilateral`;
      } else if (animType === 'LEG_EXTENSION') {
        // Extensiones en máquina: Sentado, rodilla fija, espinilla extiende hacia arriba
        const extP = progress;
        headY = 65; headZ = -15;
        shY = 45; shZ = -15;
        spineY = 28; spineZ = -15;
        pelvisY = 10; pelvisZ = 0;

        kneeLX = -16; kneeLY = 10; kneeLZ = 35;
        kneeRX = 16; kneeRY = 10; kneeRZ = 35;

        // Piernas suben a la horizontal
        const angle = (1 - extP) * Math.PI / 2; // de PI/2 a 0
        footLX = -16; footLY = 10 - Math.sin(angle) * 55; footLZ = 35 + Math.cos(angle) * 55;
        footRX = 16; footRY = 10 - Math.sin(angle) * 55; footRZ = 35 + Math.cos(angle) * 55;

        // Asiento de máquina
        queueCylinder(-25, 6, 0, 25, 6, 0, 16, '$benchColor');
        queueCylinder(0, 8, -20, 0, 50, -20, 14, '$benchColor');

        hudAngle.innerText = `Extensión de Rodilla: ${'$'}{Math.round(extP * 90)}° • Aislamiento Recto Femoral`;
      } else if (animType === 'LEG_CURL') {
        // Curl femoral tumbado prono
        const curlP = progress;
        headY = 12; headZ = 45;
        shY = 8; shZ = 35;
        spineY = 6; spineZ = 15;
        pelvisY = 6; pelvisZ = -15;
        kneeLX = -16; kneeLY = 6; kneeLZ = -45;
        kneeRX = 16; kneeRY = 6; kneeRZ = -45;

        const curlRad = curlP * Math.PI * 0.65;
        footLX = -16; footLY = 6 + Math.sin(curlRad) * 45; footLZ = -45 + Math.cos(curlRad) * 45;
        footRX = 16; footRY = 6 + Math.sin(curlRad) * 45; footRZ = -45 + Math.cos(curlRad) * 45;

        queueCylinder(-25, 2, -25, 25, 2, 25, 14, '$benchColor');
        hudAngle.innerText = `Flexión Isquiotibial: ${'$'}{Math.round(curlP * 120)}° • Tensión Biceps Femoral`;
      } else if (animType === 'VACUUM') {
        // Vacío Abdominal: De pie, manos a caderas, abdomen succionado hacia adentro
        const vacP = progress;
        headY = 82; spineY = 46; pelvisY = 5;
        // Manos apoyadas firmemente en las crestas ilíacas
        handLX = -22; handLY = 12; handLZ = 4;
        handRX = 22; handRY = 12; handRZ = 4;
        elLX = -36; elLY = 26; elLZ = -5;
        elRX = 36; elRY = 26; elRZ = -5;

        hudAngle.innerText = `Contracción Transverso: ${'$'}{Math.round(vacP * 100)}% • Vacío Lumbar Activo`;
      } else if (animType === 'RUSSIAN_TWIST') {
        // Giros rusos: Sentado en V con pies despegados, rotando disco lado a lado
        const rot = Math.sin(animTime * Math.PI * 4); // oscila de -1 a +1
        headY = 45; headZ = -15;
        shY = 32; shZ = -10;
        spineY = 18; spineZ = -5;
        pelvisY = 5; pelvisZ = 0;

        kneeLX = -14; kneeLY = 24; kneeLZ = 25;
        kneeRX = 14; kneeRY = 24; kneeRZ = 25;
        footLX = -14; footLY = 28; footLZ = 45;
        footRX = 14; footRY = 28; footRZ = 45;

        // Manos sosteniendo disco rotando de lado a lado
        const diskX = rot * 32;
        handLX = diskX - 5; handLY = 14; handLZ = 12;
        handRX = diskX + 5; handRY = 14; handRZ = 12;
        elLX = (shLeftX + handLX) / 2; elLY = 22; elLZ = 5;
        elRX = (shRightX + handRX) / 2; elRY = 22; elRZ = 5;

        // Disco girando
        queueCylinder(diskX - 4, 14, 12, diskX + 4, 14, 12, 12, '$plateColor');
        hudAngle.innerText = `Torsión Oblicua: ${'$'}{Math.round(rot * 45)}° • Anti-Flexión Lumbar`;
      } else if (animType === 'PLANK_SIDE') {
        // Plancha Lateral: Apoyado en un codo, cuerpo recto elevando cadera
        const dip = Math.sin(animTime * Math.PI * 2) * 15;
        elLX = 0; elLY = -90; elLZ = 0;
        handLX = 15; handLY = -90; handLZ = 10;
        shY = -65; shLeftX = 0; shZ = 0;
        spineY = -45 + dip; spineZ = 0;
        pelvisY = -35 + dip * 1.5; pelvisZ = 0;
        headY = -55; headX = -10;
        footLX = 0; footLY = -95; footLZ = -65;
        footRX = 0; footRY = -85; footRZ = -65;

        hudAngle.innerText = `Elevación Pelvis: ${'$'}{Math.round(-35 + dip * 1.5)}mm • Oblicuo & Cuadrado Lumbar`;
      } else if (animType === 'AB_WHEEL') {
        // Rueda Abdominal desde rodillas extendiendo al frente
        const rollP = progress;
        kneeLX = -12; kneeLY = -90; kneeLZ = -40;
        kneeRX = 12; kneeRY = -90; kneeRZ = -40;
        footLX = -12; footLY = -75; footLZ = -65;
        footRX = 12; footRY = -75; footRZ = -65;

        pelvisY = -45 - rollP * 25; pelvisZ = -40 + rollP * 30;
        spineY = -35 - rollP * 30; spineZ = -15 + rollP * 40;
        shY = -25 - rollP * 35; shZ = 10 + rollP * 50;
        headY = -15 - rollP * 35; headZ = 20 + rollP * 50;

        const wheelZ = 25 + rollP * 65;
        const wheelY = -85;
        handLX = -8; handLY = wheelY; handLZ = wheelZ;
        handRX = 8; handRY = wheelY; handRZ = wheelZ;
        elLX = -16; elLY = (shY + wheelY) / 2; elLZ = (shZ + wheelZ) / 2;
        elRX = 16; elRY = (shY + wheelY) / 2; elRZ = (shZ + wheelZ) / 2;

        // Rueda ab wheel
        queueCylinder(-7, wheelY, wheelZ, 7, wheelY, wheelZ, 12, '$plateColor');
        queueCylinder(-16, wheelY, wheelZ, 16, wheelY, wheelZ, 2, '$barColor');
        hudAngle.innerText = `Extensión Exc. Abdominal: ${'$'}{Math.round(rollP * 100)}% • Tensión Hollow Body`;
      } else if (animType === 'CRUNCH') {
        // Crunch abdominal en colchoneta
        const crunchP = progress;
        pelvisY = -80; pelvisZ = -10;
        kneeLX = -16; kneeLY = -40; kneeLZ = 25;
        kneeRX = 16; kneeRY = -40; kneeRZ = 25;
        footLX = -16; footLY = -95; footLZ = 30;
        footRX = 16; footRY = -95; footRZ = 30;

        spineY = -80 + crunchP * 20; spineZ = -30 + crunchP * 15;
        shY = -75 + crunchP * 35; shZ = -50 + crunchP * 25;
        headY = -70 + crunchP * 38; headZ = -60 + crunchP * 30;

        handLX = -18; handLY = headY + 5; handLZ = headZ;
        handRX = 18; handRY = headY + 5; handRZ = headZ;
        elLX = -30; elLY = headY; elLZ = headZ + 15;
        elRX = 30; elRY = headY; elRZ = headZ + 15;

        hudAngle.innerText = `Flexión Torácica: ${'$'}{Math.round(crunchP * 45)}° • Acortamiento Recto Abdominal`;
      } else if (animType === 'BENCH_PRESS' || animType === 'INCLINE_PRESS' || animType === 'FLOOR_PRESS') {
        // Press de Banca acostado
        const pressP = progress;
        headY = 18; headZ = -45;
        shY = 16; shZ = -28;
        spineY = 15; spineZ = -5;
        pelvisY = 15; pelvisZ = 20;

        kneeLX = -22; kneeLY = 5; kneeLZ = 38;
        kneeRX = 22; kneeRY = 5; kneeRZ = 38;
        footLX = -24; footLY = -95; footLZ = 38;
        footRX = 24; footRY = -95; footRZ = 38;

        const bY = 48 - pressP * 30; // Barra baja al esternón
        const bZ = -18;

        handLX = -28; handLY = bY; handLZ = bZ;
        handRX = 28; handRY = bY; handRZ = bZ;

        elLX = -42; elLY = 10 - pressP * 8; elLZ = bZ - (1 - pressP) * 5;
        elRX = 42; elRY = 10 - pressP * 8; elRZ = bZ - (1 - pressP) * 5;

        // Banco de press
        queueCylinder(0, 10, -50, 0, 10, 30, 14, '$benchColor');
        queueCylinder(-16, -95, -45, -16, 10, -45, 3, '$benchColor');
        queueCylinder(16, -95, -45, 16, 10, -45, 3, '$benchColor');
        queueCylinder(0, -95, 25, 0, 10, 25, 3, '$benchColor');

        eqType = 'BARBELL';
        eqY1 = bY; eqZ1 = bZ;
        hudAngle.innerText = `Ángulo Codo: ${'$'}{Math.round(170 - pressP * 85)}° • Tensión Pectoral: ${'$'}{Math.round(tension * 100)}%`;
      } else if (animType === 'SQUAT' || animType === 'LEG_PRESS') {
        // Sentadilla libre profunda
        const sqP = progress;
        const depth = sqP * 54;
        pelvisY = 5 - depth;
        spineY = 46 - depth * 0.9;
        headY = 82 - depth * 0.9;
        pelvisZ = -sqP * 28;
        spineZ = -sqP * 15;

        kneeLX = -22; kneeLY = pelvisY - 24 + sqP * 8; kneeLZ = 18;
        kneeRX = 22; kneeRY = pelvisY - 24 + sqP * 8; kneeRZ = 18;
        footLX = -24; footLY = -95; footLZ = 0;
        footRX = 24; footRY = -95; footRZ = 0;

        // Barra sobre trapecios
        const barY = spineY + 16;
        const barZ = spineZ - 4;
        handLX = -34; handLY = barY - 2; handLZ = barZ + 2;
        handRX = 34; handRY = barY - 2; handRZ = barZ + 2;
        elLX = -36; elLY = barY - 18; elLZ = barZ - 5;
        elRX = 36; elRY = barY - 18; elRZ = barZ - 5;

        eqType = 'BARBELL';
        eqY1 = barY; eqZ1 = barZ;
        hudAngle.innerText = `Profundidad Sentadilla: ${'$'}{Math.round(sqP * 100)}% • Ángulo Rodilla: ${'$'}{Math.round(180 - sqP * 105)}°`;
      } else if (animType === 'DEADLIFT') {
        // Peso Muerto convencional
        const dlP = progress;
        const hinge = dlP * 52;
        spineY = 46 - hinge * 0.6;
        headY = 82 - hinge * 0.7;
        pelvisY = 5 - dlP * 22;
        pelvisZ = -dlP * 34;

        kneeLX = -18; kneeLY = -38 - dlP * 8; kneeLZ = 10;
        kneeRX = 18; kneeRY = -38 - dlP * 8; kneeRZ = 10;
        footLX = -18; footLY = -95; footLZ = 0;
        footRX = 18; footRY = -95; footRZ = 0;

        const bY = -85 + (1 - dlP) * 80;
        const bZ = 12;
        handLX = -22; handLY = bY; handLZ = bZ;
        handRX = 22; handRY = bY; handRZ = bZ;
        elLX = -24; elLY = (shY + bY) / 2; elLZ = bZ;
        elRX = 24; elRY = (shY + bY) / 2; elRZ = bZ;

        eqType = 'BARBELL';
        eqY1 = bY; eqZ1 = bZ;
        hudAngle.innerText = `Bisagra Cadera: ${'$'}{Math.round(hinge)}° • Extensión Cadena Posterior`;
      } else if (animType === 'OVERHEAD_PRESS') {
        // Press Militar de pie
        const ohpP = progress;
        const bY = 52 + (1 - ohpP) * 48;
        handLX = -22; handLY = bY; handLZ = 2;
        handRX = 22; handRY = bY; handRZ = 2;
        elLX = -26; elLY = 40 + (1 - ohpP) * 35; elLZ = 2;
        elRX = 26; elRY = 40 + (1 - ohpP) * 35; elRZ = 2;

        eqType = 'BARBELL';
        eqY1 = bY; eqZ1 = 2;
        hudAngle.innerText = `Bloqueo Codos: ${'$'}{Math.round((1 - ohpP) * 100)}% • Activación Deltoides Anterior`;
      } else if (animType === 'LATERAL_RAISE') {
        // Elevaciones Laterales estrictas
        const latP = progress;
        const rad = (1 - latP) * 82 * Math.PI / 180;
        handLX = shLeftX - Math.cos(rad) * 44; handLY = shY + Math.sin(rad) * 44 - 35;
        handRX = shRightX + Math.cos(rad) * 44; handRY = shY + Math.sin(rad) * 44 - 35;
        elLX = (shLeftX + handLX) / 2; elLY = (shY + handLY) / 2 + 5;
        elRX = (shRightX + handRX) / 2; elRY = (shY + handRY) / 2 + 5;

        eqType = 'DUMBBELLS';
        hudAngle.innerText = `Abducción Hombros: ${'$'}{Math.round((1 - latP) * 85)}° • Deltoides Lateral`;
      } else if (animType === 'BICEP_CURL') {
        // Curl de Bíceps estricto
        const curlP = progress;
        const flex = (1 - curlP) * 125 * Math.PI / 180;
        elLX = -30; elLY = 28; elLZ = 0;
        elRX = 30; elRY = 28; elRZ = 0;
        handLX = -30; handLY = elLY + Math.sin(flex) * 28; handLZ = Math.cos(flex) * 28;
        handRX = 30; handRY = elRY + Math.sin(flex) * 28; handRZ = Math.cos(flex) * 28;

        eqType = 'BARBELL';
        eqY1 = handLY; eqZ1 = handLZ;
        hudAngle.innerText = `Flexión Bíceps: ${'$'}{Math.round((1 - curlP) * 130)}° • Contracción Máxima Pico`;
      } else if (animType === 'TRICEP_EXTENSION' || animType === 'TRICEP_PUSHDOWN') {
        // Extensión de Tríceps
        const triP = progress;
        elLX = -28; elLY = 32; elLZ = 5;
        elRX = 28; elRY = 32; elRZ = 5;
        const flex = (triP) * 105 * Math.PI / 180;
        handLX = -28; handLY = elLY - Math.cos(flex) * 30; handLZ = elLZ + Math.sin(flex) * 30;
        handRX = 28; handRY = elRY - Math.cos(flex) * 30; handRZ = elRZ + Math.sin(flex) * 30;

        hudAngle.innerText = `Extensión Codo: ${'$'}{Math.round(180 - triP * 100)}° • Bloqueo Cabeza Lateral Tríceps`;
      } else if (animType === 'PULLUP' || animType === 'LAT_PULLDOWN') {
        // Jalón dorsal / Dominadas
        const pullP = progress;
        const pullY = pullP * 38;
        shY = 60 - pullY; spineY = 46 - pullY; pelvisY = 5 - pullY; headY = 82 - pullY;
        const barY = 95;
        handLX = -42; handLY = barY; handLZ = 5;
        handRX = 42; handRY = barY; handRZ = 5;
        elLX = -40; elLY = shY - 10; elLZ = 0;
        elRX = 40; elRY = shY - 10; elRZ = 0;

        queueCylinder(-70, barY, 5, 70, barY, 5, 3, '$barColor');
        hudAngle.innerText = `Apertura Dorsal: ${'$'}{Math.round(pullP * 100)}% • Expansión V-Taper`;
      } else if (animType === 'CALF_RAISE') {
        // Gemelos: elevación sobre puntas de los pies
        const calfRise = (1 - progress) * 24;
        pelvisY += calfRise; spineY += calfRise; headY += calfRise; shY += calfRise;
        footLY = -95 + calfRise * 0.5;
        hudAngle.innerText = `Flexión Plantar: +${'$'}{Math.round(calfRise)}mm • Pico Sóleo y Gastrocnemio`;
      } else {
        hudAngle.innerText = `Tensión Muscular Diáfana: ${'$'}{Math.round(tension * 100)}%`;
      }

      // =========================================================================
      // MODELADO 3D ANATÓMICO MUSCULOSO HUMANO (VOLÚMENES REALISTAS)
      // =========================================================================
      // 1. Cabeza y Cuello con Trapecios
      queueSphere(headX, headY, headZ, 12, jointCol); // Cráneo
      queueCylinder(headX, headY - 10, headZ, spineX, spineY + 16, spineZ, 6, muscCol); // Cuello

      // Trapecios superiores (hombro a cuello)
      queueMuscle(headX - 5, headY - 8, headZ - 2, shLeftX, shY, shZ, 8, isBack ? glowCol : muscCol, isBack);
      queueMuscle(headX + 5, headY - 8, headZ - 2, shRightX, shY, shZ, 8, isBack ? glowCol : muscCol, isBack);

      // 2. Torso V-Taper Muscular
      // Pectorales masivos (Izquierdo y Derecho con separación esternal)
      const pectGlow = isChest && tension > 0.6;
      const pectCol = pectGlow ? glowCol : muscCol;
      queueMuscle(shLeftX + 5, shY - 2, shZ + 6, spineX - 2, spineY + 6, spineZ + 8, 15, pectCol, pectGlow);
      queueMuscle(shRightX - 5, shY - 2, shZ + 6, spineX + 2, spineY + 6, spineZ + 8, 15, pectCol, pectGlow);

      // Dorsales anchos (Lats V-Taper que dan la forma estética en V)
      const latGlow = isBack && tension > 0.6;
      const latCol = latGlow ? glowCol : muscCol;
      queueMuscle(shLeftX + 2, shY - 4, shZ - 4, spineX - 10, spineY - 8, spineZ - 3, 14, latCol, latGlow);
      queueMuscle(shRightX - 2, shY - 4, shZ - 4, spineX + 10, spineY - 8, spineZ - 3, 14, latCol, latGlow);

      // 3. Columna Vertebral y Costillas
      queueCylinder(spineX, spineY + 14, spineZ, spineX, spineY - 4, spineZ, 12, baseCol);

      // 4. Six-Pack Abdominal y Cintura Estrecha
      const absGlow = (isAbs || isWaist) && tension > 0.6;
      const absCol = absGlow ? glowCol : muscCol;
      // Abdomen superior
      queueMuscle(spineX - 6, spineY + 4, spineZ + 7, spineX + 6, spineY + 4, spineZ + 7, 7, absCol, absGlow);
      // Abdomen medio
      queueMuscle(spineX - 5, spineY - 4, spineZ + 6, spineX + 5, spineY - 4, spineZ + 6, 6, absCol, absGlow);
      // Abdomen bajo y transverso
      queueMuscle(spineX - 5, spineY - 12, spineZ + 5, spineX + 5, spineY - 12, spineZ + 5, 6, absCol, absGlow);

      // Oblicuos laterales (Cintura)
      const waistGlow = isWaist && tension > 0.6;
      const waistCol = waistGlow ? glowCol : muscCol;
      queueMuscle(spineX - 13, spineY - 6, spineZ, spineX - 11, pelvisY + 4, pelvisZ, 6, waistCol, waistGlow);
      queueMuscle(spineX + 13, spineY - 6, spineZ, spineX + 11, pelvisY + 4, pelvisZ, 6, waistCol, waistGlow);

      // 5. Glúteos y Caderas Musculosas
      const gluteGlow = isGlutes && tension > 0.6;
      const gluteCol = gluteGlow ? glowCol : muscCol;
      // Masas glúteas izquierda y derecha
      queueSphere(pelvisX - 11, pelvisY, pelvisZ - 7, 13, gluteCol, gluteGlow);
      queueSphere(pelvisX + 11, pelvisY, pelvisZ - 7, 13, gluteCol, gluteGlow);
      // Centro pélvico
      queueCylinder(pelvisX - 12, pelvisY, pelvisZ, pelvisX + 12, pelvisY, pelvisZ, 9, jointCol);

      // 6. Hombros Musculosos (Deltoides en 3D: Frontal, Lateral, Posterior)
      const deltGlow = isShoulder && tension > 0.6;
      const deltCol = deltGlow ? glowCol : muscCol;
      // Deltoide Izquierdo
      queueSphere(shLeftX, shY, shZ, 12, deltCol, deltGlow);
      // Deltoide Derecho
      queueSphere(shRightX, shY, shZ, 12, deltCol, deltGlow);

      // 7. Brazos Atléticos: Bíceps y Tríceps
      const armGlow = (isBiceps || isTriceps) && tension > 0.6;
      const armCol = armGlow ? glowCol : muscCol;
      // Bíceps anterior izquierdo y derecho
      queueMuscle(shLeftX, shY - 4, shZ + 2, elLX, elLY + 2, elLZ + 2, 8, isBiceps ? glowCol : armCol, isBiceps);
      queueMuscle(shRightX, shY - 4, shZ + 2, elRX, elRY + 2, elRZ + 2, 8, isBiceps ? glowCol : armCol, isBiceps);
      // Tríceps posterior
      queueMuscle(shLeftX, shY - 4, shZ - 4, elLX, elLY + 2, elLZ - 4, 9, isTriceps ? glowCol : armCol, isTriceps);
      queueMuscle(shRightX, shY - 4, shZ - 4, elRX, elRY + 2, elRZ - 4, 9, isTriceps ? glowCol : armCol, isTriceps);

      // Codos
      queueSphere(elLX, elLY, elLZ, 6, jointCol);
      queueSphere(elRX, elRY, elRZ, 6, jointCol);

      // Antebrazos musculosos (braquiorradial)
      queueMuscle(elLX, elLY, elLZ, handLX, handLY, handLZ, 7, muscCol);
      queueMuscle(elRX, elRY, elRZ, handRX, handRY, handRZ, 7, muscCol);
      queueSphere(handLX, handLY, handLZ, 5, jointCol);
      queueSphere(handRX, handRY, handRZ, 5, jointCol);

      // 8. Piernas Musculosas: Cuádriceps (Vasto externo, vasto medial 'gota', recto femoral)
      const quadGlow = isPiernas && tension > 0.6;
      const quadCol = quadGlow ? glowCol : muscCol;

      // Muslo Izquierdo (Masa de cuádriceps gruesa)
      queueMuscle(pelvisX - 12, pelvisY, pelvisZ + 3, kneeLX, kneeLY + 6, kneeLZ + 3, 14, quadCol, quadGlow);
      // Vasto lateral externo
      queueMuscle(pelvisX - 16, pelvisY, pelvisZ, kneeLX - 3, kneeLY + 8, kneeLZ, 11, quadCol, quadGlow);

      // Muslo Derecho
      queueMuscle(pelvisX + 12, pelvisY, pelvisZ + 3, kneeRX, kneeRY + 6, kneeRZ + 3, 14, quadCol, quadGlow);
      queueMuscle(pelvisX + 16, pelvisY, pelvisZ, kneeRX + 3, kneeRY + 8, kneeRZ, 11, quadCol, quadGlow);

      // Rodillas articuladas
      queueSphere(kneeLX, kneeLY, kneeLZ, 7, jointCol);
      queueSphere(kneeRX, kneeRY, kneeRZ, 7, jointCol);

      // 9. Pantorrillas Musculosas en Diamante (Gastrocnemio y Sóleo)
      const calfGlow = targetGroup.includes('Gemelos') && tension > 0.6;
      const calfCol = calfGlow ? glowCol : muscCol;
      // Pantorrilla izquierda (volumen en diamante superior)
      queueMuscle(kneeLX, kneeLY - 2, kneeLZ - 2, footLX, footLY + 18, footLZ - 2, 10, calfCol, calfGlow);
      // Tendón de Aquiles estrecho
      queueCylinder(footLX, footLY + 18, footLZ - 2, footLX, footLY, footLZ, 5, baseCol);

      // Pantorrilla derecha
      queueMuscle(kneeRX, kneeRY - 2, kneeRZ - 2, footRX, footRY + 18, footRZ - 2, 10, calfCol, calfGlow);
      queueCylinder(footRX, footRY + 18, footRZ - 2, footRX, footRY, footRZ, 5, baseCol);

      // Pies firmemente plantados
      queueCylinder(footLX, footLY, footLZ - 8, footLX, footLY, footLZ + 16, 6, baseCol);
      queueCylinder(footRX, footRY, footRZ - 8, footRX, footRY, footRZ + 16, 6, baseCol);

      // =========================================================================
      // EQUIPAMIENTO RENDERIZADO EN 3D
      // =========================================================================
      if (eqType === 'BARBELL') {
        const barLen = 75;
        queueCylinder(-barLen, eqY1, eqZ1, barLen, eqY1, eqZ1, 4, '$barColor');
        // Discos olímpicos a los lados
        queueCylinder(-58, eqY1, eqZ1, -54, eqY1, eqZ1, 20, '$plateColor');
        queueCylinder(-53, eqY1, eqZ1, -49, eqY1, eqZ1, 18, '$plateColor');
        queueCylinder(49, eqY1, eqZ1, 53, eqY1, eqZ1, 18, '$plateColor');
        queueCylinder(54, eqY1, eqZ1, 58, eqY1, eqZ1, 20, '$plateColor');
      } else if (eqType === 'DUMBBELLS') {
        // Mancuerna Izquierda
        queueCylinder(handLX - 2, handLY - 12, handLZ, handLX - 2, handLY + 12, handLZ, 3, '$barColor');
        queueSphere(handLX - 2, handLY - 12, handLZ, 9, '$plateColor');
        queueSphere(handLX - 2, handLY + 12, handLZ, 9, '$plateColor');
        // Mancuerna Derecha
        queueCylinder(handRX + 2, handRY - 12, handRZ, handRX + 2, handRY + 12, handRZ, 3, '$barColor');
        queueSphere(handRX + 2, handRY - 12, handRZ, 9, '$plateColor');
        queueSphere(handRX + 2, handRY + 12, handRZ, 9, '$plateColor');
      }

      // Ordenar elementos según profundidad Z (Painter's algorithm para 3D limpio)
      renderQueue.sort((a, b) => b.depth - a.depth);

      for (let i = 0; i < renderQueue.length; i++) {
        const item = renderQueue[i];
        if (item.type === 'sphere') {
          ctx.beginPath();
          ctx.arc(item.x, item.y, Math.max(item.r, 1), 0, Math.PI * 2);
          ctx.fillStyle = item.color;
          if (item.glow) {
            ctx.shadowColor = '#F59E0B';
            ctx.shadowBlur = 16;
          } else {
            ctx.shadowBlur = 0;
          }
          ctx.fill();
        } else if (item.type === 'muscle' || item.type === 'cylinder') {
          ctx.beginPath();
          ctx.moveTo(item.x1, item.y1);
          ctx.lineTo(item.x2, item.y2);
          ctx.strokeStyle = item.color;
          ctx.lineWidth = Math.max(item.w, 1);
          ctx.lineCap = 'round';
          if (item.glow) {
            ctx.shadowColor = '#F59E0B';
            ctx.shadowBlur = 18;
          } else {
            ctx.shadowBlur = 0;
          }
          ctx.stroke();
        } else if (item.type === 'line') {
          ctx.beginPath();
          ctx.moveTo(item.x1, item.y1);
          ctx.lineTo(item.x2, item.y2);
          ctx.strokeStyle = item.color;
          ctx.lineWidth = item.w;
          ctx.shadowBlur = 0;
          ctx.stroke();
        }
      }

      ctx.shadowBlur = 0;
      requestAnimationFrame(loop);
    }

    requestAnimationFrame(loop);
  </script>
</body>
</html>
  """.trimIndent()
}
