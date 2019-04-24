package com.yeyolotto.www.yeyo.data;

import android.provider.BaseColumns;

/**
 * Especifica el schema de la base de datos
 */
public final class YeyoContract {

    public static final class TiroEntry implements BaseColumns{
        public static final String TABLE_NAME = "tiro";
        public static final String COLUMN_FECHA = "fecha"; // Formato : 04/07/1991
        public static final String COLUMN_HORA = "hora"; // Formato : N
        public static final String COLUMN_TIRO = "tiro"; // Formato : 905-02-30

        public static final String HORA_DIA = "D";
        public static final String HORA_NOCHE = "N";
    }
}
