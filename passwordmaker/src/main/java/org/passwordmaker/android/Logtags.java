package org.passwordmaker.android;

public enum Logtags {


    MAIN_ACTIVITY("MAIN"),
    ACCOUNT_DETAIL_ACTIVITY("ADA"),
    ACCOUNT_DETAIL_FRAGMENT("ADF"),
    ACCOUNT_LIST_ACTIVITY("ALA"),
    ACCOUNT_LIST_FRAGMENT("ALF"),
    CLASSIC_SETTINGS_IMPORTER("CSI"),
    IMPORT_EXPORT_RDF("IMEX"),
    PATTERN_DATA_DETAIL_ACTIVITY("PDDA"),
    PATTERN_DATA_DETAIL_FRAGMENT("PDDF"),
    PATTERN_DATA_LIST_ACTIVITY("PDLA"),
    PATTERN_DATA_LIST_FRAGMENT("PDLF"),
    PWM_APPLICATION("PAPP")
    ;

    private final String tag;
    Logtags(String tag) {
        this.tag = "PWM/" + tag;
    }


    public String getTag() {
        return tag;
    }
}
