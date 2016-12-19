package com.music.musicplayer135.playback.events

/**
 * Represents the way focus on music is gained or lost.
 *
 * @author Paul Woitaschek
 */
enum class AudioFocus {
  GAIN,
  LOSS,
  LOSS_TRANSIENT_CAN_DUCK,
  LOSS_TRANSIENT,
  LOSS_INCOMING_CALL
}