package org.lathanh.demo.android.mvp.adapting_demo.data_binding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.demo.android.mvp.R;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Models.LoadingViewHolder;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Models.LoadingViewModel;
import org.lathanh.demo.android.mvp.databinding.AdaptingDemoDataBindingListItemBinding;

import java.util.List;

/**
 * Like
 * {@link org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_StandardFragment},
 * this "standard" edition of the Adapting Demo is intended to be
 * representative of a typical approach to adapting data for display:
 * adapting that data in an {@link RecyclerView.Adapter}.
 *
 * Unlike
 * {@link org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_StandardFragment},
 * instead of binding manually, it uses Android Data Binding to do the binding.
 *
 * @author Robert LaThanh 2015-11-09
 */
public class DataBindingDemo_StandardFragment
    extends AdaptingDemo_BaseFragment<DataModel> {

  //== Instantiation ==========================================================

  public static DataBindingDemo_StandardFragment newInstance() {
    return new DataBindingDemo_StandardFragment();
  }

  /** Default constructor for Android. */
  public DataBindingDemo_StandardFragment() {
    // empty
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods -------------------------------------------

  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<DataModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<DataModel>>() {
      @Override
      protected List<DataModel> doInBackground(Void... params) {
        return loadData();
      }

      @Override
      protected void onPostExecute(List<DataModel> dataModels) {
        Adapter adapter = new Adapter(getActivity(), dataModels);
        recyclerView.setAdapter(adapter);
        onLoadedCallback.run();
      }
    };
  } // getLoadTask()


  //== Inner classes ==========================================================

  /**
   * Adapts the DataModel at the time that it's needed; that is, when it comes
   * into view.
   */
  private class Adapter
      extends RecyclerView.Adapter<LoadingViewHolder> {

    private final List<DataModel> dataModels;
    private LayoutInflater inflater;

    public Adapter(Context context, List<DataModel> dataModels) {
      this.inflater = LayoutInflater.from(context);
      this.dataModels = dataModels;
    }

    @Override
    public LoadingViewHolder onCreateViewHolder(ViewGroup parent, int i) {
      AdaptingDemoDataBindingListItemBinding binding =
            DataBindingUtil.inflate(inflater,
              R.layout.adapting_demo_data_binding_list_item,
              parent, false);
      return new LoadingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(LoadingViewHolder viewHolder, int position) {
      long startTime = System.nanoTime();
      final DataModel dataModel = dataModels.get(position);
      final LoadingViewModel loadingViewModel =
          new LoadingViewModel(dataModel);

      // adapt now (wait for it) and then immediately set to the
      // loadingViewModel. The loading state will never be needed.
      loadingViewModel.setViewModel(adaptDataModelToViewModel(dataModel));
      loadingViewModel.setOnBindTimeNanos(startTime);

      // give the viewModel to the viewHolder, which knows how to do the actual
      // binding (using Data Binding)
      viewHolder.setLoadingViewModel(loadingViewModel);
    } // onBindViewHolder()

    @Override
    public int getItemCount() {
      return dataModels.size();
    }
  } // class Adapter

}
