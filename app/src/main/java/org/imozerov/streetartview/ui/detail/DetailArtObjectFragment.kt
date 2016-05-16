package org.imozerov.streetartview.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.flipboard.bottomsheet.BaseViewTransformer
import com.flipboard.bottomsheet.BottomSheetLayout
import com.flipboard.bottomsheet.ViewTransformer
import com.flipboard.bottomsheet.commons.BottomSheetFragment
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.fragment_detail.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.R.layout.fragment_detail
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.location.NIZHNY_NOVGOROD_LOCATION
import org.imozerov.streetartview.location.addArtObjectSimpleMarker
import org.imozerov.streetartview.location.getCachedLocation
import org.imozerov.streetartview.location.printableDistanceTo
import org.imozerov.streetartview.storage.IDataSource
import org.imozerov.streetartview.ui.extensions.addAll
import org.imozerov.streetartview.ui.extensions.getDrawableSafely
import org.imozerov.streetartview.ui.extensions.sendScreen
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by ibulatov on 10.05.2016.
 */

class DetailArtObjectFragment : BottomSheetFragment() {

    val PICK_IMAGE_REQUEST = 1

    @Inject
    lateinit var dataSource: IDataSource
    @Inject
    lateinit var tracker: Tracker
    @Inject
    lateinit var prefs: SharedPreferences

    private val artObjectUi by lazy { dataSource.getArtObject(arguments.getString(EXTRA_KEY_ART_OBJECT_DETAIL_ID)) }

    val compositeSubscription: CompositeSubscription = CompositeSubscription()

    private val mDetailImageMinHeight by lazy {activity.resources.getDimension(R.dimen.detail_image_min_height)}
    private val mDetailImageMaxHeight by lazy {activity.resources.getDimension(R.dimen.detail_image_max_height)}

