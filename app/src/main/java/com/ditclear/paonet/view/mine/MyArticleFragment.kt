package com.ditclear.paonet.view.mine

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ditclear.paonet.R
import com.ditclear.paonet.aop.annotation.SingleClick
import com.ditclear.paonet.databinding.RefreshFragmentBinding
import com.ditclear.paonet.di.scope.FragmentScope
import com.ditclear.paonet.helper.extens.dpToPx
import com.ditclear.paonet.helper.extens.navigateToActivity
import com.ditclear.paonet.helper.presenter.ListPresenter
import com.ditclear.paonet.helper.adapter.recyclerview.ItemClickPresenter
import com.ditclear.paonet.helper.adapter.recyclerview.SingleTypeAdapter
import com.ditclear.paonet.helper.extens.bindLifeCycle
import com.ditclear.paonet.view.article.ArticleDetailActivity
import com.ditclear.paonet.view.article.viewmodel.ArticleItemViewModel
import com.ditclear.paonet.view.article.viewmodel.ArticleListViewModel
import com.ditclear.paonet.view.base.BaseFragment
import com.ditclear.paonet.view.home.MainActivity
import com.ditclear.paonet.view.mine.viewmodel.MyArticleViewModel
import com.ditclear.paonet.viewmodel.StateModel
import javax.inject.Inject

/**
 * 页面描述：MyArticleActivity
 *
 * Created by ditclear on 2017/10/15.
 */
@FragmentScope
class MyArticleFragment : BaseFragment<RefreshFragmentBinding>(), ItemClickPresenter<ArticleItemViewModel>, ListPresenter {
    override val state: StateModel
        get() = viewModel.state

    private val viewModel: MyArticleViewModel  by lazy {
        getInjectViewModel(MyArticleViewModel::class.java)
    }

    val mAdapter: SingleTypeAdapter<ArticleItemViewModel> by lazy {
        SingleTypeAdapter<ArticleItemViewModel>(mContext, R.layout.article_list_item, viewModel.obserableList).apply { itemPresenter = this@MyArticleFragment }
    }

    var showTab: Boolean = false

    override fun getLayoutId(): Int = R.layout.refresh_fragment

    companion object {

        private val SHOW_TAB = "show_tab"

        fun newInstance(showTab: Boolean = false): MyArticleFragment {
            val bundle = Bundle()
            bundle.putBoolean(SHOW_TAB, showTab)
            val fragment = MyArticleFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun loadData(isRefresh: Boolean) {
        viewModel.loadData(isRefresh).bindLifeCycle(this)
                .subscribe ({},{toastFailure(it)})
    }

    override fun initArgs(savedInstanceState: Bundle?) {
        showTab = arguments?.getBoolean(SHOW_TAB, false)?:false
    }

    @SingleClick
    override fun onItemClick(v: View?, item: ArticleItemViewModel) {
        activity?.navigateToActivity(ArticleDetailActivity::class.java, item.article)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)

    }

    override fun initView() {
        (activity as MainActivity).needShowTab(showTab)

        mBinding.run {
            vm = viewModel
            presenter=this@MyArticleFragment
            recyclerView.adapter = mAdapter
            recyclerView.addItemDecoration(object : DividerItemDecoration(activity, DividerItemDecoration.VERTICAL) {
                override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect?.top = activity?.dpToPx(R.dimen.xdp_12_0)
                }
            })
        }
        show()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            show()
        }
    }

    fun show() {
        (activity as MainActivity).needShowTab(false)
    }

}