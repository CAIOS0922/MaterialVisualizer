package caios.android.material_visualizer

abstract class VisualizerCallback {
    open fun onDrawFrame(index: Int): Float = 0f
}