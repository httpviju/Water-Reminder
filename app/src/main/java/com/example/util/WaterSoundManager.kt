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
import kotlin.random.Random

object WaterSoundManager {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Synthesizes a realistic, crisp water droplet "bloop / plink" sound.
     * Played whenever water is logged.
     */
    fun playWaterDropSound(baseFreq: Float = 540f) {
        scope.launch {
            try {
                val durationMs = 160
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val samples = ShortArray(numSamples)
                var phase = 0.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples

                    // Exponential upward frequency chirp characteristic of water drop cavity
                    val freq = baseFreq + (1250f * Math.pow(progress, 0.45)).toFloat()

                    // Quick attack, exponential decay
                    val attack = if (t < 0.003) (t / 0.003) else 1.0
                    val decay = exp(-22.0 * t)
                    val envelope = attack * decay

                    phase += 2.0 * PI * freq / SAMPLE_RATE
                    val sampleVal = (sin(phase) * envelope * 0.85 * Short.MAX_VALUE).toInt()
                    samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(samples, AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Synthesizes an authentic water pouring & bubbling cascade sound.
     * Played for Water Alarm and hydration reminders!
     */
    fun playWaterAlarmSound() {
        scope.launch {
            try {
                val durationMs = 1800
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val samples = ShortArray(numSamples)
                val buffer = DoubleArray(numSamples)

                // 1. Gentle liquid stream ambience
                var prevNoise = 0.0
                val random = Random(42)
                for (i in 0 until numSamples) {
                    val white = (random.nextDouble() * 2.0 - 1.0)
                    prevNoise = prevNoise * 0.93 + white * 0.07
                    val t = i.toDouble() / SAMPLE_RATE
                    val env = when {
                        t < 0.2 -> t / 0.2
                        t > 1.4 -> (1.8 - t) / 0.4
                        else -> 1.0
                    }
                    buffer[i] = prevNoise * 0.18 * env
                }

                // 2. Cascading water droplets and splash harmonics
                data class Droplet(val startMs: Int, val freq: Float, val vol: Double)
                val droplets = listOf(
                    Droplet(50, 480f, 0.55),
                    Droplet(140, 720f, 0.70),
                    Droplet(260, 580f, 0.60),
                    Droplet(380, 880f, 0.75),
                    Droplet(520, 640f, 0.65),
                    Droplet(660, 920f, 0.80),
                    Droplet(800, 750f, 0.70),
                    Droplet(950, 600f, 0.65),
                    Droplet(1100, 820f, 0.75),
                    Droplet(1240, 700f, 0.60),
                    Droplet(1400, 960f, 0.50)
                )

                for (drop in droplets) {
                    val startSample = (drop.startMs * SAMPLE_RATE) / 1000
                    val dropDurationSamples = (0.15 * SAMPLE_RATE).toInt()
                    var dropPhase = 0.0

                    for (j in 0 until dropDurationSamples) {
                        val sampleIdx = startSample + j
                        if (sampleIdx >= numSamples) break
                        val t = j.toDouble() / SAMPLE_RATE
                        val progress = j.toDouble() / dropDurationSamples

                        val freq = drop.freq + (1100f * Math.pow(progress, 0.4)).toFloat()
                        val attack = if (t < 0.003) (t / 0.003) else 1.0
                        val decay = exp(-24.0 * t)
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
