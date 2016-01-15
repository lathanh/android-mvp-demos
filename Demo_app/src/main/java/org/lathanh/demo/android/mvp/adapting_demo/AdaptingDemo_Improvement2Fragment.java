package org.lathanh.demo.android.mvp.adapting_demo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.ViewModel;
import org.lathanh.demo.android.mvp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * In this improvement over {@link AdaptingDemo_Improvement1Fragment} &mdash;
 * and second improvement over the
 * {@link AdaptingDemo_StandardFragment standard} &mdash; we overcome
 * Improvement1's drawback: having to wait for all data to be adapted before we
 * can show any of it.
 *
 * @author Robert LaThanh 2015-11-03
 */
public class AdaptingDemo_Improvement2Fragment
    extends AdaptingDemo_BaseFragment<AdaptingDemo_Improvement2Fragment.LoadingViewModel> {

  //== Instance fields ========================================================

  private final ThreadPoolExecutor threadPoolExecutor;


  //== Instantiation ==========================================================

  public static AdaptingDemo_Improvement2Fragment newInstance() {
    return new AdaptingDemo_Improvement2Fragment();
  }

  public AdaptingDemo_Improvement2Fragment() {
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
        List<LoadingViewModel> loadingViewModels =
            new ArrayList<>(dataModels.size());
        for (DataModel dataModel : dataModels) {
          loadingViewModels.add(new LoadingViewModel(dataModel,
                                                     threadPoolExecutor));
        }
        return loadingViewModels;
      }

      @Override
      protected void onPostExecute(final List<LoadingViewModel> loadingViewModels) {
        // Set up adapter and call callback
        Adapter adapter = new Adapter(getActivity(), loadingViewModels);
        recyclerView.setAdapter(adapter);
        onLoadedCallback.run();
      }
    };
  } // getLoadTask()


  //== Inner classes ==========================================================

  /**
   * In addition to the AdaptingDemo_Models.LoadingViewModel, holds on to a
   * threadPoolExecutor that can be used to adapt DataModels to ViewModels,
   * and holds on to the ViewHolder of the View that it was bound to (so it can
   * notify that view when it changes).
   */
  public static class LoadingViewModel
      extends AdaptingDemo_Models.LoadingViewModel {

    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * We need to know what view we're bound to so that we can notify it if
     * there's a change.
     */
    private LoadingViewHolder boundViewHolder;

    public LoadingViewModel(DataModel dataModel,
                            ThreadPoolExecutor threadPoolExecutor) {
      super(dataModel);
      this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * Set the viewModel when it is ready.
     *
     * It is expected that this happens in a background thread, and this will
     * post a message to the UI thread to notify the ViewHolder if it is bound
     * (the message post will occur regardless of boundness).
     */
    public void bind(LoadingViewHolder viewHolder) {
      //-- previous bind
      if (viewHolder.backRef != null && viewHolder.backRef != this) {
        viewHolder.backRef.unbind();
      }

      //-- bind
      boundViewHolder = viewHolder;
      viewHolder.backRef = this;
      if (viewModel == null) {
        viewHolder.contentContainer.setVisibility(View.INVISIBLE);
        viewHolder.progressView.setVisibility(View.VISIBLE);

        // start task to make this view available
        new AsyncTask<Void, Void, ViewModel>() {
          @Override
          protected ViewModel doInBackground(Void... params) {
            return adaptDataModelToViewModel(dataModel);
          }

          @Override
          protected void onPostExecute(ViewModel viewModel) {
            LoadingViewModel.this.viewModel = viewModel;

            if (boundViewHolder != null &&
                boundViewHolder.backRef == LoadingViewModel.this) {
              // this ViewModel is still bound to a ViewHolder
              boundViewHolder.onChangeListener.onChange();
            }
          }
        }.executeOnExecutor(threadPoolExecutor);
      } else {
        long startTime = System.nanoTime();
        viewHolder.contentContainer.setVisibility(View.VISIBLE);
        viewHolder.progressView.setVisibility(View.GONE);

        viewHolder.label.setText(viewModel.getString());
        viewHolder.delay.setText(viewModel.getDelay());

        // bind
        long elapsed = System.nanoTime() - startTime;
        viewHolder.bind.setText(String.format("%.2f ms", elapsed / 1000000f));
      }
    }

    public void unbind() {
      boundViewHolder = null;
    }
  } // class LoadingViewModel

  /**
   * This ViewHolder also contains a handle back to the ViewModel that was bound
   * to this.
   * That way, when the ViewModel updates, it has a way of knowing whether the
   * View/ViewModel it was bound to is still bound to it (and not re-bound to
   * another; "recycled").
   * If the backRef points to a ViewModel other than itself, if must have been
   * bound to another ViewModel and it should not try to update the View with
   * its changes.
   */
  private static class LoadingViewHolder extends AdaptingDemo_Models.ViewHolder {
    private final View contentContainer;
    private final View progressView;

    private LoadingViewModel backRef;

    private final OnChangeListener onChangeListener;

    public LoadingViewHolder(View itemView, OnChangeListener onChangeListener) {
      super(itemView);
      this.contentContainer = itemView.findViewById(R.id.content);
      this.progressView = itemView.findViewById(R.id.progressBar);
      this.onChangeListener = onChangeListener;
    }
  } // class LoadingViewHolder

  /**
   * Allows a ViewHolder to notify the adapter when it has changed (and needs to
   * be re-rendered.
   *
   * <p>Note that the onChangeListener is saved with the ViewHolder, that way
   * we aren't instantiating more than one per View/ViewHolder, so we aren't
   * wasting objects (discarding and recreating), which results in GC that can
   * cause stutters in scrolling ("jank").</p>
   */
  private static class OnChangeListener {
    private final Adapter adapter;
    private int position;

    private OnChangeListener(Adapter adapter) {
      this.adapter = adapter;
    }

    public void onChange() {
      adapter.notifyItemChanged(position);
    }
  } // class OnChangeListener

  private static class Adapter extends RecyclerView.Adapter<LoadingViewHolder> {

    private final Context context;
    private final List<LoadingViewModel> loadingViewModels;

    public Adapter(Context context, List<LoadingViewModel> loadingViewModels) {
      this.context = context;
      this.loadingViewModels = loadingViewModels;
    }

    @Override
    public int getItemCount() {
      return loadingViewModels.size();
    }

    @Override
    public int getItemViewType(int position) {
      LoadingViewModel loadingViewModel = loadingViewModels.get(position);
      return loadingViewModel.viewModel != null ? 0 : 1;
    }

    @Override
    public LoadingViewHolder onCreateViewHolder(ViewGroup parent, int i) {
      View v =
          LayoutInflater.from(context).inflate(
              R.layout.adapting_demo_list_item_loading,
              parent, false);
      OnChangeListener onChangeListener = new OnChangeListener(this);
      return new LoadingViewHolder(v, onChangeListener);
    }

    @Override
    public void onBindViewHolder(LoadingViewHolder viewHolder, int position) {
      LoadingViewModel loadingViewModel = loadingViewModels.get(position);
      viewHolder.onChangeListener.position = position;
      loadingViewModel.bind(viewHolder);
    }
  } // class Adapter
}
