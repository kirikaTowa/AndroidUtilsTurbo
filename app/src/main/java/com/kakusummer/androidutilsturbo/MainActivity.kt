package com.kakusummer.androidutilsturbo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kakusummer.androidutilsturbo.databinding.ActivityMainBinding
import com.kakusummer.androidutilsturbo.ui.fragment.Fragment1
import com.kakusummer.androidutilsturbo.ui.fragment.Fragment2
import com.kakusummer.androidutilsturbo.ui.fragment.Fragment3
import com.kakusummer.androidutilsturbo.ui.fragment.Fragment4

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tabs by lazy { arrayOf(binding.tab1, binding.tab2, binding.tab3, binding.tab4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 设置状态栏和底部导航栏的内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        val adapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = adapter
    }

    private fun initData() {
        initLiveData()
    }

    private fun initLiveData() {
    }

    private fun initListener() {
        binding.apply {
            tab1.setOnClickListener {
                viewpager.currentItem = 0
                updateTabSelection(0)
            }
            tab2.setOnClickListener {
                viewpager.currentItem = 1
                updateTabSelection(1)
            }
            tab3.setOnClickListener {
                viewpager.currentItem = 2
                updateTabSelection(2)
            }
            tab4.setOnClickListener {
                viewpager.currentItem = 3
                updateTabSelection(3)
            }

            viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateTabSelection(position) // 更新选中状态
                }
            })
        }


    }

    // 更新 Tab 的选中状态
    private fun updateTabSelection(selectedPosition: Int) {
        tabs.forEachIndexed { index, tab ->
            // 将选中的 Tab 设置为选中状态，其他 Tab 设置为未选中
            tab.isSelected = index == selectedPosition
        }
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                2 -> Fragment3()
                3 -> Fragment4()
                else -> Fragment1()
            }
        }

        override fun getItemCount(): Int {
            return 4 // 4个Fragment
        }
    }


}
