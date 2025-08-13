package shop.chamanbahar.cbmsales.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int,

    val code: Double,           // Original rate before discount
    val mrp: Double,            // MRP per packet
    val weight: Double,         // Weight per packet (in grams)

    val bundle: Double,         // Packets per bundle
    val bori: Double,           // Bundles per bori
    val boriScheme: String,     // 🆕 used for scheme i.e. 10+1, 30+3 etc.
    val variantKey: String,     // 🆕 used for variant selector
    val category: String,       // ✅ Add this line if missing

    val websiteUrl: String = "" // ✅ new field for product description page
) {
    // 💡 Auto-calculated: total weight of bundle = packet weight × packet count
    val bundleWeight: Double
        get() = weight * bundle

    // 💡 Auto-calculated: total weight of bori = bundleWeight × number of bundles
    val boriWeight: Double
        get() = bundleWeight * bori

    // 🧮 Pricing Calculations
    fun rate(discountPercent: Double): Double {
        return code - (code * discountPercent / 100.0)
    }

    fun eachPkt(discountPercent: Double): Double {
        return rate(discountPercent) / bundle
    }

    fun profitPerPkt(discountPercent: Double): Double {
        return mrp - eachPkt(discountPercent)
    }

    fun profitPerBundle(discountPercent: Double): Double {
        return (mrp * bundle) - rate(discountPercent)
    }

    // Returns weight in formatted form, e.g. 800g or 1.2kg
    fun formatWeight(valueInGrams: Double): String {
        return if (valueInGrams >= 1000) {
            String.format("%.2f kg", valueInGrams / 1000.0)
        } else {
            String.format("%.0f g", valueInGrams)
        }
    }

    // Publicly exposed formatted weights for UI
    fun formattedBundleWeight(): String = formatWeight(bundleWeight)

    fun formattedBoriWeight(): String = formatWeight(boriWeight)

    fun getAvailableUnits(): List<String> {
        val units = mutableListOf<String>()
        units.add("Bundle") // Always present
        if (bori > 0) units.add("Bori")
        if (bundle > 0) units.add("Packet") // assuming individual packet = smallest unit
        return units
    }

}
