package com.kakusummer.androidutilsturbo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kakusummer.androidutilsturbo.R
import com.kakusummer.androidutilsturbo.databinding.Fragment1Binding
import com.kakusummer.androidutilsturbo.databinding.Fragment3Binding


class Fragment3 : Fragment() {
    private lateinit var binding: Fragment3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initFunctionMarquee()
    }

    private fun initFunctionMarquee() {
        binding.apply {
            if (tv1.text.length > 9) {
                tv1.isSelected = true  // 使 TextView 获得焦点，启动滚动效果,只有宽度不足以容纳的时候才会滚动
            }
        }

    }

    private fun initListener() {

    }
}