    private var mDetailImageMinWidth: Float = 0f
    private var mDetailImageMaxWidth: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.applicationContext as StreetArtViewApp).appComponent!!.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(fragment_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT)
        tracker.sendScreen("DetailArtObjectFragment: ${artObjectUi.name}")

        bindViews()
        initImagesGallery()
        setupMapAndDistanceTo()
    }

    override fun onStart() {
        super.onStart()

        compositeSubscription.addAll(
                RxView.clicks(detail_share_button).subscribe { shareArtObjectInfo() },
                RxView.clicks(detail_set_favourite_button).subscribe { changeFavouriteStatus() },
                RxView.clicks(art_object_detail_address).subscribe { showInGoogleMaps() }
        )
    }

    override fun onStop() {
        super.onStop()
        compositeSubscription.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val position = data?.getIntExtra(ImageViewActivity.EXTRA_IMAGE_CHOSEN_IN_VIEWPAGER, 0)
                art_object_detail_image.currentItem = position!!
            }
        }
    }

    private fun bindViews() {

        collapsing_toolbar.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mDetailImageMaxWidth = collapsing_toolbar.width.toFloat()
                mDetailImageMinWidth = mDetailImageMaxWidth!! * mDetailImageMinHeight / mDetailImageMaxHeight
                collapsing_toolbar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        art_object_detail_name.text = "\"${artObjectUi.name}\""
        art_object_detail_author.text = artObjectUi.authorsNames()
        if (artObjectUi.description.isBlank()) {
            art_object_detail_description.visibility = View.GONE
        } else {
            art_object_detail_description.text = artObjectUi.description
        }

        art_object_detail_address.text = artObjectUi.address
        setFavouriteIcon(artObjectUi.isFavourite)

        val picsNumber = artObjectUi.picsUrls.size
        if (picsNumber > 1) {
            art_object_images_number.text = resources.getQuantityString(R.plurals.photos, picsNumber, picsNumber)
        } else {
            art_object_images_number.visibility = View.GONE
        }
    }


    private fun initImagesGallery() {
        art_object_detail_image.adapter = GalleryPagerAdapter(activity, artObjectUi.picsUrls, { position ->
            val intent = Intent(activity, ImageViewActivity::class.java)
            intent.putExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectUi.id)
            intent.putExtra(EXTRA_IMAGE_CHOSEN_IN_DETAILS, position)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        })
    }

    private fun setupMapAndDistanceTo() {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(art_object_detail_map.id, mapFragment).commit()
        mapFragment.getMapAsync {
            val artObjectLocation = LatLng(artObjectUi.lat, artObjectUi.lng)

            val userLocation = prefs.getCachedLocation()
            if (userLocation != NIZHNY_NOVGOROD_LOCATION) {
                art_object_detail_distance.text = "${userLocation.printableDistanceTo(artObjectLocation)} km"
                art_object_detail_distance.visibility = View.VISIBLE
            }

            with (it) {
                isMyLocationEnabled = true
                addArtObjectSimpleMarker(artObjectUi)
                moveCamera(CameraUpdateFactory.newLatLngZoom(artObjectLocation, 14f))

                setOnMapLoadedCallback {
                    val builder = LatLngBounds.Builder()
                    builder.include(artObjectLocation)
                    builder.include(userLocation)
                    animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 80))
                }
            }
        }
    }

    private fun shareArtObjectInfo() {
        Observable.create<Bitmap> {
            it.onNext(Glide.with(activity.applicationContext).load(artObjectUi.picsUrls[art_object_detail_image.currentItem]).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get())
            it.onCompleted()
        }
                .map { MediaStore.Images.Media.insertImage(activity.contentResolver, it, "Street Art object temporary file", null) }
                .map { Uri.parse(it) }
                .doOnNext { tracker.sendScreen("Sharing ${artObjectUi.name}") }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    val shareImageIntent = Intent()
                    shareImageIntent.action = Intent.ACTION_SEND
                    shareImageIntent.putExtra(Intent.EXTRA_STREAM, it)
                    shareImageIntent.putExtra(Intent.EXTRA_TEXT, "${artObjectUi.authorsNames()} - ${artObjectUi.name} \n${artObjectUi.address}")
                    shareImageIntent.type = "image/*"
                    startActivity(Intent.createChooser(shareImageIntent, resources.getText(R.string.share)))
                }
    }

    private fun showInGoogleMaps() {
        tracker.sendScreen("Navigation to ${artObjectUi.name}")
        val latitude = artObjectUi.lat
        val longitude = artObjectUi.lng
        val label = artObjectUi.address
        val uriBegin = "geo: $latitude, $longitude"
        val query = "$latitude, $longitude ($label)"
        val encodedQuery = Uri.encode(query)
        val uriString = "$uriBegin?q=$encodedQuery&z=16"
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun changeFavouriteStatus() {
        artObjectUi.isFavourite = !artObjectUi.isFavourite
        dataSource.setFavourite(artObjectUi.id, artObjectUi.isFavourite)
        setFavouriteIcon(artObjectUi.isFavourite)
    }

    private fun setFavouriteIcon(isFavourite: Boolean) {
        if (isFavourite) {
            detail_set_favourite_button.setImageDrawable(activity.getDrawableSafely(R.drawable.ic_favorite_black_24dp))
        } else {
            detail_set_favourite_button.setImageDrawable(activity.getDrawableSafely(R.drawable.ic_favorite_border_black_24dp))
        }
    }

    override fun getViewTransformer(): ViewTransformer {
        return object : BaseViewTransformer() {
            override fun transformView(translation: Float, maxTranslation: Float, peekedTranslation: Float, parent: BottomSheetLayout, view: View) {

                val progress = (translation - peekedTranslation) / (maxTranslation - peekedTranslation)

                art_object_collapsing_content.layoutParams.height = (mDetailImageMinHeight + progress * (mDetailImageMaxHeight - mDetailImageMinHeight)).toInt()
                collapsing_toolbar.layoutParams.height = (mDetailImageMinHeight + progress * (mDetailImageMaxHeight - mDetailImageMinHeight)).toInt()

                val lp: CollapsingToolbarLayout.LayoutParams = art_object_collapsing_content.layoutParams as CollapsingToolbarLayout.LayoutParams

                if(translation == maxTranslation) {
                    lp.width = RelativeLayout.LayoutParams.MATCH_PARENT
                    lp.height = mDetailImageMaxHeight.toInt()
                } else {
                    lp.width = (mDetailImageMinWidth + (mDetailImageMaxWidth - mDetailImageMinWidth) * progress).toInt();
                    lp.height = (mDetailImageMinHeight + (mDetailImageMaxHeight - mDetailImageMinHeight) * progress).toInt();
                }

                art_object_collapsing_content.requestLayout()
                collapsing_toolbar.requestLayout()
            }
        }
    }


    companion object {

        val EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID"
        val EXTRA_IMAGE_CHOSEN_IN_DETAILS = "EXTRA_IMAGE_CHOSEN_IN_DETAILS"

        fun newInstance(artObjectId: String): DetailArtObjectFragment {
            val fragment = DetailArtObjectFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_KEY_ART_OBJECT_DETAIL_ID, artObjectId)
            fragment.arguments = bundle
            return fragment
        }
    }
}