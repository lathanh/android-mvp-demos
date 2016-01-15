High-performance UIs with Android Data Binding
===============================================================================
The [Adapting Demo](readme), demonstrated the benefit of separating adapting and
binding. 
Here, we will use
[Android Data Binding](https://developer.android.com/tools/data-binding/guide.html)
to simplify the binding step by letting Android do it for us.

### Components of the "Data Binding Demo"
The Data Binding Demo builds upon the same `AdaptingDemo_BaseFragment` as the
Adapting Demo. However, it uses slightly different models:

* `DataBindingDemo_Models.LoadingViewModel`: Since we're no longer doing the
  binding ourselves, it's not useful to time how long is spent in the bind() 
  method. Instead, we'll save the current timestamp in the `onBindTimeNanos`
  field during bind().
  Then, in the view, we'll print the amount of time that has elapsed since 
  bind() was called. 
  While not comparable to timing the amount of time spent in bind(), it will 
  give us an idea of how much time is spent binding.
* `DataBindingDemo_Models.LoadingViewHolder`: This ViewHolder uses Android Data
  Binding to do the binding.

Data Binding Demo, Standard Approach — Adapt _and_ Bind in the Adapter
-------------------------------------------------------------------------------
In Android's
[Data Binding Guide](https://developer.android.com/tools/data-binding/guide.html),
in various tutorials, and probably in many projects, you'll see adapting 
happening in the view itself (in the layout files) or in the objects that are
being provided to the view ("Data Objects").

*Note: What Android Data Binding calls "Data Objects" are called "View Models",
here. Android Data Binding doesn't have a concept for an object that's already
adapted and ready for binding that's separate from the object(s) that data were
adapted from.*

The `DataBindingDemo_StandardFragment` represents the "standard" approach to
Data Binding, simply giving the Data Model to the Adapter, which will adapt it
as needed; that is, at the time of binding.

The ViewModel is adapted in the `Adapter.onBindViewHolder` and then given to the
viewHolder (`setLoadingViewModel()`), which uses the auto-generated 
ViewDataBinding class to do the actual binding.

### User Experience
This is largely equivalent to `AdaptingDemo_StandardFragment`.

Data Binding Demo, Improvement 1 — The Best of All Three Worlds
-------------------------------------------------------------------------------
In `DataBinding_Improvement1Fragment`, we'll do essentially what
`AdaptingDemo_Improvement2Fragment` does — introduce a loading state for items
while they load — and use Data Binding for its easy binding and updating when an
item changes from loading to loaded. We'll have the best of all three worlds:

1. The list shows as soon as loaded,
2. The list scrolls smoothly because it only does binding, and
3. We do less work of binding and handling re-binding when the ViewModel is
   ready.

Like in AdaptingDemo_Improvement2Fragment, when the ViewModel becomes ready, we
update the LoadingViewModel. However, instead of having to notify the 
ViewHolder (which notifies the Adapter it's in), we just call 
`notifyPropertyChanged()` which works like magic because our LoadingViewModel
implemented
[BaseObservable](https://developer.android.com/tools/data-binding/guide.html#observable_objects)
(which the auto-generated ViewDataBinding class observes to know when to 
update).
