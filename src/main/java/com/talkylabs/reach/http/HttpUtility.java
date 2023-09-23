package com.talkylabs.reach.http;

import com.talkylabs.reach.Reach;

import java.util.List;

final class HttpUtility {
    public static String getUserAgentString(final List<String> userAgentExtensions) {
        StringBuilder userAgentString = new StringBuilder();
        userAgentString.append("reach-java/")
                .append(Reach.VERSION)
                .append(" (")
                .append(Reach.OS_NAME)
                .append(" ")
                .append(Reach.OS_ARCH)
                .append(") ")
                .append("java/")
                .append(Reach.JAVA_VERSION);

        if (userAgentExtensions != null && !userAgentExtensions.isEmpty()) {
            userAgentExtensions.stream().forEach(userAgentExtension -> {
                userAgentString.append(" ");
                userAgentString.append(userAgentExtension);
            });
        }

        return userAgentString.toString();
    }

    public static String getUserAgentString(final List<String> userAgentExtensions, final boolean isCustomClient) {
        return isCustomClient ? getUserAgentString(userAgentExtensions) + " custom"
                : getUserAgentString(userAgentExtensions);
    }
}
