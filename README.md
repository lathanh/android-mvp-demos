This MVP series of demos provide simple samples of strategies for building UIs
that are high-performance, responsive, and reusable.

* **High-performance** UIs are able to scrolled at 60 frames-per-second, making
  them a buttery-smooth to interact with. This is achieved by doing only binding
  in the UI thread, and other view prep in a background thread.
* **Responsive** UIs are able to able to react to touch always and quickly. This
  is also achieved by minimizing UI thread work to only binding.
* **Reusable** UI elements allow views (layouts), data models, and the logic to
  hook them up to each other to be re-used more easily. This includes minimizing
  the difference between list and non-list use-cases; for example, use the same
  code to hook up data for a Fragment as hooking it into an item of a
  RecyclerView.

Definitions
-------------------------------------------------------------------------------
First, let's clarify the difference between "Adapting" and "Binding".

### Adapting
Adapting is the work of taking "data" and _preparing_ it for display.
Some examples of adapting are:

  * Combining a person's first name and last name into one "displayName" field
  * Taking an average rating of "4.32" and formatting it for the user's locale
    (e.g., "4,32" for French and "4 32" for German)
  * Generating a string by combining a StringFormat (from a string resource)
    with values
  * Parsing HTML into Spannable text (so text surrounded by "``<em>``", for
    example is displayed in italics.)
  * Combining data from multiple data objects determine the visual state of a 
    view. 

Each of these operations takes time. Many take a negligible amount of time,
while some take a significant amount (e.g., parsing HTML).
When done in the UI thread, they can — and do — add up, and cause the UI to
not respond. Within a list/RecyclerView context, this causes "jank",
interruptions in smooth scrolling.

### Binding
Binding is the work of taking data — ideally that has already been prepared 
(adapted) — and attaching it to the view for display.
In Android specifically, that means putting them into View objects where the
renderer will draw them onto the screen.

### Rendering
Rendering is the process of taking the Views and drawing them onto the screen.
TextViews draw text onto the screen while ButtonViews draw buttons.
If you want to achieve 60 fps, then you have about 17ms (1s / 60) to hand off
the Views you want rendered _and_ to let the renderer render those Views.
If you're going to do Adapting also, that also needs to happen within those
17ms.


Adapting Demo — Overview
-------------------------------------------------------------------------------
Adapting and Binding are often both done at the same time; for lists, that's
within `RecyclerView.Adapter#onBindViewHolder()` (or `Adapter#getView()`).
The RecyclerView.Adapter encourages you to do adapting within it (it is called 
an adapter, after all).
You'll see this in Android documentation and tutorials, and thus also in many 
projects.

In this demo, we'll also start by doing it this way — adapting and binding 
together — in `AdaptingDemo_StandardFragment`.
We'll see that a couple of milliseconds of adapting work affects the smoothness
of scrolling.

Then in `AdaptingDemo_Improvement1Fragment` we'll move Adapting into the 
background by doing it in the AsyncTask that retrieves the data.
This is a simple, naive approach that gets us buttery-smooth scrolling, but the 
trade-off is that we make the user wait at a blank loading screen longer.

With an additional improvement, we can achieve buttery-smooth scrolling without
adding delay to the initial blank loading screen.
We'll do this in `AdaptingDemo_Improvement2Fragment` by allowing each row to
have loading indicator while it's adapting and using individual tasks to adapt
each row's data.

