package org.lathanh.demo.android.mvp.adapting_demo;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * In this first improvement over the {@link AdaptingDemo_StandardFragment
 * standard} approach (which adapts the DataModel into a ViewModel at the time
 * of binding), we demonstrate the benefit of adapting data for the view when
 * it's received.
 *
 * The benefit of this is that the
 * {@link android.support.v7.widget.RecyclerView.Adapter Adapter} then only has
 * to Bind (instead of Adapt and Bind), which means that it happens much more
 * quickly and can more consistently achieve high-FPS (frames-per-second),
 * buttery-smooth scrolling.
 *
 * The drawback is that all that adapting work has to be done before we can
 * start showing <em>any data</em>. In other words, we do all adapting work in
 * the background so it won't cause scrolling stutter, but make the UI wait
 * while doing so.
 *
 * @see AdaptingDemo_StandardFragment
 * @see AdaptingDemo_Improvement2Fragment
 */
public class AdaptingDemo_Improvement1Fragment
    extends AdaptingDemo_BaseFragment<ViewModel> {

  private static final String LOG_TAG = AdaptingDemo_Improvement1Fragment.class.getSimpleName();


  //== Instantiation ==========================================================

  public static AdaptingDemo_Improvement1Fragment newInstance() {
    return new AdaptingDemo_Improvement1Fragment();
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods ------------------------------------

  /**
   * Fetches data ({@link #loadData()}, prepares {@link ViewModel ViewModels}
   * from them, and returns those.
   */
  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<ViewModel>> getLoadTask(
      @NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<ViewModel>>() {
      @Override
      protected List<ViewModel> doInBackground(Void... params) {
        List<DataModel> dataModels = loadData();

        //-- Create viewModels from dataModels
        // Since this could take a while and the UI isn't really doing anything
        // (just showing a loading indicator), we'll try to do this faster by
        // parallelizing the processing
        List<Future<ViewModel>> futureViewModels =
            new ArrayList<>(dataModels.size());
        ExecutorService executorService =
            Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * 2 + 1);
        for (final DataModel dataModel : dataModels) {
          futureViewModels.add(
              executorService.submit(new Callable<ViewModel>() {
                @Override
                public ViewModel call() throws Exception {
                  return adaptDataModelToViewModel(dataModel);
                }
              }));
        }

        // collect all of the adapted viewModels
        List<ViewModel> viewModels = new ArrayList<>(dataModels.size());
        for (Future<ViewModel> futureViewModel : futureViewModels) {
          try {
            viewModels.add(futureViewModel.get());
          } catch (Exception e) {
            Log.e(LOG_TAG, "Future<ViewModel>", e);
          }
        }

        return viewModels;
      }

      @Override
      protected void onPostExecute(List<ViewModel> viewModels) {
        Adapter adapter = new Adapter(viewModels);
        recyclerView.setAdapter(adapter);
        onLoadedCallback.run();
      }
    };
  }


  //== Inner classes ==========================================================

  private class Adapter extends BaseAdapter {

    private final List<ViewModel> viewModels;

    public Adapter(List<ViewModel> viewModels) {
      super(getActivity());
      this.viewModels = viewModels;
    }

    @Override
    public void onBindViewHolder(AdaptingDemo_Models.ViewHolder viewHolder,
                                 int i) {
      long startTime = System.nanoTime();
      ViewModel viewModel = viewModels.get(i);
      viewHolder.label.setText(viewModel.getString());
      viewHolder.delay.setText(viewModel.getDelay());

      // bind
      long elapsed = System.nanoTime() - startTime;
      viewHolder.bind.setText(String.format("%.2f ms", elapsed / 1000000f));
    }

    @Override
    public int getItemCount() {
      return viewModels.size();
    }
  }

}
