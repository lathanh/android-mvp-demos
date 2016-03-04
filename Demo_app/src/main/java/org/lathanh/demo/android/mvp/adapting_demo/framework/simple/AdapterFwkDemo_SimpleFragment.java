package org.lathanh.demo.android.mvp.adapting_demo.framework.simple;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.android.mvp.adapter.simple.AdaptOnDemandSimpleBindingAdapter;
import org.lathanh.android.mvp.adapter.simple.SimpleBindingAdapter;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;
import org.lathanh.demo.android.mvp.databinding.AdapterFrameworkSimpleListItemBinding;

import java.util.List;

/**
 * Demonstration of usage of {@link SimpleBindingAdapter}, the simple Adapter
 * Framework.
 * See {@code README.md} at root of project.
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdapterFwkDemo_SimpleFragment
    extends AdaptingDemo_BaseFragment<AdaptingDemo_Models.DataModel> {

  //== Instantiation ==========================================================

  public static AdapterFwkDemo_SimpleFragment newInstance() {
    return new AdapterFwkDemo_SimpleFragment();
  }


  //== Instance methods =======================================================

  //-- 'AdaptingDemo_BaseFragment' methods ------------------------------------

  /** A task to "load data", which simply returns data models as-is. */
  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<AdaptingDemo_Models.DataModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<AdaptingDemo_Models.DataModel>>() {
      @Override
      protected List<AdaptingDemo_Models.DataModel> doInBackground(Void... params) {
        return loadData();
      }

      @Override
      protected void onPostExecute(List<AdaptingDemo_Models.DataModel>
                                       dataModels) {
        BindingAdapter bindingAdapter = new BindingAdapter(getContext(),
                                                           dataModels);
        recyclerView.setAdapter(bindingAdapter);
        onLoadedCallback.run();
      }
    };
  }

  //== Inner classes ==========================================================

  public static class BindingAdapter
      extends AdaptOnDemandSimpleBindingAdapter<AdaptingDemo_Models.DataModel,
                                                AdaptingDemo_Models.ViewModel,
                                                ViewHolder> {

    private LayoutInflater inflater;

    public BindingAdapter(Context context,
                          List<AdaptingDemo_Models.DataModel> dataModels) {
      super(new AdapterFwkDemo_SimpleAdapter());
      addAll(dataModels);
      this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(
        ViewGroup parent, int viewType) {
      AdapterFrameworkSimpleListItemBinding binding =
          AdapterFrameworkSimpleListItemBinding.inflate(inflater, parent, false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder viewHolder,
        @NonNull AdaptingDemo_Models.ViewModel viewModel,
        int position) {
      viewHolder.setViewModel(viewModel);
    }

    @Override
    protected void onViewModelReadyForViewHolder(
        @NonNull ViewHolder viewHolder,
        @NonNull AdaptingDemo_Models.ViewModel viewModel) {
      viewHolder.setViewModel(viewModel);
    }
  } // class Adapter

  /**
   * The ViewHolder when using Data Binding is simple; it just uses the layout's
   * ViewDataBinding to do the binding.
   */
  public static class ViewHolder
      extends RecyclerView.ViewHolder
      implements SimpleBindingAdapter.Taggable {

    private final AdapterFrameworkSimpleListItemBinding binding;
    private Object tag;

    public ViewHolder(AdapterFrameworkSimpleListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void setViewModel(AdaptingDemo_Models.ViewModel viewModel) {
      binding.setViewModel(viewModel);
    }

    @Override
    public void setTag(@Nullable Object tag) {
      this.tag = tag;
    }

    @Nullable
    @Override
    public Object getTag() {
      return this.tag;
    }
  } // class ViewHolder

}
