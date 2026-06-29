package com.example.mybookcatalog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

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
    private ViewPager2 viewPagerBanner;
    private final List<TextView> categoryTabs = new ArrayList<>();
    private View categoryScroll;
    private TextView textViewPageTitle;
    private View cartSummaryCard;
    private TextView textViewTotalAmount;
    private BottomNavigationView bottomNav;

    private String currentCategory = "All";
    private boolean isCartView = false;
    
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        categoryScroll = findViewById(R.id.categoryScroll);
        textViewPageTitle = findViewById(R.id.textViewPageTitle);
        cartSummaryCard = findViewById(R.id.cartSummaryCard);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        bottomNav = findViewById(R.id.bottom_navigation);

        setupBooks();
        setupRecyclerView();
        setupBanner();
        setupCategories();
        setupBottomNavigation();
        
        showHomeView();

        findViewById(R.id.buttonCheckout).setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) return;
            showPaymentBottomSheet();
        });
    }

    private void setupBanner() {
        List<Book> featuredBooks = new ArrayList<>();
        if (allBooks.size() >= 5) {
            featuredBooks.add(allBooks.get(0));
            featuredBooks.add(allBooks.get(1));
            featuredBooks.add(allBooks.get(4));
            featuredBooks.add(allBooks.get(8));
            featuredBooks.add(allBooks.get(9));
        } else {
            featuredBooks.addAll(allBooks);
        }
        
        BannerAdapter bannerAdapter = new BannerAdapter(featuredBooks);
        viewPagerBanner.setAdapter(bannerAdapter);
        
        // Carousel effect
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPagerBanner.setPageTransformer(transformer);

        // Auto-slide logic
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = viewPagerBanner.getCurrentItem() + 1;
                if (nextItem >= featuredBooks.size()) {
                    nextItem = 0;
                }
                viewPagerBanner.setCurrentItem(nextItem, true);
                bannerHandler.postDelayed(this, 3000);
            }
        };
    }

    private void startBannerAutoSlide() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerHandler.postDelayed(bannerRunnable, 3000);
        }
    }

    private void stopBannerAutoSlide() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
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

        textViewSheetTotal.setVisibility(View.GONE);

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
            processFinalCheckout(method);
        });

        bottomSheetDialog.show();
    }

    private void processFinalCheckout(String paymentMethod) {
        String date = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        String orderId = "ORD" + System.currentTimeMillis();
        
        // Save to Order History
        Order newOrder = new Order(orderId, date, new ArrayList<>(CartManager.getCartItems()));
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
        } else if (viewPagerBanner.getVisibility() == View.VISIBLE) {
            startBannerAutoSlide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBannerAutoSlide();
    }

    private void setupBooks() {
        allBooks.add(new Book("The Last Ember", "Daniel Levin", "Historical Thriller", "A former archaeologist is pulled into a deadly conspiracy when ancient secrets buried beneath Rome resurface.", 4.5f, R.drawable.thelastember));
        allBooks.add(new Book("Quantum Mirage", "Lila Chen", "Science Fiction", "In a future where time travel is illegal, a rogue physicist must choose between saving the world or saving her daughter.", 4.8f, R.drawable.thequantummirage));
        allBooks.add(new Book("Roots & Wings", "Maria Esteban", "Literary Fiction", "A moving generational story of a Cuban-American family searching for identity, belonging, and redemption.", 4.2f, R.drawable.rootandwings));
        allBooks.add(new Book("The Mind Sculptor", "Dr. Evan Shaw", "Psychology / Non-Fiction", "A groundbreaking look at neuroplasticity and how you can rewire your brain for success and happiness.", 4.9f, R.drawable.themindsculptor));
        allBooks.add(new Book("Inkbound: Chronicles of the Lost Library", "J.R. Faulkner", "Fantasy / Adventure", "A young librarian discovers that ancient books can open portals to other worlds—but not all stories have happy endings.", 4.6f, R.drawable.inkbound));
        allBooks.add(new Book("Startup Savage", "Nicole Vega", "Business", "A brutally honest guide to launching a tech startup in the real world, full of failures, pivots, and unexpected wins.", 4.4f, R.drawable.startupsavage));
        allBooks.add(new Book("Beneath Crimson Skies", "Tomasz Novak", "Historical Fiction", "The intertwined lives of resistance fighters, spies, and survivors during the Nazi occupation of Warsaw.", 4.7f, R.drawable.beneathcrimsonskies));
        allBooks.add(new Book("The Art of Stillness", "Tara Bell", "Self-Help", "Learn how to find peace in a chaotic world by mastering the ancient wisdom of stillness.", 4.3f, R.drawable.theartofstillness));
        allBooks.add(new Book("Neon Ghosts", "Khalid Jones", "Urban Fantasy", "A private investigator with the ability to see spirits uncovers a supernatural conspiracy beneath the city's neon lights.", 4.5f, R.drawable.neonghosts));
        allBooks.add(new Book("Eat Green, Live Clean", "Dr. Sanjay Patel", "Health & Wellness", "A practical guide to plant-based nutrition and detox living, backed by science and easy recipes.", 4.8f, R.drawable.eatgreenliveclean));
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
        textViewTotalAmount.setText("Free Catalog");
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

    private void applyFilters() {
        List<Book> filteredList = new ArrayList<>();

        for (Book book : allBooks) {
            boolean matchesCategory = currentCategory.equals("All") || book.getGenre().contains(currentCategory);

            if (matchesCategory) {
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
        }

        selectedTab.setSelected(true);
        selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        ViewCompat.setBackgroundTintList(selectedTab, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
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
        viewPagerBanner.setVisibility(View.VISIBLE);
        categoryScroll.setVisibility(View.GONE);
        cartSummaryCard.setVisibility(View.GONE);
        adapter.setShowAddToCart(false);
        recyclerView.setAdapter(adapter);
        currentCategory = "All";
        applyFilters();
        startBannerAutoSlide();
    }

    private void showCategoriesView() {
        isCartView = false;
        textViewPageTitle.setText(R.string.title_browse_genres);
        viewPagerBanner.setVisibility(View.GONE);
        categoryScroll.setVisibility(View.VISIBLE);
        cartSummaryCard.setVisibility(View.GONE);
        adapter.setShowAddToCart(true);
        recyclerView.setAdapter(adapter);
        currentCategory = "All";
        applyFilters();
        updateTabStyles(findViewById(R.id.catAll));
        stopBannerAutoSlide();
    }

    private void showCartView() {
        isCartView = true;
        textViewPageTitle.setText(R.string.title_shopping_cart);
        viewPagerBanner.setVisibility(View.GONE);
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
        stopBannerAutoSlide();
    }
}
