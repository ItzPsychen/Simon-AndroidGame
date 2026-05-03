package com.example.simonsays.logic

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

import kotlin.math.sin

object TonePlayer {

    // standard rate value
    private const val SAMPLE_RATE = 44100

    fun playTone(frequency: Double, durationMs: Int, settingsVolume: Float = 1.0f) {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        val angleStep = 2.0 * Math.PI * frequency / SAMPLE_RATE

        // applies a basic fade-in/fade-out
        for (i in 0 until numSamples) {
            val fadeCount = numSamples / 10
            val volume = if (i < fadeCount) {
                i.toDouble() / fadeCount
            } else if (i > numSamples - fadeCount) {
                (numSamples - i).toDouble() / fadeCount
            } else {
                1.0
            }
            samples[i] = (sin(i * angleStep) * Short.MAX_VALUE * volume * settingsVolume).toInt().toShort()
        }

        // audiotrack builder
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        // audiotrack player
        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()

        // static mode with manual release after each play
        Thread {
            Thread.sleep(durationMs.toLong() + 100)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) { }
        }.start()
    }
}
