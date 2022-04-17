package caios.android.material_visualizer

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import caios.android.material_visualizer.databinding.ViewFrameBinding

class VisualizerHolder(val binding: ViewFrameBinding): RecyclerView.ViewHolder(binding.root)

class VisualizerAdapter(context: Context): RecyclerView.Adapter<VisualizerHolder>() {

    private var callback: VisualizerCallback? = null

    private val dataList = mutableListOf<Float>()
    private val defaultHeight = convertDpToPx(context, 4f)

    private var itemCount = Int.MAX_VALUE - 1
    private var rootHeight = 0
    private var rootWidth = 0

    var vFrameAnimateDuration = 300
    var vFrameAnimateDelay = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisualizerHolder {
        return VisualizerHolder(ViewFrameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VisualizerHolder, position: Int) {
        val tag = position

        holder.binding.frame.updateLayoutParams { height = defaultHeight.toInt() }
        holder.binding.frame.tag = position

        (dataList.elementAtOrNull(position) ?: callback?.onDrawFrame(position))?.let {
            if(dataList.elementAtOrNull(position) == null) dataList.add(it) else dataList[position] = it

            holder.binding.root.afterMeasured {
                val resultHeight = ((rootHeight - defaultHeight) / 100) * it.coerceIn(1f..100f)

                ValueAnimator.ofFloat(defaultHeight, resultHeight).apply {
                    duration = vFrameAnimateDuration.toLong()
                    startDelay = vFrameAnimateDelay.toLong()
                    interpolator = DecelerateInterpolator()

                    addUpdateListener {
                        if (holder.binding.frame.tag == tag) {
                            holder.binding.frame.updateLayoutParams { height = (animatedValue as Float).toInt() }
                        }
                    }
                }.start()
            }
        }
    }

    override fun getItemCount(): Int = itemCount

    fun setCallback(callback: VisualizerCallback) {
        this.callback = callback
    }

    fun loadData(dataList: List<Float>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)

        itemCount = this.dataList.size
        notifyDataSetChanged()
    }

    private fun convertDpToPx(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }

    private inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
        if(rootWidth > 0 && rootHeight > 0) {
            f()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        rootWidth = measuredWidth
                        rootHeight = measuredHeight
                        f()
                    }
                }
            })
        }
    }
}