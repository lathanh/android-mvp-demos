package org.lathanh.demo.android.mvp.adapting_demo;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.ViewModel;

import java.util.List;

/**
 * This "standard" edition of the Adapting Demo is intended to be
 * representative of a typical approach to adapting data for display:
 * adapting that data in an {@link RecyclerView.Adapter}.
 *
 * So, the "fetched" data (as {@link DataModel DataModels} are returned by the
 * AsyncTask as-is (as DataModels).
 *
 * @see AdaptingDemo_Improvement1Fragment
 */
public class AdaptingDemo_StandardFragment
    extends AdaptingDemo_BaseFragment<DataModel> {

  //== Instantiation ==========================================================

  /** Android Fragment convention. */
  public static AdaptingDemo_StandardFragment newInstance() {
    return new AdaptingDemo_StandardFragment();
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods ------------------------------------

  /**
   * Fetches data ({@link #loadData()} and returns them as-is (as
   * {@link DataModel DataModels}.
   */
  @NonNull
  protected AsyncTask<Void, Void, List<DataModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<DataModel>>() {
      @Override
      protected List<DataModel> doInBackground(Void... params) {
        return loadData();
      }

      @Override
      protected void onPostExecute(List<DataModel> dataModels) {
        Adapter adapter = new Adapter(dataModels);
        recyclerView.setAdapter(adapter);
        onLoadedCallback.run();
      }
    };
  }


  //== Inner classes ==========================================================

  private class Adapter extends BaseAdapter {

    private final List<DataModel> dataModels;

    public Adapter(List<DataModel> dataModels) {
      super(getActivity());
      this.dataModels = dataModels;
    }

    @Override
    public void onBindViewHolder(AdaptingDemo_Models.ViewHolder viewHolder,
                                 int i) {
      long startTime = System.nanoTime();
      DataModel dataModel = dataModels.get(i);

      // Adapt the data for the view.
      // Doing this here has a couple of drawbacks:
      //   1. If the adapting takes too long, it could affect scrolling
      //      performance.
      //   2. A new viewModel object is instantiated, which is done being used
      //      when this method completes. So lots of scrolling means lots of
      //      ViewModels waiting to be GC'd, which may affect scrolling when
      //      that happens.
      ViewModel viewModel =
          AdaptingDemo_StandardFragment.adaptDataModelToViewModel(dataModel);
      viewHolder.label.setText(viewModel.getString());
      viewHolder.delay.setText(viewModel.getDelay());

      // bind time
      long elapsed = System.nanoTime() - startTime;
      viewHolder.bind.setText(String.format("%.2f ms", elapsed / 1000000f));
    }

    @Override
    public int getItemCount() {
      return dataModels.size();
    }
  }

}
