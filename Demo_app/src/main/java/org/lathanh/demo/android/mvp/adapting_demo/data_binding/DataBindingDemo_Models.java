package org.lathanh.demo.android.mvp.adapting_demo.data_binding;

import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import org.lathanh.demo.android.mvp.BR;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;
import org.lathanh.demo.android.mvp.databinding.AdaptingDemoDataBindingListItemBinding;

/**
 * This class simply contains (as inner classes) the various models that will
 * be used in the various implementations of this demo.
 *
 * Normally they would each be their own top-level classes, but putting them
 * all in one place should make them easier to see how they relate to each
 * other.
 */
public class DataBindingDemo_Models {

  /**
   * In the
   * {@link org.lathanh.demo.android.mvp.adapting_demo regular adapting demos},
   * we can easily measure the amount of time it takes to "bind" (by simply
   * timing how much time is spent in
   * {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder}).
   * However, with Data Binding, that method no longer does the binding (it
   * simply hands the ViewModel to the ViewHolder).
   * Instead, we can start a timer and end it when the binder asks the ViewModel
   * for the {@link #getBindTime()}.
   *
   * This LoadingViewModel introduces the {@link #setOnBindTimeNanos(long)}
   * field to measure that interval
   */
  public static class LoadingViewModel
      extends AdaptingDemo_Models.LoadingViewModel {

    private long onBindTimeNanos;

    public LoadingViewModel(AdaptingDemo_Models.DataModel dataModel) {
      super(dataModel);
    }

    public void setOnBindTimeNanos(long onBindTimeNanos) {
      this.onBindTimeNanos = onBindTimeNanos;
    }

    @Bindable
    public String getBindTime() {
      long elapsed = System.nanoTime() - onBindTimeNanos;
      return String.format("%.2f ms", elapsed / 1000000f);
    }

    /**
     * When the ViewModel has become ready, notify that all the properties that
     * are from that have changed.
     */
    public void setViewModel(AdaptingDemo_Models.ViewModel viewModel) {
      super.setViewModel(viewModel);
      notifyPropertyChanged(BR.viewModel);
      notifyPropertyChanged(BR.string);
      notifyPropertyChanged(BR.delay);
      notifyPropertyChanged(BR.bindTime);
    }
  } // class LoadingViewModel

  /**
   * The ViewHolder when using Data Binding is simple; it just uses the layout's
   * ViewDataBinding to do the binding.
   */
  public static class LoadingViewHolder extends RecyclerView.ViewHolder {
    private AdaptingDemoDataBindingListItemBinding binding;

    public LoadingViewHolder(AdaptingDemoDataBindingListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void setLoadingViewModel(LoadingViewModel loadingViewModel) {
      binding.setLoadingViewModel(loadingViewModel);
    }
  } // class LoadingViewHolder
}
