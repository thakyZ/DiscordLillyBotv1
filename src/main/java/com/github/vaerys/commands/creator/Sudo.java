package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.MessageHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/02/2017.
 */
public class Sudo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (!command.guild.config.debugMode) return "> Debug mode is disabled. No Sudo for you missy.";
        SplitFirstObject sudo = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, sudo.getFirstWord(), false, false);
        if (user == null) {
            return "> Could not find user.";
        }
        command.setAuthor(user.get());
        if (sudo.getRest() == null) {
            return "> You need to specify some arguments.";
        }
        try {
            new MessageHandler(sudo.getRest(), command, false);
        } catch (Exception e) {
            String errorPos = "";
            for (StackTraceElement s : e.getStackTrace()) {
                if (s.toString().contains("com.github.vaerys")) {
                    errorPos = s.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append("> I caught an Error, Please send this Error message and the message that caused this error " +
                    "to my **Direct Messages** so my developer can look at it and try to solve the issue.\n```\n");
            builder.append(e.getClass().getName());
            builder.append(": " + e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n" + Constants.PREFIX_INDENT + "at " + errorPos);
            }
            builder.append("```");
            RequestHandler.sendMessage(builder.toString(), command.channel.get());
            Utility.sendStack(e);
        }
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Sudo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Runs a command as though you were someone else.\n" + ownerOnly;
    }

    @Override
    public String usage() {
        return "[@User] [Command + args]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
