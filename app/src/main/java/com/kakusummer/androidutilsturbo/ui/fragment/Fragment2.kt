package com.kakusummer.androidutilsturbo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kakusummer.androidutilsturbo.action.music_service.MusicServiceActivity
import com.kakusummer.androidutilsturbo.databinding.Fragment2Binding


class Fragment2 : Fragment() {

    private lateinit var binding: Fragment2Binding // 绑定类

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 使用 DataBinding 进行绑定
        binding = Fragment2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.tv1.setOnClickListener {
            context?.let {
                val intent: Intent = Intent(it, MusicServiceActivity::class.java)
                it.startActivity(intent)
            }
        }
    }
}
