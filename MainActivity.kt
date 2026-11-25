package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class HistoryItem(val expression: String, val result: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDark by remember { mutableStateOf(systemDark) }
            val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                CalculatorScreen(
                    isDark = isDark,
                    onToggleTheme = { isDark = !isDark }
                )
            }
        }
    }
}

@Composable
fun CalculatorScreen(isDark: Boolean, onToggleTheme: () -> Unit) {
    var expression by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<HistoryItem>() }

    val preview = remember(expression) {
        if (expression.isBlank() || expression.all { it.isDigit() || it == '.' }) {
            ""
        } else {
            runCatching { formatResult(evaluateExpression(expression)) }.getOrDefault("")
        }
    }

    val bgDisplay = if (isDark) Color(0xFF1E222B) else Color(0xFFE8EEF7)
    val bgScreen = if (isDark) Color(0xFF13151A) else Color(0xFFF8F9FC)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgScreen
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Display Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(bgDisplay)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "कॅल्क्युलेटर",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { showHistory = !showHistory }) {
                            Text(if (showHistory) "कीपॅड" else "इतिहास")
                        }
                        FilledTonalButton(onClick = onToggleTheme) {
                            Text(if (isDark) "☀️" else "🌙")
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = expression.ifEmpty { "0" },
                        fontSize = if (expression.length > 14) 32.sp else 44.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMsg.ifEmpty { preview },
                        fontSize = 22.sp,
                        color = if (errorMsg.isNotEmpty()) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            if (showHistory) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.7f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (history.isEmpty()) {
                        item {
                            Text(
                                text = "अजून कोणताही इतिहास नाही.",
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(history) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expression = item.result.replace(",", "")
                                        errorMsg = ""
                                        showHistory = false
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(item.expression, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("= ${item.result}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Keypad Grid
                val rows = listOf(
                    listOf("AC", "( )", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "−"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "⌫", "=")
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.7f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { label ->
                                val isAc = label == "AC"
                                val isEq = label == "="
                                val isOp = label in listOf("( )", "%", "÷", "×", "−", "+")

                                val btnColor = when {
                                    isAc -> MaterialTheme.colorScheme.errorContainer
                                    isEq -> MaterialTheme.colorScheme.primary
                                    isOp -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val txtColor = when {
                                    isAc -> MaterialTheme.colorScheme.onErrorContainer
                                    isEq -> MaterialTheme.colorScheme.onPrimary
                                    isOp -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(btnColor)
                                        .clickable {
                                            errorMsg = ""
                                            when (label) {
                                                "AC" -> expression = ""
                                                "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                                "( )" -> {
                                                    val open = expression.count { it == '(' }
                                                    val close = expression.count { it == ')' }
                                                    val last = expression.lastOrNull()
                                                    expression += if (open > close && (last?.isDigit() == true || last == ')')) ")" else "("
                                                }
                                                "=" -> {
                                                    if (expression.isNotBlank()) {
                                                        val res = runCatching { formatResult(evaluateExpression(expression)) }
                                                        res.onSuccess { formatted ->
                                                            history.add(0, HistoryItem(expression, formatted))
                                                            expression = formatted.replace(",", "")
                                                        }.onFailure {
                                                            errorMsg = "अवैध समीकरण"
                                                        }
                                                    }
                                                }
                                                else -> expression += label
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = txtColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatResult(value: Double): String {
    if (abs(value) < 1e-12) return "0"
    val longVal = value.toLong()
    return if (value == longVal.toDouble()) longVal.toString() else value.toBigDecimal().stripTrailingZeros().toPlainString()
}

private fun evaluateExpression(raw: String): Double {
    val expr = raw
        .replace("×", "*")
        .replace("÷", "/")
        .replace("−", "-")
        .replace("%", "/100.0")

    return object : Any() {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < expr.length) expr[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expr.length) throw IllegalArgumentException("Unexpected character")
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    else -> return x
                }
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()
            val startPos = pos
            val x: Double
            if (eat('('.code)) {
                x = parseExpression()
                if (!eat(')'.code)) throw IllegalArgumentException("Missing ')'")
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = expr.substring(startPos, pos).toDouble()
            } else {
                throw IllegalArgumentException("Unexpected: " + ch.toChar())
            }
            return x
        }
    }.parse()
}
