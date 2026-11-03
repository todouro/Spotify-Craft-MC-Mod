/*
 * Decompiled with CFR 0.152.
 */
package dev.neonlyrics.hud;

import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.lyrics.Translator;
import dev.neonlyrics.util.AutoPerf;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

final class IslandCard {
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
    private static final int SLOT_Y = 40;
    private static long lastNs = 0L;
    private static long lastDraw = 0L;
    private static float show = 0.0f;
    private static String lyricKey = "";
    private static long lyricSince = 0L;
    private static float scroll = 0.0f;
    private static float hlX = 0.0f;
    private static float hlW = 0.0f;
    private static boolean failed = false;
    private static final float S = 0.77f;

    private IslandCard() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void render(GuiGraphicsExtractor guiGraphicsExtractor, Minecraft minecraft, Config config) {
        boolean bl;
        if (failed) {
            Island.renderIslandOld(guiGraphicsExtractor, minecraft, config);
            return;
        }
        long l = System.currentTimeMillis();
        NowPlaying nowPlaying = SpotifyClient.now();
        boolean bl2 = Island.note != null && l - Island.noteTime < 2400L;
        boolean bl3 = bl = config.lyricsIsland && nowPlaying != null && nowPlaying.hasTrack() && !bl2;
        if (!bl) {
            Island.renderIslandOld(guiGraphicsExtractor, minecraft, config);
            return;
        }
        if (config.islandBig) {
            Island.renderIslandOld(guiGraphicsExtractor, minecraft, config);
            return;
        }
        float f = Gfx.alpha;
        try {
            IslandCard.draw(guiGraphicsExtractor, minecraft, config, nowPlaying, l);
        }
        catch (Throwable throwable) {
            failed = true;
            System.err.println("[SpotifyCraft] player card disabled after error: " + String.valueOf(throwable));
            throwable.printStackTrace();
        }
        finally {
            Gfx.alpha = f;
        }
    }

    private static float u(float f) {
        return f * 0.77f;
    }

    private static void txt(GuiGraphicsExtractor guiGraphicsExtractor, String string, float f, float f2, int n, float f3, boolean bl) {
        if (string == null || string.isEmpty()) {
            return;
        }
        int n2 = Math.round(f);
        int n3 = Math.round(f2);
        if (bl) {
            Gfx.textBold(guiGraphicsExtractor, string, n2, n3, n, f3, false);
        } else {
            Gfx.text(guiGraphicsExtractor, string, n2, n3, n, f3, false);
        }
    }

