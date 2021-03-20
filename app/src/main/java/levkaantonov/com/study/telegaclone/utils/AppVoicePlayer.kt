package levkaantonov.com.study.telegaclone.utils

import android.media.MediaPlayer
import levkaantonov.com.study.telegaclone.database.getFileFromStorage
import java.io.File

class AppVoicePlayer {
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile: File
    private lateinit var mMessageKey: String

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        mMessageKey = messageKey
        mFile = File(APP_ACTIVITY.filesDir, mMessageKey)
        if (mFile.exists() && mFile.length() > 0 && mFile.isFile) {
            startPlay {
                function()
            }
            return
        }

        mFile.createNewFile()
        getFileFromStorage(mFile, fileUrl){
            startPlay {
                function()
            }
        }
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener {
                stop{
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun release() {
        try {
            mMediaPlayer.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun preparePlayer(){
        mMediaPlayer = MediaPlayer()
    }

}