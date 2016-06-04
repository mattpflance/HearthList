package com.mattpflance.hearthlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattpflance.hearthlist.data.HearthListContract;
import com.mattpflance.hearthlist.models.Card;
import com.mattpflance.hearthlist.settings.CardFiltersActivity;
import com.mattpflance.hearthlist.settings.CardFiltersFragment;

import java.util.ArrayList;

/**
 * Fragment that displays HearthStone cards
 */
public class CardsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CardFiltersFragment.CardFiltersCallback {

    private static final int CARD_LOADER = 0;

    // Keep a reference to the singleton instance to set up the Filters callback
    private static CardsFragment mSingletonInstance;

    private CardsAdapter mCardsAdapter;
    private static ArrayList<String> mSelectionArgs;

    RecyclerView mRecyclerView;
    TextView mEmptyView;
    FloatingActionButton mFilterFab;

    public CardsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CardsFragment newInstance() {
        mSingletonInstance = new CardsFragment();
        mSelectionArgs = new ArrayList<>();
        mSelectionArgs.add(Card.MIN_MANA_COST+"");
        mSelectionArgs.add(Card.MAX_MANA_COST+"");
        return mSingletonInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.cards_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mEmptyView = (TextView) view.findViewById(R.id.cards_recycler_empty_view);

        mCardsAdapter = new CardsAdapter(getActivity(), new CardsAdapter.CardsAdapterOnClickHandler() {
            @Override
            public void onClick(Cursor cursor) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                Card card = (cursor != null) ? new Card(cursor) : null;
                intent.putExtra(CardDetailsActivity.CARD_ARG_ID, card);
                startActivity(intent);
            }
        }, mEmptyView);

        mRecyclerView.setAdapter(mCardsAdapter);

        mFilterFab = (FloatingActionButton) view.findViewById(R.id.cards_fab);
        mFilterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardFiltersActivity.class);
                intent.putExtra(CardFiltersActivity.SELECTION_ARG_KEY, mSelectionArgs);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Init the loader
        getLoaderManager().initLoader(CARD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = HearthListContract.CardEntry.COLUMN_COST + " ASC";
        return new CursorLoader(
                getActivity(),
                HearthListContract.CardEntry.CONTENT_URI,
                Card.CARD_COLUMNS,
                null,                                       // Implemented in ContentProvider
                mSelectionArgs.toArray(new String[mSelectionArgs.size()]),
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCardsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardsAdapter.swapCursor(null);
    }

    public static CardsFragment getInstance() { return mSingletonInstance; }

    public void setFilterSelectionArgs(ArrayList<String> selectionArgs) {
        mSelectionArgs = selectionArgs;
        getLoaderManager().restartLoader(0, null, this);
    }
}
