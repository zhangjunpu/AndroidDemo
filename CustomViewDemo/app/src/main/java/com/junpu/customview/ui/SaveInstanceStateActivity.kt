package com.junpu.customview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.junpu.customview.R
import com.junpu.log.L

/**
 *
 * @author junpu
 * @date 2021/6/2
 */
class SaveInstanceStateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        L.vv("Activity onCreate: $savedInstanceState, ${intent.extras}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_instance_state)
    }

    override fun onStart() {
        L.vv("Activity onStart")
        super.onStart()
    }

    override fun onResume() {
        L.vv("Activity onResume")
        super.onResume()
    }

    override fun onPause() {
        L.vv("Activity onPause")
        super.onPause()
    }

    override fun onStop() {
        L.vv("Activity onStop")
        super.onStop()
    }

    override fun onDestroy() {
        L.vv("Activity onDestroy")
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        L.vv("Activity onSaveInstanceState: $outState")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        L.vv("Activity onRestoreInstanceState: $savedInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
    }
}

class SaveInstanceStateFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        L.vv("Fragment onCreate: $savedInstanceState")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        L.vv("Fragment onCreateView: $savedInstanceState")
        return inflater.inflate(R.layout.fragment_save_instance_state, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        L.vv("Fragment onViewCreated: $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        L.vv("Fragment onStart")
        super.onStart()
    }

    override fun onResume() {
        L.vv("Fragment onResume")
        super.onResume()
    }

    override fun onPause() {
        L.vv("Fragment onPause")
        super.onPause()
    }

    override fun onStop() {
        L.vv("Fragment onStop")
        super.onStop()
    }

    override fun onDestroy() {
        L.vv("Fragment onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        L.vv("Fragment onDestroyView")
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        L.vv("Fragment onActivityCreated: $savedInstanceState")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        L.vv("Fragment onSaveInstanceState: $outState")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        L.vv("Fragment onViewStateRestored: $savedInstanceState")
        super.onViewStateRestored(savedInstanceState)
    }

}