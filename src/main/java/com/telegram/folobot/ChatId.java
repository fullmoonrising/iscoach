package com.telegram.folobot;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class ChatId {
    @Getter private static final Long FOLOCHAT_ID = -1001439088515L;
    @Getter private static final Long POC_ID = -1001154453685L;
    @Getter private static final Long ANDREWSLEGACY_ID = -1001210743498L;
    @Getter private static final Long ANDREW_ID = 146072069L;
    @Getter private static final Long VITALIK_ID = 800522859L;
    @Getter private static final Long FOLOMKIN_ID = 362689512L;
    @Getter private static final Long MOONMOON_ID = 50496196L;

    public static boolean isFolochat(Chat chat) {
        return chat != null && chat.getId().equals(FOLOCHAT_ID);
    }
    public static boolean isAndrew(User user) {
        return user != null && user.getId().equals(ANDREW_ID);
    }
    public static boolean isVitalik(User user) {
        return user != null && user.getId().equals(VITALIK_ID);
    }
    public static boolean isMoonMoon(User user) { return user != null && user.getId().equals(MOONMOON_ID); }
    public static boolean isFo(User user) { return user != null && user.getId().equals(FOLOMKIN_ID); }
}
