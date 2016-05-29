package com.mattpflance.hearthlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.mattpflance.hearthlist.models.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardDetailsFragment extends Fragment {

    private Card mCard;

    private ImageView mCardImage;
    private Button mRegImageButton;
    private Button mGoldImageButton;

    public CardDetailsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CardDetailsFragment newInstance() {
        CardDetailsFragment fragment = new CardDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCard = getArguments().getParcelable(CardDetailsActivity.CARD_ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        mCardImage = (ImageView) view.findViewById(R.id.card_image);

        mRegImageButton = (Button) view.findViewById(R.id.reg_image_button);
        mRegImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO change both button's background state to pressed when clicked
                String imagePath = mCard.getRegImageUrl();
                if (imagePath != null) {
                    Glide.with(v.getContext())
                            .load(imagePath)
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Load from internal
                            .into(mCardImage);
                }
            }
        });

        mGoldImageButton = (Button) view.findViewById(R.id.gold_image_button);
        mGoldImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO change both button's background state to pressed when clicked
                String imagePath = mCard.getGoldImageUrl();
                if (imagePath != null) {
                    GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mCardImage);
                    Glide.with(v.getContext())
                            .load(mCard.getGoldImageUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Load from internal
                            .into(imageViewTarget);
                }
            }
        });

        String imagePath = mCard.getRegImageUrl();
        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Load from internal
                    .into(mCardImage);
        }

        TextView textView = (TextView) view.findViewById(R.id.artist);
        textView.setText(String.format(getString(R.string.artist_text), mCard.getArtist()));

        textView = (TextView) view.findViewById(R.id.flavor_text);
        textView.setText(String.format(getString(R.string.flavor_text), mCard.getFlavorText()));

        return view;
    }

}
