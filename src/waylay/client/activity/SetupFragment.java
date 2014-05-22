package waylay.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.waylay.client.R;

import java.lang.reflect.Method;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SetupFragment extends Fragment {

    private static final String TAG = "SetupFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView serverList;
    private static SetupAdapter adapterSetup;


    private OnFragmentInteractionListener mListener;

    protected Object mSetupActionMode;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance(String param1, String param2) {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        serverList = (ListView) view.findViewById(R.id.listSSO);
        adapterSetup = new SetupAdapter(getActivity(), WaylayApplication.servers);
        serverList.setAdapter(adapterSetup);

        serverList.setClickable(true);
        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //startBrowser();
                launchSSOSetup(position);
            }
        });

        serverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSetupActionMode != null) {
                    return false;
                }
                try {
                    Method m = mSSOActionModeCallback.getClass().getMethod("setPosition", new Class[] {Integer.class});
                    m.invoke(mSSOActionModeCallback, new Object[] {new Integer(position)});

                    mSetupActionMode = getActivity().startActionMode(mSSOActionModeCallback);
                    view.setSelected(true);
                    return true;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            }
        });


        Button buttonStopPushingData = (Button) view.findViewById(R.id.buttonStopPushingData);
        buttonStopPushingData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.stopPush();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterSetup.notifyDataSetChanged();
        mListener.onServerChange();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void stopPush();

        void onServerChange();
    }


    protected void launchSSOSetup(int position) {
		WaylayApplication.selectServer((BayesServer)serverList.getItemAtPosition(position));
		Intent i = new Intent(getActivity(), SetupActivity.class);
		startActivity(i);
	}

    private ActionMode.Callback mSSOActionModeCallback = new ActionMode.Callback() {
		int position = 0;
		public void setPosition(int pos){
			position = pos;
		}

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.layout.menu_sso, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.itemInfo:
				//launchInfo();
				startBrowser();
				mode.finish();
				return true;
			case R.id.itemEditSSO:
				launchSSOSetup(position);
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mSetupActionMode = null;
		}
	};



    protected void startBrowser() {
        Log.d(TAG, "startBrowser");
        Uri marketUri = Uri.parse("http://"+WaylayApplication.getSelectedServer().constructURLForWebAP());
        Intent marketIntent = new
                Intent(Intent.ACTION_VIEW).setData(marketUri);
        startActivity(marketIntent);

    }

}