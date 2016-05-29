package com.mattpflance.hearthlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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


/**
 * Fragment that displays HearthStone cards
 */
public class CardsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CardsAdapter mCardsAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    private static final int CARD_LOADER = 0;

    public CardsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CardsFragment newInstance() {
        CardsFragment fragment = new CardsFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
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
                null,
                null,
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
}
