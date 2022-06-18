package com.kamaboko.imageavoid.moveImage

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.math.MathUtils.dist
import com.kamaboko.imageavoid.R
import com.kamaboko.imageavoid.customView.TestView2
import com.kamaboko.imageavoid.databinding.MoveImageFragmentBinding
import kotlinx.coroutines.*

class MoveImageFragment : Fragment() {

    private lateinit var binding: MoveImageFragmentBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var imagePaint: TestView2? = null

    private var currentX = 0f
    private var currentY = 0f

    private lateinit var vibrator: Vibrator
    private lateinit var vibrationEffect: VibrationEffect

    private val MAX_WIDTH = 1500;
    private val MAX_HEIGHT = 1500;

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Menu設定
        setHasOptionsMenu(true)

        // バイブレータ設定
        vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrationEffect = VibrationEffect.createOneShot(5, 255)

        // アクティビティの結果に対するコールバックの登録
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                // アクティビティ結果NG
            } else {
                try {
                    // 選択された画像を取得
                    result.data?.data?.also { uri: Uri ->
                        val inputStream = requireActivity().contentResolver?.openInputStream(uri)
                        setImage(BitmapFactory.decodeStream(inputStream))
                    }
                } catch (e: Exception) {
                    Log.d("test", e.toString())
                    Toast.makeText(context, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }

        // binding取得
        binding = MoveImageFragmentBinding.inflate(layoutInflater)

        // 画面タッチの処理
        binding.layout.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    currentX = x
                    currentY = y
                    vibrator.vibrate(vibrationEffect)
                }
                MotionEvent.ACTION_MOVE -> {
                    val dst = dist(currentX,currentY,x,y)
                    if(dst > 50){

                        vibrator.vibrate(vibrationEffect)
                        currentX = x
                        currentY = y
                    }
                    if (imagePaint != null) {
                        imagePaint!!.touchMove(x, y)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.performClick()
                }
            }
            true
        }
        onMove()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初期画像を設定する
        binding.layout.post {
            if(imagePaint == null){
                setImage(BitmapFactory.decodeResource(resources,R.drawable.initialimage2))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onMove() {
        // 描画ようスレッド　ディレイで1秒間に描写する回数を制御
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                delay(30)
                imagePaint?.originMove()
                imagePaint?.invalidate()
            }
        }
    }

    // Menu設定
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_menu, menu)
    }

    // Menuリスナー
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                launcher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ビットマップを整形してカスタムビューを作成
    private fun setImage(image: Bitmap){
        var w = image.width
        var h = image.height

        // 表示最大高さ
        val limitHeight = if(MAX_HEIGHT > binding.layout.height){
            binding.layout.height
        } else {
            MAX_HEIGHT
        }

        // 表示最大幅
        val limitWidth = if(MAX_WIDTH > binding.layout.width){
            binding.layout.width
        } else {
            MAX_WIDTH
        }

        // 画面幅にあわせてリサイズ比
        var resizeScale = limitWidth.toDouble() / w.toDouble()

        // 横幅に合わせてリサイズ
        w = (w * resizeScale).toInt()
        h = (h * resizeScale).toInt()

        // 高さが収まっていない場合はもう一度リサイズ
        if (limitHeight < h) {
            resizeScale = limitHeight.toDouble() / h.toDouble()
            w = (w * resizeScale).toInt()
            h = (h * resizeScale).toInt()
        }

        // BitMapリサイズバージョンを再作成
        val imageRe = Bitmap.createScaledBitmap(
            image, w, h, true
        )

        // 幅を中心に寄せる
        val wAdjust = if(binding.layout.width > w){
            (binding.layout.width - w)  / 2
        }else{
            0
        }

        // 高さを中心に寄せる
        val hAdjust = if(binding.layout.height > h){
            (binding.layout.height - h)  / 2
        }else{
            0
        }

        binding.layout.removeAllViews()

        // 画像を設定
        imagePaint = TestView2(requireContext(), null, 0, imageRe, wAdjust, hAdjust)
        binding.layout.addView(imagePaint)
    }

}