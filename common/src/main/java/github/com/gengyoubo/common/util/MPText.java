package github.com.gengyoubo.common.util;

import net.minecraft.ChatFormatting;

import java.util.Arrays;

/** Animated text colouring shared by every loader. */
public enum MPText {
    manaita_infinity(80.0D,
            ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN,
            ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE),
    manaita_mode(120.0D,
            ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.YELLOW,
            ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.RED, ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.YELLOW,
            ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.RED),
    manaita_enchantment(120.0D,
            ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.AQUA, ChatFormatting.DARK_PURPLE);

    private final String[] chatFormattings;
    private final double delay;

    MPText(double delay, ChatFormatting... chatFormattings) {
        this.chatFormattings = Arrays.stream(chatFormattings).map(ChatFormatting::toString).toArray(String[]::new);
        this.delay = Math.max(delay, 0.001D);
    }

    public String formatting(String input) {
        String plainText = ChatFormatting.stripFormatting(input);
        if (plainText == null || plainText.isEmpty()) {
            return plainText == null ? "" : plainText;
        }

        StringBuilder result = new StringBuilder(plainText.length() * 3);
        int offset = (int) Math.floor((System.currentTimeMillis() & 0x3FFFL) / delay) % chatFormattings.length;
        for (int i = 0; i < plainText.length(); i++) {
            int colour = (i + chatFormattings.length - offset) % chatFormattings.length;
            result.append(chatFormattings[colour]).append(plainText.charAt(i));
        }
        return result.toString();
    }
}
