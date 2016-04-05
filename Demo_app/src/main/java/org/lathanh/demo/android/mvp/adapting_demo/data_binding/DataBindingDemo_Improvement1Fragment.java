package org.lathanh.demo.android.mvp.adapting_demo.data_binding;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.ViewModel;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Models.LoadingViewHolder;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Models.LoadingViewModel;
import org.lathanh.demo.android.mvp.databinding.AdaptingDemoDataBindingImprovedListItemBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * In this improvement over {@link DataBindingDemo_StandardFragment}, we
 * demonstrate adapting done in the background, separately from binding.
 *
 * Adapting will be done on-demand.
 * That is, when the item comes into view, that is when it will submit a task to
 * do the adapting in a background thread.
 * The View will show a loading indicator until the adapting is done.
 * When the adapting is done, the LoadingViewModel is updated and notifies
 * observers (its
 * {@link android.databinding.BaseObservable} parent's call
 * {@link android.databinding.BaseObservable#notifyPropertyChanged(int) notifyPropertyChanged()},
 * which cause the RecyclerView to update it.
 *
 * @author Robert LaThanh 2015-11-09
 */
public class DataBindingDemo_Improvement1Fragment
    extends AdaptingDemo_BaseFragment<LoadingViewModel> {

  //== Instance fields ========================================================

  private final ThreadPoolExecutor threadPoolExecutor;


  //== Instantiation ==========================================================

  public static DataBindingDemo_Improvement1Fragment newInstance() {
    return new DataBindingDemo_Improvement1Fragment();
  }

  /** Prepare the threadPoolExecutor for adapting in the background. */
  public DataBindingDemo_Improvement1Fragment() {
    BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>(128) {
      @Override
      public Runnable poll() {
        return super.pollLast();
      }
    };
    int cpus = Runtime.getRuntime().availableProcessors();
    threadPoolExecutor = new ThreadPoolExecutor(cpus + 1, cpus * 2 + 1,
        1, TimeUnit.SECONDS, queue);
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods ------------------------------------

  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<LoadingViewModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<LoadingViewModel>>() {
      @Override
      protected List<LoadingViewModel> doInBackground(Void... params) {
        List<DataModel> dataModels = loadData();

        // create a LoadingViewModel for each DataModel
        // the actual ViewModel isn't adapted here
        List<LoadingViewModel> loadingViewModels =
            new ArrayList<>(dataModels.size());
        for (DataModel dataModel : dataModels) {
          loadingViewModels.add(new LoadingViewModel(dataModel));
        }
        return loadingViewModels;
      }

      @Override
      protected void onPostExecute(List<LoadingViewModel> loadingViewModels) {
        // Set up adapter and call callback
        Adapter adapter = new Adapter(getActivity(), loadingViewModels);
        recyclerView.setAdapter(adapter);
        onLoadedCallback.run();
      }
    };
  } // getLoadTask()


  //== Inner classes ==========================================================

  private class Adapter
      extends RecyclerView.Adapter<LoadingViewHolder> {

    private final List<LoadingViewModel> loadingViewModels;
    private LayoutInflater inflater;

    public Adapter(Context context, List<LoadingViewModel> loadingViewModels) {
      this.inflater = LayoutInflater.from(context);
      this.loadingViewModels = loadingViewModels;
    }

    @Override
    public LoadingViewHolder onCreateViewHolder(ViewGroup parent, int i) {
      AdaptingDemoDataBindingImprovedListItemBinding binding =
          AdaptingDemoDataBindingImprovedListItemBinding.inflate(inflater,
                                                                 parent, false);
      return new LoadingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final LoadingViewHolder viewHolder,
                                 int position) {
      final LoadingViewModel loadingViewModel = loadingViewModels.get(position);
      viewHolder.setLoadingViewModel(loadingViewModel);

      if (loadingViewModel.getViewModel() == null) {
        // start task to make this view available
        new AsyncTask<Void, Void, ViewModel>() {
          @Override
          protected ViewModel doInBackground(Void... params) {
            return adaptDataModelToViewModel(loadingViewModel.getDataModel());
          }

          @Override
          protected void onPostExecute(ViewModel viewModel) {
            loadingViewModel.setOnBindTimeNanos(System.nanoTime());
            loadingViewModel.setViewModel(viewModel);
          }
        }.executeOnExecutor(threadPoolExecutor);
      } else {
        loadingViewModel.setOnBindTimeNanos(System.nanoTime());
      }
    } // onBindViewHolder()

    @Override
    public int getItemCount() {
      return loadingViewModels.size();
    }
  } // class Adapter

}
