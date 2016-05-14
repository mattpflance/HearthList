package com.mattpflance.hearthlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

/**
 * Creates a list of cards from a cursor to a RecyclerView
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private CardsAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    /**
     * Cache of the children views for a Card list item.
     */
    public class CardsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mCardView;
        public final ProgressBar mImageLoadingView;
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
            mImageLoadingView = (ProgressBar) view.findViewById(R.id.image_progress);
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
            // Handle onClick with given cursor
            mClickHandler.onClick(mCursor);
        }
    }

    public static interface CardsAdapterOnClickHandler {
        void onClick(Cursor cursor);
    }

    public CardsAdapter(Context context, CardsAdapterOnClickHandler dh, View emptyView) {
        mContext = context;
        mClickHandler = dh;
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
    public void onBindViewHolder(CardsAdapterViewHolder cardsAdapterVh, int position) {
        mCursor.moveToPosition(position);

        // Load card image
        byte[] image = mCursor.getBlob(CardsFragment.COL_CARD_IMG);
        if (image == null) {
            cardsAdapterVh.mCardView.setVisibility(View.GONE);
            cardsAdapterVh.mImageLoadingView.setVisibility(View.VISIBLE);
        } else {
            if (cardsAdapterVh.mImageLoadingView.getVisibility() == View.VISIBLE) {
                cardsAdapterVh.mCardView.setVisibility(View.VISIBLE);
                cardsAdapterVh.mImageLoadingView.setVisibility(View.GONE);

                // Crop the byte array
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                Bitmap.createBitmap(bitmap, 110, 100, 80, 80)
                        .compress(Bitmap.CompressFormat.PNG, 0, blob);

                Glide.with(mContext)
                        .load(blob.toByteArray())
                        .crossFade()
                        .centerCrop()
                        .into(cardsAdapterVh.mCardView);
            }
        }

        // Give the card a background gradient based on class
        setBackgroundToView(cardsAdapterVh.mCardDetailsLayout,
                mCursor.getString(CardsFragment.COL_CARD_CLASS).toLowerCase());

        // Load mana cost
        String text = "" + mCursor.getInt(CardsFragment.COL_CARD_COST);
        cardsAdapterVh.mManaTextView.setText(text);

        // Determine which icons to show for attack and health
        String typeLower = mCursor.getString(CardsFragment.COL_CARD_TYPE).toLowerCase();
        int attackId = -1;
        int healthId = -1;

        if (typeLower.equals("minion")) {
            attackId = R.drawable.ic_minion_attack;
            healthId = R.drawable.ic_minion_health;
        } else if (typeLower.equals("weapon")) {
            attackId = R.drawable.ic_weapon_attack;
            healthId = R.drawable.ic_weapon_health;
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
            text = "" + mCursor.getInt(CardsFragment.COL_CARD_ATTACK);
            cardsAdapterVh.mAttackTextView.setText(text);
            text = "" + mCursor.getInt(CardsFragment.COL_CARD_HEALTH);
            cardsAdapterVh.mHealthTextView.setText(text);
        }

        // Set card name
        cardsAdapterVh.mCardNameView.setText(mCursor.getString(CardsFragment.COL_CARD_NAME));

        // Set rarity view
        setCardRarity(cardsAdapterVh.mRarityView);

        // Set card text
        cardsAdapterVh.mCardDescView
                .setText(Html.fromHtml(mCursor.getString(CardsFragment.COL_CARD_TEXT)));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public Cursor getCursor() {
        return mCursor;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof CardsAdapterViewHolder ) {
            CardsAdapterViewHolder vfh = (CardsAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    private void setCardRarity(View view) {
        String rarity = mCursor.getString(CardsFragment.COL_CARD_RARITY).toLowerCase();
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
}