*Note: Now is a good time to point out that you'll see some not-so-best 
practices, like operating parameters in constants, weird organization of inner
classes, strange class naming, and embedding strings in Java (instead of
Android resource files). These are intended to make it simpler to understand 
the demos (so you don't have trace through as many files).*

### Components of the "Adapting Demo"
The `AdaptingDemo_BaseFragment` and `AdaptingDemo_Models` classes contain what 
all of these approaches have in common.

#### AdaptingDemo_Models
* `AdaptingDemo_Models.DataModel`: This is an object containing the data that is
  retrieved from the remote server or local database. It closely matches the 
  data source's model and simply holds that data.
* `AdaptingDemo_Models.ViewModel`: This is an object that holds data as it
  should be displayed. While it often has a strong resemblance to a data model, 
  it may also often:
    * transform/format the data for display,
    * contain data from multiple data models, and
    * contain data/state not found in and data model.
* `AdaptingDemo_Models.LoadingViewModel`: This is a ViewModel designed to hold
  another actual ViewModel, but holds `null` when the ViewModel is not yet 
  ready.
  It allows the View to determine whether the the actual ViewModel is available,
  and if not, show a loading indicator instead.
  This implementation holds on to the DataModel so that the actual ViewModel
  doesn't have to be generated (from the DataModel) until it will be used
  (when/if the item is scrolled into view).

Data Models are *Adapted* into View Models, and View Models are *Bound* to the 
View (with the help of a ViewHolder).

For now, ignore that the ViewModel and LoadingViewModel extend `BaseObservable`,
and that some fields have the `@Bindable` annotation.
These are used by the Data Binding Demo, later.
    
#### AdaptingDemo_BaseFragment
The `AdaptingDemo_BaseFragment` contains most of the code that each step in
this demo have in common.
Not only will this ensure a level playing field for comparing each approach,
but it also makes it easier to see how each of the different implementations is
 different.

##### Data Loading
`loadData()` emulates fetching of data from a place that takes `LOAD_DELAY_MS`
milliseconds, for example from a database or remote server.
For simplicity, from here on out we'll refer to it as _fetching data from the
server_.
Each demo implementation will call this from its AsyncTask to fetch the data.

Each implementation (subclass) will implement `getLoadTask()`, which will return
an AsyncTask that calls `loadData()`.
Some implementations will also do Adapting in their AsyncTask.

##### Adapting
`adaptDataModelToViewModel` Adapts a `DataModel` (one of the items in the list
returned by `loadData()`) into a `ViewModel`. How much work to take to do this
adapting is determined by the `ITEM_ADAPTING_COST_*` constants at the top of the
class.

The `BaseAdapter` (inner class) implements the `onCreateViewHolder()`, so all
implementations use the same layout/view for their items.
Each implementation will do its own binding in `onBindViewHolder()` because each
will do a different amount of work in here.

Adapting Demo, Standard Approach — Adapt _and_ Bind in the Adapter
-------------------------------------------------------------------------------
`AdaptingDemo_StandardFragment` should be pretty straightforward: the AsyncTask
in `getLoadTask()` simply returns the data (returned by
`AdaptingDemo_BaseFragment.loadData()` as-is.

It will then do adapting _and_ binding in it's adapter.
That is, in its adapter is where `adaptDataModelToViewModel()` is called to do
the adapting, and that adapted data is also then bound (to the view).

### User Experience
When you run this demo, you should observe the following (you may need to tweak
the constants at the top of `AdaptingDemo_BaseFragment` depending on your
hardware):
  1. *Loading*: Immediately after the data has been fetched (after about
     `LOAD_DELAY_MS` milliseconds), items will begin to appear.
  2. *Scrolling*: If you scroll quickly, you should get some stuttering.
     This is because some items will take too long to adapt, tying up the UI 
     thread which interrupts smooth scrolling.
     Also, after enough scrolling (perhaps a lot depending on available memory)
     there may be additional stutter when GC happens because each binding
     occurrence involves an object creation (that is immediately dereferenced).

Adapting Demo, Improvement 1 — Adapt in the AsyncTask, Bind in the Adapter
-------------------------------------------------------------------------------
`AdaptingDemo_Improvement1Fragment` will take a simple approach to improving
scrolling performance: It will do the adapting ahead of time.

After fetching the data and before returning it from
`AsyncTask.doInBackGround()`, it will also adapt all of the data.
That way when it comes time to bind, there's only binding work to be done.
The downside is that no items will be shown until all of the items have been
adapted.

Note in the implementation that we try to do all the adapting as fast as
possible by using many threads (about twice as many as there are CPUs on the
device). Because the UI is not doing anything other than showing a loading
indicator, it shouldn't have a significant negative effect on the user 
experience.

### User Experience
  1. *Loading*: The fragment will take longer than the standard approach before 
     any items appear. That's because adapting must be completed for all items 
     before any are shown
  2. *Scrolling*: Scrolling should be buttery-smooth because binding work is
     trivial (with no object instantiation).

Adapting Demo, Improvement 2 — The Best of Both Worlds
-------------------------------------------------------------------------------
We can have both buttery-smooth scrolling without adding to the load time.
We will do the adapting in the background, but we won't wait until adapting has
completed before showing them.
However, that means that each item has to be able to be displayed even if its
adapting hasn't been completed; that is, each has to have a visual loading state.
Once adapting of the item has completed, we'll update the row to show its
content (if it's on screen).

To accomplish this, `AdaptingDemo_Improvement2Fragment` uses two inner classes,
LoadingViewModel and LoadingViewHolder.
     
  * *LoadingViewModel*: This is a view model that allows the view to have a
    loading state, a loading indicator until the item has been adapted and ready
    for display.
    To accomplish this, it simply has a field for the actual ViewModel in its
    `viewModel` field.
    If the `viewModel` field is null, that means the real ViewModel isn't ready
    yet and if the item comes into view then a loading indicator should be
    displayed.
  * *LoadingViewHolder*: This is the view holder for a view that has a loading
    state (a loading indicator) that will be displayed if the item to be
    displayed is not yet ready.
    It also has the ability to allow the ViewModel that it's bound to to notify
    it when it changes so the display can be updated (by letting the adapter
    know).

In this implementation, each row is adapted lazily; that is, it is not adapted
until/unless the row comes into view.

  1. When the item first comes into view, its viewModel is null.
      * A loading indicator will be shown for the item.
  2. An AsyncTask is submitted for adapting to occur in the background
  3. When the adapting is complete, the ViewModel is added to the
     LoadingViewModel
  4.  The LoadingViewModel has a handle to the ViewHolder that it was bound to.
     If that viewHolder hasn't been bound to another item (it hasn't been
     recycled), then the LoadingViewHolder notifies the ViewHolder
     (`ViewHolder.onChangeListener()`) so it can be updated
     
### A Couple Notes

#### "Observables"
Notifying the item's ViewModel (and then the adapter) that something has changed 
takes significant infrastructure. It can be done more cleanly but with even more
code. Instead of going the route of writing/demonstrating that better 
infrastructure, we'll take advantage of Android Data Binding (the next set of 
demos) which provides this ability.

#### Minimizing OnChangeListener object creation
Notice that one OnChangeListener is created per ViewHolder (and given to the 
ViewHolder to hold on to). That OnchangeListener is updated each time the 
View/ViewHolder is bound to a different item/position/ViewModel. This is so we
don't waste objects (discard and recreate with each bind), which would result in
GC that can cause stutters in scrolling ("jank").


What's Next?
-------------------------------------------------------------------------------
We've demonstrated how to achieve "high-performance" and "responsive" UIs by
separating adapting from binding. 

### 1. Data Binding
[Android Data Binding](https://developer.android.com/tools/data-binding/guide.html) 
simplifies the binding step.
The [Data Binding demos](data-binding.md) essentially repeats the Adapting demo, 
but using Android Data Binding instead of manually binding.

### 2. A Framework
We'll apply this strategy to a framework. That is we'll create base (abstract) 
classes that steer developers into the practices demonstrated here; 
and further, provide implementations that take care of common use cases for 
them, like using a ExecutorService to do the adapting in the background.

### And Beyond
  1. Introduce updates of the data, which should update the view.
  2. Learn how this makes our adapting, binding, and state 
     saving/restoring easy for non-RecyclerView contexts. 
