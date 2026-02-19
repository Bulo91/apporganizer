package com.example.apporganizer.logic;

import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.util.Locale;

public class CategoryGuesser {

    // Valeurs de catégorie ApplicationInfo (API 26+). On les met en dur pour éviter les soucis de constantes manquantes.
    // Source : Android API (ApplicationInfo.category)
    private static final int CAT_UNDEFINED = -1;
    private static final int CAT_GAME = 0;
    private static final int CAT_AUDIO = 1;
    private static final int CAT_VIDEO = 2;
    private static final int CAT_IMAGE = 3;
    private static final int CAT_SOCIAL = 4;
    private static final int CAT_NEWS = 5;
    private static final int CAT_MAPS = 6;
    private static final int CAT_PRODUCTIVITY = 7;

    public static CategoryResult guess(String label, String packageName, ApplicationInfo appInfo) {
        // 1) Catégorie Android (API 26+)
        CategoryResult fromAndroid = fromAndroidCategory(appInfo);
        if (fromAndroid != null) return fromAndroid;

        // 2) Heuristiques
        return fromHeuristics(label, packageName);
    }

    private static CategoryResult fromAndroidCategory(ApplicationInfo appInfo) {
        if (appInfo == null) return null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null;

        int cat = appInfo.category;
        if (cat == CAT_UNDEFINED) return null;

        switch (cat) {
            case CAT_GAME:
                return new CategoryResult("Jeux", 95);
            case CAT_SOCIAL:
                return new CategoryResult("Social", 95);
            case CAT_PRODUCTIVITY:
                return new CategoryResult("Productivité", 95);
            case CAT_MAPS:
                return new CategoryResult("Transport", 90);
            case CAT_AUDIO:
            case CAT_VIDEO:
            case CAT_IMAGE:
                return new CategoryResult("Divertissement", 90);
            case CAT_NEWS:
                return new CategoryResult("Actualités", 85);
            default:
                return new CategoryResult("Autres", 70);
        }
    }

    private static CategoryResult fromHeuristics(String label, String packageName) {
        String text = (label + " " + packageName).toLowerCase(Locale.ROOT);

        // Social
        if (containsAny(text, "whatsapp", "instagram", "snapchat", "tiktok", "facebook", "messenger", "discord", "telegram"))
            return new CategoryResult("Social", 85);

        // Finance
        if (containsAny(text, "revolut", "paypal", "bank", "bnp", "boursorama", "n26", "wise", "credit", "societe"))
            return new CategoryResult("Finance", 80);

        // Transport
        if (containsAny(text, "uber", "bolt", "sncf", "ratp", "maps", "waze"))
            return new CategoryResult("Transport", 80);

        // Divertissement
        if (containsAny(text, "netflix", "prime", "disney", "spotify", "deezer", "youtube", "music", "video"))
            return new CategoryResult("Divertissement", 75);

        // Shopping
        if (containsAny(text, "amazon", "vinted", "shein", "zalando", "shop", "shopping", "aliexpress"))
            return new CategoryResult("Shopping", 75);

        // Productivité
        if (containsAny(text, "gmail", "outlook", "calendar", "drive", "docs", "sheets", "notion", "trello"))
            return new CategoryResult("Productivité", 75);

        // Outils
        if (containsAny(text, "scanner", "pdf", "zip", "file", "manager", "tools", "outil"))
            return new CategoryResult("Outils", 65);

        return new CategoryResult("Autres", 40);
    }

    private static boolean containsAny(String text, String... words) {
        for (String w : words) {
            if (text.contains(w)) return true;
        }
        return false;
    }
}
