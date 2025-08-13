import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import shop.chamanbahar.cbmsales.model.Product
import shop.chamanbahar.cbmsales.screens.CompareTabContent
import shop.chamanbahar.cbmsales.screens.DescriptionTabContent
import shop.chamanbahar.cbmsales.screens.OverviewTabContent

@Composable
fun ProductDetailsScreen(
    initialProduct: Product,
    discountPercentInit: Int,
    allProducts: List<Product>
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedProduct by remember { mutableStateOf(initialProduct) }

    val scrollState = rememberScrollState()
    val tabTitles = listOf("Overview", "Details", "Compare")

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        // 🔘 Tabs Row
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        // 💥 THIS BOX NEEDS height or weight TO WORK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 🔥 This bounds the height properly!
        ) {
            when (selectedTabIndex) {
                0 -> OverviewTabContent(
                    selectedProduct = selectedProduct,
                    onProductChange = { selectedProduct = it },
                    discountPercentInit = discountPercentInit,
                    allProducts = allProducts,
                    scrollState = scrollState
                )

                1 -> DescriptionTabContent(
                    url = selectedProduct.websiteUrl,
                    productId = selectedProduct.id.toString() // ✅ or any unique string
                )

                2 -> CompareTabContent(product = selectedProduct)
            }
        }
    }
}