    private static float tw(String string) {
        return Gfx.w(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void draw(GuiGraphicsExtractor guiGraphicsExtractor, Minecraft minecraft, Config config, NowPlaying nowPlaying, long l) {
        Object object;
        boolean bl;
        int n;
        long l2 = System.nanoTime();
        float f = Math.min(0.05f, (float)(l2 - lastNs) / 1.0E9f);
        lastNs = l2;
        if (l - lastDraw > 250L) {
            show = 0.0f;
        }
        lastDraw = l;
        if ((show += (1.0f - show) * Math.min(1.0f, f * 12.0f)) < 0.02f) {
            return;
        }
        int n2 = Theme.accent();
        int n3 = guiGraphicsExtractor.guiWidth();
        int n4 = n3 / 2;
        int n5 = Math.max(180, Math.min(240, Math.round((float)n3 * 0.32f)));
        int n6 = Math.round(IslandCard.u(n5));
        int n7 = 54;
        float f2 = 8.0f;
        int n8 = n4 - n6 / 2;
        int n9 = 5 - Math.round((1.0f - show) * 8.0f);
        int n10 = n9 + 9;
        Gfx.alpha = show;
        if (AutoPerf.allow(2)) {
            for (n = 3; n >= 1; --n) {
                Gfx.aaRound(guiGraphicsExtractor, n8 - n, (float)(n9 - n) + 1.5f, n6 + 2 * n, n7 + 2 * n, f2 + (float)n, 0, 0.05f);
            }
        }
        Gfx.aaRound(guiGraphicsExtractor, (float)n8 - 0.75f, (float)n9 - 0.75f, (float)n6 + 1.5f, (float)n7 + 1.5f, f2 + 0.75f, n2, 0.5f);
        Gfx.aaRound(guiGraphicsExtractor, n8, n9, n6, n7, f2, 726803, 0.94f);
        n = 7;
        int n11 = 28;
        int n12 = n8 + n;
        int n13 = n9 + 6;
        if (!Cover.drawSquare(guiGraphicsExtractor, n12, n13, n11, 1.0f)) {
            Gfx.aaRound(guiGraphicsExtractor, n12, n13, n11, n11, 6.0f, Gfx.lerpColor(n2, 726803, 0.65f), 1.0f);
            Gfx.aaCircle(guiGraphicsExtractor, (float)n12 + (float)n11 / 2.0f, (float)n13 + (float)n11 / 2.0f, 5.0f, n2, 0.8f);
        }
        float f3 = n12 + n11 + n;
        float f4 = n8 + n6 - n;
        float f5 = Math.max(20.0f, f4 - f3);
        int n14 = Math.round(f5);
        String string = nowPlaying.title == null || nowPlaying.title.isBlank() ? "-" : nowPlaying.title;
        String string2 = nowPlaying.artist == null ? "" : nowPlaying.artist;
        IslandCard.txt(guiGraphicsExtractor, Gfx.ellipsize(string, n14), f3, n9 + 5, 0xFFFFFF, 1.0f, true);
        if (!string2.isBlank()) {
            IslandCard.txt(guiGraphicsExtractor, Gfx.ellipsize(string2, n14), f3, n9 + 16, n2, 0.95f, false);
        }
        long l3 = nowPlaying.progressNow(0);
        long l4 = nowPlaying.durationMs;
        float f6 = l4 > 0L ? Math.max(0.0f, Math.min(1.0f, (float)l3 / (float)l4)) : 0.0f;
        float f7 = (float)n9 + 26.0f;
        float f8 = 3.0f;
        Gfx.aaRound(guiGraphicsExtractor, f3, f7, f5, f8, f8 / 2.0f, 0xFFFFFF, 0.2f);
        Gfx.aaRound(guiGraphicsExtractor, f3, f7, Math.max(f8, f5 * f6), f8, f8 / 2.0f, n2, 1.0f);
        Gfx.aaCircle(guiGraphicsExtractor, f3 + f5 * f6, f7 + f8 / 2.0f, 3.4f, 0xFFFFFF, 1.0f);
        float f9 = n9 + 32;
        IslandCard.txt(guiGraphicsExtractor, IslandCard.fmt(l3), f3, f9, 8826011, 0.9f, false);
        if (l4 > 0L) {
            String string3 = "-" + IslandCard.fmt(Math.max(0L, l4 - l3));
            IslandCard.txt(guiGraphicsExtractor, string3, f4 - IslandCard.tw(string3), f9, 8826011, 0.9f, false);
        }
        float f10 = n9 + 41;
        float f11 = 12.0f;
        Gfx.aaRound(guiGraphicsExtractor, n8 + 5, f10, n6 - 10, f11, 5.0f, 0, 0.32f);
        long l5 = nowPlaying.progressNow(config.lyricsOffsetMs + 120);
        int n15 = nowPlaying.lyricsState == NowPlaying.LyricsState.SYNCED && nowPlaying.playing ? nowPlaying.lineIndex(l5) : -1;
        NowPlaying.Line line = n15 >= 0 ? nowPlaying.lines.get(n15) : null;
        boolean bl2 = bl = line != null && !line.text().isBlank() && l5 <= nowPlaying.lineEnd(n15) + 400L;
        if (bl) {
            object = nowPlaying.trackId + "#" + n15;
            if (!((String)object).equals(lyricKey)) {
                lyricKey = object;
                lyricSince = l;
                scroll = 0.0f;
                hlX = 0.0f;
                hlW = 0.0f;
            }
            var45_43 = Anim.outCubic(Anim.since(lyricSince, 380L));
            IslandCard.drawLyric(guiGraphicsExtractor, line, nowPlaying, n15, l5, n8 + 8, f10, n6 - 16, f11, n2, f, var45_43);
        } else {
            lyricKey = "";
            float f12 = f10 + f11 / 2.0f;
            var45_43 = f12 - 4.0f;
            if (!nowPlaying.playing) {
                String string4 = "Paused";
                IslandCard.txt(guiGraphicsExtractor, string4, (float)n4 - IslandCard.tw(string4) / 2.0f, var45_43, 8826011, 0.9f, false);
            } else if (nowPlaying.lyricsState == NowPlaying.LyricsState.NOT_FOUND || nowPlaying.lyricsState == NowPlaying.LyricsState.NONE) {
                String string5 = "No synced lyrics";
                IslandCard.txt(guiGraphicsExtractor, string5, (float)n4 - IslandCard.tw(string5) / 2.0f, var45_43, 8826011, 0.75f, false);
            } else {
                for (int i = 0; i < 3; ++i) {
                    float f13 = 0.5f + 0.5f * (float)Math.sin((double)l / 300.0 - (double)i * 0.9);
                    Gfx.aaCircle(guiGraphicsExtractor, (float)n4 + (float)(i - 1) * 7.0f, f12, 1.7f, n2, 0.3f + 0.6f * f13);
                }
            }
        }
        Gfx.alpha = show;
        if (bl && config.translate) {
            object = Translator.get(line.text());
            for (int i = 1; i <= 2; ++i) {
                if (n15 + i >= nowPlaying.lines.size()) continue;
                Translator.prefetch(nowPlaying.lines.get(n15 + i).text());
            }
            if (object != null) {
                guiGraphicsExtractor.pose().pushMatrix();
                try {
                    guiGraphicsExtractor.pose().translate((float)Math.round(n4), (float)Math.round(n9 + n7 + 4));
                    guiGraphicsExtractor.pose().scale(0.85f, 0.85f);
                    Gfx.center(guiGraphicsExtractor, Gfx.ellipsize((String)object, Math.round((float)n6 / 0.85f)), 0, 0, 13035221, 0.9f, true);
                }
                finally {
                    guiGraphicsExtractor.pose().popMatrix();
                }
            }
        }
        Gfx.alpha = show;
        if (config.showClock) {
            object = LocalTime.now().format(HM);
            Gfx.textBold(guiGraphicsExtractor, (String)object, n8 - 10 - Gfx.w((String)object, Gfx.BOLD), n10 - 4, 0xFFFFFF, 0.95f, true);
        }
        int n16 = n8 + n6 + 10;
        if (config.showPing && minecraft.getConnection() != null) {
            int n17;
            ClientPacketListener clientPacketListener = minecraft.getConnection();
            UUID uUID = minecraft.player == null ? null : minecraft.player.getUUID();
            PlayerInfo playerInfo = uUID == null ? null : clientPacketListener.getPlayerInfo(uUID);
            int n18 = n17 = playerInfo == null ? 0 : playerInfo.getLatency();
            int n19 = n17 <= 0 ? 0 : (n17 < 60 ? 4 : (n17 < 120 ? 3 : (n17 < 250 ? 2 : 1)));
            float[] fArray = new float[9];
            float[] fArray2 = new float[9];
            for (int i = 0; i < 9; ++i) {
                double d = Math.PI + (double)i * Math.PI / 8.0;
                fArray[i] = (float)n16 + 3.5f + (float)Math.cos(d) * 3.0f;
                fArray2[i] = (float)n10 + 0.5f + (float)Math.sin(d) * 3.0f;
            }
            Gfx.aaStroke(guiGraphicsExtractor, fArray, fArray2, 1.1f, 8826011, 0.9f);
            String string6 = Integer.toString(n17);
            Gfx.textBold(guiGraphicsExtractor, string6, n16 + 9, n10 - 4, 0xFFFFFF, 0.95f, true);
            int n20 = n16 + 9 + Gfx.w(string6, Gfx.BOLD) + 5;
            for (int i = 0; i < 4; ++i) {
                int n21 = 2 + i * 2;
                Gfx.aaRound(guiGraphicsExtractor, n20 + i * 3, n10 + 4 - n21, 2.0f, n21, 1.0f, 0xFFFFFF, i < n19 ? 0.95f : 0.22f);
            }
            n16 = n20 + 16;
        }
        if (config.showFps) {
            Gfx.text(guiGraphicsExtractor, minecraft.getFps() + " fps", n16, n10 - 4, 8826011, 0.85f, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawLyric(GuiGraphicsExtractor guiGraphicsExtractor, NowPlaying.Line line, NowPlaying nowPlaying, int n, long l, float f, float f2, float f3, float f4, int n2, float f5, float f6) {
        String[] stringArray = line.words();
        if (stringArray == null || stringArray.length == 0) {
            return;
        }
        float f7 = nowPlaying.wordPosition(n, l);
        int n3 = (int)Math.floor(f7);
        float f8 = Gfx.w(" ");
        float[] fArray = new float[stringArray.length];
        float[] fArray2 = new float[stringArray.length];
        float f9 = 0.0f;
        for (int i = 0; i < stringArray.length; ++i) {
            fArray[i] = f9;
            fArray2[i] = Gfx.w(stringArray[i]);
            f9 += fArray2[i] + f8;
        }
        float f10 = f9 - f8;
        int n4 = Math.max(0, Math.min(stringArray.length - 1, n3 - 1));
        int n5 = Math.max(0, Math.min(stringArray.length - 1, n3));
        float f11 = n3 >= 0 ? fArray[n4] : 0.0f;
        float f12 = n3 >= 0 ? fArray[n5] + fArray2[n5] - fArray[n4] : 0.0f;
        float f13 = 0.0f;
        if (f10 > f3) {
            f13 = Math.max(0.0f, Math.min(f10 - f3, fArray[n5] + fArray2[n5] / 2.0f - f3 * 0.55f));
        }
        float f14 = 1.0f - (float)Math.exp(-f5 * 14.0f);
        scroll += (f13 - scroll) * f14;
        hlX += (f11 - hlX) * f14;
        hlW += (f12 - hlW) * f14;
        float f15 = f - scroll;
        if (f10 <= f3) {
            f15 += (f3 - f10) / 2.0f;
        }
        float f16 = f2 + 1.0f;
        float f17 = f16 + 2.0f;
        Gfx.alpha = show * f6;
        guiGraphicsExtractor.enableScissor(Math.round(f - 2.0f), Math.round(f2), Math.round(f + f3 + 2.0f), Math.round(f2 + f4));
        try {
            if (n3 >= 0 && n3 < stringArray.length) {
                Gfx.aaRound(guiGraphicsExtractor, f15 + hlX - 3.0f, f16, hlW + 6.0f, 10.0f, 3.0f, n2, 0.85f);
            }
            for (int i = 0; i < stringArray.length; ++i) {
                boolean bl = i <= n3;
                IslandCard.txt(guiGraphicsExtractor, stringArray[i], f15 + fArray[i], f17, 0xFFFFFF, bl ? 1.0f : 0.42f, false);
            }
        }
        finally {
            guiGraphicsExtractor.disableScissor();
        }
        Gfx.alpha = show;
    }

    private static String fmt(long l) {
        long l2 = Math.max(0L, l) / 1000L;
        return l2 / 60L + ":" + (l2 % 60L < 10L ? "0" : "") + l2 % 60L;
    }
}

