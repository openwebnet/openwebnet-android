package com.github.openwebnet.view.profile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.openwebnet.R
import com.github.openwebnet.view.NavigationViewItemSelectedListener
import org.slf4j.LoggerFactory

// TODO
class ProfileActivity : AppCompatActivity() {

    private val log = LoggerFactory.getLogger(NavigationViewItemSelectedListener::class.java)

    companion object {
        const val EXTRA_PROFILE_EMAIL = "com.github.openwebnet.view.profile.EXTRA_PROFILE_EMAIL"
        const val EXTRA_PROFILE_NAME = "com.github.openwebnet.view.profile.EXTRA_PROFILE_NAME"
    }

    // https://firebaseopensource.com/projects/firebase/firebaseui-android/firestore/readme.md
    // { info: id(uuid), database-version, backup-name, date, share [user-id] | schema: light, automation,  ... }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var email: String = intent.getStringExtra(EXTRA_PROFILE_EMAIL)
        var name: String = intent.getStringExtra(EXTRA_PROFILE_NAME)
        log.info("ProfileActivity: email={} name={}", email, name)
    }
}
