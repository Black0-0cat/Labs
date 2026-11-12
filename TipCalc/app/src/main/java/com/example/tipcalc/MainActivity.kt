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
import com.example.tipcalc.ui.theme.TipCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalcTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
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

    // Контейнер, в котором элементы будут выстраиваться друг под другом
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Калькулятор чаевых", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Преобразуем ввод в числа
        val totalAmount = totalAmountInput.toDoubleOrNull() ?: 0.0
        val dishesCount = dishesCountInput.toIntOrNull() ?: 0
        val tipsAmount = calculateTips(totalAmount, tipPercent.toInt())

        val coroutineScope = rememberCoroutineScope()

        OutlinedTextField(
            value = totalAmountInput,
            onValueChange = { totalAmountInput = it },
            label = { Text("Сумма заказа:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dishesCountInput,
            onValueChange = { dishesCountInput = it },
            label = { Text("Количество блюд:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Чаевые: ${tipPercent.toInt()}%", fontWeight = FontWeight.Medium)
        Slider(
            value = tipPercent,
            onValueChange = { tipPercent = it },
            valueRange = 0f..25f,
            steps = 4,
            onValueChangeFinished = {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Сумма чаевых: ${String.format("%.2f", tipsAmount)} руб.")
                }
            }
        )

        // Вычисляем текущий процент скидки на основе введенного количества блюд
        val discountPercent = getDiscountPercent(dishesCount)

        Text("Скидка:", fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val discountOptions = listOf(3, 5, 7, 10)

            discountOptions.forEach { percent ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = (discountPercent == percent),
                        onClick = null
                    )
                    Text("$percent%")
                }
            }
        }

        // --- ДОБАВЛЕННЫЕ 3 БЛОКА ТЕКСТА ВНИЗУ ---
        val discountAmount = calculateDiscountAmount(totalAmount, discountPercent)
        val finalTotal = totalAmount + tipsAmount - discountAmount

        Spacer(modifier = Modifier.height(8.dp))

        // Итоговая сумма
        Text(
            text = "Итоговая сумма: ${String.format("%.2f", finalTotal)} руб.",
            fontSize = 20.sp,
        )

        // Размер скидки
        Text(
            text = "Размер скидки: ${String.format("%.2f", discountAmount)} руб.",
            fontSize = 20.sp,
        )

        // Размер чаевых
        Text(
            text = "Размер чаевых: ${String.format("%.2f", tipsAmount)} руб.",
            fontSize = 20.sp,
        )
    }
}

// Вычисляет чистую сумму чаевых в рублях на основе введенной суммы и процента
fun calculateTips(total: Double, percent: Int): Double {
    return total * percent / 100.0
}

// Определяет процент скидки в зависимости от количества заказанных блюд
fun getDiscountPercent(dishesCount: Int): Int {
    return when {
        dishesCount in 1..2 -> 3
        dishesCount in 3..5 -> 5
        dishesCount in 6..10 -> 7
        dishesCount > 10 -> 10
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
    TipCalcTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold { innerPadding ->
            TipCalculatorScreen(snackbarHostState, Modifier.padding(innerPadding))
        }
    }
}
