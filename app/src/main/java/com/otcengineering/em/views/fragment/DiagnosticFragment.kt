package com.otcengineering.em.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.data.DiagnosticData
import com.otcengineering.em.databinding.FragmentDiagnosticBinding
import com.otcengineering.em.databinding.FragmentDiagnosticUpdateBinding
import com.otcengineering.em.model.DiagnosticUpdateViewModel
import com.otcengineering.em.utils.interfaces.OnClickListener
import com.otcengineering.em.views.components.RecyclerViewAdapter
import com.otcengineering.em.views.components.addSource

class DiagnosticFragment : Fragment() {

    private val binding: FragmentDiagnosticBinding by lazy {
        FragmentDiagnosticBinding.inflate(
            layoutInflater
        )
    }

     private val viewModel = DiagnosticUpdateViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        viewModel.getDTC(requireContext()).subscribe({

        },{
            
        })

        with(binding.recyclerController) {
            addSource(
                R.layout.row_diagnostic,
                viewModel.controll, object : OnClickListener<DiagnosticData> {
                    override fun onItemClick(view: View, t: DiagnosticData) {

                    }


                }
            )
            setRecyclerListener {
                it as RecyclerViewAdapter<*>.ViewHolder<*>
                it.onClear()
            }
        }

        with(binding.recyclerbattery) {
            addSource(
                R.layout.row_diagnostic,
                viewModel.battery, object : OnClickListener<DiagnosticData> {
                    override fun onItemClick(view: View, t: DiagnosticData) {

                    }


                }
            )
            setRecyclerListener {
                it as RecyclerViewAdapter<*>.ViewHolder<*>
                it.onClear()
            }
        }

        return binding.root
    }

}