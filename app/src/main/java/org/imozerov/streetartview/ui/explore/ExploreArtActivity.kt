package org.imozerov.streetartview.ui.explore

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_explore_art.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.detail.interfaces.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.explore.interfaces.Filterable
import org.imozerov.streetartview.ui.explore.list.ArtListFragment
import org.imozerov.streetartview.ui.explore.map.ArtMapFragment
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class ExploreArtActivity : AppCompatActivity(), ArtObjectDetailOpener {
    val TAG = "ExploreArtActivity"

    private var compositeSubscription: CompositeSubscription? = null

    @Inject
    lateinit var dataSource: DataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_art)

        (application as StreetArtViewApp).appComponent.inject(this);

        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(ArtMapFragment.newInstance(), getString(R.string.map_fragment_pager_label))
        adapter.addFragment(ArtListFragment.newInstance(), getString(R.string.list_fragment_pager_label))
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
    }

    override fun onStart() {
        super.onStart()

        val searchSubscription = RxSearchView
                .queryTextChanges(search_view)
                .doOnNext { Log.v(TAG, "Filtering $it") }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val fragment = supportFragmentManager
                            .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewpager.currentItem)
                    (fragment as? Filterable)?.applyFilter(it.toString())
                }

        val addArtObjectSubscription = RxView.clicks(add_art_object_button)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dataSource.addArtObjectStub()
                }

        compositeSubscription = CompositeSubscription();
        compositeSubscription!!.add(searchSubscription)
        compositeSubscription!!.add(addArtObjectSubscription)
    }

    override fun onStop() {
        super.onStop()
        compositeSubscription!!.clear()
    }

    override fun openArtObjectDetails(id: String?) {
        Log.d(TAG, "openArtObjectDetails($id)")
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitles[position]
        }
    }

}
