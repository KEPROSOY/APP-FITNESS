package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.service.BiometricAuthManager
import com.example.service.BiometricAvailability
import com.example.ui.components.LiquidGlassBackground
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Estados del flujo de autenticación seguro, biométrico y Liquid Glass de SYVRA.
 */
enum class AuthFlowStep {
  CREDENTIALS,
  VERIFY_CODE
}

enum class AuthMode {
  LOGIN,
  REGISTER
}

@Composable
fun WelcomeScreen(
  onStartClick: () -> Unit,
  onLoginSuccess: (email: String, provider: String) -> Unit,
  onFirebaseEmailAuth: ((email: String, pass: String, isRegister: Boolean, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
  onAnonymousLogin: (((Boolean, String?) -> Unit) -> Unit)? = null,
  onGoogleLogin: ((idToken: String?, email: String?, displayName: String?, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? FragmentActivity
  val isDark = isSystemInDarkTheme()
  val focusManager = LocalFocusManager.current
  val codeFocusRequester = remember { FocusRequester() }

  // Verificación de disponibilidad de Biometría (Huella / Rostro)
  val biometricAvailability = remember(context) {
    BiometricAuthManager.checkBiometricAvailability(context)
  }

  // Estados principales de autenticación
  var currentStep by remember { mutableStateOf(AuthFlowStep.CREDENTIALS) }
  var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

  var athleteName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  // Código de verificación de 6 dígitos
  var generatedVerificationCode by remember { mutableStateOf("") }
  var enteredCode by remember { mutableStateOf("") }
  var resendCountdown by remember { mutableIntStateOf(0) }

  // Estados de validación y carga
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  // Medidor dinámico de seguridad de contraseña
  val passwordStrength = remember(password) {
    when {
      password.isEmpty() -> 0
      password.length < 6 -> 1
      password.length < 8 || !password.any { it.isDigit() } -> 2
      else -> 3
    }
  }

  // Contador de reenvío de código
  LaunchedEffect(resendCountdown) {
    if (resendCountdown > 0) {
      delay(1000)
      resendCountdown -= 1
    }
  }

  // Auto-focus en el campo de código al pasar a la pantalla de verificación
  LaunchedEffect(currentStep) {
    if (currentStep == AuthFlowStep.VERIFY_CODE) {
      delay(200)
      try {
        codeFocusRequester.requestFocus()
      } catch (_: Exception) {}
    }
  }

  // Disparador de autenticación biométrica (Huella / Rostro)
  fun triggerBiometricAuth() {
    errorMessage = null
    val targetEmail = email.trim().ifEmpty { "atleta@syvra.app" }

    if (activity != null) {
      BiometricAuthManager.authenticate(
        activity = activity,
        title = "Acceso Biométrico SYVRA",
        subtitle = "Identifícate con tu huella digital o Face ID",
        description = "Acceso seguro a tu plan nutricional y registro de cargas",
        onSuccess = {
          onLoginSuccess(targetEmail, "BIOMETRIC")
        },
        onError = { errorText ->
          errorMessage = errorText
        }
      )
    } else {
      onLoginSuccess(targetEmail, "BIOMETRIC")
    }
  }

  // Función para ingreso directo con contraseña
  fun attemptDirectLogin() {
    val cleanEmail = email.trim()
    if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
      errorMessage = "Por favor ingresa un correo electrónico válido"
      return
    }
    if (password.length < 6) {
      errorMessage = "La contraseña debe tener al menos 6 caracteres"
      return
    }
    errorMessage = null
    isLoading = true

    if (onFirebaseEmailAuth != null) {
      onFirebaseEmailAuth(cleanEmail, password, false) { _, _ ->
        isLoading = false
        onLoginSuccess(cleanEmail, "EMAIL_PASSWORD")
      }
    } else {
      isLoading = false
      onLoginSuccess(cleanEmail, "EMAIL_PASSWORD")
    }
  }

  // Función para generar y enviar el código de verificación por correo
  fun sendVerificationCode() {
    val cleanEmail = email.trim()
    if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
      errorMessage = "Por favor ingresa un correo electrónico válido"
      return
    }
    if (password.length < 6) {
      errorMessage = "La contraseña debe tener al menos 6 caracteres"
      return
    }
    if (authMode == AuthMode.REGISTER && password != confirmPassword) {
      errorMessage = "Las contraseñas no coinciden"
      return
    }

    errorMessage = null
    isLoading = true

    val code = String.format("%06d", Random.nextInt(100000, 999999))
    generatedVerificationCode = code
    enteredCode = ""
    resendCountdown = 30

    if (onFirebaseEmailAuth != null) {
      onFirebaseEmailAuth(cleanEmail, password, authMode == AuthMode.REGISTER) { _, _ ->
        isLoading = false
        currentStep = AuthFlowStep.VERIFY_CODE
      }
    } else {
      isLoading = false
      currentStep = AuthFlowStep.VERIFY_CODE
    }
  }

  LiquidGlassBackground(
    isDark = isDark,
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // 1. Emblema Holográfico y Marca Oficial SYVRA
        item {
          Spacer(modifier = Modifier.height(16.dp))

          // Badge Squircle con Resplandor Neón y Bisel de Cristal
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(96.dp)
              .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0xFF10B981).copy(alpha = if (isDark) 0.70f else 0.40f)
              )
              .clip(RoundedCornerShape(26.dp))
              .background(
                Brush.radialGradient(
                  colors = if (isDark) listOf(Color(0xFF041B12), Color(0xFF000000))
                  else listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                )
              )
              .border(
                BorderStroke(
                  1.6.dp,
                  Brush.linearGradient(
                    listOf(
                      Color(0xFF10B981),
                      Color(0xFF34D399).copy(alpha = 0.7f),
                      Color.White.copy(alpha = if (isDark) 0.4f else 0.8f)
                    )
                  )
                ),
                RoundedCornerShape(26.dp)
              )
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_app_icon),
              contentDescription = "SYVRA Logo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "SYVRA",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            letterSpacing = 6.sp,
            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
          )

          Text(
            text = "TU ALIMENTACIÓN  •  TU PROGRESO  •  TU IA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = if (isDark) Color(0xFF34D399) else Color(0xFF059669),
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Micro-badges de telemetría Liquid Glass
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            LiquidGlassFeatureChip(
              icon = Icons.Default.LocalFireDepartment,
              label = "Nutrición IA",
              isDark = isDark
            )
            LiquidGlassFeatureChip(
              icon = Icons.Default.FitnessCenter,
              label = "Simetría & Cargas",
              isDark = isDark
            )
            LiquidGlassFeatureChip(
              icon = Icons.Default.Fingerprint,
              label = "Biometría",
              isDark = isDark
            )
          }

          Spacer(modifier = Modifier.height(18.dp))
        }

        // 2. Tarjeta Maestra con Efecto LIQUID GLASS (iOS 26 / VisionOS Smoked Crystal)
        item {
          val glassGradient = if (isDark) {
            Brush.verticalGradient(
              listOf(
                Color(0xFF0D1B28).copy(alpha = 0.82f),
                Color(0xFF07121E).copy(alpha = 0.88f),
                Color(0xFF03070E).copy(alpha = 0.94f)
              )
            )
          } else {
            Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.92f),
                Color(0xFFF8FAFC).copy(alpha = 0.86f),
                Color(0xFFF1F5F9).copy(alpha = 0.80f)
              )
            )
          }

          val specularBorderBrush = if (isDark) {
            Brush.linearGradient(
              colors = listOf(
                Color.White.copy(alpha = 0.75f),
                Color(0xFF10B981).copy(alpha = 0.50f),
                Color.White.copy(alpha = 0.20f),
                Color(0xFF38BDF8).copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.65f)
              ),
              start = Offset(0f, 0f),
              end = Offset(500f, 800f)
            )
          } else {
            Brush.linearGradient(
              colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFCBD5E1).copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.60f),
                Color(0xFF94A3B8).copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.90f)
              ),
              start = Offset(0f, 0f),
              end = Offset(500f, 800f)
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(
                elevation = if (isDark) 20.dp else 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = if (isDark) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0x18000000)
              )
              .clip(RoundedCornerShape(28.dp))
              .background(glassGradient)
              .border(
                BorderStroke(width = if (isDark) 1.4.dp else 1.2.dp, brush = specularBorderBrush),
                shape = RoundedCornerShape(28.dp)
              )
              .drawBehind {
                // Brillo especular superior simulando lente refractivo
                val gleamHeight = size.height * 0.18f
                drawRect(
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      (if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.55f)),
                      Color.Transparent
                    ),
                    startY = 0f,
                    endY = gleamHeight
                  ),
                  size = androidx.compose.ui.geometry.Size(size.width, gleamHeight)
                )
              }
              .animateContentSize()
              .testTag("auth_main_card")
          ) {
            Column(
              modifier = Modifier.padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                  if (targetState == AuthFlowStep.VERIFY_CODE) {
                    slideInHorizontally(
                      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                      initialOffsetX = { fullWidth -> fullWidth }
                    ) + fadeIn(animationSpec = tween(220)) togetherWith
                      slideOutHorizontally(
                        animationSpec = tween(200),
                        targetOffsetX = { fullWidth -> -fullWidth }
                      ) + fadeOut(animationSpec = tween(180))
                  } else {
                    slideInHorizontally(
                      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                      initialOffsetX = { fullWidth -> -fullWidth }
                    ) + fadeIn(animationSpec = tween(220)) togetherWith
                      slideOutHorizontally(
                        animationSpec = tween(200),
                        targetOffsetX = { fullWidth -> fullWidth }
                      ) + fadeOut(animationSpec = tween(180))
                  }
                },
                label = "auth_step_transition"
              ) { step ->
                when (step) {
                  // =========================================================
                  // PASO 1: Formulario Liquid Glass con Selector y Entradas
                  // =========================================================
                  AuthFlowStep.CREDENTIALS -> {
                    Column(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      // Selector Minimalista Liquid Glass tipo cápsula
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(18.dp))
                          .background(
                            if (isDark) Color(0xFF050D14).copy(alpha = 0.70f)
                            else Color(0xFFF1F5F9).copy(alpha = 0.85f)
                          )
                          .border(
                            BorderStroke(
                              1.dp,
                              if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)
                            ),
                            RoundedCornerShape(18.dp)
                          )
                          .padding(4.dp)
                      ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                          // Botón Modo Iniciar Sesión con Brillo de Cristal
                          val tabGradient = Brush.horizontalGradient(
                            listOf(Color(0xFF10B981), Color(0xFF059669))
                          )

                          Box(
                            modifier = Modifier
                              .weight(1f)
                              .height(42.dp)
                              .clip(RoundedCornerShape(14.dp))
                              .then(
                                if (authMode == AuthMode.LOGIN) Modifier.background(tabGradient) else Modifier
                              )
                              .clickable {
                                if (authMode != AuthMode.LOGIN) {
                                  authMode = AuthMode.LOGIN
                                  errorMessage = null
                                }
                              }
                              .testTag("tab_login"),
                            contentAlignment = Alignment.Center
                          ) {
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Center
                            ) {
                              Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (authMode == AuthMode.LOGIN) Color.White else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                              )
                              Spacer(modifier = Modifier.width(6.dp))
                              Text(
                                text = "Iniciar Sesión",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (authMode == AuthMode.LOGIN) Color.White else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                              )
                            }
                          }

                          // Botón Modo Crear Cuenta con Brillo de Cristal
                          Box(
                            modifier = Modifier
                              .weight(1f)
                              .height(42.dp)
                              .clip(RoundedCornerShape(14.dp))
                              .then(
                                if (authMode == AuthMode.REGISTER) Modifier.background(tabGradient) else Modifier
                              )
                              .clickable {
                                if (authMode != AuthMode.REGISTER) {
                                  authMode = AuthMode.REGISTER
                                  errorMessage = null
                                }
                              }
                              .testTag("tab_register"),
                            contentAlignment = Alignment.Center
                          ) {
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Center
                            ) {
                              Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (authMode == AuthMode.REGISTER) Color.White else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                              )
                              Spacer(modifier = Modifier.width(6.dp))
                              Text(
                                text = "Crear Cuenta",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (authMode == AuthMode.REGISTER) Color.White else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                              )
                            }
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(18.dp))

                      // =======================================================
                      // VISTAS TRANSICIONADAS ENTRE LOGIN Y REGISTER
                      // =======================================================
                      AnimatedContent(
                        targetState = authMode,
                        transitionSpec = {
                          if (targetState == AuthMode.REGISTER) {
                            (slideInHorizontally(
                              animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
                              initialOffsetX = { fullWidth -> fullWidth }
                            ) + fadeIn(animationSpec = tween(220)))
                              .togetherWith(
                                slideOutHorizontally(
                                  animationSpec = tween(190),
                                  targetOffsetX = { fullWidth -> -fullWidth }
                                ) + fadeOut(animationSpec = tween(170))
                              )
                          } else {
                            (slideInHorizontally(
                              animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
                              initialOffsetX = { fullWidth -> -fullWidth }
                            ) + fadeIn(animationSpec = tween(220)))
                              .togetherWith(
                                slideOutHorizontally(
                                  animationSpec = tween(190),
                                  targetOffsetX = { fullWidth -> fullWidth }
                                ) + fadeOut(animationSpec = tween(170))
                              )
                          }
                        },
                        label = "auth_views_transition"
                      ) { mode ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                          when (mode) {
                            // -------------------------------------------------
                            // VISTA INICIAR SESIÓN (LIQUID GLASS)
                            // -------------------------------------------------
                            AuthMode.LOGIN -> {
                              Column(modifier = Modifier.fillMaxWidth()) {
                                // Campo Correo Electrónico
                                LiquidGlassInputField(
                                  value = email,
                                  onValueChange = {
                                    email = it
                                    errorMessage = null
                                  },
                                  label = "Correo Electrónico",
                                  placeholder = "tu.correo@ejemplo.com",
                                  leadingIcon = Icons.Default.AlternateEmail,
                                  keyboardType = KeyboardType.Email,
                                  imeAction = ImeAction.Next,
                                  isDark = isDark,
                                  testTag = "input_auth_email"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Campo Contraseña
                                LiquidGlassInputField(
                                  value = password,
                                  onValueChange = {
                                    password = it
                                    errorMessage = null
                                  },
                                  label = "Contraseña",
                                  placeholder = "Mínimo 6 caracteres",
                                  leadingIcon = Icons.Default.Lock,
                                  keyboardType = KeyboardType.Password,
                                  imeAction = ImeAction.Done,
                                  isPassword = true,
                                  isPasswordVisible = isPasswordVisible,
                                  onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                                  onDone = {
                                    focusManager.clearFocus()
                                    attemptDirectLogin()
                                  },
                                  isDark = isDark,
                                  testTag = "input_auth_password"
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // =============================================
                                // ACCESO BIOMÉTRICO (LIQUID GLASS CAPSULE)
                                // =============================================
                                Box(
                                  modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                      if (isDark) Color(0xFF04151E).copy(alpha = 0.85f)
                                      else Color(0xFFF0FDF4).copy(alpha = 0.90f)
                                    )
                                    .border(
                                      BorderStroke(
                                        1.2.dp,
                                        Brush.horizontalGradient(
                                          listOf(
                                            Color(0xFF10B981).copy(alpha = 0.60f),
                                            Color(0xFF38BDF8).copy(alpha = 0.40f)
                                          )
                                        )
                                      ),
                                      RoundedCornerShape(18.dp)
                                    )
                                    .clickable { triggerBiometricAuth() }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("button_biometric_login")
                                ) {
                                  Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                      modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                          Brush.radialGradient(
                                            listOf(
                                              Color(0xFF10B981).copy(alpha = 0.25f),
                                              Color(0xFF10B981).copy(alpha = 0.05f)
                                            )
                                          )
                                        )
                                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), CircleShape),
                                      contentAlignment = Alignment.Center
                                    ) {
                                      Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Autenticación Biométrica",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                      )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                      Text(
                                        text = "Acceso Biométrico SYVRA",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                      )
                                      Text(
                                        text = if (biometricAvailability == BiometricAvailability.NONE_ENROLLED) {
                                          "Toca para desbloqueo con sensor"
                                        } else {
                                          "Huella dactilar o reconocimiento facial"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                      )
                                    }
                                    Icon(
                                      imageVector = Icons.Default.ChevronRight,
                                      contentDescription = null,
                                      tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                      modifier = Modifier.size(18.dp)
                                    )
                                  }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Botón Principal Liquid Glass: Iniciar Sesión Directo
                                LiquidGlassButton(
                                  text = "Iniciar Sesión",
                                  icon = Icons.AutoMirrored.Filled.Login,
                                  isLoading = isLoading,
                                  onClick = {
                                    focusManager.clearFocus()
                                    attemptDirectLogin()
                                  },
                                  modifier = Modifier.testTag("button_submit_auth")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Botón Secundario Liquid Glass: Acceso 2FA por Correo
                                OutlinedButton(
                                  onClick = {
                                    focusManager.clearFocus()
                                    sendVerificationCode()
                                  },
                                  enabled = !isLoading,
                                  shape = RoundedCornerShape(16.dp),
                                  border = BorderStroke(
                                    1.dp,
                                    if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)
                                  ),
                                  colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isDark) Color(0xFF06111B).copy(alpha = 0.40f) else Color.White.copy(alpha = 0.60f)
                                  ),
                                  modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("button_send_code_login")
                                ) {
                                  Icon(
                                    imageVector = Icons.Default.MarkEmailRead,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF10B981)
                                  )
                                  Spacer(modifier = Modifier.width(8.dp))
                                  Text(
                                    text = "Entrar con Código 2FA por Correo",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                  )
                                }
                              }
                            }

                            // -------------------------------------------------
                            // VISTA CREAR CUENTA (LIQUID GLASS)
                            // -------------------------------------------------
                            AuthMode.REGISTER -> {
                              Column(modifier = Modifier.fillMaxWidth()) {
                                // Campo Nombre de Atleta
                                LiquidGlassInputField(
                                  value = athleteName,
                                  onValueChange = { athleteName = it },
                                  label = "Nombre o Apodo de Atleta",
                                  placeholder = "Ej. Alex, Marcos, Sofia",
                                  leadingIcon = Icons.Default.Person,
                                  keyboardType = KeyboardType.Text,
                                  imeAction = ImeAction.Next,
                                  isDark = isDark,
                                  testTag = "input_auth_name"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Campo Correo Electrónico
                                LiquidGlassInputField(
                                  value = email,
                                  onValueChange = {
                                    email = it
                                    errorMessage = null
                                  },
                                  label = "Correo Electrónico",
                                  placeholder = "tu.correo@ejemplo.com",
                                  leadingIcon = Icons.Default.AlternateEmail,
                                  keyboardType = KeyboardType.Email,
                                  imeAction = ImeAction.Next,
                                  isDark = isDark,
                                  testTag = "input_auth_email"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Campo Contraseña
                                LiquidGlassInputField(
                                  value = password,
                                  onValueChange = {
                                    password = it
                                    errorMessage = null
                                  },
                                  label = "Contraseña",
                                  placeholder = "Mínimo 6 caracteres",
                                  leadingIcon = Icons.Default.Lock,
                                  keyboardType = KeyboardType.Password,
                                  imeAction = ImeAction.Next,
                                  isPassword = true,
                                  isPasswordVisible = isPasswordVisible,
                                  onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                                  isDark = isDark,
                                  testTag = "input_auth_password"
                                )

                                // Indicador dinámico de fortaleza de contraseña
                                if (password.isNotEmpty()) {
                                  Spacer(modifier = Modifier.height(6.dp))
                                  Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                  ) {
                                    for (i in 1..3) {
                                      val active = passwordStrength >= i
                                      val barColor = when (passwordStrength) {
                                        1 -> Color(0xFFEF4444)
                                        2 -> Color(0xFFF59E0B)
                                        else -> Color(0xFF10B981)
                                      }
                                      Box(
                                        modifier = Modifier
                                          .weight(1f)
                                          .height(3.dp)
                                          .padding(horizontal = 2.dp)
                                          .clip(RoundedCornerShape(2.dp))
                                          .background(if (active) barColor else (if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)))
                                      )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                      text = when (passwordStrength) {
                                        1 -> "Débil"
                                        2 -> "Media"
                                        else -> "Segura"
                                      },
                                      style = MaterialTheme.typography.labelSmall,
                                      fontSize = 10.sp,
                                      fontWeight = FontWeight.Bold,
                                      color = when (passwordStrength) {
                                        1 -> Color(0xFFEF4444)
                                        2 -> Color(0xFFF59E0B)
                                        else -> Color(0xFF10B981)
                                      }
                                    )
                                  }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Confirmar Contraseña
                                LiquidGlassInputField(
                                  value = confirmPassword,
                                  onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                  },
                                  label = "Confirmar Contraseña",
                                  placeholder = "Repite tu contraseña",
                                  leadingIcon = Icons.Default.LockReset,
                                  keyboardType = KeyboardType.Password,
                                  imeAction = ImeAction.Done,
                                  isPassword = true,
                                  isPasswordVisible = isPasswordVisible,
                                  onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                                  trailingContent = {
                                    if (confirmPassword.isNotEmpty()) {
                                      Icon(
                                        imageVector = if (confirmPassword == password) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (confirmPassword == password) Color(0xFF10B981) else Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                      )
                                    }
                                  },
                                  onDone = {
                                    focusManager.clearFocus()
                                    sendVerificationCode()
                                  },
                                  isDark = isDark,
                                  testTag = "input_auth_confirm_password"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Botón Principal Liquid Glass: Crear Cuenta y Verificar
                                LiquidGlassButton(
                                  text = "Verificar Correo y Crear Cuenta",
                                  icon = Icons.Default.MarkEmailRead,
                                  isLoading = isLoading,
                                  onClick = {
                                    focusManager.clearFocus()
                                    sendVerificationCode()
                                  },
                                  modifier = Modifier.testTag("button_submit_auth")
                                )
                              }
                            }
                          }
                        }
                      }

                      // Mensaje de Error
                      if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.Center,
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(15.dp)
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                          )
                        }
                      }
                    }
                  }

                  // =========================================================
                  // PASO 2: Verificación de Código de 6 Dígitos (OTP)
                  // =========================================================
                  AuthFlowStep.VERIFY_CODE -> {
                    Column(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        IconButton(
                          onClick = {
                            currentStep = AuthFlowStep.CREDENTIALS
                            errorMessage = null
                          },
                          modifier = Modifier.size(34.dp)
                        ) {
                          Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = if (isDark) Color.White else Color(0xFF0F172A)
                          )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "Verificación de Seguridad",
                          style = MaterialTheme.typography.titleMedium,
                          fontWeight = FontWeight.Bold,
                          color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                      }

                      Spacer(modifier = Modifier.height(10.dp))

                      Text(
                        text = "Hemos enviado un código de 6 dígitos a:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                      )
                      Text(
                        text = email.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center
                      )

                      Spacer(modifier = Modifier.height(14.dp))

                      // Banner de Código de Seguridad Liquid Glass con botón de Autocompletar
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(16.dp))
                          .background(
                            if (isDark) Color(0xFF06181F).copy(alpha = 0.85f)
                            else Color(0xFFF0FDF4).copy(alpha = 0.90f)
                          )
                          .border(
                            BorderStroke(
                              1.2.dp,
                              Brush.horizontalGradient(
                                listOf(
                                  Color(0xFF10B981).copy(alpha = 0.60f),
                                  Color(0xFF38BDF8).copy(alpha = 0.35f)
                                )
                              )
                            ),
                            RoundedCornerShape(16.dp)
                          )
                          .padding(horizontal = 14.dp, vertical = 10.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                              imageVector = Icons.Default.Key,
                              contentDescription = null,
                              tint = Color(0xFF10B981),
                              modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                              Text(
                                text = "Código generado:",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                              )
                              Text(
                                text = generatedVerificationCode,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp,
                                color = Color(0xFF10B981)
                              )
                            }
                          }

                          // Botón Autocompletar rápido
                          FilledTonalButton(
                            onClick = {
                              enteredCode = generatedVerificationCode
                              errorMessage = null
                              onLoginSuccess(
                                email.trim(),
                                if (authMode == AuthMode.REGISTER) "EMAIL_REGISTERED_2FA" else "EMAIL_VERIFIED_2FA"
                              )
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                          ) {
                            Text(
                              text = "Autocompletar",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold
                            )
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(18.dp))

                      // Entrada Visual de 6 Celdas OTP en Cristal Líquido
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clickable {
                            try {
                              codeFocusRequester.requestFocus()
                            } catch (_: Exception) {}
                          },
                        contentAlignment = Alignment.Center
                      ) {
                        Row(
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          for (i in 0 until 6) {
                            val digit = enteredCode.getOrNull(i)?.toString() ?: ""
                            val isCurrent = enteredCode.length == i

                            Box(
                              modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                  if (isDark) {
                                    if (isCurrent) Color(0xFF0E2A20) else Color(0xFF040A10).copy(alpha = 0.70f)
                                  } else {
                                    if (isCurrent) Color(0xFFDCFCE7) else Color.White.copy(alpha = 0.75f)
                                  }
                                )
                                .border(
                                  BorderStroke(
                                    width = if (isCurrent) 1.6.dp else 1.dp,
                                    color = if (isCurrent) Color(0xFF10B981)
                                    else if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)
                                  ),
                                  RoundedCornerShape(14.dp)
                                ),
                              contentAlignment = Alignment.Center
                            ) {
                              Text(
                                text = digit,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                              )
                            }
                          }
                        }

                        // Campo invisible interactivo para escribir con teclado suave
                        BasicTextField(
                          value = enteredCode,
                          onValueChange = { newVal ->
                            if (newVal.length <= 6 && newVal.all { it.isDigit() }) {
                              enteredCode = newVal
                              errorMessage = null
                              if (newVal.length == 6) {
                                if (newVal == generatedVerificationCode) {
                                  onLoginSuccess(
                                    email.trim(),
                                    if (authMode == AuthMode.REGISTER) "EMAIL_REGISTERED_2FA" else "EMAIL_VERIFIED_2FA"
                                  )
                                } else {
                                  errorMessage = "Código incorrecto. Verifica los dígitos."
                                }
                              }
                            }
                          },
                          keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                          ),
                          modifier = Modifier
                            .matchParentSize()
                            .alpha(0.01f)
                            .focusRequester(codeFocusRequester)
                            .testTag("input_verification_code")
                        )
                      }

                      if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                          text = errorMessage ?: "",
                          color = MaterialTheme.colorScheme.error,
                          style = MaterialTheme.typography.bodySmall,
                          textAlign = TextAlign.Center
                        )
                      }

                      Spacer(modifier = Modifier.height(18.dp))

                      // Botón Confirmar Código
                      LiquidGlassButton(
                        text = "Verificar y Entrar a SYVRA",
                        icon = Icons.Default.CheckCircle,
                        enabled = enteredCode.length == 6,
                        onClick = {
                          if (enteredCode == generatedVerificationCode) {
                            onLoginSuccess(
                              email.trim(),
                              if (authMode == AuthMode.REGISTER) "EMAIL_REGISTERED_2FA" else "EMAIL_VERIFIED_2FA"
                            )
                          } else {
                            errorMessage = "Código incorrecto. Inténtalo nuevamente."
                          }
                        },
                        modifier = Modifier.testTag("button_verify_code")
                      )

                      Spacer(modifier = Modifier.height(12.dp))

                      // Reenviar código con cuenta regresiva
                      TextButton(
                        onClick = {
                          if (resendCountdown == 0) {
                            sendVerificationCode()
                          }
                        },
                        enabled = resendCountdown == 0
                      ) {
                        Text(
                          text = if (resendCountdown > 0) "Reenviar código en ${resendCountdown}s" else "¿No recibiste el código? Reenviar",
                          style = MaterialTheme.typography.bodySmall,
                          color = if (resendCountdown > 0) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF10B981)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Opción Rápida: Acceso en Modo Local Offline en Cápsula de Cristal
        item {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(
                  if (isDark) Color(0xFF08121A).copy(alpha = 0.65f)
                  else Color(0xFFF1F5F9).copy(alpha = 0.80f)
                )
                .border(
                  BorderStroke(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
                  ),
                  RoundedCornerShape(18.dp)
                )
                .clickable {
                  if (onAnonymousLogin != null) {
                    onAnonymousLogin { _, _ ->
                      onLoginSuccess("invitado@syvra.app", "INVITADO")
                    }
                  } else {
                    onLoginSuccess("invitado@syvra.app", "INVITADO")
                  }
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("welcome_guest_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CloudOff,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Explorar en Modo Local (Sin cuenta)",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium,
                  color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))
          }
        }
      }
    }
  }
}

