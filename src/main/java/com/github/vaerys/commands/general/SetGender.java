package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class SetGender implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        SplitFirstObject userCall = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (Utility.testForPerms(command, dualPerms()) || Utility.canBypass(command.user.get(), command.guild.get())) {
            user = Utility.getUser(command, userCall.getFirstWord(), false);
            if (user != null && userCall.getRest() != null && user.getProfile(command.guild) != null) {
                adminEdit = true;
            } else {
                user = command.user;
            }
        }

        ProfileObject u = user.getProfile(command.guild);
        int maxlength = 20;
        if (user.isPatron) {
            maxlength += 20;
        }
        if (u == null) {
            return "> " + user.displayName + " Has not Spoken yet thus they have nothing to edit.";
        } else {
            if (adminEdit) {
                if (userCall.getRest().length() > maxlength) {
                    return "> Gender's Length is too long...\n(Must be under " + maxlength + " chars)";
                }
                u.setGender(userCall.getRest());
                return "> " + user.displayName + "'s Gender Edited";
            } else {
                if (args.length() > maxlength) {
                    return "> Your Gender's Length is too long...\n(Must be under " + maxlength + "chars)";
                }
                u.setGender(args);
                return "> Gender Edited";
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetGender"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set your Gender. Limit 20 chars (or 40 if you are a patron).";
    }

    @Override
    public String usage() {
        return "[Gender]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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
        return false;
    }

    @Override
    public String dualDescription() {
        return "Allows you to set the Gender of a user.";
    }

    @Override
    public String dualUsage() {
        return "[@User] [Gender]";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }
}
