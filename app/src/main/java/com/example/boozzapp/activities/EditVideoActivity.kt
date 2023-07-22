package com.example.boozzapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.Statistics
import com.arthenica.mobileffmpeg.StatisticsCallback
import com.example.boozzapp.R
import com.example.boozzapp.adapter.TemplateImageAdapter
import com.example.boozzapp.pojo.PartyModelCommandImages
import com.example.boozzapp.pojo.PartyModelStaticInputs
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.StoreUserData
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.dialog_watermark.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.math.BigDecimal
import java.util.*

class EditVideoActivity : BaseActivity() {
    lateinit var videoPojo: TemplatesItem
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var outputVideo = ""
    private var pauseDuration: Long = 0
    private var flagExporting = false
    private var flagChanges = false
    private var mediaPlayer: MediaPlayer? = null
    private var zipFileName = ""
    private var rootJsonData: JSONObject? = null
    private var isPlaying = true

    lateinit var templateImageAdapter: TemplateImageAdapter
    private val imagesList: ArrayList<PartyModelCommandImages> = ArrayList()
    lateinit var imageSelectedPojo: PartyModelCommandImages
    private val stcInputList: ArrayList<PartyModelStaticInputs> =
        ArrayList<PartyModelStaticInputs>()
    private var videoPath: String? = null

    private var isRemoveWaterMark = false
    private var isCreated = false

    private var audioGap: Long = 0
    private var executionId: Long = 0
    private var flagVideoDuration = 0
    private val video_duration = 0
    private var video_total_dur = 0
    private var fileNameInstaCrop: String? = null
    private var bitmap_thumb: Bitmap? = null
    private var finalVideoPath = ""
    private var fileName: String? = null
    private var textDataArray: JSONArray? = null
    private var filepathwithoutwater: String? = null
    private val ffmpegcommandwithoutwater = ArrayList<String>()


    interface EditVideoActivityListener {
        fun onImageChange(data: PartyModelCommandImages)
    }

