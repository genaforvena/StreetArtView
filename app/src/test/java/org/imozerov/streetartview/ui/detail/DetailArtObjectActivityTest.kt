package org.imozerov.streetartview.ui.detail

import android.content.Intent
import kotlinx.android.synthetic.main.activity_detail.*
import org.imozerov.streetartview.BuildConfig
import org.imozerov.streetartview.TestAppModule
import org.imozerov.streetartview.ui.model.ArtObjectUi
import org.imozerov.streetartview.ui.model.AuthorUi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.util.ActivityController

/**
 * Created by imozerov on 04.03.16.
 */
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
@RunWith(RobolectricGradleTestRunner::class)
class DetailArtObjectActivityTest {
    var artObjectUi: ArtObjectUi? = null
    var activityController: ActivityController<DetailArtObjectActivity>? = null

    @Before
    fun setUp() {
        val author = AuthorUi(name = "name", id = "12", photoUrl = "photo")
        val authors = listOf(author)

        artObjectUi = ArtObjectUi("id", name = "name",
                description = "desc",
                lat = 34.34, lng = 34.34,
                thumbPicUrl = "pic",
                authors = authors,
                picsUrls = listOf("art"),
                address = "Some street, 34")

        Mockito.`when`(TestAppModule.dataSourceMock.getArtObject(Mockito.anyString())).thenReturn(
                artObjectUi
        )

        val intent = Intent(RuntimeEnvironment.application, DetailArtObjectActivity::class.java)
        intent.putExtra(DetailArtObjectActivity.EXTRA_KEY_ART_OBJECT_DETAIL_ID, "12")

        activityController = Robolectric.buildActivity(DetailArtObjectActivity::class.java)
        activityController!!.withIntent(intent).create()
    }

    @Test
    fun isAbleToRecreateActivity() {
        activityController!!.start().resume().restart()
    }

    @Test
    fun viewsAreBindedToValues() {
        val activity = activityController!!.get()

        assertTrue(activity.art_object_detail_author.text == artObjectUi!!.authors[0].name)
        assertTrue(activity.art_object_detail_author.text == artObjectUi!!.name)
        assertTrue(activity.art_object_images_number.text.contains(artObjectUi!!.picsUrls.size.toString()))
        assertTrue(activity.art_object_detail_description.text == artObjectUi!!.description)
        assertTrue(activity.art_object_detail_address.text == artObjectUi!!.address)
    }
}