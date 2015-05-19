package com.android.markschmidt.nonzeroday;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddObjectiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddObjectiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddObjectiveFragment extends Fragment {

    public static final String ARG_ADD = "add";
    public static final String ARG_ID = "id";
    private static final String DIALOG_IMAGE = "image";

    private Objective mObjective;
    private String mSNoun;
    private String mMNoun;
    private String mVerb;
    private String mPVerb;
    private String mTitle;
    private int mImage;


    private EditText mSNounField;
    private EditText mVerbField;
    private EditText mPVerbField;
    private EditText mTitleField;
    private EditText mMNounField;
    //private ImageView mImageSelector;
    private TextView mToolbarTitle;
    private Button mConfirmButton;
    private Button mCancelButton;
    private ImageButton mUpButton;
    private RecyclerView mRecyclerView;
    private ImageSelectorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = "AddObjectiveFragment";
    private OnFragmentInteractionListener mCallbacks;

    private boolean isAddingNewObjective;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AddObjectiveFragment.
     */

    public static AddObjectiveFragment newInstance(UUID id, boolean add)
    {
        AddObjectiveFragment fragment = new AddObjectiveFragment();
        Bundle args = new Bundle();
        if(id != null)
            args.putString(ARG_ID, id.toString());
        args.putBoolean(ARG_ADD, add);
        fragment.setArguments(args);

        return fragment;

    }

    public AddObjectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAddingNewObjective = getArguments().getBoolean(ARG_ADD);
            if(!isAddingNewObjective) {
                mObjective = NonzeroDayLab.get(getActivity()).getObjective(UUID.fromString(getArguments().getString(ARG_ID)));
                if(mObjective != null) {
                    mSNoun = mObjective.getSingularNoun();
                    mMNoun = mObjective.getMultipleNoun();
                    mVerb = mObjective.getVerb();
                    mPVerb = mObjective.getPastVerb();
                    mTitle = mObjective.getTitle();
                    mImage = mObjective.getImage();
                }
            }else{
                mObjective = new Objective(null, null, null, null, null, 0);
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_objective, container, false);
        mToolbarTitle = (TextView)v.findViewById(R.id.toolbar_title);
        if(isAddingNewObjective)
            mToolbarTitle.setText(R.string.add_objective_add_title);
        else
            mToolbarTitle.setText(R.string.add_objective_edit_title);

        /*
        mImageSelector = (ImageView)v.findViewById(R.id.image_selector);
        mImageSelector.setImageDrawable(NonzeroDayLab.get(getActivity()).getImage(mObjective.getImage()));
        mImageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ImageSelectorDialog dialog = ImageSelectorDialog.newInstance(mObjective.getId());
                dialog.setTargetFragment(AddObjectiveFragment.this, 0);
                dialog.show(fm, DIALOG_IMAGE);
            }
        });
        */

        mRecyclerView = (RecyclerView)v.findViewById(R.id.image_selector_recycle_view);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new ImageSelectorAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);




        mSNounField = (EditText)v.findViewById(R.id.add_objective_noun);
        if(mSNoun != null)
            mSNounField.setHint(mSNoun);
        mSNounField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSNoun = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mVerbField = (EditText)v.findViewById(R.id.add_objective_verb);
        if(mVerb != null)
            mVerbField.setHint(mVerb);
        mVerbField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVerb = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPVerbField = (EditText)v.findViewById(R.id.add_objective_pverb);
        if(mPVerb != null)
            mPVerbField.setHint(mPVerb);
        mPVerbField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPVerb = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mTitleField = (EditText)v.findViewById(R.id.add_objective_title);
        if(mTitle != null)
            mTitleField.setHint(mTitle);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mMNounField = (EditText)v.findViewById(R.id.add_objective_noun_2);
        if(mSNoun != null)
            mMNounField.setHint(mMNoun);
        mMNounField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMNoun = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener cancelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        };



        mCancelButton = (Button)v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(cancelClick);

        mUpButton = (ImageButton)v.findViewById(R.id.up_button);
        mUpButton.setOnClickListener(cancelClick);

        mConfirmButton = (Button)v.findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                if(isAddingNewObjective) {
                    mObjective.setSingularNoun(mSNoun);
                    mObjective.setMultipleNoun(mMNoun);
                    mObjective.setTitle(mTitle);
                    mObjective.setVerb(mVerb);
                    mObjective.setPastVerb(mPVerb);
                    mObjective.setImage(mImage);
                    mCallbacks.onAddNewObjective(mObjective);
                }
                else
                    mCallbacks.onEditObjective(mObjective.getId(), new Objective(mSNoun, mVerb, mPVerb, mTitle, mMNoun, mImage));
            }
        });
        return v;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onImageChosen(int image)
    {
        mObjective.setImage(image);
        mImage = image;
        //mImageSelector.setImageDrawable(NonzeroDayLab.get(getActivity()).getImage(image));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }




    @Override
    public void onPause() {
        super.onPause();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onAddNewObjective(Objective objective);

        public void onEditObjective(UUID originalID, Objective objective);
    }


}