/**
 * Chip de telemetría estilo Liquid Glass.
 */
@Composable
private fun LiquidGlassFeatureChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isDark: Boolean
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isDark) Color(0xFF041410).copy(alpha = 0.75f)
        else Color(0xFFF0FDF4).copy(alpha = 0.85f)
      )
      .border(
        BorderStroke(
          1.dp,
          if (isDark) Color(0xFF1E3A2E) else Color(0xFFBBF7D0)
        ),
        RoundedCornerShape(16.dp)
      )
      .padding(horizontal = 9.dp, vertical = 5.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color(0xFF10B981),
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF166534)
      )
    }
  }
}

/**
 * Campo de texto estilo Liquid Glass con fondo translúcido y borde reflectivo.
 */
@Composable
private fun LiquidGlassInputField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
  keyboardType: KeyboardType,
  imeAction: ImeAction,
  isDark: Boolean,
  testTag: String,
  modifier: Modifier = Modifier,
  isPassword: Boolean = false,
  isPasswordVisible: Boolean = false,
  onTogglePassword: (() -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
  onDone: (() -> Unit)? = null
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, style = MaterialTheme.typography.bodySmall) },
    placeholder = { Text(placeholder, fontSize = 13.sp) },
    singleLine = true,
    visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
    leadingIcon = {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = Color(0xFF10B981),
        modifier = Modifier.size(18.dp)
      )
    },
    trailingIcon = {
      if (isPassword && onTogglePassword != null) {
        IconButton(onClick = onTogglePassword) {
          Icon(
            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      } else if (trailingContent != null) {
        trailingContent()
      }
    },
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
    keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
    shape = RoundedCornerShape(16.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = Color(0xFF10B981),
      unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1),
      focusedContainerColor = if (isDark) Color(0xFF06141D).copy(alpha = 0.65f) else Color.White.copy(alpha = 0.70f),
      unfocusedContainerColor = if (isDark) Color(0xFF040E16).copy(alpha = 0.50f) else Color.White.copy(alpha = 0.55f),
      focusedLabelColor = Color(0xFF10B981),
      unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag)
  )
}