    var onClickListener = object : EditVideoActivityListener {
        override fun onImageChange(data: PartyModelCommandImages) {
            imageSelectedPojo = data
            ImagePicker.with(activity)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080, 1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)
        activity = this
        storeUserData = StoreUserData(activity)
        videoPojo = intent.getParcelableExtra("videoPojo")!!

        dataSourceFactory = buildDataSourceFactory()
        mediaPlayer = MediaPlayer()

        zipFileName = videoPojo.zip?.lastIndexOf(".")?.let { videoPojo.zip?.substring(0, it) } ?: ""
        val jsonFile = "python.json"
        val jsonFilePath: String =
            getZipDirectoryPath(this) + zipFileName + File.separator + jsonFile
        performTaskWithCallback(jsonFilePath)


        try {
            outputVideo = getZipDirectoryPath(this) + zipFileName + File.separator + "output.mp4"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        templateImageAdapter = TemplateImageAdapter(activity, imagesList, onClickListener)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rv_image_list.layoutManager = layoutManager
        rv_image_list.adapter = templateImageAdapter

        val set = ConstraintSet()
        set.clone(
            clPreview
        )
        set.setDimensionRatio(
            cvView.id, videoPojo.width + ":" + videoPojo.height
        )
        set.applyTo(clPreview)

        initializeExoPlayer()

        editBack.setOnClickListener { finish() }
        tvEditSongName.text = videoPojo.title

        ivCloseWatermark.setOnClickListener {
            showDialog()
        }


        clPreview.setOnClickListener {
            playPausePlayer(isPlaying)
        }

        rl_preview_control.setOnClickListener { v ->
            if (flagChanges) {
                processmessage.text = "Crafting your video… Please wait a moment!"
                exportVideo("preview")
            } else {
                isPlaying = true
                playPausePlayer(isPlaying)
            }

        }
        tvExport.setOnClickListener {
            showDialog()
        }


        val watermark_position: String = videoPojo.watermarkPosition!!
        val params = remove.layoutParams as RelativeLayout.LayoutParams
        if (watermark_position == "RIGHT_TOP_TO_LEFT_BOTTOM") {
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "LEFT_BOTTOM_TO_RIGHT_TOP") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
        } else if (watermark_position == "RIGHT_BOTTOM_TO_LEFT_TOP") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "LEFT_TOP_TO_RIGHT_TOP") {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "LEFT_BOTTOM_TO_RIGHT_BOTTOM") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
        } else if (watermark_position == "RIGHT_TOP_TO_LEFT_TOP") {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "RIGHT_BOTTOM_TO_LEFT_BOTTOM") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "TOP_LEFT") {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
        } else if (watermark_position == "TOP_RIGHT") {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        } else if (watermark_position == "BOTTOM_LEFT") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
        } else if (watermark_position == "BOTTOM_RIGHT") {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
        }
    }


    override fun onStop() {
        super.onStop()
        player?.release()

    }

    private fun readJsonData(filePath: String) {
        val command = readFromFile(filePath)

        if (command.isNullOrEmpty()) {
            return
        }
        rootJsonData = command.let { JSONObject(it) }

        // get Images
        val images = rootJsonData?.getJSONArray("images")
        if (images != null) {
            for (i in 0 until images.length()) {
                val image_obj = images.getJSONObject(i)
                val imagePath =
                    getZipDirectoryPath(this) + zipFileName + File.separator + image_obj.getString("name")

                imagesList.add(
                    PartyModelCommandImages(
                        image_obj.getString("name"),
                        image_obj.getInt("w"),
                        image_obj.getInt("h"),
                        imagePath,
                        image_obj.getJSONArray("prefix"),
                        image_obj.getJSONArray("postfix")
                    )
                )
            }
        }

        val video_obj = rootJsonData?.getJSONObject("video")

        if (video_obj != null) {
            audioGap = video_obj.getString("duration").toDouble().toLong()
        }
        if (video_obj != null) {
            flagVideoDuration = video_obj.getString("duration").toInt()
        }
        // Log.e("VideoDuration>>> making", "ZipFile: " + audio_gap)

        try {
            textDataArray = rootJsonData!!.getJSONArray("texts")

            if (textDataArray != null && textDataArray!!.length() > 0) {
                val modelCommandImages = PartyModelCommandImages("text", textDataArray!!)
                imagesList.add(modelCommandImages)
                for (i in 0 until textDataArray!!.length()) {
                    val textObject = textDataArray!!.getJSONObject(i)
                    textObject.put("replaced_value", "")
                    textObject.put("date", 0)
                    textObject.put("month", 0)
                    textObject.put("year", 0)
                    textObject.put("hour", 0)
                    textObject.put("minutes", 0)
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshImageAdapter() {
        Log.i("TAG", "handleResult: " + imagesList.size)
        templateImageAdapter.notifyDataSetChanged()
    }


    private fun readFromFile(path: String): String {
        var ret = ""
        try {
            val inputStream: InputStream = FileInputStream(File(path))
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var receiveString: String?
            val stringBuilder = StringBuilder()
            while (bufferedReader.readLine().also { receiveString = it } != null) {
                stringBuilder.append(receiveString)
            }
            inputStream.close()
            ret = stringBuilder.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("FileException>>>>", Log.getStackTraceString(e));
        }
        return ret
    }

    private fun performTaskWithCallback(filePath: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val backgroundTask = async(Dispatchers.Default) {
                // Start the background task
                readJsonData(filePath)
            }

            // Wait for the background task to complete
            backgroundTask.await()

            // Handle the result on the main thread
            refreshImageAdapter()
        }
    }

    private fun getZipDirectoryPath(mContext: Context): String? {
        val externalDirectory = mContext.filesDir.absolutePath
        // String externalDirectory =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        val dir = File(
            externalDirectory + File.separator + mContext.resources.getString(R.string.zip_directory)
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }

    fun showDialog() {
        val holdDialog = Dialog(activity)
        holdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        holdDialog.setContentView(R.layout.dialog_watermark)

        // Set the background of the dialog window to transparent
        holdDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Calculate the desired height of the dialog (e.g., half of the screen)
        val windowHeight = activity.window.decorView.height
        val dialogHeight = windowHeight / 2

        // Set the dialog's window layout parameters
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight)
        holdDialog.window?.setLayout(layoutParams.width, layoutParams.height)

        holdDialog.show()

        holdDialog.llRemoveWaterMark.setOnClickListener {
            isRemoveWaterMark = true
            holdDialog.dismiss()
        }

        holdDialog.ivCloseDialog.setOnClickListener {
            if (!isRemoveWaterMark) {
                saveVideo(finalVideoPath, "export")
            }
            holdDialog.dismiss()
        }

        /*    if (!flagExporting) {
                processmessage.text = "Crafting your video… Please wait a moment!"
                if (finalVideoPath != "" && File(finalVideoPath).exists()) {
                    if (flagChanges) {
                        exportVideo("export")
                    } else {
                        saveVideo(finalVideoPath, "export")
                    }
                } else {
                    if (isRemoveWaterMark && !flagChanges) {
                        if (finalVideoPath != "" && File(finalVideoPath).exists()) {

                        } else {
                            val time = System.currentTimeMillis()
                            fileName = "Boozz_$time.mp4"
                            fileNameInstaCrop = "Boozz_Insta$time.mp4"
                            finalVideoPath =
                                File(getDownloadedPath(activity) + fileName).absolutePath
                        }
                        saveVideo(outputVideo, "export")
                    } else {
                        exportVideo("export")
                    }
                }
            } else {
                val animation = AnimationUtils.loadAnimation(activity, R.anim.shake_error)
                ll_progress.startAnimation(animation)
            }*/
    }

    private fun buildDataSourceFactory(): DataSource.Factory {
        val userAgent = Util.getUserAgent(this, "Boozz_Status_Maker")
        return DefaultDataSourceFactory(this, userAgent)
    }

    override fun onResume() {
        super.onResume()
        pauseDuration = 0
        isPlaying = true
        playPausePlayer(isPlaying)
    }

    //endregion
    private fun initializeExoPlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
            exoPlayerView.player = player
            exoPlayerView.setBackgroundColor(Color.BLACK)
            exoPlayerView.useController = false
            player!!.addListener(object : Player.EventListener {
                @Deprecated("Deprecated in Java")
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                        pauseDuration = 0
                        playPausePlayer(isPlaying)
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        progressBar_exoplayer.visibility = View.VISIBLE
                        //                        loadingAnimationUtils.show();
                    } else if (playbackState == Player.STATE_READY) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                    } else if (playbackState == Player.STATE_IDLE) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                    }
                }
            })
        }
        prepareExoPlayer()
    }


    override fun onPause() {
        super.onPause()
        isPlaying = false
        playPausePlayer(isPlaying)
        releasePlayer()
    }

    private fun releasePlayer() {
        if (player != null) {
            try {
                pauseDuration = player!!.currentPosition
            } catch (e: java.lang.Exception) {
                if (e is IllegalStateException) { // bypass IllegalStateException
                    // You can again call the method and make a counter for deadlock situation or implement your own code according to your situation
                    var checkAgain = true
                    var counter = 0
                    var i = 0
                    while (i < 2) {
                        if (checkAgain) {
                            mediaPlayer!!.reset()
                            pauseDuration = player!!.currentPosition
                            if (pauseDuration > 0) {
                                checkAgain = false
                                counter++
                            }
                        } else {
                            if (counter == 0) {
                                throw e
                            }
                        }
                        i++
                    }
                }
            }
            player!!.release()
            player = null
            mediaSource = null
            Log.i("Player>>>", "Player released")
        }
    }

    private fun prepareExoPlayer() {
        mediaSource = dataSourceFactory?.let {
            ProgressiveMediaSource.Factory(it).createMediaSource(Uri.parse(outputVideo))
        }
        player?.prepare(mediaSource as ProgressiveMediaSource, true, false)
    }

    private fun playPausePlayer(play: Boolean) {

        if (player != null) if (play) {
            exo_thumb.isVisible = false
            try {
                player!!.seekTo(pauseDuration)
            } catch (_: Exception) {
            }
            player!!.playWhenReady = true
            player!!.playbackState
            rl_preview_control.isVisible = false
            isPlaying = false
        } else {
            try {
                pauseDuration = player!!.currentPosition
            } catch (_: Exception) {

            }
            player!!.playWhenReady = false
            player!!.playbackState

            if (!flagExporting) {
                rl_preview_control.isVisible = false
            } else {
                rl_preview_control.isVisible = false
            }
            isPlaying = true
        }
        pauseButton.isVisible = isPlaying

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            if (data.data != null) {
                val updatedImageList = imagesList.mapNotNull {
                    if (imageSelectedPojo == it) {
                        val imageName = uri.path?.let { path -> File(path).name }
                        imageName?.let { name -> it.copy(imgName = name, imgPath = uri.path!!) }
                    } else {
                        it
                    }
                }.toList()

                imagesList.clear()
                imagesList.addAll(updatedImageList)
                Log.i("TAG", "onActivityResult: " + imagesList.toString())
                Log.i("TAG", "onActivityResult: " + imagesList.size)

                refreshImageAdapter()
                isPlaying = false
                playPausePlayer(isPlaying)
                progressBar_exoplayer.visibility = View.GONE
//                                                loadingAnimationUtils.dismiss();
                //                                                loadingAnimationUtils.dismiss();
                rl_preview_control.setVisibility(View.VISIBLE)
                processmessage.text = "Crafting your video… Please wait a moment!"
                flagChanges = true

            }


            // Use Uri object instead of File to avoid storage permissions
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportVideo(exportType: String) {
        val time = System.currentTimeMillis()
        fileName = "Boozz_$time.mp4"
        fileNameInstaCrop = "Boozz_Insta$time.mp4"
        val newVideoPath = getFileDirectoryPath(activity) + fileName
        File(newVideoPath)
        // makwithoutwatercommand()
        executeCommand(newVideoPath, exportType)

    }


    private fun makwithoutwatercommand() {
        filepathwithoutwater =
            getDownloadedPath(activity) + "Boozz_" + System.currentTimeMillis() + 1 + ".mp4"
        ffmpegcommandwithoutwater.clear()
        try {
            val str_e = rootJsonData!!.getJSONArray("e")
            if (str_e.length() != 0) {
                for (i in 0 until str_e.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_e.getString(i)))
                }
            }
            // for images
            for (i in imagesList.indices) {
                if (!imagesList[i].imgName.equals("text")) {
                    if (imagesList[i].prefix.length() !== 0) for (j in 0 until imagesList[i].prefix.length()) {
                        ffmpegcommandwithoutwater.add(
                            replaceKeyWords(
                                imagesList[i].prefix.getString(j)
                            )
                        )
                    }
                    if (imagesList[i].imgPathExtra == null) ffmpegcommandwithoutwater.add(
                        imagesList[i].imgPath
                    ) else imagesList[i].imgPathExtra?.let {
                        ffmpegcommandwithoutwater.add(
                            it
                        )
                    }
                    if (imagesList[i].postfix
                            .length() !== 0
                    ) for (j in 0 until imagesList[i].postfix.length()) {
                        ffmpegcommandwithoutwater.add(
                            replaceKeyWords(
                                imagesList[i].postfix.getString(j)
                            )
                        )
                    }
                }
            }
            // for static inputs
            val static_inputs = rootJsonData!!.getJSONArray("static_inputs")
            for (i in 0 until static_inputs.length()) {
                val st_in_obj = static_inputs.getJSONObject(i)
                val path = getZipDirectoryPath(activity) +
                        zipFileName + File.separator + st_in_obj.getString("name")
                stcInputList.add(
                    PartyModelStaticInputs(
                        st_in_obj.getString("name"), path,
                        st_in_obj.getJSONArray("prefix"),
                        st_in_obj.getJSONArray("postfix")
                    )
                )
                videoPath = stcInputList[0].videoPath
                if (stcInputList[i].prefix
                        .length() !== 0
                ) for (j in 0 until stcInputList[i].prefix.length()) {
                    ffmpegcommandwithoutwater.add(
                        replaceKeyWords(
                            stcInputList[i].prefix.getString(j)
                        )
                    )
                }
                ffmpegcommandwithoutwater.add(stcInputList[i].videoPath)
                if (stcInputList[i].postfix.length() !== 0
                ) for (j in 0 until stcInputList[i].postfix.length()) {
                    ffmpegcommandwithoutwater.add(
                        replaceKeyWords(
                            stcInputList[i].postfix.getString(j)
                        )
                    )
                }
            }
            val str_m = rootJsonData!!.getJSONArray("m")
            if (str_m.length() != 0) {
                for (i in 0 until str_m.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_m.getString(i)))
                }
            }
            if (!isRemoveWaterMark) {
                val img_count = imagesList.size
                val startc_input_count = stcInputList.size
                val total_count = img_count + startc_input_count
                val half_dut = video_duration / 2
                val add_watermarh =
                    "[base_video];[base_video][$total_count]overlay=enable='between(t,0,$half_dut)':x=30:y=30[watermarked_part1];[watermarked_part1][$total_count]overlay=enable='between(t,$half_dut,$video_duration)':x=(main_w-overlay_w-30):y=(main_h-overlay_h-30)"
                val str_i = rootJsonData!!.getJSONArray("r")
                if (str_i.length() != 0) {
                    for (i in 0 until str_i.length()) {
                        ffmpegcommandwithoutwater.add(replaceKeyWordsDynamic(str_i.getString(i)) + add_watermarh)
                    }
                }
            } else {
                val str_r = rootJsonData!!.getJSONArray("r")
                if (str_r.length() != 0) {
                    for (i in 0 until str_r.length()) {
                        ffmpegcommandwithoutwater.add(replaceKeyWordsDynamic(str_r.getString(i)))
                    }
                }
            }
            //            JSONArray str_r = rootJsonData.getJSONArray("r");
