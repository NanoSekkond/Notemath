package com.example.notemath;

import java.util.HashMap;

public class LocaleManager {
    private static LocaleManager localeManager;
    private static int locale;
    private static final HashMap<String, String[]> strings = new HashMap<>();
    public static final int EN = 0;
    public static final int ES = 1;
    public static LocaleManager instanceOfLocale() {
        if (localeManager == null) {
            localeManager = new LocaleManager();
        }
        return localeManager;
    }

    public LocaleManager() {
        locale = EN;
        strings.put("app_name", new String[]{"Notemath"});
        strings.put("note_title", new String[]{"Title", "Título"});
        strings.put("settings_button", new String[]{"Settings", "Ajustes"});
        strings.put("delete_button", new String[]{"Delete Note", "Borrar Nota"});
        strings.put("note_desc", new String[]{"Note", "Nota"});
        strings.put("note_settings_button", new String[]{"Note Settings", "Ajustes de Nota"});
        strings.put("help_button", new String[]{"Help", "Ayuda"});
        strings.put("font_size_title", new String[]{"Font size: ", "Tamaño de la fuente: "});
        strings.put("decimal_points_title", new String[]{"Digits after decimal point: ", "Digitos después de la coma: "});
        strings.put("font_size_desc", new String[]{"Changes the font size of your title and description.", "Cambia el tamaño de la fuente de tu título y descripción."});
        strings.put("decimal_points_desc", new String[]{"Changes the amount of digits after the decimal dot.", "Cambia la cantidad de dígitos después de la coma"});
        strings.put("delete_title", new String[]{"Delete", "Borrar"});
        strings.put("delete_desc", new String[]{"Are you sure you want to delete this note?", "Estás seguro que querés borrar esta nota?"});
        strings.put("yes_button", new String[]{"Yes", "Sí"});
        strings.put("no_button", new String[]{"No", "No"});
        strings.put("language_english", new String[]{"English", "Inglés"});
        strings.put("language_spanish", new String[]{"Spanish", "Españól"});
        strings.put("language_setting", new String[]{"Language: English", "Idioma: Españól"});
        strings.put("default_settings", new String[]{"Default Settings.", "Ajustes por defecto."});
    }

    public int getLocale() {
        return locale;
    }

    public void setLocale(int newLocale) {
        locale = newLocale;

    }

    public String getString(String name) {
        return strings.get(name)[locale];
    }
}
