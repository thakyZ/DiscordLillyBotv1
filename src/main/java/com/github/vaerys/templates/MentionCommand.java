package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MentionCommand extends Command {

    @Override
    public String getCommand(CommandObject command) {
        return "@" + command.client.bot.name + " " + names()[0];
    }

    @Override
    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        if (mention.getRest() == null) {
            return false;
        }
        if (!command.message.get().getMentions().contains(command.client.bot)) {
            return false;
        }
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        for (String s : names()) {
            String regex = "^(<@|<@!)" + command.client.bot.longID + "> " + s.toLowerCase();
            String toMatch = mention.getFirstWord() + " " + call.getFirstWord().toLowerCase();
            Matcher matcher = Pattern.compile(regex).matcher(toMatch);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getArgs(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    public String type() {
        return Command.TYPE_MENTION;
    }

    @Override
    public void init() {

    }

    public String dualType() {
        return null;
    }

    public String dualDescription() {
        return null;
    }

    public Permissions[] dualPerms() {
        return new Permissions[0];
    }

    public String dualUsage() {
        return null;
    }
}