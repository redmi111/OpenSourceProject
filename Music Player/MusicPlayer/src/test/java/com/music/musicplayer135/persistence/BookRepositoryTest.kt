package com.music.musicplayer135.persistence

import android.os.Build
import com.music.musicplayer135.BookMocker
import com.music.musicplayer135.BuildConfig
import com.music.musicplayer135.TestApp
import com.music.musicplayer135.persistence.internals.BookStorage
import com.music.musicplayer135.persistence.internals.InternalDb
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * Test for the book repository.
 *
 * @author Paul Woitaschek
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP), manifest = "src/main/AndroidManifest.xml", application = TestApp::class)
class BookRepositoryTest {

    init {
        ShadowLog.stream = System.out
    }

    private lateinit var repo: BookRepository

    @Before
    fun setUp() {
        val internalDb = InternalDb(RuntimeEnvironment.application)
        val internalBookRegister = BookStorage(internalDb)
        repo = BookRepository(internalBookRegister)
    }

    @Test
    fun testInOut() {
        val dummy = BookMocker.mock(5)
        repo.addBook(dummy)
        val firstBook = repo.activeBooks.first()
        val dummyWithUpdatedId = dummy.copy(id = firstBook.id)

        assertThat(dummyWithUpdatedId).isEqualTo(firstBook)
    }
}