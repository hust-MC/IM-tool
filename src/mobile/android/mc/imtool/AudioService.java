package mobile.android.mc.imtool;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioRecord;

public class AudioService
{
	boolean isRecording = false;

	// 设置开始键触发事件
	public void Record()
	{
		Log.d("MC", "record");
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				record();
			}
		});
		thread.start();
	}

	public void record()
	{
		int frequency = 11025;
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

		try
		{
			int bufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, frequency,
					channelConfiguration, audioEncoding, bufferSize);

			isRecording = true;

			int trackSize = AudioTrack.getMinBufferSize(frequency,
					channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					11025, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, trackSize,
					AudioTrack.MODE_STREAM);

			short[] buffer = new short[bufferSize];

			audioRecord.startRecording(); // 开始录音
			audioTrack.play(); // 开始播放
			
			Log.d("MC",String.valueOf(isRecording));
			while (isRecording)
			{
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize); // 从麦克风读取音频
				short[] tmpBuf = new short[bufferReadResult]; // 存入缓存
				System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult); // 复制文件
				audioTrack.write(buffer, 0, bufferReadResult);
			}
			audioTrack.stop();
			audioRecord.stop();

		} catch (Throwable t)
		{
			Log.e("AudioRecord", "Recording Failed");
		}
	}
}
