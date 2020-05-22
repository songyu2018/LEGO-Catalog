package com.elifox.legocatalog.legotheme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elifox.legocatalog.R
import com.elifox.legocatalog.data.Result
import com.elifox.legocatalog.databinding.FragmentThemesBinding
import com.elifox.legocatalog.di.Injectable
import com.elifox.legocatalog.di.injectViewModel
import com.elifox.legocatalog.ui.VerticalItemDecoration
import com.elifox.legocatalog.ui.hide
import com.elifox.legocatalog.ui.show
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class LegoThemeFragment : Fragment(), Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LegoThemeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = injectViewModel(viewModelFactory)

        /*
        * Android data binding generates a Binding class based on this layout. This class holds all the bindings from the layout properties,
        * i.e., the defined variable to the corresponding views. It also provides generated setters for your data elements from the layout.
        * The name of the generated class is based on the name of the layout file. This name is converted to Pascal case and the Binding suffix is added to it.
        * */
        val binding = FragmentThemesBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = LegoThemeAdapter()
        binding.recyclerView.addItemDecoration(
                VerticalItemDecoration(resources.getDimension(R.dimen.margin_normal).toInt(), true) )
        binding.recyclerView.adapter = adapter

        subscribeUi(binding, adapter)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun subscribeUi(binding: FragmentThemesBinding, adapter: LegoThemeAdapter) {
        viewModel.legoThemes.observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBar.hide()
                    result.data?.let { adapter.submitList(it) }
                }
                Result.Status.LOADING -> binding.progressBar.show()
                Result.Status.ERROR -> {
                    binding.progressBar.hide()
                    Snackbar.make(binding.root, result.message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}
