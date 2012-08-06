
package net.gabuchan.androidrecipe.recipe071;

import java.io.File;
import java.io.IOException;

import net.gabuchan.androidrecipe.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Recipe071Activity extends Activity {
    // プレビューのためのSurfaceView
    SurfaceView mCameraView;
    // メディアレコーダー
    MediaRecorder mRecorder;
    // 録画中フラグ
    boolean isRecording = false;
    // 録画ボタン
    Button mRecordButton;
    // 録画した動画ファイルのパス
    String mPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_071);

        // ボタンを取得しておく
        mRecordButton = (Button) findViewById(R.id.record_button);
        // SurfaceViewも取得
        mCameraView = (SurfaceView) findViewById(R.id.camera_view);
    }

    public void onButtonClick(View view) {
        if (isRecording) {
            // 録画中だったら
            isRecording = false; // 録画中フラグを解除
            // ボタンのテキストを変更
            mRecordButton.setText(R.string.start);
            stopRecording();
        } else {
            // 録画中じゃなかったら
            isRecording = true; // 録画中フラグをセット
            // ボタンのテキストを変更
            mRecordButton.setText(R.string.stop);
            // 録画スタート！
            startRecording();
        }
    }

    public void onPlayClick(View view) {
        if (isRecording) {
            showToast("撮影中です＞＜");
            return;
        }
        if (mPath == null) {
            showToast("録画してから再生してください。");
            return;
        }
        // Intentで再生
        Uri uri = Uri.parse("file:/" + mPath);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }

    private void startRecording() {
        // 保存先のディレクトリのFileオブジェクトを生成
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "gabubon2");
        // ディレクトリがなければ作成する
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                showToast("SDカードにディレクトリが作成できませんでした。");
            }
        }
        // ファイル名を時間から生成
        String fileName = System.currentTimeMillis() + ".mp4";
        // ディレクトリとファイル名を繋げてFileオブジェクトを作る
        File file = new File(dir, fileName);
        // 出力ファイルのパス
        mPath = file.getAbsolutePath();

        mRecorder = new MediaRecorder();
        // オーディオ入力にマイクをセット
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // ビデオ入力にカメラをセット
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 出力フォーマットにMPEG_4を指定
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 出力ファイルのパスを指定
        mRecorder.setOutputFile(mPath);

        // ここから動画的な設定

        // フレームレートをセット
        mRecorder.setVideoFrameRate(30);
        // 撮影サイズを指定
        mRecorder.setVideoSize(800, 480);
        // ビデオエンコーダーにH264をセット
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
        // オーディオエンコーダーにAMR_NBをセット
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        // プレビュー表示にSurfaceをセット
        mRecorder.setPreviewDisplay(mCameraView.getHolder().getSurface());

        // API Level 9(Android 2.3)以降なので今回は使わない
        // mRecorder.setOrientationHint(90);

        try {
            // 準備して
            mRecorder.prepare();
            // 録画スタート！
            mRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // 録画を停止して
        mRecorder.stop();
        // 解放
        mRecorder.release();
        // トースト表示
        showToast(mPath + "に保存しました。");
        // スキャンさせる（ギャラリーに反映が目的）
        MediaScannerConnection.scanFile(this, new String[] {
                mPath.toString()
        }, null, null);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
