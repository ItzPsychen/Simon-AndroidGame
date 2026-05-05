package com.example.simonsays.logic

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

import kotlin.math.sin

object TonePlayer {
    object ToneConstants {
        // frequencies for colored buttons
        val COLOR_FREQUENCIES = listOf(
            200.00, // red
            260.00, // green
            330.00, // blue
            410.00, // magenta
            500.00, // yellow
            600.00  // cyan
        )

        // default frequency
        const val DEFAULT_FREQUENCY = 440.0
    }

    private const val SAMPLE_RATE = 44100
    private val trackCache = mutableMapOf<String, AudioTrack>()

    // play tone using AudioTrack
    fun playTone(frequency: Double, durationMs: Int, volume: Float = 1.0f) {
        val key = "$frequency-$durationMs"
        
        try {
            val audioTrack = trackCache.getOrPut(key) {
                createStaticTrack(frequency, durationMs)
            }

            audioTrack.setVolume(volume)
            
            if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop()
                audioTrack.reloadStaticData()
            }
            
            audioTrack.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // creates the static track for the tone
    private fun createStaticTrack(frequency: Double, durationMs: Int): AudioTrack {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        val angleStep = 2.0 * Math.PI * frequency / SAMPLE_RATE

        // generate sine wave with fade-in/fade-out to avoid clicking sounds
        for (i in 0 until numSamples) {
            val fadeCount = numSamples / 10
            val fadeVolume = if (i < fadeCount) {
                i.toDouble() / fadeCount
            } else if (i > numSamples - fadeCount) {
                (numSamples - i).toDouble() / fadeCount
            } else {
                1.0
            }
            samples[i] = (sin(i * angleStep) * Short.MAX_VALUE * fadeVolume).toInt().toShort()
        }

        // builds the audioTrack and returns it
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

        audioTrack.write(samples, 0, samples.size)
        return audioTrack
    }
}
