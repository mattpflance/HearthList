package com.mattpflance.hearthlist;

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

        Button regImageButton = (Button) view.findViewById(R.id.reg_image_button);
        regImageButton.setOnClickListener(new View.OnClickListener() {
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

        Button goldImageButton = (Button) view.findViewById(R.id.gold_image_button);
        goldImageButton.setOnClickListener(new View.OnClickListener() {
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

        String info = mCard.getArtist();
        TextView textView = (TextView) view.findViewById(R.id.artist);
        if (info != null) {
            textView.setText(String.format(getString(R.string.artist_text), info));
        } else {
            textView.setVisibility(View.GONE);
        }

        info = mCard.getFlavorText();
        textView = (TextView) view.findViewById(R.id.flavor_text);
        if (info != null) {
            textView.setText(String.format(getString(R.string.flavor_text), info));
        } else {
            textView.setVisibility(View.GONE);
        }

        info = mCard.getCardSet();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_set_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.card_set_row).setVisibility(View.GONE);
        }

        info = mCard.getPlayerClass();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_class_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.card_class_row).setVisibility(View.GONE);
        }

        info = mCard.getType();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_type_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.card_type_row).setVisibility(View.GONE);
        }

        info = mCard.getRarity();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_rarity_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.card_rarity_row).setVisibility(View.GONE);
        }

        info = mCard.getRace();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_race_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.card_race_row).setVisibility(View.GONE);
        }

        info = mCard.getHowToGetReg();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_how_to_get_text_view);
            textView.setText(info);
        } else {
            view.findViewById(R.id.how_to_get_row).setVisibility(View.GONE);
        }

        info = mCard.getHowToGetGold();
        if (info != null) {
            textView = (TextView) view.findViewById(R.id.card_how_to_get_gold_text_view);
            textView.setText(mCard.getHowToGetGold());
        } else {
            view.findViewById(R.id.how_to_get_gold_row).setVisibility(View.GONE);
        }

        return view;
    }

}
