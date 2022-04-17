package caios.android.material_visualizer_sample

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import caios.android.material_visualizer.VisualizerAdapter
import caios.android.material_visualizer.VisualizerCallback
import caios.android.material_visualizer_sample.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val random = Random(System.currentTimeMillis())

        binding.visualizer.setCallback(object : VisualizerCallback() {
            override fun onDrawFrame(index: Int): Float {
                return random.nextFloat() * 50
            }
        })

        binding.recButton.setOnClickListener {
            if(binding.visualizer.isActive()) {
                binding.visualizer.stop()
            } else {
                binding.visualizer.start()
            }
        }
    }
}