package com.mayur.personalitydevelopment.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mayur.personalitydevelopment.R
import com.mayur.personalitydevelopment.databinding.FragmentOptionsBottomSheetBinding
import com.mayur.personalitydevelopment.listener.BottomSheetListener

class OptionsBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentOptionsBottomSheetBinding
    private var mListener: BottomSheetListener? = null
    private var isCommentWriter: Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_options_bottom_sheet, container, false)

        isCommentWriter = requireArguments().getBoolean("isCommentWriter")

        if (isCommentWriter) {
            binding.bottomSheetDelete.visibility = View.VISIBLE
            binding.bottomSheetReport.visibility = View.GONE
        } else {
            binding.bottomSheetDelete.visibility = View.GONE
            binding.bottomSheetReport.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.bottomSheetReport.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onBottomSheetItemClick(1)
        }

        binding.bottomSheetDelete.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onBottomSheetItemClick(2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomSheetListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    context.toString()
                            .toString() + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): OptionsBottomSheetFragment {
            val fragment = OptionsBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}