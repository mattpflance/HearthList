package com.mattpflance.hearthlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mattpflance.hearthlist.models.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardDetailsFragment extends Fragment {

    private Card mCard;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.card_image);

        String imagePath = mCard.getRegImageURL();
        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Load from internal
                    .into(imageView);
        }

        return view;
    }

}
