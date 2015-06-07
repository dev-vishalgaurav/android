package course.labs.asynctasklab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class DownloaderTaskFragment extends Fragment {

	private DownloadFinishedListener mCallback;
	private Context mContext;
	private DownloaderTask mDownloaderTask = null;
	@SuppressWarnings("unused")
	private static final String TAG = "Lab-Threads";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Preserve across reconfigurations
		setRetainInstance(true);

		// TODO: Create new DownloaderTask that "downloads" data
		mDownloaderTask = new DownloaderTask();
		
		// TODO: Retrieve arguments from DownloaderTaskFragment
		ArrayList<Integer> resList = getArguments().getIntegerArrayList(MainActivity.TAG_FRIEND_RES_IDS);

		// Prepare them for use with DownloaderTask.
		
		// TODO: Start the DownloaderTask
		mDownloaderTask.execute(resList);
	}

	// Assign current hosting Activity to mCallback
	// Store application context for use by downloadTweets()
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mContext = activity.getApplicationContext();

		// Make sure that the hosting activity has implemented
		// the correct callback interface.
		try {
			mCallback = (DownloadFinishedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DownloadFinishedListener");
		}
	}

	// Null out mCallback
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

	// TODO: Implement an AsyncTask subclass called DownLoaderTask.
	// This class must use the downloadTweets method (currently commented
	// out). Ultimately, it must also pass newly available data back to
	// the hosting Activity using the DownloadFinishedListener interface.

	public class DownloaderTask extends AsyncTask<ArrayList<Integer>, Integer, String[]> {

		// TODO: Uncomment this helper method
		// Simulates downloading Twitter data from the network

		@Override
		protected String[] doInBackground(ArrayList<Integer>... params) {
			
			return downloadTweets(params[0]);
		}

		@Override
		protected void onPreExecute() {
			// Nothing to do 
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String[] result) {
			mCallback.notifyDataRefreshed(result);
			super.onPostExecute(result);
		}

		private String[] downloadTweets(ArrayList<Integer> resourceIDS) {
			final int simulatedDelay = 2000;
			String[] feeds = new String[resourceIDS.size()];
			try {
				for (int idx = 0; idx < resourceIDS.size(); idx++) {
					InputStream inputStream;
					BufferedReader in;
					try {
						// Pretend downloading takes a long time
						Thread.sleep(simulatedDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					inputStream = mContext.getResources().openRawResource(
							resourceIDS.get(idx));
					in = new BufferedReader(new InputStreamReader(inputStream));

					String readLine;
					StringBuffer buf = new StringBuffer();

					while ((readLine = in.readLine()) != null) {
						buf.append(readLine);
					}

					feeds[idx] = buf.toString();

					if (null != in) {
						in.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return feeds;
		}

	}

}