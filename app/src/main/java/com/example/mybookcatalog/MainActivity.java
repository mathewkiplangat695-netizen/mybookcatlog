package com.example.mybookcatalog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final List<Book> allBooks = new ArrayList<>();
    private BookAdapter adapter;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private final List<TextView> categoryTabs = new ArrayList<>();
    private View categoryScroll;
    private View searchCardView;
    private EditText editTextSearch;
    private TextView textViewPageTitle;
    private View cartSummaryCard;
    private TextView textViewTotalAmount;
    private BottomNavigationView bottomNav;

    private String currentCategory = "All";
    private boolean isCartView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        categoryScroll = findViewById(R.id.categoryScroll);
        searchCardView = findViewById(R.id.searchCardView);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewPageTitle = findViewById(R.id.textViewPageTitle);
        cartSummaryCard = findViewById(R.id.cartSummaryCard);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        bottomNav = findViewById(R.id.bottom_navigation);

        setupBooks();
        setupRecyclerView();
        setupCategories();
        setupSearch();
        setupBottomNavigation();
        
        showHomeView();

        findViewById(R.id.buttonCheckout).setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) return;
            showPaymentBottomSheet();
        });
    }

    private void showPaymentBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_payment_bottom_sheet, (ViewGroup) findViewById(android.R.id.content), false);
        bottomSheetDialog.setContentView(view);

        TextView textViewSheetTotal = view.findViewById(R.id.textViewSheetTotal);
        final RadioButton radioMpesa = view.findViewById(R.id.radioMpesa);
        final RadioButton radioCard = view.findViewById(R.id.radioCard);
        View optionMpesa = view.findViewById(R.id.optionMpesa);
        View optionCard = view.findViewById(R.id.optionCard);
        View buttonConfirmPayment = view.findViewById(R.id.buttonConfirmPayment);

        final double total = CartManager.getTotalPrice();
        textViewSheetTotal.setText(getString(R.string.label_total_amount, total));

        optionMpesa.setOnClickListener(v -> {
            radioMpesa.setChecked(true);
            radioCard.setChecked(false);
        });

        optionCard.setOnClickListener(v -> {
            radioCard.setChecked(true);
            radioMpesa.setChecked(false);
        });

        buttonConfirmPayment.setOnClickListener(v -> {
            if (!radioMpesa.isChecked() && !radioCard.isChecked()) {
                Toast.makeText(this, R.string.msg_please_select_payment, Toast.LENGTH_SHORT).show();
                return;
            }

            String method = radioMpesa.isChecked() ? getString(R.string.payment_method_mpesa) : getString(R.string.payment_method_card);
            bottomSheetDialog.dismiss();
            processFinalCheckout(total, method);
        });

        bottomSheetDialog.show();
    }

    private void processFinalCheckout(double total, String paymentMethod) {
        String date = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        String orderId = "ORD" + System.currentTimeMillis();
        
        // Save to Order History
        Order newOrder = new Order(orderId, date, total, new ArrayList<>(CartManager.getCartItems()));
        SessionManager.addOrder(newOrder);

        Toast.makeText(this, getString(R.string.msg_payment_successful, paymentMethod), Toast.LENGTH_LONG).show();
        
        CartManager.clearCart();
        updateCartBadge();
        showHomeView();
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        if (isCartView) {
            showCartView();
        }
    }

    private void setupBooks() {
        allBooks.add(new Book("The Last Ember", "Daniel Levin", "Historical Thriller", "A former archaeologist is pulled into a deadly conspiracy when ancient secrets buried beneath Rome resurface.", 1200.00, 4.5f, R.drawable.thelastember));
        allBooks.add(new Book("Quantum Mirage", "Lila Chen", "Science Fiction", "In a future where time travel is illegal, a rogue physicist must choose between saving the world or saving her daughter.", 1500.00, 4.8f, R.drawable.thequantummirage));
        allBooks.add(new Book("Roots & Wings", "Maria Esteban", "Literary Fiction", "A moving generational story of a Cuban-American family searching for identity, belonging, and redemption.", 950.00, 4.2f, R.drawable.rootandwings));
        allBooks.add(new Book("The Mind Sculptor", "Dr. Evan Shaw", "Psychology / Non-Fiction", "A groundbreaking look at neuroplasticity and how you can rewire your brain for success and happiness.", 1800.00, 4.9f, R.drawable.themindsculptor));
        allBooks.add(new Book("Inkbound: Chronicles of the Lost Library", "J.R. Faulkner", "Fantasy / Adventure", "A young librarian discovers that ancient books can open portals to other worlds—but not all stories have happy endings.", 1100.00, 4.6f, R.drawable.inkbound));
        allBooks.add(new Book("Startup Savage", "Nicole Vega", "Business", "A brutally honest guide to launching a tech startup in the real world, full of failures, pivots, and unexpected wins.", 1300.00, 4.4f, R.drawable.startupsavage));
        allBooks.add(new Book("Beneath Crimson Skies", "Tomasz Novak", "Historical Fiction", "The intertwined lives of resistance fighters, spies, and survivors during the Nazi occupation of Warsaw.", 1400.00, 4.7f, 0));
        allBooks.add(new Book("The Art of Stillness", "Tara Bell", "Self-Help", "Learn how to find peace in a chaotic world by mastering the ancient wisdom of stillness.", 800.00, 4.3f, 0));
        allBooks.add(new Book("Neon Ghosts", "Khalid Jones", "Urban Fantasy", "A private investigator with the ability to see spirits uncovers a supernatural conspiracy beneath the city's neon lights.", 1150.00, 4.5f, 0));
        allBooks.add(new Book("Eat Green, Live Clean", "Dr. Sanjay Patel", "Health & Wellness", "A practical guide to plant-based nutrition and detox living, backed by science and easy recipes.", 1600.00, 4.8f, 0));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookAdapter(new ArrayList<>(allBooks), new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Book book) {
                CartManager.addBook(book);
                updateCartBadge();
                Toast.makeText(MainActivity.this, getString(R.string.msg_added_to_cart, book.getTitle()), Toast.LENGTH_SHORT).show();
            }
        });

        cartAdapter = new CartAdapter(CartManager.getCartItems(), new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateCartBadge();
                updateCartTotal();
            }

            @Override
            public void onItemRemoved() {
                updateCartBadge();
                updateCartTotal();
                if (CartManager.getCartItems().isEmpty()) {
                    showHomeView();
                    bottomNav.setSelectedItemId(R.id.nav_home);
                }
            }
        });
    }

    private void updateCartTotal() {
        textViewTotalAmount.setText(getString(R.string.label_price_kes, CartManager.getTotalPrice()));
    }

    private void updateCartBadge() {
        if (bottomNav == null) return;
        int count = CartManager.getItemCount();
        BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
            badge.setBadgeTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            badge.setVisible(false);
        }
    }

    private void setupCategories() {
        categoryTabs.add(findViewById(R.id.catAll));
        categoryTabs.add(findViewById(R.id.catFiction));
        categoryTabs.add(findViewById(R.id.catThriller));
        categoryTabs.add(findViewById(R.id.catSciFi));
        categoryTabs.add(findViewById(R.id.catSelfHelp));
        categoryTabs.add(findViewById(R.id.catBusiness));

        for (TextView tab : categoryTabs) {
            tab.setOnClickListener(v -> {
                currentCategory = ((TextView) v).getText().toString();
                applyFilters();
                updateTabStyles((TextView) v);
            });
        }
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        String query = editTextSearch.getText().toString().toLowerCase().trim();
        List<Book> filteredList = new ArrayList<>();

        for (Book book : allBooks) {
            boolean matchesCategory = currentCategory.equals("All") || book.getGenre().contains(currentCategory);
            boolean matchesQuery = book.getTitle().toLowerCase().contains(query) || book.getAuthor().toLowerCase().contains(query);
            
            if (matchesCategory && matchesQuery) {
                filteredList.add(book);
            }
        }
        adapter.updateList(filteredList);
    }

    private void updateTabStyles(TextView selectedTab) {
        for (TextView tab : categoryTabs) {
            tab.setSelected(false);
            tab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            ViewCompat.setBackgroundTintList(tab, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_light)));
            tab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        selectedTab.setSelected(true);
        selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);

        // Apply unique colors for selected state
        int colorRes = R.color.cat_all;
        int id = selectedTab.getId();
        if (id == R.id.catFiction) colorRes = R.color.cat_fiction;
        else if (id == R.id.catThriller) colorRes = R.color.cat_thriller;
        else if (id == R.id.catSciFi) colorRes = R.color.cat_scifi;
        else if (id == R.id.catSelfHelp) colorRes = R.color.cat_selfhelp;
        else if (id == R.id.catBusiness) colorRes = R.color.cat_business;

        ViewCompat.setBackgroundTintList(selectedTab, ColorStateList.valueOf(ContextCompat.getColor(this, colorRes)));
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showHomeView();
                return true;
            } else if (id == R.id.nav_categories) {
                showCategoriesView();
                return true;
            } else if (id == R.id.nav_cart) {
                showCartView();
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return false;
            }
            return false;
        });
    }

    private void showHomeView() {
        isCartView = false;
        textViewPageTitle.setText(R.string.title_book_catalog);
        searchCardView.setVisibility(View.VISIBLE);
        categoryScroll.setVisibility(View.GONE);
        cartSummaryCard.setVisibility(View.GONE);
        adapter.setShowAddToCart(false);
        recyclerView.setAdapter(adapter);
        currentCategory = "All";
        editTextSearch.setText("");
        applyFilters();
    }

    private void showCategoriesView() {
        isCartView = false;
        textViewPageTitle.setText(R.string.title_browse_genres);
        searchCardView.setVisibility(View.VISIBLE);
        categoryScroll.setVisibility(View.VISIBLE);
        cartSummaryCard.setVisibility(View.GONE);
        adapter.setShowAddToCart(true);
        recyclerView.setAdapter(adapter);
        currentCategory = "All";
        editTextSearch.setText("");
        applyFilters();
        updateTabStyles(findViewById(R.id.catAll));
    }

    private void showCartView() {
        isCartView = true;
        textViewPageTitle.setText(R.string.title_shopping_cart);
        searchCardView.setVisibility(View.GONE);
        categoryScroll.setVisibility(View.GONE);
        
        List<CartItem> cartItems = CartManager.getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, R.string.msg_cart_empty, Toast.LENGTH_SHORT).show();
            cartSummaryCard.setVisibility(View.GONE);
            recyclerView.setAdapter(null);
        } else {
            cartSummaryCard.setVisibility(View.VISIBLE);
            updateCartTotal();
            recyclerView.setAdapter(cartAdapter);
        }
    }
}
