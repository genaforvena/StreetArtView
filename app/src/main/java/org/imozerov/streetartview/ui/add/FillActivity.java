package org.imozerov.streetartview.ui.add;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.imozerov.streetartview.R;
import org.imozerov.streetartview.bus.RxBus;

import rx.Subscription;

public class FillActivity extends AppCompatActivity {

    public static final String EXTRA_PICKED_IMAGE_URI = "EXTRA_PICKED_IMAGE_URI";

    private Subscription rxBusSubscription;
    private RxBus rxBus;

    private EditText address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill);

        Uri imageUri;
        try {
            imageUri = getIntent().getParcelableExtra(EXTRA_PICKED_IMAGE_URI);
        } catch (ClassCastException cce) {
            throw new RuntimeException("The activity that start FillActivity must set image Uri extra");
        }

        setSupportActionBar((Toolbar) findViewById(R.id.fill_toolbar));

        ImageView fillImage = (ImageView) findViewById(R.id.fill_image);
        address = (EditText) findViewById(R.id.fill_address);

        Glide.with(this)
                .load(imageUri)
                .into(fillImage);

//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (addressEmpty()) {
//                    Toast.makeText(FillActivity.this, getString(R.string.non_empty_address), Toast.LENGTH_LONG).show();
//                } else {
//                    // TODO: request to server to add new artwork
//                }
//            }
//        });

    }

    private boolean addressEmpty() {
        return "".equals((address.getText().toString()));
    }

}