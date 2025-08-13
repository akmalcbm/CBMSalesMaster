package shop.chamanbahar.cbmsales.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import shop.chamanbahar.cbmsales.helper.InfoBadgeBottom
import shop.chamanbahar.cbmsales.helper.InfoBadgeTop
import shop.chamanbahar.cbmsales.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewTabContent(
    selectedProduct: Product,
    onProductChange: (Product) -> Unit,
    discountPercentInit: Int,
    allProducts: List<Product>,
    scrollState: ScrollState
) {
    var discountPercent by remember { mutableStateOf(discountPercentInit.toString()) }
    val discount = discountPercent.toDoubleOrNull() ?: 0.0

    val variantProducts = allProducts.filter { it.variantKey == selectedProduct.variantKey }

    val animatedRate by animateFloatAsState(
        selectedProduct.rate(discount).toFloat(), label = "rate"
    )
    val animatedEachPkt by animateFloatAsState(
        selectedProduct.eachPkt(discount).toFloat(), label = "eachPkt"
    )
    val animatedProfitPkt by animateFloatAsState(
        selectedProduct.profitPerPkt(discount).toFloat(), label = "profitPkt"
    )
    val animatedProfitBd by animateFloatAsState(
        selectedProduct.profitPerBundle(discount).toFloat(), label = "profitBd"
    )

    val badgeRowState = rememberLazyListState()

    val summaryBadges = buildList {
        add("Profit/Bd" to "₹%.2f".format(animatedProfitBd))
        if (selectedProduct.boriScheme != "NA") {
            add("Scheme" to selectedProduct.boriScheme)
        }
    }

    LaunchedEffect(summaryBadges.size) {
        if (selectedProduct.boriScheme != "NA") {
            badgeRowState.animateScrollToItem(summaryBadges.lastIndex)
        }
    }

    // ✅ ONLY ONE Column + verticalScroll!
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .imePadding()
    ) {

        Text(
            text = selectedProduct.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = selectedProduct.imageResId),
            contentDescription = selectedProduct.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(variantProducts) { variant ->
                val isSelected = selectedProduct.id == variant.id
                AssistChip(
                    onClick = { onProductChange(variant) },
                    label = {
                        Text(
                            text = variant.name.substringBefore(")") + ")",
                            style = if (isSelected)
                                MaterialTheme.typography.labelLarge
                            else
                                MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                        else Color(0xFFE0E0E0),
                        labelColor = if (isSelected) Color.White else Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                listOf(
                    "CODE" to "%.0f".format(selectedProduct.code),
                    "MRP" to "₹%.0f".format(selectedProduct.mrp),
                    "WEIGHT" to selectedProduct.formatWeight(selectedProduct.weight)
                )
            ) { (label, value) ->
                InfoBadgeTop(label, value)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                listOf(
                    "BUNDLE" to "${selectedProduct.bundle.toInt()} pkt",
                    "BORI" to "${selectedProduct.bori.toInt()}",
                    "Bundle Wt" to selectedProduct.formattedBundleWeight(),
                    "Bori Wt" to selectedProduct.formattedBoriWeight()
                )
            ) { (label, value) ->
                InfoBadgeTop(label, value)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = discountPercent,
            onValueChange = { discountPercent = it },
            label = { Text("Input Discount in %") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                listOf(
                    "RATE" to "₹%.2f".format(animatedRate),
                    "Each Pkt" to "₹%.2f".format(animatedEachPkt),
                    "Profit/Pkt" to "₹%.2f".format(animatedProfitPkt)
                )
            ) { (label, value) ->
                InfoBadgeBottom(label, value)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = badgeRowState,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(summaryBadges) { (label, value) ->
                val isScheme = label == "Scheme"
                val chipColor =
                    if (isScheme) Color(0xFFEF6C00) else MaterialTheme.colorScheme.surfaceVariant
                val chipTextColor =
                    if (isScheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                InfoBadgeBottom(
                    label = label,
                    value = value,
                    background = chipColor,
                    contentColor = chipTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedProduct.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
