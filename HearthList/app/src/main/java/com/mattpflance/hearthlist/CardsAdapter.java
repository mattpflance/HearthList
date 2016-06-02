package com.mattpflance.hearthlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.mattpflance.hearthlist.models.Card;


/**
 * Creates a list of cards from a cursor to a RecyclerView
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsAdapterViewHolder> {

    private Cursor mCursor;
    private final Context mContext;
    private final CardsAdapterOnClickHandler mClickHandler;
    private final View mEmptyView;

    /**
     * Cache of the children views for a Card list item.
     */
    public class CardsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mCardView;
        public final TextView mManaTextView;
        public final FrameLayout mAttackView;
        public final FrameLayout mHealthView;
        public final ImageView mAttackIcon;
        public final ImageView mHealthIcon;
        public final TextView mAttackTextView;
        public final TextView mHealthTextView;
        public final View mRarityView;
        public final LinearLayout mCardDetailsLayout;
        public final TextView mCardNameView;
        public final TextView mCardDescView;

        public CardsAdapterViewHolder(View view) {
            super(view);
            mCardView = (ImageView) view.findViewById(R.id.card_image);
            mManaTextView = (TextView) view.findViewById(R.id.mana_textview);
            mAttackView = (FrameLayout) view.findViewById(R.id.attack_view);
            mHealthView = (FrameLayout) view.findViewById(R.id.health_view);
            mAttackIcon = (ImageView) view.findViewById(R.id.attack_icon);
            mHealthIcon = (ImageView) view.findViewById(R.id.health_icon);
            mAttackTextView = (TextView) view.findViewById(R.id.attack_textview);
            mHealthTextView = (TextView) view.findViewById(R.id.health_textview);
            mRarityView = view.findViewById(R.id.rarity_view);
            mCardDetailsLayout = (LinearLayout) view.findViewById(R.id.card_details);
            mCardNameView = (TextView) view.findViewById(R.id.card_name_view);
            mCardDescView = (TextView) view.findViewById(R.id.card_text_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mClickHandler.onClick(mCursor);
        }
    }

    public static interface CardsAdapterOnClickHandler {
        void onClick(Cursor cursor);
    }


    public CardsAdapter(Context context, CardsAdapterOnClickHandler onClickHandler, View emptyView) {
        mContext = context;
        mClickHandler = onClickHandler;
        mEmptyView = emptyView;
    }


    @Override
    public CardsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_list_item_view, viewGroup, false);
            view.setFocusable(true);
            return new CardsAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final CardsAdapterViewHolder cardsAdapterVh, int position) {
        mCursor.moveToPosition(position);

        // Load card image
        String imagePath = mCursor.getString(Card.COL_REG_IMG_URL);

        // TODO Resize Bitmap
        if (imagePath != null) {
            Glide.with(mContext)
                    .load(imagePath)
                    .transform(new CardsAdapterImageTransformation(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(cardsAdapterVh.mCardView);
        }

        // Give the card a background gradient based on class
        String playerClass = mCursor.getString(Card.COL_CLASS);
        if (playerClass != null) {
            //setBackgroundToView(cardsAdapterVh.mCardDetailsLayout, playerClass.toLowerCase());
        }

        // Load mana cost
        String text = "" + mCursor.getInt(Card.COL_COST);
        cardsAdapterVh.mManaTextView.setText(text);

        // Determine which icons to show for attack and health
        String cardType = mCursor.getString(Card.COL_TYPE);
        int attackId = -1;
        int healthId = -1;

        if (cardType != null) {
            String typeLower = cardType.toLowerCase();
            if (typeLower.equals("minion")) {
                attackId = R.drawable.ic_minion_attack;
                healthId = R.drawable.ic_minion_health;
            } else if (typeLower.equals("weapon")) {
                attackId = R.drawable.ic_weapon_attack;
                healthId = R.drawable.ic_weapon_health;
            }
        }

        if (attackId == -1 && healthId == -1) {
            // This is a spell, hide the icons
            cardsAdapterVh.mAttackView.setVisibility(View.INVISIBLE);
            cardsAdapterVh.mHealthView.setVisibility(View.INVISIBLE);
        } else {
            cardsAdapterVh.mAttackView.setVisibility(View.VISIBLE);
            cardsAdapterVh.mHealthView.setVisibility(View.VISIBLE);
            // Otherwise, load icons
            cardsAdapterVh.mAttackIcon.setImageResource(attackId);
            cardsAdapterVh.mHealthIcon.setImageResource(healthId);

            // Now load the attack and health values
            text = "" + mCursor.getInt(Card.COL_ATTACK);
            cardsAdapterVh.mAttackTextView.setText(text);
            text = "" + mCursor.getInt(Card.COL_HEALTH);
            cardsAdapterVh.mHealthTextView.setText(text);
        }

        // Set card name
        cardsAdapterVh.mCardNameView.setText(mCursor.getString(Card.COL_NAME));

        // Set rarity view
        setCardRarity(cardsAdapterVh.mRarityView);

        // Set card text
        String cardText = mCursor.getString(Card.COL_TEXT);
        if (cardText != null) {
            cardsAdapterVh.mCardDescView.setText(Html.fromHtml(cardText));
        }
    }

    @Override
    public int getItemViewType(int position) {
        // THIS SHOULD ALWAYS RETURN 0
        return 0;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void setCardRarity(View view) {
        String rarity = mCursor.getString(Card.COL_RARITY).toLowerCase();
        int colorId;

        switch (rarity) {
            case "common":
                colorId = ContextCompat.getColor(mContext, R.color.common);
                break;
            case "rare":
                colorId = ContextCompat.getColor(mContext, R.color.rare);
                break;
            case "epic":
                colorId = ContextCompat.getColor(mContext, R.color.epic);
                break;
            case "legendary":
                colorId = ContextCompat.getColor(mContext, R.color.legendary);
                break;
            default:
                colorId = -1;
                break;
        }

        if (colorId != -1) {
            view.setVisibility(View.VISIBLE);
            view.setBackgroundColor(colorId);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void setBackgroundToView(LinearLayout backgroundView, String playerClass) {

        Drawable drawable;

        switch (playerClass) {
            case "warrior":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_warrior);
                break;
            case "shaman":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_shaman);
                break;
            case "rogue":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_rogue);
                break;
            case "paladin":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_paladin);
                break;
            case "hunter":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_hunter);
                break;
            case "druid":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_druid);
                break;
            case "warlock":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_warlock);
                break;
            case "mage":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_mage);
                break;
            case "priest":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_priest);
                break;
            default:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_neutral);
                break;
        }

        // Set background
        backgroundView.setBackground(drawable);
    }


    private static class CardsAdapterImageTransformation extends BitmapTransformation {

        private Context mContext;

        public CardsAdapterImageTransformation(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int cropStart = Utility.dpToPx(mContext, 33);
            int length = Utility.dpToPx(mContext, 38);
            return Bitmap.createBitmap(toTransform, cropStart, cropStart, length, length);
        }

        @Override
        public String getId() {
            // Return some id that uniquely identifies your transformation.
            return "com.mattpflance.hearthlist.CardsAdapterImageTransformation";
        }
    }
}
