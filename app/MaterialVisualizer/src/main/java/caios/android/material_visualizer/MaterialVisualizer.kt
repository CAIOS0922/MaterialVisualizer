package caios.android.material_visualizer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MaterialVisualizer(context: Context, attributeSet: AttributeSet): RecyclerView(context, attributeSet) {

    private val visualizerAdapter by lazy { VisualizerAdapter(context) }

    private var isActive = false

    private var vOrientation = LinearLayoutManager.HORIZONTAL
    private var vScrollSpeed = 50

    private val autoScrollProcess = object : Runnable {
        override fun run() {
            smoothScrollBy(vScrollSpeed, 0, LinearInterpolator(), 100)
            if(isActive) postDelayed(this, 100)
        }
    }

    private val scrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if(visualizerAdapter.itemCount - 1 == (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) isActive = false
        }
    }

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.MaterialVisualizer, 0, 0).use {
            vOrientation = it.getInt(R.styleable.MaterialVisualizer_vOrientation, 0)
            vScrollSpeed = it.getInt(R.styleable.MaterialVisualizer_vScrollSpeed, 50)
            visualizerAdapter.vFrameAnimateDuration = it.getInt(R.styleable.MaterialVisualizer_vFrameAnimateDuration, 300)
            visualizerAdapter.vFrameAnimateDelay = it.getInt(R.styleable.MaterialVisualizer_vFrameAnimateDelay, 0)
        }

        clipToPadding = false
        adapter = visualizerAdapter
        layoutManager = LinearLayoutManager(context).apply {
            orientation = if(vOrientation == 0) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        updatePadding(left = measuredWidth / 2, right = measuredWidth / 2)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(scrollListener)
    }

    fun start() {
        isActive = true
        handler.post(autoScrollProcess)
    }

    fun stop() {
        isActive = false
        handler.removeCallbacks(autoScrollProcess)
    }

    fun loadData(dataList: List<Float>) {
        visualizerAdapter.loadData(dataList)
    }

    fun isActive() = isActive

    fun setCallback(callback: VisualizerCallback) {
        visualizerAdapter.setCallback(callback)
    }
}