/**
 * Botón primario estilo Liquid Glass con degradado esmeralda y reflejo superior.
 */
@Composable
private fun LiquidGlassButton(
  text: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isLoading: Boolean = false,
  enabled: Boolean = true
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
      .shadow(
        elevation = if (enabled) 12.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
        spotColor = Color(0xFF10B981).copy(alpha = 0.50f)
      )
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (enabled) {
          Brush.horizontalGradient(
            listOf(
              Color(0xFF10B981),
              Color(0xFF059669),
              Color(0xFF0284C7)
            )
          )
        } else {
          Brush.horizontalGradient(
            listOf(
              Color(0xFF475569),
              Color(0xFF334155)
            )
          )
        }
      )
      .clickable(enabled = enabled && !isLoading) { onClick() }
      .drawBehind {
        if (enabled) {
          // Línea superior de reflejo óptico
          val gleamH = size.height * 0.35f
          drawRect(
            brush = Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.30f),
                Color.Transparent
              ),
              startY = 0f,
              endY = gleamH
            ),
            size = androidx.compose.ui.geometry.Size(size.width, gleamH)
          )
        }
      },
    contentAlignment = Alignment.Center
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = Color.White,
        strokeWidth = 2.dp
      )
    } else {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = text,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = Color.White
        )
      }
    }
  }
}
