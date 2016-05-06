package org.imozerov.streetartview.ui.explore.base

import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.ui.explore.ExploreArtActivity
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
import org.imozerov.streetartview.ui.explore.sort.SortOrder
import org.imozerov.streetartview.ui.explore.sort.storeSortOrder
import org.imozerov.streetartview.ui.model.ArtObjectUi
import org.imozerov.streetartview.ui.model.AuthorUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import org.robolectric.shadows.ShadowPreferenceManager
import org.robolectric.util.ActivityController
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by imozerov on 05.05.16.
 */
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
@RunWith(RobolectricGradleTestRunner::class)
class ArtListPresenterTest {
    var activityController: ActivityController<ExploreArtActivity>? = null

    var presenterUnderTest: ArtListPresenter? = null
    var artView: MockView? = null

    @Before
    fun setUp() {
        ShadowLog.stream = System.out;

        activityController = Robolectric.buildActivity(ExploreArtActivity::class.java)
        activityController!!.create().start()

        val sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(activityController!!.get());
        sharedPreferences.storeSortOrder(SortOrder.byDate)

        presenterUnderTest = UnderTestPresenter()
        artView = MockView()
    }

    @Test
    fun bindView_dataPropagatedToUi() {
        presenterUnderTest!!.bindView(artView!!, activityController!!.get())

        assertEquals(3, artView!!.artObjects.size)
    }

    @Test
    fun applyFilter_filtersList() {
        presenterUnderTest!!.bindView(artView!!, activityController!!.get())

        presenterUnderTest!!.applyFilter("1")

        assertEquals(1, artView!!.artObjects.size)
        assertTrue(artView!!.artObjects[0].name == "1")
    }

    @Test
    fun sortOrder_changedOnSharedPrefsChange() {
        val sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(activityController!!.get());
        sharedPreferences.storeSortOrder(SortOrder.byDistance)

        presenterUnderTest!!.bindView(artView!!, activityController!!.get())
        presenterUnderTest!!.onSharedPreferenceChanged(sharedPreferences, SortOrder.KEY)

        assertTrue(artView!!.artObjects[0].distanceTo < artView!!.artObjects[2].distanceTo)
        assertTrue(artView!!.artObjects[1].distanceTo < artView!!.artObjects[2].distanceTo)
    }

    class UnderTestPresenter : ArtListPresenter() {
        override fun computationScheduler(): Scheduler {
            return Schedulers.immediate()
        }

        override fun fetchData(): Observable<List<ArtObjectUi>> {
            return Observable.just(
                    listOf(
                            ArtObjectUi(id = "1", name = "1", authors = listOf(AuthorUi(id = "1", name = "1", photoUrl = "1")), description = "1", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 34.toDouble(), lng = 34.toDouble(), address = "1"),
                            ArtObjectUi(id = "2", name = "2", authors = listOf(AuthorUi(id = "2", name = "2", photoUrl = "2")), description = "2", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 35.toDouble(), lng = 34.toDouble(), address = "1"),
                            ArtObjectUi(id = "3", name = "3", authors = listOf(AuthorUi(id = "3", name = "3", photoUrl = "3")), description = "3", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 36.toDouble(), lng = 34.toDouble(), address = "1")
                    )
            )
        }
    }

    class MockView : ArtView {
        var artObjects = listOf<ArtObjectUi>()

        override fun showArtObjects(artObjectUis: List<ArtObjectUi>) {
            artObjects = artObjectUis
        }

        override fun stopRefresh() {
        }
    }
}