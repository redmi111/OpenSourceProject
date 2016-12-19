package com.music.musicplayer135.features.folder_chooser

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.music.musicplayer135.R
import com.music.musicplayer135.features.folder_chooser.FolderChooserActivity.Companion.newInstanceIntent
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.*
import com.music.musicplayer135.mvp.RxBaseActivity
import com.music.musicplayer135.uitools.visible
import i
import java.io.File
import javax.inject.Inject

/**
 * Activity for choosing an audiobook folder. If there are multiple SD-Cards, the Activity unifies
 * them to a fake-folder structure. We must make sure that this is not choosable. When there are no
 * multiple sd-cards, we will directly show the content of the 1 SD Card.
 *
 *
 * Use [newInstanceIntent] to get a new intent with the necessary
 * values.
 *
 * @author Paul Woitaschek
 */
class FolderChooserActivity : RxBaseActivity<FolderChooserView, FolderChooserPresenter>(), FolderChooserView, HideFolderDialog.OnChosenListener {

  override fun newPresenter() = FolderChooserPresenter()

  override fun provideView() = this

  override fun showSubFolderWarning(first: String, second: String) {
    val message = "${getString(R.string.adding_failed_subfolder)}\n$first\n$second"
    Toast.makeText(this, message, Toast.LENGTH_LONG)
      .show()
  }

  private lateinit var adapter: FolderChooserAdapter
  private lateinit var spinnerAdapter: MultiLineSpinnerAdapter<File>
  private lateinit var choose: View
  private lateinit var upButton: ImageButton
  private lateinit var currentFolder: TextView
  private lateinit var spinnerGroup: View

  @Inject lateinit var permissionHelper: PermissionHelper

  override fun askAddNoMediaFile(folderToHide: File) {
    val hideFolderDialog = HideFolderDialog.newInstance(folderToHide)
    hideFolderDialog.show(supportFragmentManager, HideFolderDialog.TAG)
  }

  override fun getMode() = OperationMode.valueOf(intent.getStringExtra(NI_OPERATION_MODE))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    App.component().inject(this)

    // find views
    setContentView(R.layout.activity_folder_chooser)
    choose = findViewById(R.id.choose)
    val abort = findViewById(R.id.abort)
    val recyclerView = findViewById(R.id.recycler) as RecyclerView
    upButton = find(R.id.upButton)
    currentFolder = find(R.id.currentFolder)
    val toolSpinner: Spinner = find(R.id.toolSpinner)
    spinnerGroup = find(R.id.spinnerGroup)

    // toolbar
    setupActionbar(toolbar = find(R.id.toolbar),
      upIndicator = R.drawable.close,
      title = getString(R.string.audiobook_folders_title))

    // listeners
    choose.setOnClickListener { presenter().chooseClicked() }
    abort.setOnClickListener { finish() }
    upButton.setOnClickListener { onBackPressed() }

    //recycler
    adapter = FolderChooserAdapter(this, getMode()) {
      presenter().fileSelected(it)
    }
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    recyclerView.adapter = adapter

    // spinner
    spinnerAdapter = MultiLineSpinnerAdapter(toolSpinner, this, Color.WHITE)
    toolSpinner.adapter = spinnerAdapter
    toolSpinner.itemSelectionStream()
      .filter { it != AdapterView.INVALID_POSITION } // filter invalid entries
      .skip(1) // skip the first that passed as its no real user input
      .subscribe {
        i { "spinner selected with position $it and adapter.count ${spinnerAdapter.count}" }
        val item = spinnerAdapter.getItem(it)
        presenter().fileSelected(item.data)
      }
  }

  override fun onStart() {
    super.onStart()

    // permissions
    permissionHelper.storagePermission(this) { presenter().gotPermission() }
  }

  override fun onBackPressed() {
    if (!presenter().backConsumed()) {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) {
      finish()
      return true
    } else {
      return super.onOptionsItemSelected(item)
    }
  }

  override fun setCurrentFolderText(text: String) {
    currentFolder.text = text
  }

  override fun showNewData(newData: List<File>) {
    adapter.newData(newData)
  }

  override fun setChooseButtonEnabled(chooseEnabled: Boolean) {
    choose.isEnabled = chooseEnabled
  }

  override fun newRootFolders(newFolders: List<File>) {
    i { "newRootFolders called with $newFolders" }
    spinnerGroup.visible = newFolders.size > 1

    val newData = newFolders
      .map {
        val name = if (it.absolutePath == FolderChooserPresenter.MARSHMALLOW_SD_FALLBACK) {
          getString(R.string.storage_all)
        } else {
          it.name
        }
        MultiLineSpinnerAdapter.Data(it, name)
      }
    spinnerAdapter.setData(newData)
  }


  /**
   * Sets the choose button enabled or disabled, depending on where we are in the hierarchy
   */
  override fun setUpButtonEnabled(upEnabled: Boolean) {
    upButton.isEnabled = upEnabled
    val upIcon = if (upEnabled) drawable(R.drawable.ic_arrow_upward) else null
    upButton.setImageDrawable(upIcon)
  }

  override fun onChosen() {
    presenter().hideFolderSelectionMade()
  }

  enum class OperationMode {
    COLLECTION_BOOK,
    SINGLE_BOOK
  }

  companion object {

    private const val NI_OPERATION_MODE = "niOperationMode"

    /**
     * Generates a new intent with the necessary extras

     * @param c             The context
     * *
     * @param operationMode The operation mode for the activity
     * *
     * @return The new intent
     */
    fun newInstanceIntent(c: Context, operationMode: OperationMode): Intent {
      val intent = Intent(c, FolderChooserActivity::class.java)
      intent.putExtra(NI_OPERATION_MODE, operationMode.name)
      return intent
    }
  }
}