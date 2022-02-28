package ru.mt.miks.model;

import java.util.HashMap;
import java.util.Map;

public enum LapMarker {
    NORMAL(0,""),
    FIGHT(61,"Fight"),
    PIT(13, "PitUser"),
    BROKE(22, "BrokeOldCar"),
    MISS_PREV(32, "MissPrev"),
    LONG_LAP(33, "LongLight"),
    STOP_AND_GO(51, "StopGo"),
    STOP_AND_GO_LINE(52, "StopGoPitLine"),
    STOP_AND_GO_LIGHT(55, "StopGoSimaphore"),
    STOP_AND_GO_OVERTIME(56, "StopGoOvertime"),
    RED_FLAG(62, "WaitSimaphore"),
    FAILURE(63, "tech"), // not used
    COMPENSATION(64, ""),
    HANDICAP(65, "handicap"), // to be confirmed
    ;

    private static final Map<Integer, LapMarker> codes = new HashMap<>();
    private static final Map<String, LapMarker> strCodes = new HashMap<>();
    static {
        for (LapMarker marker : LapMarker.values()) {
            codes.put(marker.getCode(), marker);
            strCodes.put(marker.getStrCode(), marker);
        }
    }

    private final int code;
    private final String strCode;

    LapMarker(int code, String strCode) {
        this.code = code;
        this.strCode = strCode;
    }

    public int getCode() {
        return code;
    }

    public String getStrCode() {
        return strCode;
    }

    public static LapMarker fromCode(String strCode) {
        return strCodes.get(strCode);
    }

    public static LapMarker fromCode(int code) {
        return codes.get(code);
    }
}
