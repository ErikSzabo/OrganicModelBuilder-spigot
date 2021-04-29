package me.devrik.organicmodelbuilder.message;

public enum Message {
    CMD_LOAD("translations.load"),
    CMD_ROLL("translations.roll"),
    CMD_ADJUST("translations.adjust"),
    CMD_END("translations.end"),
    CMD_LIST("translations.list"),
    CMD_UNDO("translations.undo"),
    CMD_CANCEL("translations.cancel"),
    CMD_INIT("translations.init"),
    CMD_INVALID("translations.invalid"),
    NO_PERMISSION("translations.no-permission"),
    PLAYER_ONLY("translations.player-only"),
    NOT_ENOUGH_ARGS("translations.not-enough-args"),
    YRP_NUMBER("translations.yrp-number"),
    NOT_CREATING("translations.not-creating"),
    PART_MODIFIED("translations.part-modified"),
    NOT_COMPLETED("translations.not-completed"),
    INIT_SUCCESS("translations.init-success"),
    INIT_FAILED("translations.init-failed"),
    INIT_ALREADY("translations.init-already"),
    LOADED_MODELS("translations.loaded-models"),
    SCALE_NOT_NUMBER("translations.scale-not-number"),
    SCALE_TOO_BIG("translations.scale-too-big"),
    PATTERN_ERROR("translations.pattern-error"),
    ALREADY_CREATING("translations.already-creating"),
    MODEL_NOT_FOUND("translations.model-not-found"),
    MODEL_LOADED1("translations.model-loaded1"),
    MODEL_LOADED2("translations.model-loaded2"),
    MODEL_LOADED3("translations.model-loaded3"),
    ROTATION_NOT_NUMBER("translations.rotation-not-number"),
    ROLL_SUCCESS("translations.roll-success"),
    HELP("translations.help"),
    NO_MORE_TO_PASTE1("translations.no-more-to-paste1"),
    NO_MORE_TO_PASTE2("translations.no-more-to-paste2"),
    PASTE_ALL1("translations.paste-all1"),
    PASTE_ALL2("translations.paste-all2"),
    PASTE_ALL3("translations.paste-all3"),
    PASTE_ALL4("translations.paste-all4"),
    UNDID("translations.undid"),
    NOT_ANY_PLACED("translations.not-any-placed"),
    PART_NOT_FOUND("translations.part-not-found"),
    ADD_TO_HISTORY("translations.add-to-history"),
    VALIDATION_SUCCESS("translations.validation-success"),
    MERGING_PARTS("translations.merging-parts"),
    CANCEL_SUCCESS("translations.cancel-success"),
    PART_PLACED("translations.part-placed"),
    PLACE_NEXT_PART("translations.place-next-part"),
    OR_MODIFY("translations.or-modify"),
    NOTHING_TO_UNDO("translations.nothing-to-undo");

    private String value;

    Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
