package org.lathanh.demo.android.mvp.adapting_demo.framework.adaptable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.android.mvp.adapter.adaptable.AdaptOnDemandAdaptableBindingAdapter;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;
import org.lathanh.demo.android.mvp.databinding.AdapterFrameworkAdaptableListItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of usage of
 * {@link org.lathanh.android.mvp.adapter.adaptable.AdaptableBindingAdapter},
 * the Adaptable Adapter Framework.
 * See {@code README.md} at root of project.
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdapterFwkDemo_AdaptableFragment
    extends AdaptingDemo_BaseFragment<AdapterFwkDemo_AdaptableViewModel> {

  //== Instantiation ==========================================================

  public static AdapterFwkDemo_AdaptableFragment newInstance() {
    return new AdapterFwkDemo_AdaptableFragment();
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods ------------------------------------

  /**
   * A task to "load data", which returns data models within
   * {@link AdapterFwkDemo_AdaptableViewModel}.
   */
  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<AdapterFwkDemo_AdaptableViewModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<AdapterFwkDemo_AdaptableViewModel>>() {
      @Override
      protected List<AdapterFwkDemo_AdaptableViewModel> doInBackground(Void... params) {
        List<AdaptingDemo_Models.DataModel> dataModels = loadData();

        // create an AdaptableViewModel for each DataModel
        // the actual ViewModel isn't adapted here
        List<AdapterFwkDemo_AdaptableViewModel> adaptableViewModels =
            new ArrayList<>(dataModels.size());
        for (AdaptingDemo_Models.DataModel dataModel : dataModels) {
          adaptableViewModels.add(
              new AdapterFwkDemo_AdaptableViewModel(dataModel));
        }
        return adaptableViewModels;
      }

      @Override
      protected void onPostExecute(List<AdapterFwkDemo_AdaptableViewModel>
                                       adaptableViewModels) {
        AdapterFwkDemo_AdaptableAdapter actualAdapter =
            new AdapterFwkDemo_AdaptableAdapter();
        BindingAdapter bindingAdapter =
            new BindingAdapter(getContext(), adaptableViewModels, actualAdapter);
        recyclerView.setAdapter(bindingAdapter);
        onLoadedCallback.run();
      }
    };
  }

  //== Inner classes ==========================================================

  public static class BindingAdapter
      extends AdaptOnDemandAdaptableBindingAdapter<AdaptingDemo_Models.ViewModel,
                                                   AdapterFwkDemo_AdaptableViewModel,
      ViewHolder> {

    private final List<AdapterFwkDemo_AdaptableViewModel> items;
    private LayoutInflater inflater;

    public BindingAdapter(Context context,
                          List<AdapterFwkDemo_AdaptableViewModel> items,
                          AdapterFwkDemo_AdaptableAdapter actualAdapter) {
      super(actualAdapter);
      this.inflater = LayoutInflater.from(context);
      this.items = items;
    }

    @NonNull
    @Override
    protected AdapterFwkDemo_AdaptableViewModel get(int position) {
      return items.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      AdapterFrameworkAdaptableListItemBinding binding =
          AdapterFrameworkAdaptableListItemBinding.inflate(inflater, parent,
                                                           false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,
                                 @NonNull AdapterFwkDemo_AdaptableViewModel
                                     adaptableViewModel, int position) {
      adaptableViewModel.setOnBindTimeNanos(System.nanoTime());
      viewHolder.setAdaptableViewModel(adaptableViewModel);
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  } // class Adapter

  /**
   * The ViewHolder when using Data Binding is simple; it just uses the layout's
   * ViewDataBinding to do the binding.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final AdapterFrameworkAdaptableListItemBinding binding;

    public ViewHolder(AdapterFrameworkAdaptableListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void setAdaptableViewModel(AdapterFwkDemo_AdaptableViewModel
                                        adaptableViewModel) {
      binding.setAdaptableViewModel(adaptableViewModel);
    }
  } // class ViewHolder

}
