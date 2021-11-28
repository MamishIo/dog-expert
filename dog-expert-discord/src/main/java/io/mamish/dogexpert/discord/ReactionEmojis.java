package io.mamish.dogexpert.discord;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.KnownCustomEmoji;

import java.util.Optional;

public class ReactionEmojis {

    private static final String STANDARD_EMOJI_RAGE = "rage";
    private static final String STANDARD_EMOJI_HEART_EYES = "heart_eyes";
    // I would love to have this one, but it's part of a Unicode block that's hard to use
    //private static final String STANDARD_EMOJI_SMILE_TEARS = "smiling_face_with_tear";
    private static final String STANDARD_EMOJI_RELIEVED = "relieved";
    private static final String STANDARD_EMOJI_MONOCLE_DOUBT = "face_with_monocle";
    private static final String STANDARD_EMOJI_DOG = "dog";

    private static final String CUSTOM_EMOJI_COOLDOG = "cooldog";

    private final String tagRage;
    private final String tagHeartEyes;
    private final String tagRelieved;
    private final String tagMonocleDoubt;
    private final String tagDog;
    private final String tagCooldog;

    public ReactionEmojis(DiscordApi discordApi) {
        tagRage = tagFromName(STANDARD_EMOJI_RAGE);
        tagHeartEyes = tagFromName(STANDARD_EMOJI_HEART_EYES);
        tagRelieved = tagFromName(STANDARD_EMOJI_RELIEVED);
        tagMonocleDoubt = tagFromName(STANDARD_EMOJI_MONOCLE_DOUBT);
        tagDog = tagFromName(STANDARD_EMOJI_DOG);

        tagCooldog = tagFromCustom(discordApi, CUSTOM_EMOJI_COOLDOG);
    }

    public String getTagRage() {
        return tagRage;
    }

    public String getTagHeartEyes() {
        return tagHeartEyes;
    }

    public String getTagRelieved() {
        return tagRelieved;
    }

    public String getTagMonocleDoubt() {
        return tagMonocleDoubt;
    }

    public String getTagDog() {
        return tagDog;
    }

    public String getTagCooldog() {
        return tagCooldog;
    }

    private static String tagFromName(String name) {
        return EmojiParser.parseToUnicode(":" + name + ":");
    }

    private static String tagFromCustom(DiscordApi discordApi, String custom) {
        Optional<KnownCustomEmoji> match = discordApi.getCustomEmojisByName(custom).stream().findFirst();
        if (match.isPresent()) {
            return match.get().getReactionTag();
        } else {
            throw new RuntimeException("No such custom emoji found: " + custom);
        }
    }
}
