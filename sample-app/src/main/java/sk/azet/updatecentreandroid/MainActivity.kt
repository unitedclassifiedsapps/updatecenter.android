package sk.azet.updatecentreandroid

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import sk.azet.updatecenter.OnVersionCheckedListener
import sk.azet.updatecenter.SemanticVersion
import sk.azet.updatecenter.UpdateCenter
import sk.azet.updatecenter.datasource.FirebaseVersionDataSource
import sk.azet.updatecenter.VersionDataSource
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var remoteUpdateCenter: UpdateCenter
    lateinit var localUpdateCenter: UpdateCenter
    lateinit var listener: OnVersionCheckedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)
        listener = object : OnVersionCheckedListener {
            override fun notLatestVersion(currentVersion: String, latestVersion: String) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Newer version available")
                    .setMessage("Your version: $currentVersion, latest available: $latestVersion")
                    .show()
            }

            override fun shouldUpdate(currentVersion: String, latestVersion: String) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("App should be updated")
                    .setMessage("Your version: $currentVersion, latest available: $latestVersion")
                    .show()
            }

            override fun mustUpdate(currentVersion: String, latestVersion: String) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("This app version is outdated")
                    .setMessage("Your version: $currentVersion, latest available: $latestVersion")
                    .show()
            }

            override fun onError(currentVersion: String, exception: Throwable?) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Error")
                    .setMessage(exception?.message)
                    .show()
            }
        }

        mode_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            latest_editText.isEnabled = !isChecked
            must_checkbox.isEnabled = !isChecked
            should_checkBox.isEnabled = !isChecked
        }
        check_buttom.setOnClickListener {
            if (mode_checkbox.isChecked) {
                setUpRemoteCenter()
                remoteUpdateCenter.check()
            } else {
                setUpLocalCenter()
                localUpdateCenter.check()
            }
        }
    }

    private fun setUpRemoteCenter() {
        remoteUpdateCenter = UpdateCenter(
            FirebaseVersionDataSource(
                this,
                FirebaseRemoteConfig.getInstance(),
                current_editText.text.toString()),
            listener
        )
    }

    private fun setUpLocalCenter() {
        localUpdateCenter = UpdateCenter(object : VersionDataSource {
            override val currentLocalVersion: SemanticVersion
                get() = SemanticVersion.fromVersionString(current_editText.text.toString())
            override val latestVersion: SemanticVersion
                get() = SemanticVersion.fromVersionString(latest_editText.text.toString())
            override val shouldUpdateValue: Boolean
                get() = should_checkBox.isChecked
            override val mustUpdateValue: Boolean
                get() = must_checkbox.isChecked

            override fun getUpdateValues(onSuccess: (Boolean, Boolean, SemanticVersion, SemanticVersion) -> Unit, onError: (SemanticVersion, Throwable?) -> Unit) {
                onSuccess(mustUpdateValue, shouldUpdateValue, currentLocalVersion, latestVersion)
            }
        }, listener)
    }
}
