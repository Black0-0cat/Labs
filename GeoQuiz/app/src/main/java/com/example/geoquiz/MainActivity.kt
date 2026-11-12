package com.example.geoquiz


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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeoQuizTheme {
        Greeting("Android")
    }
}

// Модель одного вопроса
data class Question(
    val text: String,
    val answer: Boolean
)

// Список вопросов из лабораторной работы
val questionBank = listOf(
    Question("Canberra is the capital of Australia.", true),
    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
    Question("The source of the Nile River is in Egypt.", false),
    Question("The Amazon River is the longest river in the Americas.", true),
    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoQuizScreen(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    // Индекс текущего вопроса
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questionBank[currentIndex]

    var isAnswered by remember { mutableStateOf(false) } // Ответил ли пользователь на текущий вопрос
    var score by remember { mutableIntStateOf(0) }         // Счётчик правильных ответов

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Текст вопроса
        Text(
            text = currentQuestion.text,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Кнопки TRUE и FALSE
        val checkAnswer: (Boolean) -> Unit = { userAnswer ->
            if (userAnswer == currentQuestion.answer) {
                score++
            }
            isAnswered = true // Прячем кнопки TRUE и FALSE
        }
        if (!isAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { checkAnswer(true) }) {
                    Text("TRUE")
                }
                Button(onClick = { checkAnswer(false) }) {
                    Text("FALSE")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка NEXT >
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { /* Логика добавится на Этапе 4 */ }) {
                Text("NEXT >")
            }
        }
    }
}
