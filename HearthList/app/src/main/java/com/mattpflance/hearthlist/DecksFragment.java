package com.mattpflance.hearthlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattpflance.hearthlist.data.HearthListContract;
import com.mattpflance.hearthlist.dialogs.DeckCreationDialog;

public class DecksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DECK_LOADER = 1;

//    private CardsAdapter mCardsAdapter;
//
//    RecyclerView mRecyclerView;
//    TextView mEmptyView;
    FloatingActionButton mDecksFab;

    public DecksFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static DecksFragment newInstance() {
        DecksFragment fragment = new DecksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decks, container, false);

//        mRecyclerView = (RecyclerView) view.findViewById(R.id.decks_recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.setHasFixedSize(true);
//        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
//
//        mEmptyView = (TextView) view.findViewById(R.id.cards_recycler_empty_view);
//
//        mCardsAdapter = new CardsAdapter(getActivity(), new CardsAdapter.CardsAdapterOnClickHandler() {
//            @Override
//            public void onClick(Cursor cursor) {
//                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
//                Card card = (cursor != null) ? new Card(cursor) : null;
//                intent.putExtra(CardDetailsActivity.CARD_ARG_ID, card);
//                startActivity(intent);
//            }
//        }, mEmptyView);
//
//        mRecyclerView.setAdapter(mCardsAdapter);

        mDecksFab = (FloatingActionButton) view.findViewById(R.id.decks_fab);
        mDecksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Init the loader
        getLoaderManager().initLoader(DECK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                HearthListContract.DeckEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mCardsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mCardsAdapter.swapCursor(null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == DeckCreationDialog.CREATION_CODE) {
            Log.i("DeckFrag", "DeckName: " + data.getStringExtra(DeckCreationDialog.DECK_NAME_KEY));
            Log.i("DeckFrag", "DeckClass: " + data.getStringExtra(DeckCreationDialog.DECK_CLASS_KEY));
        }
    }

    private void showDialog() {
        DeckCreationDialog dialog = new DeckCreationDialog();
        dialog.setTargetFragment(this, DeckCreationDialog.CREATION_CODE);
        dialog.show(getFragmentManager(), null);
    }
}
