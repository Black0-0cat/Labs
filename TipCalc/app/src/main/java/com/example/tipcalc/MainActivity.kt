package com.example.tipcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.tipcalc.ui.theme.TipCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ВНИМАНИЕ: Используйте имя ВАШЕЙ темы (например, MyApplicationTheme или TipCalculatorTheme)
            TipCalcTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Вызываем наш главный экран и передаем ему отступы и хост снейкбара
                    TipCalculatorScreen(
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TipCalculatorScreen(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    // Стейты для хранения текста, введенного пользователем
    var totalAmountInput by remember { mutableStateOf("") }

    var dishesCountInput by remember { mutableStateOf("") }

    // Стейт для хранения позиции слайдера
    var tipPercent by remember { mutableFloatStateOf(0f) }

    // Флаг: false — показываем скидку, true — показываем итог (Пункт 7 методички)
    var isTotalCalculated by remember { mutableStateOf(false) }

    // Контейнер, в котором элементы будут выстраиваться друг под другом
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Калькулятор чаевых", fontSize = 24.sp, fontWeight = FontWeight.Bold)


// Преобразуем ввод в числа. Если в полях пусто или буквы — подставится 0.0 или 0
        val totalAmount = totalAmountInput.toDoubleOrNull() ?: 0.0
        val dishesCount = dishesCountInput.toIntOrNull() ?: 0
        val tipsAmount = calculateTips(totalAmount, tipPercent.toInt())

        val coroutineScope = rememberCoroutineScope() // Нужен для запуска Snackbar

        OutlinedTextField(
            value = totalAmountInput,
            onValueChange = {
                totalAmountInput = it
                isTotalCalculated = false // Если пользователь меняет сумму, сбрасываем экран в режим "Скидка"
            },
            label = { Text("Сумма заказа:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Только цифровая клавиатура
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dishesCountInput,
            onValueChange = {
                dishesCountInput = it
                isTotalCalculated = false // Если пользователь меняет количество, сбрасываем экран в режим "Скидка"
            },
            label = { Text("Количество блюд:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Чаевые: ${tipPercent.toInt()}%", fontWeight = FontWeight.Medium)
        Slider(
            value = tipPercent,
            onValueChange = {
                tipPercent = it
                isTotalCalculated = false
            },
            valueRange = 0f..25f, // Диапазон изменения от 0 до 25% (Пункт 4 методички)
            steps = 4,           // Значение шага равно 5 (разделяет отрезок на точки 0, 5, 10, 15, 20, 25)
            onValueChangeFinished = {
                // Запускаем всплывающий Snackbar, когда пользователь отпустил ползунок (Пункт 4 методички)
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss() // Убираем старый снейкбар, если он висит
                    snackbarHostState.showSnackbar("Сумма чаевых: ${String.format("%.2f", tipsAmount)} руб.")
                }
            }
        )


        // --- ВСТАВИТЬ В COLUMN СТРОГО ПОД СЛАЙДЕРОМ ---

// Вычисляем текущий процент скидки на основе введенного количества блюд
        val discountPercent = getDiscountPercent(dishesCount)

        Text("Скидка:", fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Равномерно распределяем кнопки по ширине экрана
        ) {
            // Список всех доступных вариантов скидок из условий задачи
            val discountOptions = listOf(3, 5, 7, 10)

            discountOptions.forEach { percent ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        // Кнопка выберется автоматически, если вычисленный процент совпал с её номиналом (Пункт 5 методички)
                        selected = (discountPercent == percent),
                        onClick = null // Реализуется программно, а не пользователем!
                    )
                    Text("$percent%")
                }
            }
        }
    }
}

// Вычисляет чистую сумму чаевых в рублях на основе введенной суммы и процента
fun calculateTips(total: Double, percent: Int): Double {
    return total * percent / 100.0
}

// Определяет процент скидки в зависимости от количества заказанных блюд (Пункт 5 методички)
fun getDiscountPercent(dishesCount: Int): Int {
    return when {
        dishesCount in 1..2 -> 3   // 1-2 блюда – 3%
        dishesCount in 3..5 -> 5   // 3-5 блюд – 5%
        dishesCount in 6..10 -> 7  // 6-10 блюд – 7%
        dishesCount > 10 -> 10     // более 10 блюд – 10%
        else -> 0
    }
}

// Вычисляет сумму скидки в рублях
fun calculateDiscountAmount(total: Double, percent: Int): Double {
    return total * percent / 100.0
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipCalculatorPreview() {
    TipCalcTheme { // Используйте имя вашей темы
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold { innerPadding ->
            TipCalculatorScreen(snackbarHostState, Modifier.padding(innerPadding))
        }
    }
}