package org.imozerov.streetartview.ui.explore.base

import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.ui.explore.ExploreArtActivity
import org.imozerov.streetartview.ui.explore.interfaces.ArtView
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

        presenterUnderTest = UnderTestPresenter()
        artView = MockView()
    }

    @Test
    fun testBindView() {
        presenterUnderTest!!.bindView(artView!!, activityController!!.get())

        assertEquals(3, artView!!.artObjects.size)
    }

    @Test
    fun testApplyFilter() {
        presenterUnderTest!!.bindView(artView!!, activityController!!.get())

        presenterUnderTest!!.applyFilter("1")

        assertEquals(1, artView!!.artObjects.size)
        assertTrue(artView!!.artObjects[0].name == "1")
    }

    class UnderTestPresenter : ArtListPresenter() {
        override fun computationScheduler(): Scheduler {
            return Schedulers.immediate()
        }

        override fun fetchData(): Observable<List<ArtObjectUi>> {
            return Observable.just(
                    listOf(
                            ArtObjectUi(id = "1", name = "1", authors = listOf(AuthorUi(id = "1", name = "1", photoUrl = "1")), description = "1", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 34.toDouble(), lng = 34.toDouble(), address = "1", distanceTo = 1),
                            ArtObjectUi(id = "2", name = "2", authors = listOf(AuthorUi(id = "2", name = "2", photoUrl = "2")), description = "2", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 34.toDouble(), lng = 34.toDouble(), address = "1", distanceTo = 2),
                            ArtObjectUi(id = "3", name = "3", authors = listOf(AuthorUi(id = "3", name = "3", photoUrl = "3")), description = "3", thumbPicUrl = "1", picsUrls = listOf<String>(), lat = 34.toDouble(), lng = 34.toDouble(), address = "1", distanceTo = 3)
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