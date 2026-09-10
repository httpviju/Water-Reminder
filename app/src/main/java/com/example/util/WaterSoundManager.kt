package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object WaterSoundManager {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Synthesizes a soft, pleasant water droplet "tip" sound.
     */
    fun playWaterDropSound(baseFreq: Float = 650f) {
        scope.launch {
            try {
                val durationMs = 150
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val samples = ShortArray(numSamples)
                var phase = 0.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples

                    // Upward frequency chirp characteristic of a water drop
                    val freq = baseFreq + (1000f * Math.pow(progress, 0.5)).toFloat()

                    // Quick attack, exponential decay
                    val attack = if (t < 0.005) (t / 0.005) else 1.0
                    val decay = exp(-25.0 * t)
                    val envelope = attack * decay

                    phase += 2.0 * PI * freq / SAMPLE_RATE
                    // 0.6 volume multiplier for a soft, pleasant sound
                    val sampleVal = (sin(phase) * envelope * 0.6 * Short.MAX_VALUE).toInt()
                    samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(samples, AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Synthesizes a sequence of gentle "tip tip" water droplets for the alarm.
     * Replaces the harsh bubbling noise with a clean, relaxing sound.
     */
    fun playWaterAlarmSound() {
        scope.launch {
            try {
                val durationMs = 1500
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val samples = ShortArray(numSamples)
                val buffer = DoubleArray(numSamples)

                // Sequence of "tip tip" sounds
                data class Droplet(val startMs: Int, val freq: Float, val vol: Double)
                val droplets = listOf(
                    Droplet(100, 650f, 0.6),   // tip
                    Droplet(400, 650f, 0.6),   // tip
                    Droplet(700, 850f, 0.65),  // tip (slightly higher pitch)
                    Droplet(1000, 650f, 0.6)   // tip
                )

                for (drop in droplets) {
                    val startSample = (drop.startMs * SAMPLE_RATE) / 1000
                    val dropDurationSamples = (0.15 * SAMPLE_RATE).toInt() // 150ms per drop
                    var dropPhase = 0.0

                    for (j in 0 until dropDurationSamples) {
                        val sampleIdx = startSample + j
                        if (sampleIdx >= numSamples) break
                        val t = j.toDouble() / SAMPLE_RATE
                        val progress = j.toDouble() / dropDurationSamples

                        val freq = drop.freq + (1000f * Math.pow(progress, 0.5)).toFloat()
                        val attack = if (t < 0.005) (t / 0.005) else 1.0
                        val decay = exp(-25.0 * t)
                        val env = attack * decay * drop.vol

                        dropPhase += 2.0 * PI * freq / SAMPLE_RATE
                        buffer[sampleIdx] += sin(dropPhase) * env
                    }
                }

                // Convert to 16-bit PCM
                for (i in 0 until numSamples) {
                    val intVal = (buffer[i] * Short.MAX_VALUE).toInt()
                    samples[i] = intVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(samples, AudioAttributes.USAGE_ALARM)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun playPcmBuffer(samples: ShortArray, audioUsage: Int) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(samples.size * 2, minBufferSize)

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(audioUsage)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()

        Thread {
            try {
                val sleepDuration = (samples.size * 1000L / SAMPLE_RATE) + 120L
                Thread.sleep(sleepDuration)
                audioTrack.stop()
                audioTrack.release()
            } catch (ignored: Exception) {
            }
        }.start()
    }
}

