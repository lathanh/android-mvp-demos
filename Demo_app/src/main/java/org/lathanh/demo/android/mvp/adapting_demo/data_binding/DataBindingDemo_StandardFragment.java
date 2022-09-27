package org.lathanh.demo.android.mvp.adapting_demo.data_binding;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models.DataModel;
import org.lathanh.demo.android.mvp.databinding.AdaptingDemoDataBindingStandardListItemBinding;

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
   * The ViewHolder when using Data Binding is simple; it just uses the layout's
   * ViewDataBinding to do the binding.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private AdaptingDemoDataBindingStandardListItemBinding binding;

    public ViewHolder(AdaptingDemoDataBindingStandardListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void setDataModel(DataModel dataModel) {
      binding.setDataModel(dataModel);
    }
  } // class LoadingViewHolder

  /**
   * Adapts the DataModel at the time that it's needed; that is, when it comes
   * into view.
   */
  private class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<DataModel> dataModels;
    private LayoutInflater inflater;

    public Adapter(Context context, List<DataModel> dataModels) {
      this.inflater = LayoutInflater.from(context);
      this.dataModels = dataModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
      AdaptingDemoDataBindingStandardListItemBinding binding =
          AdaptingDemoDataBindingStandardListItemBinding.inflate(inflater,
                                                                 parent, false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
      final DataModel dataModel = dataModels.get(position);
      viewHolder.setDataModel(dataModel);
    } // onBindViewHolder()

    @Override
    public int getItemCount() {
      return dataModels.size();
    }
  } // class Adapter

}
