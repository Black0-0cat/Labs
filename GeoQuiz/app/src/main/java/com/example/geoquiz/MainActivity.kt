package com.example.geoquiz // <-- Проверьте совпадение с именем пакета вашего проекта!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geoquiz.ui.theme.GeoQuizTheme
import kotlinx.coroutines.launch

// Мягкая палитра для тёмной темы (без резких контрастов)
val DarkBackground = Color(0xFF121212)      // Основной тёмный фон
val DarkSurface = Color(0xFF1E1E1E)         // Цвет верхней панели
val DarkButton = Color(0xFF333333)          // Цвет активных кнопок
val DarkButtonDisabled = Color(0xFF222222)  // Цвет заблокированной кнопки
val SoftWhiteText = Color(0xFFE0E0E0)       // Мягкий светлый текст
val MutedGrayText = Color(0xFF666666)       // Приглушённый текст для неактивной кнопки

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    containerColor = DarkBackground,
                    topBar = {
                        TopAppBar(
                            title = { Text("GeoQuiz", color = SoftWhiteText) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DarkSurface
                            )
                        )
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    GeoQuizScreen(
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Question(
    val text: String,
    val answer: Boolean
)

val questionBank = listOf(
    Question("Canberra is the capital of Australia.", true),
    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
    Question("The source of the Nile River is in Egypt.", false),
    Question("The Amazon River is the longest river in the Americas.", true),
    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GeoQuizPreview() {
    GeoQuizTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            containerColor = DarkBackground,
            topBar = {
                TopAppBar(
                    title = { Text("GeoQuiz", color = SoftWhiteText) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
                )
            }
        ) { innerPadding ->
            GeoQuizScreen(snackbarHostState, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun GeoQuizScreen(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val currentQuestion = questionBank[currentIndex]
    val isLastQuestion = currentIndex == questionBank.lastIndex

    // Настройка мягких тёмных цветов для кнопок
    val darkButtonColors = ButtonDefaults.buttonColors(
        containerColor = DarkButton,
        contentColor = SoftWhiteText,
        disabledContainerColor = DarkButtonDisabled,
        disabledContentColor = MutedGrayText
    )

    val checkAnswer: (Boolean) -> Unit = { userAnswer ->
        if (userAnswer == currentQuestion.answer) {
            score++
        }
        isAnswered = true

        if (isLastQuestion) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Тест завершён! Правильных ответов: $score из ${questionBank.size}",
                    duration = SnackbarDuration.Indefinite
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = currentQuestion.text,
            color = SoftWhiteText,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Пункт 2: Кнопки скрываются после ответа на текущий вопрос
        if (!isAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { checkAnswer(true) },
                    colors = darkButtonColors
                ) {
                    Text("TRUE")
                }
                Button(
                    onClick = { checkAnswer(false) },
                    colors = darkButtonColors
                ) {
                    Text("FALSE")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Пункт 3: Кнопка Next блокируется и скрывается после ответа на последний вопрос
        if (!(isLastQuestion && isAnswered)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    enabled = isAnswered,
                    onClick = {
                        if (!isLastQuestion) {
                            currentIndex++
                            isAnswered = false
                        }
                    },
                    colors = darkButtonColors
                ) {
                    Text("NEXT >")
                }
            }
        }
    }
}