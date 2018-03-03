package org.schabi.newpipe.player.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.Subtitles;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.SubtitlesFormat;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;

import javax.annotation.Nonnull;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM;

public class PlayerHelper {
    private PlayerHelper() {}

    private static final StringBuilder stringBuilder = new StringBuilder();
    private static final Formatter stringFormatter = new Formatter(stringBuilder, Locale.getDefault());
    private static final NumberFormat speedFormatter = new DecimalFormat("0.##x");
    private static final NumberFormat pitchFormatter = new DecimalFormat("##%");

    ////////////////////////////////////////////////////////////////////////////
    // Exposed helpers
    ////////////////////////////////////////////////////////////////////////////

    public static String getTimeString(int milliSeconds) {
        long seconds = (milliSeconds % 60000L) / 1000L;
        long minutes = (milliSeconds % 3600000L) / 60000L;
        long hours = (milliSeconds % 86400000L) / 3600000L;
        long days = (milliSeconds % (86400000L * 7L)) / 86400000L;

        stringBuilder.setLength(0);
        return days > 0 ? stringFormatter.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds).toString()
                : hours > 0 ? stringFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : stringFormatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public static String formatSpeed(float speed) {
        return speedFormatter.format(speed);
    }

    public static String formatPitch(float pitch) {
        return pitchFormatter.format(pitch);
    }

    public static String mimeTypesOf(final SubtitlesFormat format) {
        switch (format) {
            case VTT: return MimeTypes.TEXT_VTT;
            case TTML: return MimeTypes.APPLICATION_TTML;
            default: throw new IllegalArgumentException("Unrecognized mime type: " + format.name());
        }
    }

    @NonNull
    public static String captionLanguageOf(@NonNull final Context context,
                                           @NonNull final Subtitles subtitles) {
        final String displayName = subtitles.getLocale().getDisplayName(subtitles.getLocale());
        return displayName + (subtitles.isAutoGenerated() ? " (" + context.getString(R.string.caption_auto_generated)+ ")" : "");
    }

    public static String resizeTypeOf(@NonNull final Context context,
                                      @AspectRatioFrameLayout.ResizeMode final int resizeMode) {
        switch (resizeMode) {
            case RESIZE_MODE_FIT: return context.getResources().getString(R.string.resize_fit);
            case RESIZE_MODE_FILL: return context.getResources().getString(R.string.resize_fill);
            case RESIZE_MODE_ZOOM: return context.getResources().getString(R.string.resize_zoom);
            default: throw new IllegalArgumentException("Unrecognized resize mode: " + resizeMode);
        }
    }

    public static String cacheKeyOf(@NonNull final StreamInfo info, @NonNull VideoStream video) {
        return info.getUrl() + video.getResolution() + video.getFormat().getName();
    }

    public static String cacheKeyOf(@NonNull final StreamInfo info, @NonNull AudioStream audio) {
        return info.getUrl() + audio.getAverageBitrate() + audio.getFormat().getName();
    }

    public static boolean isResumeAfterAudioFocusGain(@NonNull final Context context) {
        return isResumeAfterAudioFocusGain(context, false);
    }

    public static boolean isPlayerGestureEnabled(@NonNull final Context context) {
        return isPlayerGestureEnabled(context, true);
    }

    public static boolean isUsingOldPlayer(@NonNull final Context context) {
        return isUsingOldPlayer(context, false);
    }

    public static boolean isRememberingPopupDimensions(@Nonnull final Context context) {
        return isRememberingPopupDimensions(context, true);
    }

    @NonNull
    public static SeekParameters getSeekParameters(@NonNull final Context context) {
        return isUsingInexactSeek(context, false) ?
                SeekParameters.CLOSEST_SYNC : SeekParameters.EXACT;
    }

    public static long getPreferredCacheSize(@NonNull final Context context) {
        return 64 * 1024 * 1024L;
    }

    public static long getPreferredFileSize(@NonNull final Context context) {
        return 512 * 1024L;
    }

    /**
     * Returns the number of milliseconds the player buffers for before starting playback.
     * */
    public static int getPlaybackStartBufferMs(@NonNull final Context context) {
        return 500;
    }

    /**
     * Returns the minimum number of milliseconds the player always buffers to after starting
     * playback.
     * */
    public static int getPlaybackMinimumBufferMs(@NonNull final Context context) {
        return 25000;
    }

    /**
     * Returns the maximum/optimal number of milliseconds the player will buffer to once the buffer
     * hits the point of {@link #getPlaybackMinimumBufferMs(Context)}.
     * */
    public static int getPlaybackOptimalBufferMs(@NonNull final Context context) {
        return 60000;
    }

    public static boolean isUsingDSP(@NonNull final Context context) {
        return true;
    }

    public static int getShutdownFlingVelocity(@Nonnull final Context context) {
        return 10000;
    }

    public static int getTossFlingVelocity(@Nonnull final Context context) {
        return 2500;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Private helpers
    ////////////////////////////////////////////////////////////////////////////

    @NonNull
    private static SharedPreferences getPreferences(@NonNull final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static boolean isResumeAfterAudioFocusGain(@NonNull final Context context, final boolean b) {
        return getPreferences(context).getBoolean(context.getString(R.string.resume_on_audio_focus_gain_key), b);
    }

    private static boolean isPlayerGestureEnabled(@NonNull final Context context, final boolean b) {
        return getPreferences(context).getBoolean(context.getString(R.string.player_gesture_controls_key), b);
    }

    private static boolean isUsingOldPlayer(@NonNull final Context context, final boolean b) {
        return getPreferences(context).getBoolean(context.getString(R.string.use_old_player_key), b);
    }

    private static boolean isRememberingPopupDimensions(@Nonnull final Context context, final boolean b) {
        return getPreferences(context).getBoolean(context.getString(R.string.popup_remember_size_pos_key), b);
    }

    private static boolean isUsingInexactSeek(@NonNull final Context context, final boolean b) {
        return getPreferences(context).getBoolean(context.getString(R.string.use_inexact_seek_key), b);
    }
}
