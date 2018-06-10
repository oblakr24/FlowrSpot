package dev.rokoblak.flowrspot.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import dev.rokoblak.flowrspot.R
import dev.rokoblak.flowrspot.app.FlowrApp
import dev.rokoblak.flowrspot.data.Flower
import dev.rokoblak.flowrspot.data.NetworkResult
import dev.rokoblak.flowrspot.details.NotImplementedActivity
import dev.rokoblak.flowrspot.utils.ContentLoadingView
import dev.rokoblak.flowrspot.utils.VerticalSpaceDecoration
import dev.rokoblak.flowrspot.utils.hideKeyboard
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.math.roundToInt


class HomeActivity : AppCompatActivity(), ContentLoadingView {
    // UI
    private val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val drawer by lazy { find<DrawerLayout>(R.id.drawer_layout) }
    private val appBarLayout by lazy { find<AppBarLayout>(R.id.app_bar_layout) }
    private val header by lazy { find<ViewGroup>(R.id.home_header) }
    private val searchContainer by lazy { find<ViewGroup>(R.id.container_search) }
    private val headerTitleText by lazy { find<TextView>(R.id.header_title) }
    private val headerSubtitleText by lazy { find<TextView>(R.id.header_subtitle) }
    private val searchEditText by lazy { find<EditText>(R.id.edit_search) }
    private val searchCancelButton by lazy { find<View>(R.id.btn_cancel) }
    private val searchIcon by lazy { find<ImageView>(R.id.icon_search) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.recycler_flowers) }
    private val bottomNavContainer by lazy { find<ViewGroup>(R.id.bottom_navigation) }
    // Content, loading and empty containers
    override val contentView: View by lazy { find<View>(R.id.recycler_flowers) }
    override val loadingView: View by lazy { find<View>(R.id.container_loading) }
    override val emptyView: View by lazy { find<View>(R.id.container_empty) }
    override val emptyMessage: TextView by lazy { find<TextView>(R.id.text_empty_message) }
    private val swipeRefresh by lazy { find<SwipeRefreshLayout>(R.id.swipe_container) }
    // Header search text max/min bottom margins, depending on collapse amount
    private val maxSearchBottomMargin by lazy { resources.getDimension(R.dimen.search_bottom_margin_large) }
    private val minSearchBottomMargin by lazy { resources.getDimension(R.dimen.search_bottom_margin_small) }

    private val offsetChangedListener: AppBarLayout.OnOffsetChangedListener by lazy {
        object : AppBarLayout.OnOffsetChangedListener {
            var handledOffset = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (verticalOffset == handledOffset) {
                    return
                }
                handledOffset = verticalOffset

                val fractionExpanded = 1f - ((-verticalOffset).toFloat() / appBarLayout.totalScrollRange)
                onHeaderCollapseChanged(fractionExpanded)
            }
        }
    }

    private val textWatcher by lazy {
        object : TextWatcher {
            private var timer: Timer? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(seq: CharSequence?, start: Int, before: Int, count: Int) {
                if (seq.isNullOrBlank()) {
                    searchCancelButton.visibility = View.GONE
                    searchIcon.visibility = View.VISIBLE
                } else {
                    searchIcon.visibility = View.GONE
                    searchCancelButton.visibility = View.VISIBLE
                }
            }

            // Wait 500 ms after the last char had been entered, then trigger the search
            override fun afterTextChanged(s: Editable?) {
                timer?.cancel()
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        s?.toString()?.let { query ->
                            if (query.isBlank()) {
                                hideKeyboard()
                            }
                            observeData(query)
                        }
                    }
                }, 500)
            }
        }
    }

    private val onFlowerClickedListener: OnFlowerClickedListener = {
        startActivity<NotImplementedActivity>()
    }

    private val onFlowerFavoritedListener: OnFlowerFavoritedListener = { _, position ->
        adapter.notifyItemChanged(position)
    }

    private val adapter by lazy { FlowersPagedListAdapter(onFlowerClickedListener, onFlowerFavoritedListener) }

    private val viewModel by lazy {
        val dataSource = (application as FlowrApp).dataSource
        val config by lazy {
            PagedList.Config.Builder()
                    .setPageSize(20)
                    .setInitialLoadSizeHint(20)
                    .build()
        }
        ViewModelProviders.of(this, HomeViewModelFactory(dataSource, config)).get(HomeViewModel::class.java)
    }

    private val dataObserver by lazy {
        Observer<PagedList<Flower>> { pagedList ->
            adapter.submitList(pagedList)
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Back to non-splash theme
        setTheme(R.style.AppTheme_HomeScreen)

        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.title = ""
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_open)

        initUI()

        viewModel.getDataResult().observe(this, Observer<NetworkResult> { networkResult ->
            val res = when (networkResult) {
                is NetworkResult.Success -> showContent(false)
                is NetworkResult.NoData -> showEmpty(getString(R.string.empty_no_data))
                is NetworkResult.Timeout -> showEmpty(getString(R.string.empty_no_network))
                is NetworkResult.NoNetwork -> showEmpty(getString(R.string.empty_timeout))
                is NetworkResult.Error -> showEmpty(getString(R.string.empty_error_try_again))
                null -> showEmpty()
            }
            // Hide keyboard when anything is returned
            hideKeyboard()
        })

        showLoading()
        observeData()
    }

    private fun observeData(query: String = "") {
        with(viewModel.getFlowers(query)) {
            runOnUiThread {
                removeObservers(this@HomeActivity)
                observe(this@HomeActivity, dataObserver)
            }
        }
    }

    private fun initUI() {
        recyclerView.addItemDecoration(VerticalSpaceDecoration(dip(12),
                dip(12) + resources.getDimension(R.dimen.bottom_nav_height).toInt()))
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(textWatcher)

        searchCancelButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        }

        appBarLayout.addOnOffsetChangedListener(offsetChangedListener)

        // Bottom navigation icons
        with(bottomNavContainer) {
            val clickListenerToDetails: View.OnClickListener = View.OnClickListener { startActivity<NotImplementedActivity>() }
            find<View>(R.id.icon_bottom_favorite).setOnClickListener(clickListenerToDetails)
            find<View>(R.id.icon_bottom_comment).setOnClickListener(clickListenerToDetails)
            find<View>(R.id.icon_bottom_new_sighting).setOnClickListener(clickListenerToDetails)
            find<View>(R.id.icon_bottom_sighting_list).setOnClickListener(clickListenerToDetails)
        }

        // Reload the data when swipe to refresh is pulled
        swipeRefresh.setOnRefreshListener {
            showLoading()
            observeData(searchEditText.text.toString())
        }
    }

    /**
     * Animate the header contents' bottom padding and scale the title and subtitle's opacity
     */
    private fun onHeaderCollapseChanged(fractionExpanded: Float) {
        val attenuatedAlphaLight = attenuateFraction(fractionExpanded, 0.4f)
        headerTitleText.alpha = attenuatedAlphaLight
        headerSubtitleText.alpha = attenuatedAlphaLight

        val scaledPadding = scaleValue(maxSearchBottomMargin, minSearchBottomMargin, fractionExpanded)
        // Set the padding instead of margin so that the layout doesn't need to be requested again
        searchContainer.setPadding(0, 0, 0, scaledPadding.roundToInt())

        header.post {
            searchContainer.invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appBarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Helper, gets the valje between min and max given a fraction from 0.0 to 1.0
    private fun scaleValue(max: Float, min: Float, fraction: Float) = min + (max - min) * fraction

    // Helper, attenuates a fraction from 0.0 to 1.0 by some factor (makes for a steeper transition)
    private fun attenuateFraction(fraction: Float, factor: Float) = (fraction - factor) / (1.0f - factor)
}
