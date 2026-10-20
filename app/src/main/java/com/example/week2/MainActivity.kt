package com.example.week2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class HardwareItem(
    val id: String,
    val name: String,
    val category: String,
    val cost: Double,
    val stockCount: Int
)

val mockHardwareList = listOf(
    HardwareItem(
        id = "101",
        name = "MacBook Pro M3 Max",
        category = "Geliştirici Bilgisayarı",
        cost = 3499.0,
        stockCount = 5
    ),
    HardwareItem(
        id = "102",
        name = "NVIDIA RTX 4090 Sunucu",
        category = "Yapay Zeka / GPU",
        cost = 1599.0,
        stockCount = 2
    ),
    HardwareItem(
        id = "103",
        name = "Google Pixel 8 Pro",
        category = "Mobil Test Cihazı",
        cost = 999.0,
        stockCount = 8
    ),
    HardwareItem(
        id = "104",
        name = "Dell UltraSharp 49\"",
        category = "Monitör",
        cost = 1199.0,
        stockCount = 4
    ),
    HardwareItem(
        id = "105",
        name = "Sony WH-1000XM5 Kulaklık",
        category = "Odaklanma Ekipmanı",
        cost = 399.0,
        stockCount = 12
    ),
    HardwareItem(
        id = "106",
        name = "Raspberry Pi 5 8GB",
        category = "IoT Geliştirme Kiti",
        cost = 85.0,
        stockCount = 25
    ),
    HardwareItem(
        id = "107",
        name = "HHKB Professional Keyboard",
        category = "Ergonomik Klavye",
        cost = 299.0,
        stockCount = 0
    ),
    HardwareItem(
        id = "108",
        name = "AMD Ryzen Threadripper",
        category = "Yapay Zeka / GPU",
        cost = 4999.0,
        stockCount = 1
    )
)


val DarkBackground = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val NeonGreen = Color(0xFF00E676)
val TextGray = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DarkBackground,
                    surface = SurfaceDark,
                    primary = NeonGreen,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DevClusterScreen()
                }
            }
        }
    }
}


@Composable
fun DevClusterScreen() {
    // Konfigürasyon değişimi (ekran döndürme vb.) koruması: rememberSaveable
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Slot Table kilitli dinamik durum
    var totalCost by remember { mutableDoubleStateOf(0.0) }

    // Turnike Mantığı (Memoization): Arama algoritmaları sadece searchQuery değiştiğinde çalışır
    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            mockHardwareList
        } else {
            mockHardwareList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "DEVCLUSTER SYS_PANEL",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Animasyonlu Toplam Bütçe Göstergesi
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "AYRILAN ÖDENEK: $",
                style = MaterialTheme.typography.titleLarge,
                color = TextGray
            )
            AnimatedContent(
                targetState = totalCost,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn(tween(300))) togetherWith
                            (slideOutVertically { height -> -height } + fadeOut(tween(300)))
                },
                label = "cost_animation"
            ) { targetCost ->
                Text(
                    text = "$targetCost",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // State Hoisting: State aşağı aktarılıyor, event yukarı fırlatılıyor
        HardwareSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn & Nokta Atışı Recomposition (key = { it.id })
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = filteredList,
                key = { item -> item.id }
            ) { item ->
                HardwareCard(
                    item = item,
                    onItemClick = { clickedItem ->
                        totalCost += clickedItem.cost
                    }
                )
            }
        }
    }
}


@Composable
fun HardwareSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Donanım veya kategori sorgula...", color = TextGray) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonGreen,
            unfocusedBorderColor = TextGray,
            cursorColor = NeonGreen
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun HardwareCard(
    item: HardwareItem,
    onItemClick: (HardwareItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        // Modifier Sıralaması: Önce .clickable(), ardından .padding() hiyerarşisi
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(item) }
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(4.dp))

                val isOutOfStock = item.stockCount == 0
                Box(
                    modifier = Modifier
                        .background(
                            if (isOutOfStock) Color(0xFFCF6679).copy(alpha = 0.2f)
                            else NeonGreen.copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isOutOfStock) "STOK: YOK" else "STOK: ${item.stockCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOutOfStock) Color(0xFFFFB4AB) else NeonGreen
                    )
                }
            }
            Text(
                text = "$${item.cost}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}