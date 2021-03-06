package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagNoBreakShort extends TagObject {

    public TagNoBreakShort(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return removeAllTag(from);
    }

    @Override
    public String tagName() {
        return "<!br>";
    }

    @Override
    public String prefix() {
        return name + "\n";
    }

    @Override
    public int argsRequired() {
        return 0;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String desc() {
        return "When this tag is put at the end of a line it removes the line break.";
    }
}
