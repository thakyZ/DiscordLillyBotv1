package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateChar extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<CharacterObject> userChars = command.user.characters;
        String charName = args.split(" ")[0];
        CharacterObject selectedChar = command.guild.characters.getCharByName(charName);
        List<IRole> cosmeticRoles = command.user.getCosmeticRoles(command);
        List<Long> cosmeticRoleIDs = cosmeticRoles.stream().map(iRole -> iRole.getLongID()).collect(Collectors.toList());
        String cosmeticString = command.guild.config.moduleRoles ? " and cosmetic roles" : "";
        if (selectedChar != null) {
            if (selectedChar.getUserID() != command.user.longID) {
                return "> That is not your character you cannot update it.";
            }
            if (selectedChar.getNickname().equals(command.user.displayName)
                    && selectedChar.getRoleIDs().containsAll(cosmeticRoleIDs)) {
                return "> You haven't updated your details since the last time this character was updated.\n" +
                        "Characters use your nickname " + cosmeticString + " to use as data.";
            }
            selectedChar.update(command.user.displayName, cosmeticRoles);
            String response = "> Your character has been updated using your nickname" + cosmeticString + ".";
            if (isSubtype(command, names()[0])) {
                response += "\nIf you were attempting to create a new character and not edit this character you need to specify a different character ID.";
            }
            return response;
        } else {
            int maxChars = command.guild.characters.maxCharsForUser(command);
            int rewardCount = command.user.getRewardValue(command);
            if (userChars.size() == maxChars) {
                if (command.guild.config.modulePixels && command.guild.config.xpGain && rewardCount != 4) {
                    return "> You have reached the maximum allowed characters for your level," +
                            " you will have to either rank up or delete an old character to make room.";
                }
                return "> You have reached the maximum allowed characters. You will have to delete an old character to make room.";
            }
            command.guild.characters.addChar(charName, cosmeticRoles, command.user);
            int remainingSlots = (maxChars - userChars.size() - 1);
            String response = "> New Character Created using your nickname " + cosmeticString + " to fill in data." +
                    "\n(" + remainingSlots + " Character slot";
            if (remainingSlots != 1) response += "s";
            response += "remaining)";
            if (isSubtype(command, names()[0])) {
                response += "\nTo update the name";
                if (command.guild.config.moduleRoles) response += " or roles";
                response += " linked to this character just run this command again.";
            }
            return response;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"NewChar", "UpdateChar"};
    }

    @Override
    public String description(CommandObject command) {
        String cosmetic = command.guild.config.moduleRoles ? "Cosmetic roles and " : "";
        return "Updates/Creates a character with the data from your discord account into the character. (" + cosmetic + "Nickname)";
    }

    @Override
    public String usage() {
        return "[Character ID]";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
    }

    @Override
    public String channel() {
        return CHANNEL_CHAR;
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
    public void init() {

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