//            if (str_r.length() != 0) {
//                for (int i = 0; i < str_r.length(); i++) {
//                    ffmpegcommandwithoutwater.add(replaceKeyWordsDynamic(str_r.getString(i)));
//                }
//            }
            val str_n = rootJsonData!!.getJSONArray("n")
            if (str_n.length() != 0) {
                for (i in 0 until str_n.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_n.getString(i)))
                }
            }
            val str_g = rootJsonData!!.getJSONArray("g")
            if (str_g.length() != 0) {
                for (i in 0 until str_g.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_g.getString(i)))
                }
            }
            val str_c = rootJsonData!!.getJSONArray("c")
            if (str_c.length() != 0) {
                for (i in 0 until str_c.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_c.getString(i)))
                }
            }
            val str_d = rootJsonData!!.getJSONArray("d")
            if (str_d.length() != 0) {
                for (i in 0 until str_d.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_d.getString(i)))
                }
            }
            val str_s = rootJsonData!!.getJSONArray("s")
            if (str_s.length() != 0) {
                for (i in 0 until str_s.length()) {
                    ffmpegcommandwithoutwater.add(replaceKeyWords(str_s.getString(i)))
                }
            }
            ffmpegcommandwithoutwater.add("-flags")
            ffmpegcommandwithoutwater.add("+global_header")
            ffmpegcommandwithoutwater.add("-qscale:v")
            ffmpegcommandwithoutwater.add("3")
            ffmpegcommandwithoutwater.add(filepathwithoutwater!!)
        } catch (e: java.lang.Exception) {
            Log.e("FFMPEG>>>", Log.getStackTraceString(e))
        }
    }

    private fun executeCommand(filePath: String, exportType: String) {
        stcInputList.clear()
        val ffmpegCmd: MutableList<String> = ArrayList()
        try {
            val str_e = rootJsonData!!.getJSONArray("e")
            if (str_e.length() != 0) {
                for (i in 0 until str_e.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_e.getString(i)))
                }
            }
            // for images
            for (i in imagesList.indices) {
                if (!imagesList[i].imgName.equals("text")) {
                    if (imagesList[i].prefix.length() > 0) for (j in 0 until imagesList[i].prefix.length()) {
                        ffmpegCmd.add(replaceKeyWords(imagesList[i].prefix.getString(j)))
                    }
                    if (imagesList[i].imgPathExtra == null) {
                        imagesList[i].imgPath.let { ffmpegCmd.add(it) }
                    } else {
                        ffmpegCmd.add(imagesList[i].imgPathExtra.toString())
                    }
                    if ((imagesList[i].postfix.length()
                            ?: 0) > 0
                    ) for (j in 0 until (imagesList[i].postfix.length()
                        ?: 0)) {
                        replaceKeyWords(imagesList[i].postfix.getString(j))
                            .let {
                                ffmpegCmd.add(it)
                            }
                    }
                }
            }

            // for static inputs
            val static_inputs = rootJsonData!!.getJSONArray("static_inputs")
            for (i in 0 until static_inputs.length()) {
                val st_in_obj = static_inputs.getJSONObject(i)
                val path =
                    getZipDirectoryPath(activity) + zipFileName + File.separator + st_in_obj.getString(
                        "name"
                    )
                stcInputList.add(
                    PartyModelStaticInputs(
                        st_in_obj.getString("name"),
                        path,
                        st_in_obj.getJSONArray("prefix"),
                        st_in_obj.getJSONArray("postfix")
                    )
                )
                videoPath = stcInputList[0].videoPath
                if (stcInputList[i].prefix.length() !== 0) for (j in 0 until stcInputList[i].prefix.length()) {
                    ffmpegCmd.add(replaceKeyWords(stcInputList[i].prefix.getString(j)))
                }
                ffmpegCmd.add(stcInputList[i].videoPath)
                if (stcInputList[i].postfix.length() !== 0) for (j in 0 until stcInputList[i].postfix.length()) {
                    ffmpegCmd.add(replaceKeyWords(stcInputList[i].postfix.getString(j)))
                }
            }
            if (!isRemoveWaterMark) {
                ffmpegCmd.add("-ignore_loop")
                ffmpegCmd.add("0")
                ffmpegCmd.add("-i")
                ffmpegCmd.add(getZipDirectoryPath(activity) + getString(R.string.watermark))
            }
            val str_m = rootJsonData!!.getJSONArray("m")
            if (str_m.length() != 0) {
                for (i in 0 until str_m.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_m.getString(i)))
                }
            }
            if (!isRemoveWaterMark) {
                val img_count = imagesList.size
                val startc_input_count: Int = stcInputList.size
                val total_count = img_count + startc_input_count
                val half_dut: Int = video_duration / 2
                val add_watermarh =
                    "[base_video];[base_video][$total_count]overlay=enable='between(t,0,$half_dut)':x=30:y=30[watermarked_part1];[watermarked_part1][$total_count]overlay=enable='between(t,$half_dut,$video_duration)':x=(main_w-overlay_w-30):y=(main_h-overlay_h-30)"
                val str_i = rootJsonData!!.getJSONArray("r")
                if (str_i.length() != 0) {
                    for (i in 0 until str_i.length()) {
                        ffmpegCmd.add(replaceKeyWordsDynamic(str_i.getString(i)) + add_watermarh)
                    }
                }
            } else {
                val str_r = rootJsonData!!.getJSONArray("r")
                if (str_r.length() != 0) {
                    for (i in 0 until str_r.length()) {
                        ffmpegCmd.add(replaceKeyWordsDynamic(str_r.getString(i)))
                    }
                }
            }
            val str_n = rootJsonData!!.getJSONArray("n")
            if (str_n.length() != 0) {
                for (i in 0 until str_n.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_n.getString(i)))
                }
            }
            val str_g = rootJsonData!!.getJSONArray("g")
            if (str_g.length() != 0) {
                for (i in 0 until str_g.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_g.getString(i)))
                }
            }
            val str_c = rootJsonData!!.getJSONArray("c")
            if (str_c.length() != 0) {
                for (i in 0 until str_c.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_c.getString(i)))
                }
            }
            val str_d = rootJsonData!!.getJSONArray("d")
            if (str_d.length() != 0) {
                for (i in 0 until str_d.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_d.getString(i)))
                }
            }
            val str_s = rootJsonData!!.getJSONArray("s")
            if (str_s.length() != 0) {
                for (i in 0 until str_s.length()) {
                    ffmpegCmd.add(replaceKeyWords(str_s.getString(i)))
                }
            }
            ffmpegCmd.add("-flags")
            ffmpegCmd.add("+global_header")
            ffmpegCmd.add("-qscale:v")
            ffmpegCmd.add("3")
            ffmpegCmd.add(filePath)
        } catch (e: java.lang.Exception) {
            Log.e("FFMPEG>>>", Log.getStackTraceString(e))
        }
        val command =
            ffmpegCmd.toTypedArray()
        val stringBuilder = java.lang.StringBuilder()
        for (i in ffmpegCmd.indices) {
            stringBuilder.append(ffmpegCmd[i]).append(" ")
        }
        //        Log.e("command_lists", stringBuilder.toString());
