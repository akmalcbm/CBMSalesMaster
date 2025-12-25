package shop.chamanbahar.cbmsales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import shop.chamanbahar.cbmsales.R
import shop.chamanbahar.cbmsales.model.Product

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow(products())
    val products: StateFlow<List<Product>> = _products

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredProducts: StateFlow<List<Product>> = combine(_products, _searchQuery) { products, query ->
        if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _products.value)

    var isGrid = MutableStateFlow(true)
    var discountPercent = MutableStateFlow(0)

    fun toggleLayout() {
        isGrid.value = !isGrid.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDiscount(percent: Int) {
        discountPercent.value = percent
    }

    private fun products(): List<Product> = listOf(
        Product(1, "(₹ 5) Meat Masala", "Chaman Bahar Meat Masala (₹ 5)", R.drawable.masala_rs_5, 95.0, 5.0, 8.0, 25.0, 40.0, "NA", "Meat Masala", "https://chamanbahar.shop/product/chaman-bahar-meat-masala-%E2%82%B95" ),
        Product(2, "(₹ 10) Meat Masala", "Chaman Bahar Meat Masala (₹ 10)", R.drawable.masala_rs_10, 95.0, 10.0, 16.0, 13.0, 40.0, "NA", "Meat Masala", "https://chamanbahar.shop/product/chaman-bahar-meat-masala-%E2%82%B910"),
        Product(3, "(₹ 30) Meat Masala", "Chaman Bahar Meat Masala (₹ 30)", R.drawable.masala_rs_30, 95.0, 30.0, 55.0, 4.0, 40.0, "NA", "Meat Masala","https://chamanbahar.shop/product/chaman-bahar-meat-masala"),
        Product(4, "(200g) Meat Masala", "Chaman Bahar Meat Masala (200 Gram)", R.drawable.masala_200g, 310.0, 88.0, 200.0, 5.0, 20.0, "NA", "Meat Masala","https://chamanbahar.shop/product/chaman-bahar-meat-masala-200gm"),
        Product(5, "(500g) Meat Masala", "Chaman Bahar Meat Masala (500 Gram)", R.drawable.masala_500g, 300.0, 198.0, 500.0, 2.0, 20.0, "NA", "Meat Masala","https://chamanbahar.shop/product/chaman-bahar-meat-masala-500gm"),

        Product(6, "(₹ 5) Haldi Powder", "Chaman Bahar (₹ 5) Haldi Powder", R.drawable.haldi_rs_5, 77.0, 5.0, 8.0, 26.0, 30.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder-%E2%82%B9-5"),
        Product(7, "(₹ 10) Haldi Powder", "Chaman Bahar Meat (₹ 10) Haldi Powder", R.drawable.haldi_rs_10, 120.0, 10.0, 20.0, 20.0, 20.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder-rs-10"),
        Product(8, "(200g) Haldi Powder", "Chaman Bahar (200g) Haldi Powder", R.drawable.haldi_200g, 225.0, 72.0, 200.0, 5.0, 20.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder-200-gm"),

        Product(9, "(50g Box) Haldi Powder", "(50g Box) Haldi Powder", R.drawable.haldi_box_50g, 120.0, 21.0, 50.0, 10.0, 20.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder-50-gm"),
        Product(10, "(100g Box) Haldi Powder", "(100g Box) Haldi Powder", R.drawable.haldi_box_100g, 240.0, 41.0, 100.0, 10.0, 20.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder-100-gm"),
        Product(11, "(200g Box) Haldi Powder", "(200g Box) Haldi Powder", R.drawable.haldi_box_200g, 225.0, 72.0, 200.0, 5.0, 20.0, "NA", "Haldi Powder","https://chamanbahar.shop/product/chaman-bahar-haldi-powder"),

        Product(12, "(₹ 5) Mishran Garam Masala", "NA", R.drawable.mishran_5, 95.0, 5.0, 8.0, 25.0, 20.0, "NA", "Mishran Garam Masala","https://chamanbahar.shop/product/mishran-garam-masala-%E2%82%B95"),
        Product(13, "(200g Box) Mishran Garam Masala", "NA", R.drawable.mishran_200g, 400.0, 115.0, 200.0, 5.0, 20.0, "NA", "Mishran Garam Masala","https://chamanbahar.shop/product/mishran-garam-masala"),

        Product(14, "(200g Box) Kitchen King", "NA", R.drawable.kk200, 435.0, 125.0, 200.0, 5.0, 10.0, "NA", "Kitchen King","https://chamanbahar.shop/product/kitchen-king"),

        Product(15, "Fry Masala Pkt", "NA", R.drawable.fry_pkt, 360.0, 31.0, 50.0, 20.0, 20.0, "NA", "Fry Masala","https://chamanbahar.shop/product/chaman-bahar-chicken-machhli-fry-masala"),

        Product(16, "(₹ 10) Biryani/Pulav Masala Box", "NA", R.drawable.briyani_10, 95.0, 10.0, 8.0, 12.0, 10.0, "NA", "Biryani","https://chamanbahar.shop/product/cbm-biryani-pulav-masala"),
        Product(17, "(50g Box) Biryani Masala", "NA", R.drawable.briyani_50, 380.0, 52.0, 50.0, 10.0, 10.0, "NA", "Biryani","https://chamanbahar.shop/product/biryani-masala"),

        Product(18, "(₹ 10) Chicken Masala Pkt", "NA", R.drawable.chicken_10, 95.0, 10.0, 16.0, 13.0, 40.0, "NA", "Chicken","https://chamanbahar.shop/product/chicken-meat-masala"),
        Product(19, "(50g Box) Chicken/Mutton Masala", "NA", R.drawable.chicken_mutton_50_box, 320.0, 43.0, 50.0, 10.0, 10.0, "NA", "Chicken","https://chamanbahar.shop/product/chicken-mutton-masala"),

        Product(20, "(₹ 10) Chaat Masala Box", "NA", R.drawable.chaat_10, 95.0, 10.0, 18.0, 12.0, 10.0, "NA", "Chaat Masala","https://chamanbahar.shop/product/chaman-bahar-chaat-masala"),
        Product(21, "(50g Box) Chaat Masala", "NA", R.drawable.chaat_50, 210.0, 29.0, 50.0, 10.0, 10.0, "NA", "Chaat Masala","https://chamanbahar.shop/product/chaman-bahar-chaat-masala-50-gm"),

        Product(22, "(₹ 10 Box) Kashmiri Mirch", "NA", R.drawable.kashmiri_10, 95.0, 10.0, 12.0, 12.0, 20.0, "NA", "Kashmiri Mirch","https://chamanbahar.shop/product/kashmiri-mirch-powder"),
        Product(23, "(50g Box) Kashmiri Mirch", "NA", R.drawable.kashimiri_50, 375.0, 52.0, 50.0, 10.0, 10.0, "NA", "Kashmiri Mirch","https://chamanbahar.shop/product/kashmiri-mirch-powder"),

        Product(24, "(50g Box) Fish & Chicken Roasted Masala", "NA", R.drawable.fish_chicken_roasted_50_box, 195.0, 35.0, 50.0, 10.0, 20.0, "NA", "Fry Masala","https://chamanbahar.shop/product/fish-chicken-roasted-masala"),

        Product(25, "(100g Box) Roasted Jeera Powder", "NA", R.drawable.jeera_100_b, 310.0, 80.0, 100.0, 5.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-roasted-jeera-powder-100-gm"),

        Product(26, "(100g Box) Kali Mirch Powder", "NA", R.drawable.kali_mirch_100_b, 655.0, 165.0, 100.0, 5.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-kali-mirch-powder-100-gm"),
        Product(27, "(50g Box) Kali Mirch Powder", "NA", R.drawable.kali_mirch_100_b, 330.0, 85.0, 50.0, 5.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-kali-mirch-powder-100-gm"),

        Product(20, "(₹ 10) Chole Masala Box", "NA", R.drawable.chole_10, 95.0, 10.0, 12.0, 12.0, 10.0, "NA", "Chole Masala","https://chamanbahar.shop/product/chaman-bahar-chaat-masala"),
        Product(20, "(₹ 10) Paneer Masala Box", "NA", R.drawable.paneer_10, 95.0, 10.0, 12.0, 12.0, 10.0, "NA", "Paneer Masala","https://chamanbahar.shop/product/chaman-bahar-chaat-masala"),
        Product(20, "(₹ 10) Kasoori Methi Box", "NA", R.drawable.kasoori_10, 95.0, 10.0, 8.0, 12.0, 10.0, "NA", "Kasoori Methi","https://chamanbahar.shop/product/chaman-bahar-chaat-masala"),
        Product(20, "(100g Box) Seekh Kabab Masala Box", "NA", R.drawable.seekh_100gm, 245.0, 60.0, 100.0, 5.0, 10.0, "NA", "Seekh Masala","https://chamanbahar.shop/product/chaman-bahar-chaat-masala"),


        Product(32, "(25g Box) Chicken Tikka Masala", "NA", R.drawable.chickentikka_25_b, 275.0, 37.0, 25.0, 10.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-chicken-tikka-masala"),
        Product(33, "(25g Box) Special Meat Masala", "NA", R.drawable.spl_meat_25_b, 202.0, 27.0, 25.0, 10.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-special-meat-masala"),
        Product(34, "(25g Box) Al-faham Masala", "NA", R.drawable.alfaham_25_b, 260.0, 35.0, 25.0, 10.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-alfaham-masala"),
        Product(35, "(50g Box) Jaljeera Powder", "NA", R.drawable.jaljeera_50_b, 180.0, 25.0, 50.0, 10.0, 10.0,"NA", "Other","https://chamanbahar.shop/product/chaman-bahar-jaljeera-powder-50-gm"),



        Product(36, "(1Kg Loose) Fish Masala", "NA", R.drawable.placeholder_image, 330.0, 450.0, 1000.0, 1.0, 10.0,"NA", "Loose",""),

        Product(37, "(1Kg Loose) CBM-Mishran Garam Masala", "NA", R.drawable.placeholder_image, 225.0, 350.0, 1000.0, 10.0, 2.0,"NA", "Loose","")

    )
}
