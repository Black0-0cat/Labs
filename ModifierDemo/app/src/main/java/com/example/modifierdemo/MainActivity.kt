package com.example.modifierdemo // Убедитесь, что этот пакет совпадает с вашей первой строчкой!

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.modifierdemo.R
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modifierdemo.ui.theme.ModifierDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModifierDemoTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Передаем модификатор с системными отступами SafeArea
                    DemoScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    // 1. Первый модификатор: рамка + внутренний отступ
    val mymodifier = Modifier
        .border(width = 2.dp, color = Color.Black)
        .padding(all = 10.dp)

    // 2. Второй модификатор: фиксированная высота
    val secondModifier = Modifier.height(100.dp)

    // Объединяем входящий modifier (от Scaffold) с паддингом в 20dp для Column
    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Демонстрация сочетания модификаторов через .then()
        Text(
            text = "Hello Compose",
            modifier = mymodifier.then(secondModifier),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Вызов нашего CustomImage с модификатором: отступы, ширина и скругление углов
        CustomImage(
            image = R.drawable.vacation,
            modifier = Modifier
                .padding(16.dp)
                .width(270.dp)
                .clip(shape = RoundedCornerShape(30.dp))
        )
    }
}

// Кастомный компонент с поддержкой Modifier
@Composable
fun CustomImage(image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = modifier // Исправлена синтаксическая ошибка методички
    )
}

// Предварительный просмотр
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ModifierDemoTheme() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            DemoScreen(Modifier.padding(innerPadding))
        }
    }
}