//        Log.e("FFMPEG>>>command ", Arrays.toString(command));
        try {
            //ffmpeg = FFmpeg.getInstance(mContext);
            video_total_dur = getAudioDuration(activity, videoPath)
            isCreated = true
            Config.enableLogCallback { message ->
                Log.e(
                    Config.TAG, message.getText()
                )
            }
            Config.enableStatisticsCallback(object : StatisticsCallback {
                override fun apply(statistics: Statistics?) {
                    if (statistics != null) {
                        Log.d(
                            Config.TAG, java.lang.String.format(
                                "frame: %d, time: %d", statistics.videoFrameNumber, statistics.time
                            )
                        )
                    }
                    if (statistics == null) {
                        return
                    }
                    runOnUiThread {
                        val timeInMilliseconds: Int = statistics.time
                        if (timeInMilliseconds > 0) {
                            if (video_total_dur != 0) {
                                val totalVideoDuration: Int = video_total_dur
                                val completePercentage =
                                    BigDecimal(timeInMilliseconds).multiply(BigDecimal(100)).divide(
                                        BigDecimal(totalVideoDuration),
                                        0,
                                        BigDecimal.ROUND_HALF_UP
                                    ).toString()
                                runOnUiThread {

                                    if ((completePercentage.toInt() > 5) && (completePercentage.toInt() < 100)) {
                                        cp_export_progress.progress = completePercentage.toInt()
                                    }
                                    Log.i(
                                        "FFMPEG>>>", String.format(
                                            "Encoding video: %% %s", completePercentage
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            });
            isPlaying = false
            flagExporting = true
            playPausePlayer(isPlaying)
            rl_preview_control.visibility = View.GONE
            cp_export_progress.progress = 1
            Handler().postDelayed({
                cp_export_progress.setProgress(
                    5
                )
            }, 500)
            rl_export_video.visibility = View.VISIBLE
            executionId = FFmpeg.executeAsync(command) { executionId1, returnCode ->
                if (returnCode === RETURN_CODE_SUCCESS) {
                    runOnUiThread {
                        Log.d(
                            Config.TAG, "Finished command : ffmpeg " + Arrays.toString(command)
                        )
                        llWaterMark.isEnabled = true
                        llWaterMark.visibility = View.GONE
                        if (!isRemoveWaterMark) {
                            llWaterMark.visibility = View.VISIBLE
                            llWaterMark.isEnabled = true
                        } else {
                            llWaterMark.visibility = View.GONE
                        }
                        val file: File = File(getDownloadedPath(activity) + fileNameInstaCrop)
                        if (file.exists()) file.delete()
                        rl_export_video.setVisibility(View.GONE)
                        cp_export_progress.setProgress(0)
                        bitmap_thumb = ThumbnailUtils.createVideoThumbnail(
                            filePath, MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        if (finalVideoPath != null && File(finalVideoPath).exists()) File(
                            finalVideoPath
                        ).delete()
                        finalVideoPath = ""
                        finalVideoPath = filePath
                        outputVideo = filePath
                        pauseDuration = 0
                        releasePlayer()
                        initializeExoPlayer()
                        deleteAllVideo()
                        flagChanges = false
                        flagExporting = false
                        Handler().postDelayed({
                            runOnUiThread(object : Runnable {
                                override fun run() {
                                }
                            })
                        }, 20)
                        if (exportType.equals("preview", ignoreCase = true)) {
                            if (isPlaying) {
                                playPausePlayer(isPlaying)
                                isPlaying = false

                            } else {
                                playPausePlayer(isPlaying)
                                isPlaying = true
                            }
                        } else {
                            saveVideo(filePath, exportType)
                        }
                    }
                } else {
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            Log.e(
                                Config.TAG, java.lang.String.format(
                                    "Async command execution failed with returnCode=%d.", returnCode
                                )
                            )


                            Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT)
                                .show()
                            outputVideo =
                                (getZipDirectoryPath(activity) + zipFileName + File.separator + "output.mp4")
                            releasePlayer()
                            initializeExoPlayer()
                            flagExporting = false
                            File(finalVideoPath).delete()
                            finalVideoPath = ""
                            rl_export_video.visibility = View.GONE
                            cp_export_progress.progress = 0

                        }
                    })
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FFMPEG>>>", Log.getStackTraceString(e))
        }
    }


    private fun killExportProgress() {
        try {
            Log.d("killExportProg", "id $executionId")
            if (executionId != 0L) FFmpeg.cancel(executionId)
        } catch (ignored: java.lang.Exception) {
        }
        cp_export_progress.progress = 0
        rl_export_video.visibility = View.GONE
        rl_preview_control.visibility = View.VISIBLE
        releasePlayer()
        initializeExoPlayer()
        flagExporting = false

    }

    //Save video with different option...
    private fun saveVideo(filePath: String, exportType: String) {
        val file = File(getDownloadedPath(activity) + fileName)
        if (!file.exists()) {
            finalSaveVideo(filePath, file.path, exportType)

        }
    }

    //For saving video...
    private fun finalSaveVideo(srcPath: String, dstPath: String, exportType: String) {
        val srcFile = File(srcPath)
        val dstFile = File(dstPath)
        try {
            copyFileToStorage(srcFile, dstFile, exportType)
            refreshGallery(activity, dstFile)
        } catch (e: java.lang.Exception) {
        }
    }

    //Copy file to app folder...
    private fun copyFileToStorage(src: File, dst: File, exportType: String) {
        try {
            val inChannel = FileInputStream(src).channel
            val outChannel = FileOutputStream(dst).channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
            if (exportType.equals("export", ignoreCase = true)) {
                Log.e("copyFileToStorage", "videoCountIncrement")
            }
            // }
        } catch (e: java.lang.Exception) {
            //  Log.e("CopyFailed>>>", Log.getStackTraceString(e));
        }
    }

    private fun deleteAllVideo() {
        val dir: File = File(getFileDirectoryPath(activity))
        if (dir.isDirectory) {
            val children = dir.list()
            for (aChildren in children) {
                val file = File(dir, aChildren)
                if (!fileName.equals(file.name, ignoreCase = true)) file.delete()
                //Log.i("VideoFile>>>", file.getName() + "");
            }
        }
    }

    //endregion
    //region Get and check zone...
    private fun getFileDirectoryPath(mContext: Activity): String {
        val externalDirectory = mContext.filesDir.absolutePath
        val dir = File(
            externalDirectory + File.separator + mContext.resources.getString(R.string.app_name)
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }

    private fun refreshGallery(mContext: Activity, file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        mContext.sendBroadcast(mediaScanIntent)
    }

    private fun getDownloadedPath(mContext: Context): String {
        val externalDirectory: String
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            externalDirectory = getExternalFilesDir(Environment.DIRECTORY_MOVIES).toString();
        }
        else
        {*/
//        externalDirectory = Environment.getExternalStorageDirectory().toString();

        //  File dir = new File(externalDirectory + File.separator + mContext.getResources().getString(R.string.app_name_for_save));
        externalDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        // }
        val dir = File(
            externalDirectory + File.separator + mContext.resources.getString(R.string.app_name)
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }

    //for replacing keywords...
    private fun replaceKeyWords(str: String): String {
        var str_new = str.replace("{pythoncomplex}", "filter_complex")
        str_new = str_new.replace("{pythonmerge}", "alphamerge")
        str_new = str_new.replace("{pythono}", "overlay")
        str_new = str_new.replace("{pythonz}", "zoom")
        return str_new.replace("{pythonf}", "fade")
    }

    private fun replaceKeyWordsDynamic(str: String): String {
        var str_new = str.replace("{pythoncomplex}", "filter_complex")
        str_new = str_new.replace("{pythonmerge}", "alphamerge")
        str_new = str_new.replace("{pythono}", "overlay")
        str_new = str_new.replace("{pythonz}", "zoom")
        if (textDataArray != null) for (i in 0 until textDataArray!!.length()) {
            try {
                val jsonObject: JSONObject = textDataArray!!.getJSONObject(i)
                str_new = str_new.replace(
                    jsonObject.getString("replace_key"), jsonObject.getString("replaced_value")
                )
                str_new = str_new.replace(
                    "{folder_path}", (getZipDirectoryPath(activity) + zipFileName + File.separator)
                )
            } catch (e: JSONException) {
                Log.e("textJson>>>", Log.getStackTraceString(e))
            }
        }
        return str_new.replace("{pythonf}", "fade")
    }

    //endregion
    private fun getAudioDuration(mContext: Activity?, filePath: String?): Int {
        val uri = Uri.parse(filePath)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(mContext, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationStr!!.toInt()
    }

}